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
package org.linagora.linShare.view.tapestry.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.view.tapestry.components.QuickSharePopup;
import org.slf4j.LoggerFactory;

import common.Logger;

/**
 * Generic mail completion methods
 *
 */
public class MailCompletionService {	
	/**
	 * Regular expression to validate mails
	 */
	public static final Pattern MAILREGEXP = Pattern.compile("^[A-Z0-9'._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$");
	
	/**
	 * Returns the formated label for a user, i.e. "Michael georges" <michael@yoursociety.com>
	 * if the user name and firstname aren't null, michael@yoursociety.com otherwise
	 * 
	 * @param user
	 * @return
	 */
	public static String formatLabel(final UserVo user){
		StringBuffer buf = new StringBuffer();
		
		if(user.getLastName()!=null&&user.getFirstName()!=null){
			//uservo from USER table or ldap
			buf.append("\"").append(user.getLastName().trim()).append(" ").append(user.getFirstName().trim()).append("\"");
			buf.append(" <").append(user.getMail()).append(">,");
		} else {
			//uservo from favorite table
			buf.append(user.getMail()).append(",");
		}
		return buf.toString();
	}
	
	/**
	 * Returns the formated mails in one string for a list of UserVo
	 * 
	 * @param users
	 * @return
	 */
	public static String formatList(final List<UserVo> users) {
		String result = "";
		for (UserVo userVo : users) {
			result += formatLabel(userVo);
		}
		return result;
	}
	 
	/**
	 * Returns the list of mails giving the formated label
	 * 
	 * @param recipientsList
	 * @return
	 */
	public static List<String> parseEmails(final String recipientsList){
			 	
		
		String[] recipients = recipientsList.replaceAll(";", ",").split(",");
		ArrayList<String> emails = new ArrayList<String> ();
		
		for (String oneUser : recipients) {
			
			String email = contentInsideToken(oneUser, "<",">");
			if(email==null) email = oneUser.trim();
			
			if(!email.equals("")) {
				if (!emails.contains(email)) {
					emails.add(email); 
				}
			}
		}
		
		return emails;
	}
	
	/**
	 * Gives the content inside two tokens
	 * 
	 * @param str
	 * @param tokenright
	 * @param tokenleft
	 * @return
	 */
	public static String contentInsideToken(final String str, final String tokenright, final String tokenleft) {
		int deb = str.indexOf(tokenright,0);
		int end = str.indexOf(tokenleft,1);
		if(deb==-1||end==-1) return null;
		else return str.substring(deb+1, end).trim();
	}
}
