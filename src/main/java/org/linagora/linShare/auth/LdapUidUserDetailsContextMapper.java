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
package org.linagora.linShare.auth;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.security.userdetails.ldap.UserDetailsContextMapper;



/**
 *  LDAP Attributes and Customized UserDetails
 *  LdapUidUserDetailsContextMapper is a way to customize userdetails of a spring LdapAuthenticationProvider.
 *  use LdapAuthenticationProvider.setUserDetailsContextMapper(...) to set this custom mapper.
 *  
 *  only mapUserFromContext() is implemented and used in authentication process.
 *  
 * @author slevesque
 *
 */
public class LdapUidUserDetailsContextMapper implements UserDetailsContextMapper  {

	
//	Only the first method is relevant for authentication. If you provide an implementation
//	of this interface, you can control exactly how the UserDetails object is created.
//	The first parameter is an instance of Spring LDAP's DirContextOperations which gives you access
//	to the LDAP attributes which were loaded. The username parameter is the name used to authenticate
//	and the final parameter is the list of authorities loaded for the user. 

//	The way the context data is loaded varies slightly depending on the type of authentication you are using.
//	With the BindAuthenticatior, the context returned from the bind operation will be used to read the attributes,
//	otherwise the data will be read using the standard context obtained from the configured ContextSource
//	(when a search is configured to locate the user, this will be the data returned by the search object). 

	private final String ldapLoginAttribute;
	
	public LdapUidUserDetailsContextMapper(String ldapLoginAttribute) {
		this.ldapLoginAttribute = ldapLoginAttribute;
	}
	
	public UserDetails mapUserFromContext(DirContextOperations ctx,
			String username, GrantedAuthority[] authority) {
        if (username == null || username.length() == 0) {
            throw new UsernameNotFoundException("username must not be null");
        }

//        try {
//        	System.out.println("***find LDAP entrie for uid="+username);
//			for (NamingEnumeration ae = ctx.getAttributes().getAll(); ae.hasMoreElements();) {
//			    Attribute attr = (Attribute)ae.next();
//			    String attrId = attr.getID();
//			    for (NamingEnumeration vals = attr.getAll(); vals.hasMore();) {
//			      String value = vals.next().toString();
//			      System.out.println(attrId + ": " + value);
//			    }
//			}
//		} catch (NamingException e) {
//			e.printStackTrace();
//		}
        
		String usermail = ctx.getStringAttribute(ldapLoginAttribute);
		
		//set email as the new username of the user (from the login form it was uid)
		if(usermail==null) throw new UsernameNotFoundException(ldapLoginAttribute+" must not be null");
		return new User(usermail, "", true, true, true, true,authority);
	}

	public void mapUserToContext(UserDetails arg0, DirContextAdapter arg1) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
