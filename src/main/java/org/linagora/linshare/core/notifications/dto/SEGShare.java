/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.core.notifications.dto;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.User;

/**
 * @author FMartin
 *
 */
public class SEGShare {

	protected String mail;

	protected String firstName;

	protected String lastName;

	protected Boolean downloaded = false;

	public SEGShare(Contact c, Boolean downloaded) {
		this.mail = StringUtils.trimToNull(c.getMail());
		this.firstName = null;
		this.lastName = null;
		this.downloaded = downloaded;
	}

	public SEGShare(User user, Boolean downloaded) {
		this.mail = StringUtils.trimToNull(user.getMail());
		this.firstName = StringUtils.trimToNull(user.getFirstName());
		this.lastName = StringUtils.trimToNull(user.getLastName());
		this.downloaded = downloaded;
	}

	public SEGShare(MailContact mailContact, Boolean downloaded) {
		this.mail = mailContact.getMail();
		this.firstName = mailContact.getFirstName();
		this.lastName = mailContact.getLastName();
		this.downloaded = downloaded;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Boolean downloaded) {
		this.downloaded = downloaded;
	}

	@Override
	public String toString() {
		return "ShareForShareGroup [mail=" + mail + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", downloaded=" + downloaded + "]";
	}
}
