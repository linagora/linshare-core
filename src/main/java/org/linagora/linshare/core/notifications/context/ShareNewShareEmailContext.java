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
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.notifications.dto.MailContact;

public class ShareNewShareEmailContext extends EmailContext {

	protected User shareOwner;

	protected User shareRecipient;

	protected Set<ShareEntry> shares;

	protected ShareContainer shareContainer;

	protected AnonymousUrl anonymousUrl;

	protected boolean isAnonymous;

	public ShareNewShareEmailContext(User shareOwner, User shareRecipient, Set<ShareEntry> shares,
			ShareContainer shareContainer) {
		super(shareRecipient.getDomain(), false);
		this.shareOwner = shareOwner;
		this.shareRecipient = shareRecipient;
		this.shares = shares;
		this.language = shareRecipient.getMailLocale();
		this.anonymousUrl = null;
		this.isAnonymous = false;
		this.shareContainer = shareContainer;
	}

	public ShareNewShareEmailContext(User shareOwner, AnonymousUrl anonymousUrl, ShareContainer shareContainer) {
		super(shareOwner.getDomain(), true);
		this.shareOwner = shareOwner;
		this.anonymousUrl = anonymousUrl;
		this.language = shareOwner.getMailLocale();
		this.shareRecipient = null;
		this.shares = null;
		this.isAnonymous = true;
		this.shareContainer = shareContainer;
	}

	public User getShareOwner() {
		return shareOwner;
	}

	public void setShareOwner(User shareOwner) {
		this.shareOwner = shareOwner;
	}

	public User getShareRecipient() {
		return shareRecipient;
	}

	public void setShareRecipient(User shareRecipient) {
		this.shareRecipient = shareRecipient;
	}

	public Set<ShareEntry> getShares() {
		return shares;
	}

	public void setShares(Set<ShareEntry> shares) {
		this.shares = shares;
	}

	public AnonymousUrl getAnonymousUrl() {
		return anonymousUrl;
	}

	public void setAnonymousUrl(AnonymousUrl anonymousUrl) {
		this.anonymousUrl = anonymousUrl;
	}

	public ShareContainer getShareContainer() {
		return shareContainer;
	}

	public void setShareContainer(ShareContainer shareContainer) {
		this.shareContainer = shareContainer;
	}

	public boolean isAnonymous() {
		return isAnonymous;
	}

	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.SHARE_NEW_SHARE_FOR_RECIPIENT;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.SHARE_NEW_SHARE_FOR_RECIPIENT;
	}

	@Override
	public String getMailRcpt() {
		if (isAnonymous) {
			return anonymousUrl.getContact().getMail();
		}
		return shareRecipient.getMail();
	}

	@Override
	public String getMailReplyTo() {
		return shareOwner.getMail();
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(shareOwner, "Missing shareOwner");
		Validate.notNull(shareContainer, "Missing shareContainer");
	}

	/**
	 * Helpers
	 */

	@Override
	public Locale getLocale() {
		if (language == null) {
			// Locale was not override by the current logged in user.
			if (isAnonymous) {
				return Language.toLocale(shareOwner.getMailLocale());
			} else {
				return Language.toLocale(shareRecipient.getMailLocale());
			}
		}
		return super.getLocale();
	}

	public MailContact getMailContactShareOwner() {
		return new MailContact(shareOwner);
	}

	public MailContact getMailContactShareRecipient() {
		if (isAnonymous) {
			return new MailContact(anonymousUrl.getContact());
		}
		return new MailContact(shareRecipient);
	}

}
