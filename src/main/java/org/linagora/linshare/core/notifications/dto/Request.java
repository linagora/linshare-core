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
package org.linagora.linshare.core.notifications.dto;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;

public class Request {

	final protected String subject;

	final protected Date activationDate;

	final protected Date expirationDate;

	final protected Boolean wasPreviouslyCreated;

	final protected Integer authorizedFiles;

	final protected Integer uploadedFilesCount;

	public Request(UploadRequestUrl url) {
		super();
		UploadRequest request = url.getUploadRequest();
		this.subject = request.getUploadRequestGroup().getSubject();
		this.activationDate = request.getActivationDate();
		this.expirationDate = request.getExpiryDate();
		this.authorizedFiles = request.getMaxFileCount();
		this.uploadedFilesCount = url.getUploadRequestEntries().size();
		this.wasPreviouslyCreated = DateUtils.isSameDay(request.getActivationDate(), request.getCreationDate());
	}

	public Request(String subject, Date activationDate, Date expirationDate, Integer authorizedFiles,
			Integer documentsCount) {
		super();
		this.subject = subject;
		this.activationDate = activationDate;
		this.expirationDate = expirationDate;
		this.authorizedFiles = authorizedFiles;
		this.uploadedFilesCount = documentsCount;
		this.wasPreviouslyCreated = DateUtils.isSameDay(activationDate, expirationDate);
	}

	public Request(UploadRequest uploadRequest) {
		this.activationDate = uploadRequest.getActivationDate();
		this.expirationDate = uploadRequest.getExpiryDate();
		this.subject = uploadRequest.getUploadRequestGroup().getSubject();
		this.authorizedFiles = null;
		this.uploadedFilesCount = null;
		this.wasPreviouslyCreated = DateUtils.isSameDay(uploadRequest.getActivationDate(), uploadRequest.getCreationDate());
	}

	public String getSubject() {
		return subject;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public Integer getAuthorizedFiles() {
		return authorizedFiles;
	}

	public Integer getUploadedFilesCount() {
		return uploadedFilesCount;
	}

	public Boolean getWasPreviouslyCreated() {
		return wasPreviouslyCreated;
	}

	@Override
	public String toString() {
		return "Request [subject=" + subject + ", activationDate=" + activationDate + ", expirationDate="
				+ expirationDate + ", authorizedFiles=" + authorizedFiles + ", uploadedFilesCount=" + uploadedFilesCount
				+ "]";
	}

}
