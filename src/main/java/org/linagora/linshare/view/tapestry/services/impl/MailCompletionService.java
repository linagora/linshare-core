/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
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
package org.linagora.linshare.view.tapestry.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.linagora.linshare.core.domain.vo.UserVo;

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
	public static String formatLabel(final UserVo user) {
		return formatLabel(user, false);
	}
	
	public static String formatLabel(final UserVo user, boolean virgule) {
		return formatLabel(user.getMail(), user.getFirstName(), user.getLastName(), virgule);
	}
	
	public static String formatLabel(String mail, String firstName, String lastName, boolean virgule) {
		StringBuffer buf = new StringBuffer();
		
		if(lastName != null && firstName != null){
			//uservo from USER table or ldap
			buf.append('"').append(lastName.trim()).append(' ').append(firstName.trim()).append('"');
			buf.append(" <").append(mail).append(">");
			if(virgule)	buf.append(",");
		} else {
			//uservo from favorite table
			buf.append(mail).append(',');
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
	public static List<String> parseEmails(final String recipientsList) {
		String[] recipients = recipientsList.replaceAll(";", ",").split(",");
		ArrayList<String> emails = new ArrayList<String> ();
		
		for (String oneUser : recipients) {
			String email = contentInsideToken(oneUser, "<",">");
			if (email == null) {
				email = oneUser.trim();
			}
			if (!email.equals("")) {
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
		if (deb == -1 || end == -1) {
			return null;
		} else {
			return str.substring(deb+1, end).trim();
		}
	}
}
