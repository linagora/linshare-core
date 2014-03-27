/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import java.util.HashSet;
import java.util.Set;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Language;
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
    private AccountType selectedUserType;


    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SetupRender
    public void init() {
        welcomeMessageViews = getWelcomeList(welcomeTexts);
    }

    public Zone onActionFromEditGuestMessage(Language language) {
        selectedLanguage = language;
        selectedUserType = AccountType.GUEST;
        welcomeMessage = WelcomeMessageUtils.getWelcomeText(welcomeTexts, selectedLanguage, selectedUserType).getWelcomeText();
        return welcomeMessageBoxZone;
    }

    public Zone onActionFromEditInternalMessage(Language language) {
        selectedLanguage = language;
        selectedUserType = AccountType.INTERNAL;
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
