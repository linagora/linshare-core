/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
import java.util.Set;
import java.util.UUID;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.exception.BusinessException;

import com.google.common.collect.Sets;

public class UploadRequestGroup {

	private long id;

	private String uuid;

	private String subject;

	private String body;

	private Set<UploadRequest> uploadRequests = Sets.newHashSet();

	private Date creationDate;

	private Date modificationDate;

	private Integer maxFileCount;

	private Long maxDepositSize;

	private Long maxFileSize;

	private Date activationDate;

	private Date notificationDate;

	private Date expiryDate;

	private Boolean canDelete;

	private Boolean canClose;

	private Boolean canEditExpiryDate;

	private Language locale;

	private boolean protectedByPassword;

	private String mailMessageId;

	private Boolean enableNotification;

	private Boolean collective;

	private Account owner;

	private AbstractDomain abstractDomain;

	private UploadRequestStatus status;

	public UploadRequestGroup() {
		super();
	}

	public UploadRequestGroup(String subject, String body) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.subject = subject;
		this.body = body;
	}

	public UploadRequestGroup(Account owner,
			AbstractDomain abstractDomain,
			String subject,
			String body,
			Date activationDate,
			Boolean canDelete,
			Boolean canClose,
			Boolean canEditExpiryDate,
			Language locale,
			Boolean protectedByPassword,
			Boolean enableNotification,
			Boolean collective,
			UploadRequestStatus status,
			Date expiryDate,
			Date notificationDate,
			Integer maxFileCount,
			Long maxDepositSize,
			Long maxFileSize) {
		super();
		this.owner = owner;
		this.abstractDomain = abstractDomain;
		this.subject = subject;
		this.body = body;
		this.activationDate = activationDate;
		this.canDelete = canDelete;
		this.canClose = canClose;
		this.canEditExpiryDate = canEditExpiryDate;
		this.locale = locale;
		this.protectedByPassword = protectedByPassword;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.enableNotification = enableNotification;
		this.collective = collective;
		this.status = status;
		this.expiryDate = expiryDate;
		this.notificationDate = notificationDate;
		this.maxFileCount = maxFileCount;
		this.maxDepositSize = maxDepositSize;
		this.maxFileSize = maxFileSize;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Set<UploadRequest> getUploadRequests() {
		return uploadRequests;
	}

	public void setUploadRequests(Set<UploadRequest> uploadRequests) {
		this.uploadRequests = uploadRequests;
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

	public Integer getMaxFileCount() {
		return maxFileCount;
	}

	public void setMaxFileCount(Integer maxFileCount) {
		this.maxFileCount = maxFileCount;
	}

	public Long getMaxDepositSize() {
		return maxDepositSize;
	}

	public void setMaxDepositSize(Long maxDepositSize) {
		this.maxDepositSize = maxDepositSize;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Boolean getCanDelete() {
		return canDelete;
	}

	public void setCanDelete(Boolean canDelete) {
		this.canDelete = canDelete;
	}

	public Boolean getCanClose() {
		return canClose;
	}

	public void setCanClose(Boolean canClose) {
		this.canClose = canClose;
	}

	public Boolean getCanEditExpiryDate() {
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

	public String getMailMessageId() {
		return mailMessageId;
	}

	public void setMailMessageId(String mailMessageId) {
		this.mailMessageId = mailMessageId;
	}

	public Boolean getEnableNotification() {
		return enableNotification;
	}

	public void setEnableNotification(Boolean enableNotification) {
		this.enableNotification = enableNotification;
	}

	public Boolean isCollective() {
		return collective;
	}

	public void setCollective(Boolean collective) {
		this.collective = collective;
	}

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	public AbstractDomain getAbstractDomain() {
		return abstractDomain;
	}

	public void setAbstractDomain(AbstractDomain abstractDomain) {
		this.abstractDomain = abstractDomain;
	}

	public UploadRequestStatus getStatus() {
		return status;
	}

	public void setStatus(UploadRequestStatus status) {
		this.status = status;
	}

	public void updateStatus(UploadRequestStatus to) throws BusinessException {
		status = status.transition(to);
	}

	/*
	 * Business setters
	 */
	
	public void setBusinessSubject(String subject) {
		if (subject != null) {
			this.subject = subject;
		}
	}

	public void setBusinessBody(String body) {
		if (body != null) {
			this.body = body;
		}
	}

	public void setBusinessNotificationDate(Date date) {
		if (date != null) {
			this.notificationDate = date;
		}
	}

	public void setBusinessActivationDate(Date date) {
		if (date != null && date.after(new Date())) {
			this.activationDate = date;
		}
	}

	public void setBusinessCanClose(Boolean canClose) {
		if (canClose != null) {
			this.canClose = canClose;
		}
	}

	public void setBusinessCanEditExpiryDate(Boolean canEditExpiryDate) {
		if (canEditExpiryDate != null) {
			this.canEditExpiryDate = canEditExpiryDate;
		}
	}

	public void setBusinessCanDelete(Boolean canDelete) {
		if (canDelete != null) {
			this.canDelete = canDelete;
		}
	}

	public void setBusinessSecured(Boolean protectedByPassword) {
		if (protectedByPassword != null) {
			this.protectedByPassword = protectedByPassword;
		}
	}

	public void setBusinessMaxDepositSize(Long size) {
		if (size != null) {
			this.maxDepositSize = size;
		}
	}

	public void setBusinessMaxFileSize(Long size) {
		if (size != null) {
			this.maxFileSize = size;
		}
	}

	public void setBusinessMaxFileCount(Integer size) {
		if (size != null) {
			this.maxFileCount = size;
		}
	}

	public void setBusinessLocale(Language locale) {
		if (locale != null) {
			this.locale = locale;
		}
	}

	public void setBusinessExpiryDate(Date date) {
		if (date != null) {
			this.expiryDate = date;
		}
	}

	public void setBusinessEnableNotification(Boolean enableNotification) {
		if (enableNotification != null) {
			this.enableNotification = enableNotification;
		}
	}

	public Boolean isCreated() {
		return this.getStatus().equals(UploadRequestStatus.CREATED);
	}

	public Boolean isEnabled() {
		return this.getStatus().equals(UploadRequestStatus.ENABLED);
	}

	public Boolean isCanceled() {
		return this.getStatus().equals(UploadRequestStatus.CANCELED);
	}

	public Boolean isClosed() {
		return this.getStatus().equals(UploadRequestStatus.CLOSED);
	}

	public Boolean isArchived() {
		return this.getStatus().equals(UploadRequestStatus.ARCHIVED);
	}

	public Boolean isPurged() {
		return this.getStatus().equals(UploadRequestStatus.PURGED);
	}

	public Boolean isDeleted() {
		return this.getStatus().equals(UploadRequestStatus.DELETED);
	}

	@Override
	public String toString() {
		return "UploadRequestGroup [uuid=" + uuid + ", subject=" + subject + ", body=" + body + ", creationDate="
				+ creationDate + ", modificationDate=" + modificationDate + ", maxFileCount=" + maxFileCount
				+ ", maxDepositSize=" + maxDepositSize + ", maxFileSize=" + maxFileSize + ", activationDate="
				+ activationDate + ", notificationDate=" + notificationDate + ", expiryDate=" + expiryDate
				+ ", canDelete=" + canDelete + ", canClose=" + canClose + ", canEditExpiryDate=" + canEditExpiryDate
				+ ", locale=" + locale + ", protectedByPassword=" + protectedByPassword + ", enableNotification=" + enableNotification
				+ ", collective=" + collective + ", status=" + status + "]";
	}
}
