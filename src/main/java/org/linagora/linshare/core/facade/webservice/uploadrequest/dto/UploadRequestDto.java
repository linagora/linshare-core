/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

package org.linagora.linshare.core.facade.webservice.uploadrequest.dto;

import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;

import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "UploadRequest")
public class UploadRequestDto {

	private String uuid;

	@ApiModelProperty(value = "Owner")
	private ContactDto owner;

	@ApiModelProperty(value = "Recipient")
	private ContactDto recipient;

	// could be null
	private Integer maxFileCount;

	// could be null
	private Long maxDepositSize;

	// could be null
	private Long maxFileSize;

	private Date activationDate;

	// could be null
	private Date expiryDate;

	private Boolean canDeleteDocument;

	private Boolean canClose;

	private String subject;

	// could be null
	private String body;

	private boolean isClosed;

	private boolean protectedByPassword;

	private long usedSpace = 0;

	Set<String> extensions = Sets.newHashSet();

	private String locale;

	public UploadRequestDto() {
		super();
	}

	public UploadRequestDto(UploadRequestUrl requestUrl) {
		this.owner = new ContactDto(requestUrl.getUploadRequest().getUploadRequestGroup().getOwner());
		this.recipient = null;
		this.maxFileCount = requestUrl.getUploadRequest().getMaxFileCount();
		this.maxDepositSize = requestUrl.getUploadRequest().getMaxDepositSize();
		this.maxFileSize = requestUrl.getUploadRequest().getMaxFileSize();
		this.activationDate = requestUrl.getUploadRequest().getActivationDate();
		this.expiryDate = requestUrl.getUploadRequest().getExpiryDate();
		this.canDeleteDocument = requestUrl.getUploadRequest().isCanDelete();
		this.canClose = requestUrl.getUploadRequest().isCanClose();
		this.subject = requestUrl.getUploadRequest().getUploadRequestGroup().getSubject();
		this.body = requestUrl.getUploadRequest().getUploadRequestGroup().getBody();
		this.isClosed = false;
		if (requestUrl.getUploadRequest().getStatus().equals(UploadRequestStatus.CLOSED)) {
			this.isClosed = true;
			this.canDeleteDocument = false;
			this.canClose = false;
		}
		this.uuid = requestUrl.getUuid();
		this.recipient = new ContactDto(requestUrl.getContact());
		this.protectedByPassword = requestUrl.isProtectedByPassword();
		this.locale = requestUrl.getUploadRequest().getLocale();
	}

	public UploadRequest toObject() {
		UploadRequest e = new UploadRequest();
		e.setActivationDate(getActivationDate());
		e.setCanClose(isCanClose());
		e.setCanDelete(isCanDeleteDocument());
		e.setSecured(isProtectedByPassword());
		e.setMaxDepositSize(getMaxDepositSize());
		e.setMaxFileCount(getMaxFileCount());
		e.setLocale(getLocale());
		e.setExpiryDate(getExpiryDate());
		return e;
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

	public ContactDto getRecipient() {
		return recipient;
	}

	public void setRecipient(ContactDto recipient) {
		this.recipient = recipient;
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

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isCanClose() {
		return canClose;
	}

	public void setCanClose(boolean canClose) {
		this.canClose = canClose;
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

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public boolean isCanDeleteDocument() {
		return canDeleteDocument;
	}

	public void setCanDeleteDocument(boolean canDeleteDocument) {
		this.canDeleteDocument = canDeleteDocument;
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

	public Set<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(Set<String> extensions) {
		this.extensions = extensions;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * Helpers
	 */
	public void addExtensions(String mimeType) {
		this.extensions.add(mimeType);
	}
}
