/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.notifications.context;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;

public class FileWarnOwnerBeforeExpiryEmailContext extends EmailContext {

	protected DocumentEntry document;
	protected Integer day;

	public FileWarnOwnerBeforeExpiryEmailContext(DocumentEntry document, Integer day) {
		super(document.getEntryOwner().getDomain(), false);
		this.document = document;
		this.day = day;
		this.language = document.getEntryOwner().getMailLocale();
	}

	public DocumentEntry getDocument() {
		return document;
	}

	public User getOwner() {
		return (User) document.getEntryOwner();
	}

	public void setDocument(DocumentEntry document) {
		this.document = document;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.FILE_WARN_OWNER_BEFORE_FILE_EXPIRY;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.FILE_WARN_OWNER_BEFORE_FILE_EXPIRY;
	}

	@Override
	public String getMailRcpt() {
		return document.getEntryOwner().getMail();
	}

	@Override
	public String getMailReplyTo() {
		return null;
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(document, "Missing shareEntry");
	}

}
