/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.exception.BusinessException;

import com.google.common.collect.Sets;

/**
 * This class represents the space where one user or more can upload files in response to an upload request.
 * If the linked {@link UploadRequestGroup} is collective, only one user can upload files.
 * Otherwise, one or more users can upload files, depending on the {@link UploadRequestGroup}.
 * For each user accessing the upload request, a unique {@link UploadRequestUrl} is generated.
 */
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

	private Language locale;

	private boolean protectedByPassword;

	private String mailMessageId;

	private Set<UploadRequestUrl> uploadRequestURLs = Sets.newHashSet();

	private Set<UploadRequestHistory> uploadRequestHistory = Sets.newHashSet();

	private Boolean notified = Boolean.valueOf(false);

	private Boolean pristine = Boolean.valueOf(true);

	private Boolean enableNotification = Boolean.valueOf(true);

	public UploadRequest() {
		super();
	}

	public UploadRequest(UploadRequestGroup urg) {
		this.setActivationDate(urg.getActivationDate());
		this.setCanDelete(urg.getCanDelete());
		this.setCanClose(urg.getCanClose());
		this.setCanEditExpiryDate(urg.getCanEditExpiryDate());
		this.setLocale(urg.getLocale());
		this.setProtectedByPassword(urg.isProtectedByPassword());
		this.setEnableNotification(urg.getEnableNotification());
		this.setExpiryDate(urg.getExpiryDate());
		this.setNotificationDate(urg.getNotificationDate());
		this.setMaxFileCount(urg.getMaxFileCount());
		this.setMaxDepositSize(urg.getMaxDepositSize());
		this.setMaxFileSize(urg.getMaxFileSize());
		this.setUploadRequestGroup(urg);
		this.setStatus(urg.getStatus());
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

	public Language getLocale() {
		return locale;
	}

	public void setLocale(Language locale) {
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

	public boolean isProtectedByPassword() {
		return protectedByPassword;
	}

	public void setProtectedByPassword(boolean protectedByPassword) {
		this.protectedByPassword = protectedByPassword;
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

	public Boolean isPristine() {
		return pristine;
	}

	public void setPristine(Boolean pristine) {
		this.pristine = pristine;
	}

	public Boolean getEnableNotification() {
		return enableNotification;
	}

	public void setEnableNotification(Boolean enableNotification) {
		this.enableNotification = enableNotification;
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
	
	public void setBusinessCanEditExpiryDate(Boolean canEditExpiryDate) {
		if (canEditExpiryDate != null) {
			this.canEditExpiryDate = canEditExpiryDate;
		}
	}

	public void setBusinessSecured(Boolean protectedByPassword) {
		if (protectedByPassword != null) {
			this.protectedByPassword = protectedByPassword;
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

	public void setBusinessLocale(Language locale) {
		if (locale != null) {
			this.locale = locale;
		}
	}

	public void setBusinessExpiryDate(Date date) {
		if (date != null) {
			this.expiryDate = date;
		}
	}

	public void setBusinessNotificationDate(Date notificationDate) {
		if (notificationDate != null) {
			this.notificationDate = notificationDate;
		}
	}
	
	public void setBusinessEnableNotification(Boolean enableNotification) {
		if (enableNotification != null) {
			this.enableNotification = enableNotification;
		}
	}

	public Boolean isCreated() {
		return this.getStatus().equals(UploadRequestStatus.CREATED);
	}

	public Boolean isEnabled() {
		return this.getStatus().equals(UploadRequestStatus.ENABLED);
	}

	public Boolean isCanceled() {
		return this.getStatus().equals(UploadRequestStatus.CANCELED);
	}

	public Boolean isClosed() {
		return this.getStatus().equals(UploadRequestStatus.CLOSED);
	}

	public Boolean isArchived() {
		return this.getStatus().equals(UploadRequestStatus.ARCHIVED);
	}

	public Boolean isPurged() {
		return this.getStatus().equals(UploadRequestStatus.PURGED);
	}

	public Boolean isDeleted() {
		return this.getStatus().equals(UploadRequestStatus.DELETED);
	}

	/**
	 * used to check if there is changes in Upload request parameters in order to
	 * sent an email notification
	 * 
	 * @param obj
	 * @return boolean
	 */
	public boolean businessEquals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UploadRequest other = (UploadRequest) obj;
		if (activationDate == null) {
			if (other.activationDate != null)
				return false;
		} else if (!activationDate.equals(other.activationDate))
			return false;
		if (canClose == null) {
			if (other.canClose != null)
				return false;
		} else if (!canClose.equals(other.canClose))
			return false;
		if (canDelete == null) {
			if (other.canDelete != null)
				return false;
		} else if (!canDelete.equals(other.canDelete))
			return false;
		if (canEditExpiryDate == null) {
			if (other.canEditExpiryDate != null)
				return false;
		} else if (!canEditExpiryDate.equals(other.canEditExpiryDate))
			return false;
		if (enableNotification == null) {
			if (other.enableNotification != null)
				return false;
		} else if (!enableNotification.equals(other.enableNotification))
			return false;
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		if (maxDepositSize == null) {
			if (other.maxDepositSize != null)
				return false;
		} else if (!maxDepositSize.equals(other.maxDepositSize))
			return false;
		if (maxFileCount == null) {
			if (other.maxFileCount != null)
				return false;
		} else if (!maxFileCount.equals(other.maxFileCount))
			return false;
		if (maxFileSize == null) {
			if (other.maxFileSize != null)
				return false;
		} else if (!maxFileSize.equals(other.maxFileSize))
			return false;
		if (notificationDate == null) {
			if (other.notificationDate != null)
				return false;
		} else if (!notificationDate.equals(other.notificationDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UploadRequest [id=" + id + ", uploadRequestGroup=" + uploadRequestGroup + ", uuid=" + uuid
				+ ", maxFileCount=" + maxFileCount + ", maxDepositSize=" + maxDepositSize + ", maxFileSize="
				+ maxFileSize + ", status=" + status + ", activationDate=" + activationDate + ", creationDate="
				+ creationDate + ", modificationDate=" + modificationDate + ", notificationDate=" + notificationDate
				+ ", expiryDate=" + expiryDate + ", canDelete=" + canDelete + ", canClose=" + canClose
				+ ", canEditExpiryDate=" + canEditExpiryDate + ", locale=" + locale + ", protectedByPassword=" + protectedByPassword
				+ ", mailMessageId=" + mailMessageId + ", uploadRequestURLs=" + uploadRequestURLs
				+ ", uploadRequestHistory=" + uploadRequestHistory + ", notified=" + notified + ", pristine=" + pristine
				+ ", enableNotification=" + enableNotification + "]";
	}

}
