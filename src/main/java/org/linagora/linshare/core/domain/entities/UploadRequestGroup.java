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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.Set;

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

	private String locale;

	private boolean secured;

	private String mailMessageId;
	
	private Boolean enableNotification;
	
	private Boolean restricted;

	private Account owner;

	private AbstractDomain abstractDomain;
	
	private UploadRequestStatus status;
	
	public UploadRequestGroup() {
		super();
	}

	public UploadRequestGroup(String subject, String body) {
		super();
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
			String locale,
			Boolean secured,
			Boolean enableNotification,
			Boolean restricted,
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
		this.secured = secured;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.enableNotification = enableNotification;
		this.restricted = restricted;
		this.status = status;
		this.expiryDate = expiryDate;
		this.notificationDate = notificationDate;
		this.maxFileCount = maxFileCount;
		this.maxDepositSize = maxDepositSize;
		this.maxFileSize = maxFileSize;
	}


	public UploadRequestGroup(UploadProposition proposition) {
		super();
		subject = proposition.getSubject();
		body = proposition.getBody();
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

	public Boolean getRestricted() {
		return restricted;
	}

	public void setRestricted(Boolean restricted) {
		this.restricted = restricted;
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

	public void setBusinessSecured(Boolean secured) {
		if (secured != null) {
			this.secured = secured;
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

	public void setBusinessLocale(String locale) {
		if (locale != null && !locale.isEmpty()) {
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
}
