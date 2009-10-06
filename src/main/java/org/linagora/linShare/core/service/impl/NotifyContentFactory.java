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
package org.linagora.linShare.core.service.impl;

import org.apache.commons.lang.StringUtils;

/**
 *
 */
public class NotifyContentFactory {

    public static String makeGuestMailContent(String mailTemplate, String password) {
        String content = StringUtils.replace(mailTemplate, "${password}", password);
        return content;
    }
    public static String makeSharedMailContent(String mailTemplate, String firstName, String lastName, String number,String url) {
        String content = StringUtils.replace(mailTemplate, "${firstName}", firstName);
        content = StringUtils.replace(content, "${lastName}", lastName);
        content = StringUtils.replace(content, "${number}", number);
        content = StringUtils.replace(content, "${url}", url);
        return content;
    }
    
    
}
