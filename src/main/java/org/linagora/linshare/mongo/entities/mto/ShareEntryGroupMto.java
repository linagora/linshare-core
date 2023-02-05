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
package org.linagora.linshare.mongo.entities.mto;

import java.util.Date;

import org.linagora.linshare.core.domain.entities.ShareEntryGroup;

public class ShareEntryGroupMto {

	protected String uuid;

	protected String subject;

	protected Date notificationDate;

	protected Date creationDate;

	protected Date expirationDate;

	protected Boolean enabledUSDA;

	protected Boolean notified;

	protected Boolean processed;

	public ShareEntryGroupMto() {
	}

	public ShareEntryGroupMto(ShareEntryGroup seg) {
		super();
		this.uuid = seg.getUuid();
		this.subject = seg.getSubject();
		this.notificationDate = seg.getNotificationDate();
		this.creationDate = seg.getCreationDate();
		this.expirationDate = seg.getExpirationDate();
		this.enabledUSDA = (notificationDate == null ? false : true);
		this.notified = seg.getNotified();
		this.processed = seg.getProcessed();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Boolean getEnabledUSDA() {
		return enabledUSDA;
	}

	public void setEnabledUSDA(Boolean enabledUSDA) {
		this.enabledUSDA = enabledUSDA;
	}

	public Boolean getNotified() {
		return notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public Boolean getProcessed() {
		return processed;
	}

	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}
}
