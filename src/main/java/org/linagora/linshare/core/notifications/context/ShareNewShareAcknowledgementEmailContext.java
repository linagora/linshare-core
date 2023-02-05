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

import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;

public class ShareNewShareAcknowledgementEmailContext extends EmailContext {

	protected User shareOwner;

	protected ShareContainer shareContainer;

	protected Set<Entry> shares;

	public ShareNewShareAcknowledgementEmailContext(User sender, ShareContainer shareContainer, Set<Entry> shares) {
		super(sender.getDomain(), false);
		this.shareOwner = sender;
		this.shares = shares;
		this.shareContainer = shareContainer;
		this.language = shareOwner.getMailLocale();
	}

	public User getShareOwner() {
		return shareOwner;
	}

	public void setShareOwner(User shareOwner) {
		this.shareOwner = shareOwner;
	}

	public Set<Entry> getShares() {
		return shares;
	}

	public void setShares(Set<Entry> shares) {
		this.shares = shares;
	}

	public ShareContainer getShareContainer() {
		return shareContainer;
	}

	public void setShareContainer(ShareContainer shareContainer) {
		this.shareContainer = shareContainer;
	}

	@Override
	public MailContentType getType() {
		return MailContentType.SHARE_NEW_SHARE_ACKNOWLEDGEMENT_FOR_SENDER;
	}

	@Override
	public MailActivationType getActivation() {
		return MailActivationType.SHARE_NEW_SHARE_ACKNOWLEDGEMENT_FOR_SENDER;
	}

	@Override
	public String getMailRcpt() {
		return shareOwner.getMail();
	}

	@Override
	public String getMailReplyTo() {
		return null;
	}

	@Override
	public void validateRequiredField() {
		Validate.notNull(shareOwner, "Missing shareEntry");
		Validate.notNull(shareContainer, "Missing shareEntry");
		Validate.notNull(shares, "Missing shareEntry");
	}

}
