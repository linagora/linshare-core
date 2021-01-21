/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2020 LINAGORA
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
package org.linagora.linshare.mongo.entities.mto;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;

public class UploadRequestMto {

	private String uuid;

	private String subject;

	private String body;

	private String uploadRequestGroupUuid;

	private Integer maxFileCount;

	private Long maxDepositSize;

	private Long maxFileSize;

	private UploadRequestStatus status;

	private Date activationDate;

	private Date creationDate;

	private Date modificationDate;

	private Date notificationDate;

	private Date expiryDate;

	private String uploadPropositionRequestUuid;

	private Boolean canDelete;

	private Boolean canClose;

	private Boolean canEditExpiryDate;

	private Language locale;

	private Boolean protectedByPassword;

	private String mailMessageId;

	private AccountMto owner;

	private DomainMto domain;

	private Boolean notified;

	private Boolean dirtyLocalConf;

	private Boolean enableNotification;

	private Boolean statusUpdated;

	public UploadRequestMto() {
		super();
	}

	public UploadRequestMto(UploadRequest request) {
		this.uuid = request.getUuid();
		this.body = request.getUploadRequestGroup().getBody();
		this.subject = request.getUploadRequestGroup().getSubject();
		this.uploadRequestGroupUuid = request.getUploadRequestGroup().getUuid();
		this.domain = new DomainMto(request.getUploadRequestGroup().getAbstractDomain());
		this.maxFileCount = request.getMaxFileCount();
		this.protectedByPassword = request.isProtectedByPassword();
		this.owner = new AccountMto(request.getUploadRequestGroup().getOwner());
		this.notified = request.isNotified();
		this.locale = request.getLocale();
		this.maxDepositSize = request.getMaxDepositSize();
		this.maxFileSize = request.getMaxFileSize();
		this.status = request.getStatus();
		this.expiryDate = request.getExpiryDate();
		this.canClose = request.isCanClose();
		this.canDelete = request.isCanDelete();
		this.canEditExpiryDate = request.isCanEditExpiryDate();
		this.dirtyLocalConf = !request.isPristine();
		this.enableNotification = request.getEnableNotification();
		this.statusUpdated = false;
	}

	public UploadRequestMto(UploadRequest request, Boolean statusUpdated) {
		this.uuid = request.getUuid();
		this.body = request.getUploadRequestGroup().getBody();
		this.subject = request.getUploadRequestGroup().getSubject();
		this.uploadRequestGroupUuid = request.getUploadRequestGroup().getUuid();
		this.domain = new DomainMto(request.getUploadRequestGroup().getAbstractDomain());
		this.maxFileCount = request.getMaxFileCount();
		this.protectedByPassword = request.isProtectedByPassword();
		this.owner = new AccountMto(request.getUploadRequestGroup().getOwner());
		this.notified = request.isNotified();
		this.locale = request.getLocale();
		this.maxDepositSize = request.getMaxDepositSize();
		this.maxFileSize = request.getMaxFileSize();
		this.status = request.getStatus();
		this.expiryDate = request.getExpiryDate();
		this.canClose = request.isCanClose();
		this.canDelete = request.isCanDelete();
		this.canEditExpiryDate = request.isCanEditExpiryDate();
		this.dirtyLocalConf = !request.isPristine();
		this.enableNotification = request.getEnableNotification();
		this.statusUpdated = statusUpdated;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public UploadRequestStatus getStatus() {
		return status;
	}

	public void setStatus(UploadRequestStatus status) {
		this.status = status;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
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

	public String getUploadPropositionRequestUuid() {
		return uploadPropositionRequestUuid;
	}

	public void setUploadPropositionRequestUuid(String uploadPropositionRequestUuid) {
		this.uploadPropositionRequestUuid = uploadPropositionRequestUuid;
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

	public Boolean isProtectedByPassword() {
		return protectedByPassword;
	}

	public void setProtectedByPassword(Boolean protectedByPassword) {
		this.protectedByPassword = protectedByPassword;
	}

	public String getMailMessageId() {
		return mailMessageId;
	}

	public void setMailMessageId(String mailMessageId) {
		this.mailMessageId = mailMessageId;
	}

	public AccountMto getOwner() {
		return owner;
	}

	public void setOwner(AccountMto owner) {
		this.owner = owner;
	}

	public Boolean getNotified() {
		return notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public DomainMto getDomain() {
		return domain;
	}

	public void setDomain(DomainMto domain) {
		this.domain = domain;
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

	public String getUploadRequestGroupUuid() {
		return uploadRequestGroupUuid;
	}

	public void setUploadRequestGroupUuid(String groupUuid) {
		this.uploadRequestGroupUuid = groupUuid;
	}

	public Boolean getDirtyLocalConf() {
		return dirtyLocalConf;
	}

	public void setDirtyLocalConf(Boolean dirtyLocalConf) {
		this.dirtyLocalConf = dirtyLocalConf;
	}

	public Boolean getEnableNotification() {
		return enableNotification;
	}

	public void setEnableNotification(Boolean enableNotification) {
		this.enableNotification = enableNotification;
	}

	public Boolean getStatusUpdated() {
		return statusUpdated;
	}

	public void setStatusUpdated(Boolean statusUpdated) {
		this.statusUpdated = statusUpdated;
	}
}
