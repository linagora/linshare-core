/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
