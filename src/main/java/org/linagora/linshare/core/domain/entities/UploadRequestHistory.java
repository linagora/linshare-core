/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.UUID;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.UploadRequestHistoryEventType;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;

public class UploadRequestHistory implements Comparable<UploadRequestHistory> {

	private long id;

	private UploadRequestStatus status;

	private boolean statusUpdated;

	private UploadRequest uploadRequest;

	private UploadRequestHistoryEventType eventType;

	private String uuid;

	private Date activationDate;

	private Date expiryDate;

	private Date notificationDate;

	private Long maxDepositSize;

	private Integer maxFileCount;

	private Long maxFileSize;

	private String uploadPropositionRequestUuid;

	private Boolean canDelete;

	private Boolean canClose;

	private Boolean canEditExpiryDate;

	private Language locale;

	private boolean protectedByPassword;

	private Date creationDate;

	private Date modificationDate;

	private String mailMessageID;

	public UploadRequestHistory() {
		super();
	}

	public UploadRequestHistory(UploadRequest update,
			UploadRequestHistoryEventType eventType) {
		this(update, eventType, false);
	}

	public UploadRequestHistory(UploadRequest update,
			UploadRequestHistoryEventType eventType, boolean statusUpdated) {
		super();
		// on cascade
		this.setCreationDate(new Date());
		this.setModificationDate(new Date());
		this.setUuid(UUID.randomUUID().toString());

		this.statusUpdated = statusUpdated;
		this.eventType = eventType;
		this.status = update.getStatus();
		this.activationDate = update.getActivationDate();
		this.expiryDate = update.getExpiryDate();
		this.notificationDate = update.getNotificationDate();
		this.maxDepositSize = update.getMaxDepositSize();
		this.maxFileCount = update.getMaxFileCount();
		this.maxFileSize = update.getMaxFileSize();
		this.uploadPropositionRequestUuid = update
				.getUploadPropositionRequestUuid();
		this.canDelete = update.isCanDelete();
		this.canClose = update.isCanClose();
		this.canEditExpiryDate = update.isCanEditExpiryDate();
		this.locale = update.getLocale();
		this.protectedByPassword = update.isProtectedByPassword();
		this.mailMessageID = update.getMailMessageId();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UploadRequestStatus getStatus() {
		return status;
	}

	public void setStatus(UploadRequestStatus status) {
		this.status = status;
	}

	public boolean isStatusUpdated() {
		return statusUpdated;
	}

	public void setStatusUpdated(boolean statusUpdated) {
		this.statusUpdated = statusUpdated;
	}

	public UploadRequestHistoryEventType getEventType() {
		return eventType;
	}

	public void setEventType(UploadRequestHistoryEventType eventType) {
		this.eventType = eventType;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Long getMaxDepositSize() {
		return maxDepositSize;
	}

	public void setMaxDepositSize(Long maxDepositSize) {
		this.maxDepositSize = maxDepositSize;
	}

	public Integer getMaxFileCount() {
		return maxFileCount;
	}

	public void setMaxFileCount(Integer maxFileCount) {
		this.maxFileCount = maxFileCount;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public String getUploadPropositionRequestUuid() {
		return uploadPropositionRequestUuid;
	}

	public void setUploadPropositionRequestUuid(
			String uploadPropositionRequestUuid) {
		this.uploadPropositionRequestUuid = uploadPropositionRequestUuid;
	}

	public Boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(Boolean canDelete) {
		this.canDelete = canDelete;
	}

	public Boolean isCanClose() {
		return canClose;
	}

	public void setCanClose(Boolean canClose) {
		this.canClose = canClose;
	}

	public Boolean isCanEditExpiryDate() {
		return canEditExpiryDate;
	}

	public void setCanEditExpiryDate(Boolean canEditExpiryDate) {
		this.canEditExpiryDate = canEditExpiryDate;
	}

	public Language getLocale() {
		return locale;
	}

	public void setLocale(Language locale) {
		this.locale = locale;
	}

	public boolean isProtectedByPassword() {
		return protectedByPassword;
	}

	public void setProtectedByPassword(boolean protectedByPassword) {
		this.protectedByPassword = protectedByPassword;
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

	public String getMailMessageID() {
		return mailMessageID;
	}

	public void setMailMessageID(String mailMessageID) {
		this.mailMessageID = mailMessageID;
	}

	public UploadRequest getUploadRequest() {
		return uploadRequest;
	}

	public void setUploadRequest(UploadRequest uploadRequest) {
		this.uploadRequest = uploadRequest;
	}

	@Override
	public int compareTo(UploadRequestHistory o) {
		return this.modificationDate.compareTo(o.modificationDate);
	}
}
