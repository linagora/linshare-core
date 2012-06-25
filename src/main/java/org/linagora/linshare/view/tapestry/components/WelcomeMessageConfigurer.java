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
package org.linagora.linshare.view.tapestry.components;

import java.util.HashSet;
import java.util.Set;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.UserType;
import org.linagora.linshare.core.domain.entities.WelcomeText;
import org.linagora.linshare.view.tapestry.beans.WelcomeMessageView;
import org.linagora.linshare.view.tapestry.utils.WelcomeMessageUtils;

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

    private Set<WelcomeMessageView> getWelcomeList(Set<WelcomeText> welcomes) {
        Set<WelcomeMessageView> welcomeMessagesView = new HashSet<WelcomeMessageView>();
        for (Language lang : Language.values()) {
            WelcomeMessageView welcomeMessageView_ = new WelcomeMessageView(lang);
            if (welcomes != null) {
                for (WelcomeText welcome : welcomes) {
                    if (lang.equals(welcome.getLanguage())) {
                        String truncatedText = welcome.getWelcomeText();
                        if (truncatedText.length() > 100) {
                            truncatedText = truncatedText.substring(0, 100);
                            truncatedText = truncatedText + "...";
                        }
                        welcomeMessageView_.setWelcomeMessage(truncatedText);
                    }
                }
            }
            welcomeMessagesView.add(welcomeMessageView_);
        }
        return welcomeMessagesView;
    }

}
