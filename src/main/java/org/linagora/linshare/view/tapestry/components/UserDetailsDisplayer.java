/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.view.tapestry.components;

import java.text.SimpleDateFormat;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.UserFacade;

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
    @Property
    private UserVo detailedUser = new UserVo("", "", "", "", AccountType.GUEST);

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    public Zone getShowUser(String mail) throws BusinessException {
//        detailedUser = userFacade.findUserFromAuthorizedDomainOnly(userLoggedIn.getDomainIdentifier(), mail);
        detailedUser = userFacade.loadUserDetails(mail, userLoggedIn.getDomainIdentifier(), userLoggedIn.getDomainIdentifier());
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
