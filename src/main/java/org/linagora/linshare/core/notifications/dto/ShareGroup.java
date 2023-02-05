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
package org.linagora.linshare.core.notifications.dto;

import java.util.Date;
import java.util.UUID;

import org.linagora.linshare.core.domain.entities.ShareEntryGroup;

public class ShareGroup {

	protected String uuid;

	protected String subject;

	protected Date creationDate;

	protected Date notificationDate;

	protected Date expirationDate;

	public ShareGroup(String subject, Date creationDate, Date notificationDate, Date expirationDate) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.subject = subject;
		this.creationDate = creationDate;
		this.notificationDate = notificationDate;
		this.expirationDate = expirationDate;
	}

	public ShareGroup(ShareEntryGroup group) {
		super();
		this.uuid = group.getUuid();
		this.subject = group.getSubject();
		this.creationDate = group.getCreationDate();
		this.notificationDate = group.getNotificationDate();
		this.expirationDate = group.getExpirationDate();
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

}
