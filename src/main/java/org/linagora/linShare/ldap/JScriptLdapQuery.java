package org.linagora.linShare.ldap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.linagora.linShare.core.domain.entities.Domain;
import org.linagora.linShare.core.domain.entities.Internal;
import org.linagora.linShare.core.domain.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NameNotFoundException;

public class JScriptLdapQuery {
	
	/** 	Attributes 		**/
	
	// Logger
	private static final Logger logger = LoggerFactory.getLogger(JScriptEvaluator.class);
	private static final String LOG_INFO_KEY_PARSE = "Keys parsed which will be used in LDAP query are:";
	
	// Java to JavaScript Translator object
	private JScriptEvaluator jScriptEvaluator;
	
	// Ladp Jndi Service
	private JndiServices ldapJndiService;
	
	// Ldap Domain
	private Domain domain;

	
	/** 	Methods 	**/
	
	public JScriptLdapQuery(JScriptEvaluator jScriptEvaluator,
			JndiServices ldapJndiService, Domain domain) {
		super();
		this.jScriptEvaluator = jScriptEvaluator;
		this.domain = domain;
		
		if(ldapJndiService == null) {
			try {
				this.ldapJndiService = new JndiServices(domain.getLdapConnection().toLdapProperties());
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		} else {
			this.ldapJndiService = ldapJndiService;	
		}
		this.jScriptEvaluator.setJndiService(this.ldapJndiService);
	}
	
	public JScriptLdapQuery(JScriptEvaluator jScriptEvaluator,
			Domain domain) {
		this(jScriptEvaluator,null,domain);
	}
	
	/**
	 * Get user from Ldap.
	 * @param userId
	 * @return User
	 */
	public User getUser(String userId) {
		
		String command = domain.getPattern().getGetUserCommand();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("domain", domain.getDifferentialKey());
		Map<String, List<String>> retMap = jScriptEvaluator.evalToEntryMap(domain, command, params);
        return mapToUser(retMap, parseKeys(domain));
	}
	
	/**
	 * Get all users from the selected Ldap domain.
	 * @return List of Users
	 */
	public List<User> getAllDomainUsers() {
		
		String command = domain.getPattern().getGetAllDomainUsersCommand();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("domain", domain.getDifferentialKey());
		List<String> ret = jScriptEvaluator.evalToStringList(domain, command, params);
		List<User> users = dnListToUsersList(ret);
        return users;
	}

	/**
	 * This method allow to search a user from part of his mail or/and first name or/and last name 
	 * @param mail
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public List<User> searchUser(String mail, String firstName,	String lastName) {
		
		String command = domain.getPattern().getSearchUserCommand();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mail", addExpansionCharacters(mail));
		params.put("firstName", addExpansionCharacters(firstName));
		params.put("lastName", addExpansionCharacters(lastName));
		params.put("domain", domain.getDifferentialKey());
		List<String> uidList = jScriptEvaluator.evalToStringList(domain, command, params);
        return dnListToUsersList(uidList);
	}
		
	
	/**
	 * Ldap Authentification method
	 * @param login
	 * @param userPasswd
	 * @return
	 * @throws NamingException
	 */
	public User auth(String login, String userPasswd) throws NamingException {
	
		String command = domain.getPattern().getAuthCommand();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("login", login);
		params.put("domain", domain.getDifferentialKey());
		List<String> retList = jScriptEvaluator.evalToStringList(domain, command, params);
		
		
		if (retList == null || retList.size() < 1) {
			throw new NameNotFoundException("No user found for login: "+login+" and domain: " + domain.getIdentifier());
		} else if (retList.size() > 1) {
			logger.error("The authentification query had returned more than one user !!!");
			return null;
		}
		
		
		String dn = retList.get(0);
		if(ldapJndiService.auth(userPasswd, dn)) {
			return dnToUser(parseKeys(domain), dn);
		}
		return null;
	}
		
	/**
	 * This method is designed to add expansion characters to the input string. 
	 * @param string : any string
	 * @return 
	 */
	private String addExpansionCharacters(String string) {
		if (string==null || string.length()<1){
			string="*";
		} else {
			string = "*" + string + "*";
		}
		return string;
	}

	
	/**
	 * Create an user list from an dn list 
	 * @param dnList
	 * @return
	 */
	private List<User> dnListToUsersList(List<String> dnList) {
		String[] keys = parseKeys(domain);
		List<User> users = new ArrayList<User>();
		for (String string : dnList) {
			User user = dnToUser(keys, string);
	        users.add(user);
		}
		return users;
	}

	/**
	 * This method retrieve user informations from user ldap dn and build an user object.  
	 * @param keys : Ldap user attribute list
	 * @param string : Distinguish Name
	 * @return
	 */
	private User dnToUser(String[] keys, String dn) {
		String unitCommand = "ldap.entry(dn,\"objectClass=*\");";
		Map<String, Object> unitParams = new HashMap<String, Object>();
		unitParams.put("dn", dn);
		Map<String, List<String>> retMap = jScriptEvaluator.evalToEntryMap(domain, unitCommand, unitParams);
		return mapToUser(retMap, keys);
	}

	/**
	 * This method is designed to build a User object from a Ldap entry result.
	 * @param retMap
	 * @param keys
	 * @param domain
	 * @return
	 */
	private User mapToUser(Map<String, List<String>> retMap, String[] keys) {
		String mail = (String) retMap.get(keys[0]).get(0);
    	String firstName = (String) retMap.get(keys[1]).get(0);
        String lastName = (String) retMap.get(keys[2]).get(0);
        User user = new Internal(mail, firstName, lastName, mail);
        user.setDomain(domain);
		return user;
	}

	/**
	 * This method is used to extract mail first name and last name from the configuration. 
	 * @param domain
	 * @return String list containing mail, first name and last name in this order.
	 */
	private String[] parseKeys(Domain domain) {
		String keys = domain.getPattern().getGetUserResult().replaceAll("\\s+", " ").trim().toLowerCase();
		logger.info(LOG_INFO_KEY_PARSE + keys);
		return keys.split(" ");
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		jScriptEvaluator.setJndiService(null);
		jScriptEvaluator=null;
		ldapJndiService = null;
		domain=null;
		super.finalize();
	}
}
