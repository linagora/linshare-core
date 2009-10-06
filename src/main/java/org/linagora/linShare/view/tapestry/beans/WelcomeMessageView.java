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
package org.linagora.linShare.view.tapestry.beans;

import java.io.Serializable;
import org.linagora.linShare.core.domain.constants.Language;

/** This class will be used for welcome message management.
 */
public class WelcomeMessageView implements Serializable {

    private Language language;

    private String guestWelcomeMessage;

    private String internalWelcomeMessage;

    public WelcomeMessageView(Language language) {
        this.language = language;
    }

    public String getGuestWelcomeMessage() {
        return guestWelcomeMessage;
    }

    public String getInternalWelcomeMessage() {
        return internalWelcomeMessage;
    }

    public Language getLanguage() {
        return language;
    }

    public void setGuestWelcomeMessage(String guestWelcomeMessage) {
        this.guestWelcomeMessage = guestWelcomeMessage;
    }

    public void setInternalWelcomeMessage(String internalWelcomeMessage) {
        this.internalWelcomeMessage = internalWelcomeMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WelcomeMessageView other = (WelcomeMessageView) obj;
        if (this.language != other.language) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.language != null ? this.language.hashCode() : 0);
        return hash;
    }

}
