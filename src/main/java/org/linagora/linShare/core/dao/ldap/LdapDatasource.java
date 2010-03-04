/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.dao.ldap;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.linagora.linShare.core.dao.LdapDao;
import org.linagora.linShare.core.domain.entities.Internal;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.AttributesMapperCallbackHandler;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.simple.AbstractParameterizedContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.support.LdapUtils;


public class LdapDatasource implements LdapDao {

	protected static final Logger technicalTracer = LoggerFactory
	.getLogger(LdapDatasource.class);

	/**
	 * We have one
	 */
    private final ContextSource contextSource;
	private final LdapTemplate ldapTemplate;
	private final String baseDn_ldap;
	private final String ldap_filter;
	private final String ldapLoginAttribute;
	private final int pageSize; // paging ldap result (limit result from ldap)
	

	//By default the scope is subtree
	private int scope=SearchControls.SUBTREE_SCOPE;
	//map the password to userPassword ldap field
	private final String PASSWORDFIELD = "userPassword";
	
	/**
	 * 
	 * @param ldap
	 * @param baseDn_ldap
	 * @param ldap_filter
	 */
	public LdapDatasource(ContextSource contextSource, LdapTemplate ldapTemplate, 
			String baseDn_ldap, String ldap_filter, 
			String ldapLoginAttribute, int pageSize) {
        this.contextSource = contextSource;
		this.ldapTemplate = ldapTemplate;
		this.baseDn_ldap = baseDn_ldap;
		this.ldap_filter = ldap_filter;
		this.ldapLoginAttribute = ldapLoginAttribute;
		this.pageSize = pageSize;
	}
	/**
	 * 
	 * @param ldap
	 * @param baseDn_ldap
	 * @param ldap_filter
	 * @param scope
	 */
	public LdapDatasource(ContextSource contextSource, LdapTemplate ldap,
			String baseDn_ldap,String ldap_filter,
			String ldapLoginAttribute, int scope, int pageSize ){
		this(contextSource, ldap,baseDn_ldap,ldap_filter, ldapLoginAttribute, pageSize);
		this.scope=scope;
	}

	/**
	 * in this implementation the exist method match only for FedMail
	 */

	public boolean exist(String... keys) {

		try {
			final String searchQuery = MessageFormat.format(ldap_filter, (Object[])keys);
			technicalTracer.debug("Query LDAP for :"+searchQuery);
			SearchControls searchControls=new SearchControls();
			searchControls.setSearchScope(scope);
			NamingEnumeration<SearchResult> naming=ldapTemplate.getContextSource().getReadOnlyContext().search(baseDn_ldap,searchQuery, searchControls);
			if(naming.hasMore()){
				return true;
			}
		} catch (NamingException e) {
			throw new TechnicalException(TechnicalErrorCode.GENERIC,"Object not found in ldap",e);
		} catch (javax.naming.NamingException e) {
			throw new TechnicalException(TechnicalErrorCode.GENERIC,"Object not found in ldap");
		}
		return false;
	}

    /** Get values from LDAP.
     * 
     * @param keys search criterias
     * @param attributes attributes we want to get.
     * @return a Map<attribute, values>
     */
	public Map<String,String> getValues(List<String> keys ,String...attributes ){
		try {
			final String searchQuery = MessageFormat.format(ldap_filter, keys.toArray());
			technicalTracer.debug("Query LDAP for :"+searchQuery);
			HashMap<String, String> hashMap=new HashMap<String, String>();
			SearchControls searchControls=new SearchControls();
			searchControls.setSearchScope(scope);
			NamingEnumeration<SearchResult> naming=ldapTemplate.getContextSource().getReadOnlyContext().search(baseDn_ldap, searchQuery, searchControls);
			if(naming.hasMore()){
				SearchResult searchResult=naming.next();
				for(String currentAttribute:attributes){
					hashMap.put(currentAttribute, searchResult.getAttributes().get(currentAttribute).get().toString());
				}
				
			}
			
			return hashMap;

		}catch(Exception e){
			throw new TechnicalException(TechnicalErrorCode.GENERIC,"Object not found in ldap");
		}
	}

	public boolean auth(String password, String... keys) {
		final String searchQuery = MessageFormat.format(ldap_filter, (Object[]) keys);
		technicalTracer.debug("Query LDAP for :"+searchQuery);
		List<String> results = ldapTemplate.search(baseDn_ldap, searchQuery, new DnContextMapper());
        if (results.size() != 1) {
            // user is not in LDAP, probably a guest or root account
            return false;
        }
        DirContext ctx = null;
        try {
            ctx = contextSource.getContext(results.get(0), password);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            LdapUtils.closeContext(ctx);
        }
    }

/**
 * get the userPassword for an user
 * this is important to get the salt (last four bytes)
 * @param keys to compose login field
 * @return user password
 */
	public byte[] getUserPassword(String... keys) {
		
		 byte[] res = null;
		
		try {

			SearchControls searchControls=new SearchControls();
			searchControls.setSearchScope(scope);
			NamingEnumeration<SearchResult> naming=ldapTemplate.getContextSource().getReadOnlyContext().search(baseDn_ldap, MessageFormat.format(ldap_filter, (Object[])keys), searchControls);
			if(naming.hasMore()){
				SearchResult searchResult=naming.next();
				res = (byte[])searchResult.getAttributes().get(PASSWORDFIELD).get();
			}
		} catch (NamingException e) {
			throw new TechnicalException(TechnicalErrorCode.GENERIC,"Object not found in ldap",e);
		}catch(Exception e){
			throw new TechnicalException(TechnicalErrorCode.GENERIC,"problem to get PASSWORD FIELD",e);
		}
		return res;
	}
	
	

    /** Search a user (near match search).
     * @param mail user mail.
     * @param firstName user first name.
     * @param lastName user last name.
     * @return list of users.
     */
    public LdapSearchResult<User> searchUser(String mail, String firstName, String lastName) {
        AndFilter filter = new AndFilter();
        if (mail != null && mail.length() > 0) {
            filter.and(new LikeFilter("mail", "*" + mail + "*"));
        } else {
            filter.and(new LikeFilter("mail", "*"));
        }
        if (firstName != null && firstName.length() > 0) {
            filter.and(new LikeFilter("givenName", firstName + "*"));
        } else {
            filter.and(new LikeFilter("givenName", "*"));
        }
        if (lastName != null && lastName.length() > 0) {
            filter.and(new LikeFilter("sn", lastName + "*"));
        } else {
            filter.and(new LikeFilter("sn", "*"));
        }

        technicalTracer.info("Search pattern = " + filter.encode());
        
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(scope);

        CollectingNameClassPairCallbackHandler handler = new AttributesMapperCallbackHandler(new UserAttributesMapper());
        if(isPaged()) {
            //you can use a pagedresultcookie to keep information of the position of your last research (not use here)
            PagedResultsDirContextProcessor pagingProcessor = new PagedResultsDirContextProcessor(pageSize);         
            ldapTemplate.search(baseDn_ldap, filter.encode(), searchControls, handler, pagingProcessor);
        } else {
            ldapTemplate.search(baseDn_ldap, filter.encode(), searchControls, handler);
        }
        
        @SuppressWarnings( "unchecked" )
        List<User> users = (List<User>) handler.getList();
        
        LdapSearchResult<User> searchResult =  new LdapSearchResult<User>(users,null);
        
	    if(isSearchResultTrucated(users.size())) {
	    	searchResult.setTruncated(false);
	    } else {
	    	searchResult.setTruncated(true);
	    }
        
        //old search without paging
       // List<User> users = ldapTemplate.search(baseDn_ldap, filter.encode(),searchControls,new UserAttributesMapper());
        //return users;
	    
	    return searchResult;
    }

    /** 
     * @see LdapDao#searchUserAnyWhere(String, String, String)
     */
    public LdapSearchResult<User> searchUserAnyWhere(String mail, String firstName, String lastName) {
        AndFilter filter = new AndFilter();
        if (mail != null && mail.length() > 0) {
        	
            filter.and(new LikeFilter("mail", "*" + mail + "*"));
        } else {
            filter.and(new LikeFilter("mail", "*"));
        }
        if (firstName != null && firstName.length() > 0) {
            filter.and(new LikeFilter("givenName", "*" +firstName + "*"));
        } else {
            filter.and(new LikeFilter("givenName", "*"));
        }
        if (lastName != null && lastName.length() > 0) {
            filter.and(new LikeFilter("sn", "*" +lastName + "*"));
        } else {
            filter.and(new LikeFilter("sn", "*"));
        }

        technicalTracer.info("Search pattern = " + filter.encode());
        
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(scope);

        CollectingNameClassPairCallbackHandler handler = new AttributesMapperCallbackHandler(new UserAttributesMapper());

        if(isPaged()) {
            PagedResultsDirContextProcessor pagingProcessor = new PagedResultsDirContextProcessor(pageSize);         
            ldapTemplate.search(baseDn_ldap, filter.encode(), searchControls, handler, pagingProcessor);
        } else {
            ldapTemplate.search(baseDn_ldap, filter.encode(), searchControls, handler);
        }
        
        @SuppressWarnings( "unchecked" )
        List<User> users = (List<User>) handler.getList();
        
        LdapSearchResult<User> searchResult =  new LdapSearchResult<User>(users,null);
        
	    searchResult.setTruncated(isSearchResultTrucated(users.size()));

        //old search without paging
        //List<User> users = ldapTemplate.search(baseDn_ldap, filter.encode(), new UserAttributesMapper());
        return searchResult;
    }
    
    /** Search a user (exact match search).
     * @param mail user mail.
     * @return founded user.
     */
    public User searchUser(String mail) {
        if (mail == null || mail.length() == 0) {
            throw new IllegalArgumentException("Argument must not be empty or null");
        }
        String compiled = ldap_filter.replaceAll("\\{0\\}", mail);
        try {
        	List<User> users = ldapTemplate.search(baseDn_ldap, compiled, new UserAttributesMapper());
        	if (users.size() == 0) {
                return null;
            } else if (users.size() == 1) {
                return users.get(0);
            } else {
                throw new IllegalStateException("More than one user found with email : " + mail);
            }
        } catch(Exception e) {
        	technicalTracer.warn("Cannot connect to Ldap directory",e);
        	return null;
        }
        
    }

    /** This class is used to map ldap attributes with a user entity. */
    private class UserAttributesMapper implements AttributesMapper {

        public Object mapFromAttributes(Attributes attributes) throws javax.naming.NamingException {
            String mail = (String) attributes.get(ldapLoginAttribute).get();
            String firstName = "";
            if(attributes.get("givenName")!=null){
            	firstName = (String) attributes.get("givenName").get();
            }
            String lastName = (String) attributes.get("sn").get();
            return new Internal(mail, firstName, lastName, mail);
        }

    }

    private final static class DnContextMapper extends AbstractParameterizedContextMapper<String> {
        @Override
        protected String doMapFromContext(DirContextOperations ctx) {
            return ctx.getNameInNamespace();
        }
    };
    
    public boolean isSearchResultTrucated(int listSize){
    	if( isPaged() && listSize < pageSize ) {
    		return false;
    	} else {
    		return true;
    	}
    }

    public boolean isPaged(){
    	return pageSize > 0;
    }

}
