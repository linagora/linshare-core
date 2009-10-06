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

import java.util.ArrayList;
import java.util.List;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.ldap.LdapAuthoritiesPopulator;

/** Provides autorizations for users referenced un LDAP.
 */
public class LdapAuthorizationsProvider implements LdapAuthoritiesPopulator {

    private UserFacade userFacade;

    public LdapAuthorizationsProvider(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    /** Get Authorizations for a user.
     * @param userData context object returned by the user.
     * @param username user login.
     * @return a list of GrantedAuthority
     */
    public GrantedAuthority[] getGrantedAuthorities(DirContextOperations userData, String username) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        
        String usermail = username; //mail is the login
        
        //if username is uid as a login and not an email
        if(username!=null && username.indexOf("@")==-1){
        	usermail = userData.getStringAttribute("mail");
        }
        
        UserVo user = userFacade.findUser(usermail);

        grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_AUTH));

        if (user == null) {
            return grantedAuthorities.toArray(new GrantedAuthority[0]);
        }

        if (user.isAdministrator()) {
            grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_ADMIN));
        }
        if (!user.isGuest()) {
            grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_INTERNAL));
        }
        if (!user.isGuest() || user.isUpload()) {
            // the user is an internal or he has the rights for upload
            grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_UPLOAD));
        }
        return grantedAuthorities.toArray(new GrantedAuthority[0]);
    }
}
