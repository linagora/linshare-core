/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.core.notifications.dto;

import java.util.Date;

import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;

public class Request {

	final protected String subject;

	final protected Date activationDate;

	final protected Date expirationDate;

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
	}

	public Request(String subject, Date activationDate, Date expirationDate, Integer authorizedFiles,
			Integer documentsCount) {
		super();
		this.subject = subject;
		this.activationDate = activationDate;
		this.expirationDate = expirationDate;
		this.authorizedFiles = authorizedFiles;
		this.uploadedFilesCount = documentsCount;
	}

	public Request(UploadRequest uploadRequest) {
		this.activationDate = uploadRequest.getActivationDate();
		this.expirationDate = uploadRequest.getExpiryDate();
		this.subject = null;
		this.authorizedFiles = null;
		this.uploadedFilesCount = null;
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

	@Override
	public String toString() {
		return "Request [subject=" + subject + ", activationDate=" + activationDate + ", expirationDate="
				+ expirationDate + ", authorizedFiles=" + authorizedFiles + ", uploadedFilesCount=" + uploadedFilesCount
				+ "]";
	}

}
