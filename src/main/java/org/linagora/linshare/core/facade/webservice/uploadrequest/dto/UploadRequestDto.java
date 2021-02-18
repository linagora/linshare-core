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

package org.linagora.linshare.core.facade.webservice.uploadrequest.dto;

import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;

import com.google.common.collect.Sets;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "UploadRequest")
public class UploadRequestDto {

	private String uuid;

	@Schema(description = "Owner")
	private ContactDto owner;

	@Schema(description = "Recipient")
	private ContactDto recipient;

	@Schema(description = "The list of recipients")
	private Set<ContactDto> recipients;

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

	private Language locale;

	private Boolean collective;

	@Schema(description = "Number of uploaded files")
	private Integer nbrUploadedFiles;

	public UploadRequestDto() {
		super();
	}

	public UploadRequestDto(UploadRequestUrl requestUrl) {
		this.owner = new ContactDto(requestUrl.getUploadRequest().getUploadRequestGroup().getOwner());
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
		this.collective = requestUrl.getUploadRequest().getUploadRequestGroup().isCollective();
	}

	public UploadRequest toObject() {
		UploadRequest e = new UploadRequest();
		e.setActivationDate(getActivationDate());
		e.setCanClose(isCanClose());
		e.setCanDelete(isCanDeleteDocument());
		e.setProtectedByPassword(isProtectedByPassword());
		e.setMaxDepositSize(getMaxDepositSize());
		e.setMaxFileCount(getMaxFileCount());
		e.setLocale(getLocale());
		e.setExpiryDate(getExpiryDate());
		return e;
	}

	public static UploadRequestDto toDto(UploadRequestUrl uploadRequestUrl) {
		UploadRequestDto requestDto = new UploadRequestDto(uploadRequestUrl);
		Set<ContactDto> recipients = Sets.newHashSet();
		if (requestDto.getCollective()) {
			uploadRequestUrl.getUploadRequest().getUploadRequestURLs()
			.forEach(requestUrl -> recipients.add(new ContactDto(requestUrl.getContact())));
		} else {
			recipients.add(new ContactDto(uploadRequestUrl.getContact()));
		}
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

	public Language getLocale() {
		return locale;
	}

	public void setLocale(Language locale) {
		this.locale = locale;
	}

	public Set<ContactDto> getRecipients() {
		return recipients;
	}

	public void setRecipients(Set<ContactDto> recipients) {
		this.recipients = recipients;
	}

	/**
	 * Helpers
	 */
	public void addExtensions(String mimeType) {
		this.extensions.add(mimeType);
	}

	public Boolean getCollective() {
		return collective;
	}

	public void setCollective(Boolean collective) {
		this.collective = collective;
	}

	public Integer getNbrUploadedFiles() {
		return nbrUploadedFiles;
	}

	public void setNbrUploadedFiles(Integer nbrUploadedFiles) {
		this.nbrUploadedFiles = nbrUploadedFiles;
	}
}
