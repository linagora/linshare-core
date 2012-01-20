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
package org.linagora.linShare.view.tapestry.components;

import java.text.SimpleDateFormat;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.constants.UserType;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;

/**
 * display in a pôpup the user details
 * 
 * @author ncharles
 *
 */
public class UserDetailsDisplayer {

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @SessionState
    private UserVo userLoggedIn;
    @SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=700", "height=300"})
    private WindowWithEffects userDetailsWindow;
    @InjectComponent
    private Zone userDetailsTemplateZone;
    @Inject
    private UserFacade userFacade;
    @Inject
    private Messages messages;
    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @SuppressWarnings("unused")
    @Property
    private UserVo detailedUser = new UserVo("", "", "", "", UserType.GUEST);

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    public Zone getShowUser(String mail) throws BusinessException {
//        detailedUser = userFacade.findUserFromAuthorizedDomainOnly(userLoggedIn.getDomainIdentifier(), mail);
        detailedUser = userFacade.loadUserDetails(mail, userLoggedIn.getDomainIdentifier());
//        detailedUser = userFacade.searchUser(mail, "", "", userLoggedIn);
        return userDetailsTemplateZone;
    }

    public boolean isShowComment() {
        if (detailedUser.isGuest()) {
            if (detailedUser.getOwnerLogin().equals(userLoggedIn.getLogin())) {
                if ((detailedUser.getComment() != null) && (!detailedUser.getComment().equals(""))) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getUserComment() {
        return detailedUser.getComment();
    }

    public String getFormattedExpiryDate() {
        if (detailedUser.getExpirationDate() != null) {
            String dateFormat = messages.get("global.pattern.date");
            SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
            return dateFormatter.format(detailedUser.getExpirationDate());
        } else {
            return "";
        }
    }
}
