/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ShareEntryGroup {

	protected long id;

	protected Account owner;

	protected String uuid;

	protected String subject;

	protected Date notificationDate;

	protected Date creationDate;

	protected Date modificationDate;

	protected Boolean notified = false;

	protected Boolean processed = false;

	protected Date expirationDate;

	protected Set<AnonymousShareEntry> anonymousShareEntries;

	protected Set<ShareEntry> shareEntries;

	/**
	 * Temporary members. Not persisted.
	 */
	protected Boolean tmpNeedNotification;

	protected Map<DocumentEntry, List<Entry>> tmpDocuments;

	protected Map<DocumentEntry, Boolean> tmpDocumentsWereDownloaded;
	protected Map<DocumentEntry, Boolean> tmpAllSharesWereNotDownloaded;

	public ShareEntryGroup() {
		super();
	}

	public ShareEntryGroup(Account owner, String subject) {
		super();
		this.owner = owner;
		this.subject = subject;
		this.notificationDate = null;
		this.notified = false;
		this.processed = false;
	}

	@Override
	public String toString() {
		return "ShareEntryGroup [uuid=" + uuid + ", subject=" + subject
				+ ", tmpNeedNotification=" + tmpNeedNotification + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
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

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Set<AnonymousShareEntry> getAnonymousShareEntries() {
		return anonymousShareEntries;
	}

	public void setAnonymousShareEntries(
			Set<AnonymousShareEntry> anonymousShareEntries) {
		this.anonymousShareEntries = anonymousShareEntries;
	}

	public Set<ShareEntry> getShareEntries() {
		return shareEntries;
	}

	public void setShareEntries(Set<ShareEntry> shareEntries) {
		this.shareEntries = shareEntries;
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

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public boolean needNotification() {
		if (tmpNeedNotification != null) {
			return tmpNeedNotification;
		}
		tmpDocuments = Maps.newHashMap();
		tmpDocumentsWereDownloaded = Maps.newHashMap();
		tmpAllSharesWereNotDownloaded = Maps.newHashMap();
		for (ShareEntry shareEntry : getShareEntries()) {
			DocumentEntry documentEntry = shareEntry.getDocumentEntry();
			List<Entry> list = tmpDocuments.get(documentEntry);
			if (list == null) {
				list = Lists.newArrayList();
			}
			list.add(shareEntry);
			tmpDocuments.put(documentEntry, list);
			if (shareEntry.getDownloaded() > 0) {
				tmpDocumentsWereDownloaded.put(documentEntry, true);
			} else {
				tmpAllSharesWereNotDownloaded.put(documentEntry, true);
			}
		}
		for (AnonymousShareEntry anonymousShareEntry : getAnonymousShareEntries()) {
			DocumentEntry documentEntry = anonymousShareEntry
					.getDocumentEntry();
			List<Entry> list = tmpDocuments.get(documentEntry);
			if (list == null) {
				list = Lists.newArrayList();
			}
			list.add(anonymousShareEntry);
			tmpDocuments.put(documentEntry, list);
			if (anonymousShareEntry.getDownloaded() > 0) {
				tmpDocumentsWereDownloaded.put(documentEntry, true);
			} else {
				tmpAllSharesWereNotDownloaded.put(documentEntry, true);
			}
		}
		if (tmpDocuments.size() == tmpDocumentsWereDownloaded.size()) {
			tmpNeedNotification = false;
		} else {
			tmpNeedNotification = true;
		}
		return tmpNeedNotification;
	}

	public Boolean getTmpNeedNotification() {
		return tmpNeedNotification;
	}

	public Map<DocumentEntry, List<Entry>> getTmpDocuments() {
		return tmpDocuments;
	}

	public Map<DocumentEntry, Boolean> getTmpDocumentsWereDownloaded() {
		return tmpDocumentsWereDownloaded;
	}

	public Map<DocumentEntry, Boolean> getTmpAllSharesWereNotDownloaded() {
		return tmpAllSharesWereNotDownloaded;
	}

	/*
	 * Set business
	 */

	public void setBusinessExpirationDate(Date expirationDate) {
		if (expirationDate != null) {
			this.expirationDate = expirationDate;
		}
	}

	public void setBusinessNotificationDate(Date notificationDate) {
		if (notificationDate != null) {
			this.notificationDate = notificationDate;
		}
	}

	public void setBusinessSubject(String subject) {
		if (subject != null && !subject.isEmpty()) {
			this.subject = subject;
		}
	}
}
