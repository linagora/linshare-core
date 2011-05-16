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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

/** Helps Managing guests and root administrator authentication.
 *
 */
public class DaoCasAuthProvider implements UserDetailsService {

    private final UserFacade userFacade;
    
    private final static Log logger = LogFactory.getLog(DaoCasAuthProvider.class);

    public DaoCasAuthProvider(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

        if (username == null || username.length() == 0) {
            throw new UsernameNotFoundException("username must not be null");
        }
        
        UserVo userVo = null;
        String password = "";
        
        //if username is uid as a login and not an email
//        if(username.indexOf("@")==-1 ){  	
//        	userVo = userFacade.findUserFromLdapwithUid(username);
//        	if(userVo==null) throw new UsernameNotFoundException("User not found with uid"+username);
//        }  else {
//        	//username (login) is email
//        	userVo = userFacade.findUser(username);
//        }
        

        if (userVo == null || password == null || Role.SYSTEM.equals(userVo.getRole())) {
            throw new UsernameNotFoundException("User not found");
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_AUTH));

        if (userVo.isUpload()) {
            grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_UPLOAD));
        }
        if (userVo.isAdministrator()) {
            grantedAuthorities.add(new GrantedAuthorityImpl(AuthRole.ROLE_ADMIN));
        }
        return new User(userVo.getLogin(), password, true, true, true, true,
            grantedAuthorities.toArray(new GrantedAuthority[0]));
    }

}
