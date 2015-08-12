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
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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

	protected Set<AnonymousShareEntry> anonymousShareEntries;

	protected Set<ShareEntry> shareEntries;

	/**
	 * Temporary members. Not persisted.
	 */
	protected Boolean tmpNeedNotification;

	protected Map<DocumentEntry, Set<Entry>> tmpDocuments;

	protected Map<DocumentEntry, Boolean> tmpDocumentsWasDownloaded;

	public ShareEntryGroup() {
		super();
	}

	public ShareEntryGroup(Account owner, String subject,
			Date notificationDate) {
		super();
		this.owner = owner;
		this.subject = subject;
		this.notificationDate = notificationDate;
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

	public boolean needNotification() {
		if (tmpNeedNotification != null) {
			return tmpNeedNotification;
		}
		tmpDocuments = Maps.newHashMap();
		tmpDocumentsWasDownloaded = Maps.newHashMap();
		for (ShareEntry shareEntry : getShareEntries()) {
			DocumentEntry documentEntry = shareEntry.getDocumentEntry();
			Set<Entry> set = tmpDocuments.get(documentEntry);
			if (set == null) {
				set = Sets.newHashSet();
			}
			set.add(shareEntry);
			tmpDocuments.put(documentEntry, set);
			if (shareEntry.getDownloaded() > 0) {
				tmpDocumentsWasDownloaded.put(documentEntry, true);
			}
		}
		for (AnonymousShareEntry anonymousShareEntry : getAnonymousShareEntries()) {
			DocumentEntry documentEntry = anonymousShareEntry
					.getDocumentEntry();
			Set<Entry> set = tmpDocuments.get(documentEntry);
			if (set == null) {
				set = Sets.newHashSet();
			}
			set.add(anonymousShareEntry);
			tmpDocuments.put(documentEntry, set);
			if (anonymousShareEntry.getDownloaded() > 0) {
				tmpDocumentsWasDownloaded.put(documentEntry, true);
			}
		}
		if (tmpDocuments.size() == tmpDocumentsWasDownloaded.size()) {
			tmpNeedNotification = false;
		} else {
			tmpNeedNotification = true;
		}
		return tmpNeedNotification;
	}

	public Boolean getTmpNeedNotification() {
		return tmpNeedNotification;
	}

	public Map<DocumentEntry, Set<Entry>> getTmpDocuments() {
		return tmpDocuments;
	}

	public Map<DocumentEntry, Boolean> getTmpDocumentsWasDownloaded() {
		return tmpDocumentsWasDownloaded;
	}
}
