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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.UploadRequestHistory;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "UploadRequestHistory")
@ApiModel(value = "UploadRequestHistory", description = "History of an upload request")
public class UploadRequestHistoryDto {

	@ApiModelProperty(value = "Status")
	private String status;

	@ApiModelProperty(value = "Status updated")
	private boolean statusUpdated;

	@ApiModelProperty(value = "Even type")
	private String eventType;

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Activation date")
	private Date activationDate;

	@ApiModelProperty(value = "Expiry date")
	private Date expiryDate;

	@ApiModelProperty(value = "Notification date")
	private Date notificationDate;

	@ApiModelProperty(value = "Max deposit size")
	private Long maxDepositSize;

	@ApiModelProperty(value = "Max file count")
	private Integer maxFileCount;

	@ApiModelProperty(value = "Max file size")
	private Long maxFileSize;

	@ApiModelProperty(value = "Upload proposition request uuid")
	private String uploadPropositionRequestUuid;

	@ApiModelProperty(value = "Can delete")
	private Boolean canDelete;

	@ApiModelProperty(value = "Can close")
	private Boolean canClose;

	@ApiModelProperty(value = "Can expiry date")
	private Boolean canEditExpiryDate;

	@ApiModelProperty(value = "Locale")
	private String locale;

	@ApiModelProperty(value = "Secured")
	private boolean secured;

	@ApiModelProperty(value = "Creation date")
	private Date creationDate;

	@ApiModelProperty(value = "Modification date")
	private Date modificationDate;

	@ApiModelProperty(value = "Date of the action")
	private String mailMessageID;

	public UploadRequestHistoryDto() {
	}

	public UploadRequestHistoryDto(UploadRequestHistory u) {
		this.status = u.getStatus().toString();
		this.statusUpdated = u.isStatusUpdated();
		this.eventType = u.getEventType().toString();
		this.uuid = u.getUuid();
		this.activationDate = u.getActivationDate();
		this.expiryDate = u.getExpiryDate();
		this.notificationDate = u.getNotificationDate();
		this.maxDepositSize = u.getMaxDepositSize();
		this.maxFileCount = u.getMaxFileCount();
		this.maxFileSize = u.getMaxFileSize();
		this.uploadPropositionRequestUuid = u.getUploadPropositionRequestUuid();
		this.canDelete = u.isCanDelete();
		this.canClose = u.isCanClose();
		this.canEditExpiryDate = u.isCanEditExpiryDate();
		this.locale = u.getLocale();
		this.secured = u.isSecured();
		this.creationDate = u.getCreationDate();
		this.modificationDate = u.getModificationDate();
		this.mailMessageID = u.getMailMessageID();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isStatusUpdated() {
		return statusUpdated;
	}

	public void setStatusUpdated(boolean statusUpdated) {
		this.statusUpdated = statusUpdated;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
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

	public void setUploadPropositionRequestUuid(String uploadPropositionRequestUuid) {
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

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public boolean isSecured() {
		return secured;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
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
}
