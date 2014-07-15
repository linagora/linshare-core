/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.core.domain.vo;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.UploadRequestHistoryEventType;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequestHistory;

public class UploadRequestHistoryVo {

	private boolean statusUpdated;

	private UploadRequestStatus status;

	private UploadRequestHistoryEventType eventType;

	private String uuid;

	private Date activationDate;

	private Date expiryDate;

	private Date notificationDate;

	private long maxDepositSize;

	private int maxFileCount;

	private long maxFileSize;

	private String uploadPropositionUuid;

	private boolean canDelete;

	private boolean canClose;

	private boolean canEditExpiryDate;

	private Language locale;

	private boolean secured;

	private Date creationDate;

	private Date modificationDate;

	public UploadRequestHistoryVo() {
		super();
	}

	public UploadRequestHistoryVo(UploadRequestHistory hist) {
		uuid = hist.getUuid();
		maxFileCount = hist.getMaxFileCount();
		maxDepositSize = hist.getMaxDepositSize();
		maxFileSize = hist.getMaxFileSize();
		status = hist.getStatus();
		activationDate = hist.getActivationDate();
		creationDate = hist.getCreationDate();
		modificationDate = hist.getModificationDate();
		notificationDate = hist.getNotificationDate();
		expiryDate = hist.getExpiryDate();
		canDelete = hist.isCanDelete();
		canClose = hist.isCanClose();
		canEditExpiryDate = hist.isCanEditExpiryDate();
		locale = Language.fromTapestryLocale(hist.getLocale());
		secured = hist.isSecured();
	}

	public boolean isStatusUpdated() {
		return statusUpdated;
	}

	public void setStatusUpdated(boolean statusUpdated) {
		this.statusUpdated = statusUpdated;
	}

	public UploadRequestStatus getStatus() {
		return status;
	}

	public void setStatus(UploadRequestStatus status) {
		this.status = status;
	}

	public UploadRequestHistoryEventType getEventType() {
		return eventType;
	}

	public void setEventType(UploadRequestHistoryEventType eventType) {
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

	public long getMaxDepositSize() {
		return maxDepositSize;
	}

	public void setMaxDepositSize(long maxDepositSize) {
		this.maxDepositSize = maxDepositSize;
	}

	public int getMaxFileCount() {
		return maxFileCount;
	}

	public void setMaxFileCount(int maxFileCount) {
		this.maxFileCount = maxFileCount;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public String getUploadPropositionUuid() {
		return uploadPropositionUuid;
	}

	public void setUploadPropositionUuid(String uploadPropositionUuid) {
		this.uploadPropositionUuid = uploadPropositionUuid;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public boolean isCanClose() {
		return canClose;
	}

	public void setCanClose(boolean canClose) {
		this.canClose = canClose;
	}

	public boolean isCanEditExpiryDate() {
		return canEditExpiryDate;
	}

	public void setCanEditExpiryDate(boolean canEditExpiryDate) {
		this.canEditExpiryDate = canEditExpiryDate;
	}

	public Language getLocale() {
		return locale;
	}

	public void setLocale(Language locale) {
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
}
