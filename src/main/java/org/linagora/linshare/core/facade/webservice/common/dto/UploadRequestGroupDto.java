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

package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Date;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "UploadRequestGroup")
public class UploadRequestGroupDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Label")
	private String label;

	@Schema(description = "Body")
	private String body;

	@Schema(description = "Creation date")
	private Date creationDate;

	@Schema(description = "Modification date")
	private Date modificationDate;

	@Schema(description = "Max file count")
	private Integer maxFileCount;

	@Schema(description = "Max deposit size")
	private Long maxDepositSize;

	@Schema(description = "Max file size")
	private Long maxFileSize;

	@Schema(description = "Activation date")
	private Date activationDate;

	@Schema(description = "Notification date")
	private Date notificationDate;

	@Schema(description = "Expiry date")
	private Date expiryDate;

	@Schema(description = "Can Delete")
	private Boolean canDelete;

	@Schema(description = "Can Close")
	private Boolean canClose;

	@Schema(description = "Can Edit Expiry Date")
	private Boolean canEditExpiryDate;

	@Schema(description = "Locale")
	private String locale;

	@Schema(description = "Define if the upload request is protected with a password")
	private boolean protectedByPassword;

	@Schema(description = "Mail message id")
	private String mailMessageId;

	@Schema(description = "Enable Notification")
	private Boolean enableNotification;

	@Schema(description = "Is collective upload request group")
	private Boolean collective;

	@Schema(description = "Owner")
	private GenericUserDto owner;

	@Schema(description = "Abstract Domain")
	private DomainDto domainDto;

	@Schema(description = "Status")
	private UploadRequestStatus status;

	public UploadRequestGroupDto() {
		super();
	}

	public UploadRequestGroupDto(UploadRequestGroup entity) {
		super();
		this.uuid = entity.getUuid();
		this.label = entity.getSubject();
		this.body = entity.getBody();
		this.creationDate = entity.getCreationDate();
		this.modificationDate = entity.getModificationDate();
		this.maxFileCount = entity.getMaxFileCount();
		this.maxDepositSize = entity.getMaxDepositSize();
		this.maxFileSize = entity.getMaxFileSize();
		this.activationDate = entity.getActivationDate();
		this.notificationDate = entity.getNotificationDate();
		this.expiryDate = entity.getExpiryDate();
		this.canDelete = entity.getCanDelete();
		this.canClose = entity.getCanClose();
		this.canEditExpiryDate = entity.getCanEditExpiryDate();
		this.locale = entity.getLocale();
		this.protectedByPassword = entity.isProtectedByPassword();
		this.mailMessageId = entity.getMailMessageId();
		this.enableNotification = entity.getEnableNotification();
		this.collective = entity.isCollective();
		this.owner = new GenericUserDto((User) entity.getOwner());
		this.domainDto = new DomainDto(entity.getAbstractDomain(), true);
		this.status = entity.getStatus();
	}

	public UploadRequestGroup toObject() {
		UploadRequestGroup uploadRequestGroup = new UploadRequestGroup();
		uploadRequestGroup.setUuid(getUuid());
		uploadRequestGroup.setSubject(getLabel());
		uploadRequestGroup.setBody(getBody());
		uploadRequestGroup.setCreationDate(getCreationDate());
		uploadRequestGroup.setModificationDate(getModificationDate());
		uploadRequestGroup.setMaxFileCount(getMaxFileCount());
		uploadRequestGroup.setMaxDepositSize(getMaxDepositSize());
		uploadRequestGroup.setMaxFileSize(getMaxFileSize());
		uploadRequestGroup.setActivationDate(getActivationDate());
		uploadRequestGroup.setNotificationDate(getNotificationDate());
		uploadRequestGroup.setExpiryDate(getExpiryDate());
		uploadRequestGroup.setCanDelete(isCanDelete());
		uploadRequestGroup.setCanClose(isCanClose());
		uploadRequestGroup.setCanEditExpiryDate(isCanEditExpiryDate());
		uploadRequestGroup.setLocale(getLocale());
		uploadRequestGroup.setProtectedByPassword(isProtectedByPassword());
		uploadRequestGroup.setMailMessageId(getMailMessageId());
		uploadRequestGroup.setEnableNotification(isEnableNotification());
		uploadRequestGroup.setCollective(isCollective());
		uploadRequestGroup.setStatus(getStatus());
		return uploadRequestGroup;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
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

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
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

	public Boolean isEnableNotification() {
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

	public GenericUserDto getOwner() {
		return owner;
	}

	public void setOwner(GenericUserDto owner) {
		this.owner = owner;
	}

	public DomainDto getDomainDto() {
		return domainDto;
	}

	public void setDomainDto(DomainDto domainDto) {
		this.domainDto = domainDto;
	}

	public UploadRequestStatus getStatus() {
		return status;
	}

	public void setStatus(UploadRequestStatus status) {
		this.status = status;
	}

	/*
	 * Transformers
	 */
	public static Function<UploadRequestGroup, UploadRequestGroupDto> toDto() {
		return urg -> new UploadRequestGroupDto(urg);
	}
}