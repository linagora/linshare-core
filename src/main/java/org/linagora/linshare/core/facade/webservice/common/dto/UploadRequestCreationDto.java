/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.UploadRequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "UploadRequestCreation")
public class UploadRequestCreationDto {

	@Schema(description = "Activation date")
	private Date activationDate;

	// could be null
	@Schema(description = "Expiry date")
	private Date expiryDate;

	@Schema(description = "Notification date")
	private Date notificationDate;

	@Schema(description = "Label")
	private String label;

	private List<String> contactList = Lists.newArrayList();

	private Integer maxFileCount;

	// could be null
	private Long maxDepositSize;

	// could be null
	private Long maxFileSize;

	private Boolean canDelete;

	private Boolean canClose;

	// could be null
	private String body;

	private boolean protectedByPassword;

	private Language locale;

	private Boolean enableNotification;

	private Boolean canEditExpiryDate;

	public UploadRequestCreationDto() {
		super();
	}

	public UploadRequest toObject() {
		UploadRequest e = new UploadRequest();
		e.setActivationDate(getActivationDate());
		e.setCanClose(checkCanCloseCanDelete(canClose));
		e.setCanDelete(checkCanCloseCanDelete(canDelete));
		e.setProtectedByPassword(isProtectedByPassword());
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

	private Boolean checkCanCloseCanDelete(Boolean input) {
		return Objects.isNull(input) ? false : input;
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

	public boolean isCanClose() {
		return canClose;
	}

	public void setCanClose(boolean canClose) {
		this.canClose = canClose;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Language getLocale() {
		return locale;
	}

	public void setLocale(Language locale) {
		this.locale = locale;
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

	public List<String> getContactList() {
		return contactList;
	}

	public void setContactList(List<String> contactList) {
		this.contactList = contactList;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public boolean isProtectedByPassword() {
		return protectedByPassword;
	}

	public void setProtectedByPassword(boolean protectedByPassword) {
		this.protectedByPassword = protectedByPassword;
	}
}