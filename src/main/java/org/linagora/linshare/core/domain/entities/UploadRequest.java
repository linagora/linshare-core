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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.exception.BusinessException;

import com.google.common.collect.Sets;

public class UploadRequest implements Cloneable {

	private long id;

	private UploadRequestGroup uploadRequestGroup;

	private String uuid;

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

	private String locale;

	private boolean secured;

	private String mailMessageId;

	private Set<UploadRequestUrl> uploadRequestURLs = Sets.newHashSet();

	private Set<UploadRequestHistory> uploadRequestHistory = Sets.newHashSet();

	private Account owner;

	private AbstractDomain abstractDomain;

	private Boolean notified = new Boolean(false);

	public UploadRequest() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UploadRequestGroup getUploadRequestGroup() {
		return uploadRequestGroup;
	}

	public void setUploadRequestGroup(UploadRequestGroup uploadRequestGroup) {
		this.uploadRequestGroup = uploadRequestGroup;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public void setUploadPropositionRequestUuid(
			String uploadPropositionRequestUuid) {
		this.uploadPropositionRequestUuid = uploadPropositionRequestUuid;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getMailMessageId() {
		return mailMessageId;
	}

	public void setMailMessageId(String mailMessageID) {
		this.mailMessageId = mailMessageID;
	}

	public Set<UploadRequestUrl> getUploadRequestURLs() {
		return uploadRequestURLs;
	}

	public void setUploadRequestURLs(Set<UploadRequestUrl> uploadRequestURLs) {
		this.uploadRequestURLs = uploadRequestURLs;
	}

	public Set<UploadRequestHistory> getUploadRequestHistory() {
		return uploadRequestHistory;
	}

	public void setUploadRequestHistory(
			Set<UploadRequestHistory> uploadRequestHistory) {
		this.uploadRequestHistory = uploadRequestHistory;
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

	public boolean isSecured() {
		return secured;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	public Boolean isNotified() {
		return notified;
	}
	
	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	@Override
	public UploadRequest clone() {
		UploadRequest req = null;
		try {
			req = (UploadRequest) super.clone();
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}
		req.id = 0;
		req.uploadRequestURLs = Sets.newHashSet();
		req.uploadRequestHistory = Sets.newHashSet();
		return req;
	}

	/*
	 * Business setters
	 */

	public void setBusinessActivationDate(Date date) {
		if (date != null) {
			this.activationDate = date;
		}
	}

	public void setBusinessCanClose(Boolean canClose) {
		if (canClose != null) {
			this.canClose = canClose;
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
}
