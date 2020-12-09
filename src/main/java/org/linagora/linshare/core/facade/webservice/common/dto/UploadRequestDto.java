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
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.google.common.collect.Sets;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "UploadRequest")
public class UploadRequestDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Owner")
	private ContactDto owner;

	@Schema(description = "The list of recipients")
	private Set<ContactDto> recipients;

	@Schema(description = "Activation date")
	private Date activationDate;

	@Schema(description = "Modification date")
	private Date modificationDate;

	@Schema(description = "Creation date")
	private Date creationDate;

	// could be null
	@Schema(description = "Expiry date")
	private Date expiryDate;

	@Schema(description = "Notification date")
	private Date notificationDate;

	@Schema(description = "Label")
	private String label;

	@Schema(description = "Status")
	private UploadRequestStatus status;

	private Integer maxFileCount;

	// could be null
	private Long maxDepositSize;

	// could be null
	private Long maxFileSize;

	private Boolean canDeleteDocument;

	private Boolean canClose;

	// could be null
	private String body;

	private boolean isClosed;

	private boolean protectedByPassword;

	private long usedSpace = 0;

	Set<String> extensions = Sets.newHashSet();

	private String locale;

	private Boolean enableNotification;

	private Boolean canEditExpiryDate;

	private Boolean collective;

	public UploadRequestDto() {
		super();
	}

	public UploadRequestDto(UploadRequest entity, boolean full) {
		super();
		this.uuid = entity.getUuid();
		this.owner = new ContactDto(entity.getUploadRequestGroup().getOwner());
		this.creationDate = entity.getCreationDate();
		this.modificationDate = entity.getModificationDate();
		this.activationDate = entity.getActivationDate();
		this.expiryDate = entity.getExpiryDate();
		this.label = entity.getUploadRequestGroup().getSubject();
		this.status = entity.getStatus();
		this.notificationDate = entity.getNotificationDate();
		this.enableNotification = entity.getEnableNotification();
		if (full) {
			this.maxFileCount = entity.getMaxFileCount();
			this.maxDepositSize = entity.getMaxDepositSize();
			this.maxFileSize = entity.getMaxFileSize();
			this.canDeleteDocument = entity.isCanDelete();
			this.canClose = entity.isCanClose();
			this.body = entity.getUploadRequestGroup().getBody();
		}
		this.recipients = null;
		if (entity.getStatus().equals(UploadRequestStatus.CLOSED)) {
			this.isClosed = true;
			this.canDeleteDocument = false;
			this.canClose = false;
		}
		this.protectedByPassword = false;
		this.locale = entity.getLocale();
		this.collective = entity.getUploadRequestGroup().isCollective();
	}

	public UploadRequest toObject() {
		UploadRequest e = new UploadRequest();
		e.setActivationDate(getActivationDate());
		e.setCreationDate(getCreationDate());
		e.setModificationDate(getModificationDate());
		e.setCanClose(isCanClose());
		e.setCanDelete(isCanDeleteDocument());
		e.setSecured(isProtectedByPassword());
		e.setMaxDepositSize(getMaxDepositSize());
		e.setMaxFileCount(getMaxFileCount());
		e.setLocale(getLocale());
		e.setExpiryDate(getExpiryDate());
		e.setMaxFileSize(getMaxFileSize());
		e.setNotificationDate(getNotificationDate());
		e.setEnableNotification(getEnableNotification());
		e.setCanEditExpiryDate(getCanEditExpiryDate());
		return e;
	}

	public static UploadRequestDto toDto(UploadRequest uploadRequest, Boolean full) {
		UploadRequestDto requestDto = new UploadRequestDto(uploadRequest, full);
		Set<ContactDto> recipients = Sets.newHashSet();
		uploadRequest.getUploadRequestURLs()
				.forEach(requestUrl -> recipients.add(new ContactDto(requestUrl.getContact())));
		requestDto.setRecipients(recipients);
		return requestDto;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ContactDto getOwner() {
		return owner;
	}

	public void setOwner(ContactDto owner) {
		this.owner = owner;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public UploadRequestStatus getStatus() {
		return status;
	}

	public void setStatus(UploadRequestStatus status) {
		this.status = status;
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

	public Boolean isCanDeleteDocument() {
		return canDeleteDocument;
	}

	public void setCanDeleteDocument(Boolean canDeleteDocument) {
		this.canDeleteDocument = canDeleteDocument;
	}

	public Boolean isCanClose() {
		return canClose;
	}

	public void setCanClose(Boolean canClose) {
		this.canClose = canClose;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public boolean isProtectedByPassword() {
		return protectedByPassword;
	}

	public void setProtectedByPassword(boolean protectedByPassword) {
		this.protectedByPassword = protectedByPassword;
	}

	public long getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(long usedSpace) {
		this.usedSpace = usedSpace;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Set<ContactDto> getRecipients() {
		return recipients;
	}

	public void setRecipients(Set<ContactDto> recipient) {
		this.recipients = recipient;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Boolean getEnableNotification() {
		return enableNotification;
	}

	public void setEnableNotification(Boolean enableNotification) {
		this.enableNotification = enableNotification;
	}

	public Boolean getCanEditExpiryDate() {
		return canEditExpiryDate;
	}

	public void setCanEditExpiryDate(Boolean canEditExpiryDate) {
		this.canEditExpiryDate = canEditExpiryDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	/*
	 * Transformers
	 */
	public static Function<UploadRequest, UploadRequestDto> toDto(Boolean full) {
		return uploadRequest -> toDto(uploadRequest, full);
	}

	public Boolean getCollective() {
		return collective;
	}

	public void setCollective(Boolean collective) {
		this.collective = collective;
	}
}