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
package org.linagora.linShare.view.tapestry.utils;

import java.util.Locale;
import java.util.Set;
import org.linagora.linShare.core.domain.constants.Language;
import org.linagora.linShare.core.domain.constants.UserType;
import org.linagora.linShare.core.domain.entities.WelcomeText;

/** Helpers for Welcome message processing.
 *
 */
public class WelcomeMessageUtils {

    public static WelcomeText getWelcomeText(Set<WelcomeText> welcomeTexts, Language selectedLanguage, UserType userType) {
        for (WelcomeText welcomeText_ : welcomeTexts) {
            if (selectedLanguage.equals(welcomeText_.getLanguage())) {
                return welcomeText_;
            }
        }
        return null;
    }

    public static Language getLanguageFromLocale(Locale locale) {
        if (Locale.FRENCH.equals(locale)) {
        	return Language.FRENCH;
        }
        /* java.util.Locale doesn't support dutch */
        if (locale.getLanguage() == "nl_NL" || locale.getLanguage() == "nl") {
        	return Language.DUTCH;
        }
        return Language.DEFAULT;
    }

    /**
     * Return the locale, with the following priority : persistentLocale (cookie), userLocale (user parameter),
     * browser language
     * @param persistentLocale
     * @param requestLocal
     * @param userLocale
     * @return
     */
    public static Locale getNormalisedLocale(Locale persistentLocale, Locale requestLocal, Locale userLocale) {
        if (persistentLocale == null) {
        	if (userLocale!=null) {        		
        		return normaliseLocale(userLocale);
        	} else {
        		return normaliseLocale(requestLocal);
        	}
        } else {
        	return normaliseLocale(persistentLocale);
        }
    }
    
    private static Locale normaliseLocale(Locale locale) {
    	if (Locale.FRENCH.equals(locale) || Locale.FRANCE.equals(locale)) {
            return Locale.FRENCH;
        } else if ("nl".equals(locale.getLanguage()) || "nl_NL".equals(locale.getLanguage())) {
            /* java.util.Locale doesn't support dutch */
        	return locale;
        } else {
            return Locale.ENGLISH;
        }
    }
}
