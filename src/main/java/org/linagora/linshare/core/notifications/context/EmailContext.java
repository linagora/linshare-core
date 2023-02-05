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

import java.util.Locale;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;

public abstract class EmailContext {

	/**
	 * Domain of the mail recipient use to retrieve related configuration like
	 * LinShare URL, mail activation, ...
	 * 
	 */
	protected AbstractDomain fromDomain;

	protected boolean needToRetrieveGuestDomain;

	protected Language language;

	protected String inReplyTo = "";

	protected String references = "";

	public EmailContext(AbstractDomain domain, boolean needToRetrieveGuestDomain) {
		super();
		this.fromDomain = domain;
		this.needToRetrieveGuestDomain = needToRetrieveGuestDomain;
	}

	public abstract MailContentType getType();

	public abstract MailActivationType getActivation();

	public abstract String getMailRcpt();

	public abstract String getMailReplyTo();

	public String getBusinessMailReplyTo() {
		String mailReplyTo = getMailReplyTo();
		if (LinShareConstants.defaultRootMailAddress.equals(mailReplyTo)
				|| LinShareConstants.defaultSystemMailAddress.equals(mailReplyTo)) {
			// It is a technical email address that does not exist.
			return null;
		}
		return mailReplyTo;
	}

	public abstract void validateRequiredField();

	public AbstractDomain getFromDomain() {
		return fromDomain;
	}

	public boolean isNeedToRetrieveGuestDomain() {
		return needToRetrieveGuestDomain;
	}

	public Language getLanguage() {
		if (language == null) {
			return Language.ENGLISH;
		}
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Locale getLocale() {
		return Language.toLocale(language);
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public String getReferences() {
		return references;
	}

	public void setReferences(String references) {
		this.references = references;
	}

	/**
	 * Helpers
	 */

	public void updateFromDomain(AbstractDomain fromDomain) {
		this.fromDomain = fromDomain;
		this.needToRetrieveGuestDomain = false;
	}

}
