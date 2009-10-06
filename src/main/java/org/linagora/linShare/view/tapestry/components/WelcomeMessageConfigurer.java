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

import java.util.HashSet;
import java.util.Set;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.linagora.linShare.core.domain.constants.Language;
import org.linagora.linShare.core.domain.entities.UserType;
import org.linagora.linShare.core.domain.entities.WelcomeText;
import org.linagora.linShare.view.tapestry.beans.WelcomeMessageView;
import org.linagora.linShare.view.tapestry.utils.WelcomeMessageUtils;

/** This component is used for welcome message configuration.
 */
public class WelcomeMessageConfigurer {

    /* ***********************************************************
     *                         Parameters
     ************************************************************ */
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    private Set<WelcomeText> welcomeTexts;

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @InjectComponent
    private Zone welcomeMessageBoxZone;

    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @Property
    private String welcomeMessage;
    @Persist
    private Language selectedLanguage;
    @Property
    private WelcomeMessageView welcomeMessageView;
    @Property
    private Set<WelcomeMessageView> welcomeMessageViews;

    @Persist
    private UserType selectedUserType;


    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SetupRender
    public void init() {
//        if (welcomeTexts == null) {
//            welcomeTexts = new HashSet<WelcomeText>();
//            WelcomeText welcomeText1 = new WelcomeText();
//
//            welcomeText1.setLanguage(Language.DEFAULT);
//            welcomeText1.setUserType(UserType.GUEST);
//            welcomeText1.setWelcomeText("test1");
//            welcomeTexts.add(welcomeText1);
//
//            WelcomeText welcomeText2 = new WelcomeText();
//            welcomeText2.setLanguage(Language.DEFAULT);
//            welcomeText2.setUserType(UserType.INTERNAL);
//            welcomeText2.setWelcomeText("test2");
//            welcomeTexts.add(welcomeText2);
//        }
        welcomeMessageViews = getWelcomeList(welcomeTexts);
    }

    public Zone onActionFromEditGuestMessage(Language language) {
        selectedLanguage = language;
        selectedUserType = UserType.GUEST;
        welcomeMessage = WelcomeMessageUtils.getWelcomeText(welcomeTexts, selectedLanguage, selectedUserType).getWelcomeText();
        return welcomeMessageBoxZone;
    }

    public Zone onActionFromEditInternalMessage(Language language) {
        selectedLanguage = language;
        selectedUserType = UserType.INTERNAL;
        welcomeMessage = WelcomeMessageUtils.getWelcomeText(welcomeTexts, selectedLanguage, selectedUserType).getWelcomeText();
        return welcomeMessageBoxZone;
    }

    public void onSuccessFromWelcomeMessageForm() {
        WelcomeText welcomeText = WelcomeMessageUtils.getWelcomeText(welcomeTexts, selectedLanguage, selectedUserType);
        welcomeText.setWelcomeText(welcomeMessage);
    }

//    private WelcomeText getWelcomeText(Set<WelcomeText> welcomeTexts, Language language, UserType userType) {
//        for (WelcomeText welcomeText_ : welcomeTexts) {
//            if (selectedLanguage.equals(welcomeText_.getLanguage()) && userType.equals(welcomeText_.getUserType())) {
//                return welcomeText_;
//            }
//        }
//        return null;
//    }

    private Set<WelcomeMessageView> getWelcomeList(Set<WelcomeText> welcomes) {
        Set<WelcomeMessageView> welcomeMessagesView = new HashSet<WelcomeMessageView>();
        for (Language lang : Language.values()) {
            WelcomeMessageView welcomeMessageView_ = new WelcomeMessageView(lang);
            if (welcomes != null) {
                for (WelcomeText welcome : welcomes) {
                    if (lang.equals(welcome.getLanguage())) {
                        String truncatedText = welcome.getWelcomeText();
                        if (truncatedText.length() > 20) {
                            truncatedText = truncatedText.substring(0, 20);
                            truncatedText = truncatedText + "...";
                        }
                        if (UserType.GUEST.equals(welcome.getUserType())) {
                            welcomeMessageView_.setGuestWelcomeMessage(truncatedText);
                        } else if (UserType.INTERNAL.equals(welcome.getUserType())) {
                            welcomeMessageView_.setInternalWelcomeMessage(truncatedText);
                        }
                    }
                }
            }
            welcomeMessagesView.add(welcomeMessageView_);
        }
        return welcomeMessagesView;
    }

}
