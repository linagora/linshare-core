/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
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
package org.linagora.linshare.mongo.entities;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.webservice.utils.StatisticServiceUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement(name = "SharedSpaceDocument")
public class WorkGroupDocument extends WorkGroupNode {

	protected Long size;

	protected String mimeType;

	protected String humanMimeType;

	@JsonIgnore
	protected String documentUuid;

	protected Boolean hasRevision;

	@JsonIgnore
	protected Long lastRevision;

	protected String sha256sum;

	protected Date uploadDate;

	@JsonIgnore
	protected Boolean ciphered;

	protected Boolean hasThumbnail;

	public WorkGroupDocument() {
		super();
		this.nodeType = WorkGroupNodeType.DOCUMENT;
	}

	public WorkGroupDocument(Account author, String name, Document document, WorkGroup workGroup,
			WorkGroupNode nodeParent) {
		super(new AccountMto(author, true), name, nodeParent.getUuid(), workGroup.getLsUuid());
		this.size = document.getSize();
		this.mimeType = document.getType();
		this.humanMimeType = StatisticServiceUtils.getHumanMimeType(mimeType);
		this.hasRevision = false;
		this.lastRevision = 0L;
		this.uploadDate = new Date();
		this.sha256sum = document.getSha256sum();
		this.ciphered = false;
		this.hasThumbnail = document.getHasThumbnail();
		this.nodeType = WorkGroupNodeType.DOCUMENT;
	}

	public WorkGroupDocument(Account author, String name, Long size, String mimeType, WorkGroup workGroup,
			WorkGroupNode nodeParent) {
		super(new AccountMto(author, true), name, nodeParent.getUuid(), workGroup.getLsUuid());
		this.size = size;
		this.mimeType = mimeType;
		this.humanMimeType = StatisticServiceUtils.getHumanMimeType(mimeType);
		this.hasRevision = false;
		this.lastRevision = 0L;
		this.uploadDate = new Date();
		this.ciphered = false;
		this.nodeType = WorkGroupNodeType.DOCUMENT;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@XmlTransient
	public String getDocumentUuid() {
		return documentUuid;
	}

	public void setDocumentUuid(String documentUuid) {
		this.documentUuid = documentUuid;
	}

	public Boolean getHasRevision() {
		return hasRevision;
	}

	public void setHasRevision(Boolean hasRevision) {
		this.hasRevision = hasRevision;
	}

	@XmlTransient
	public Long getLastRevision() {
		return lastRevision;
	}

	public void setLastRevision(Long lastRevision) {
		this.lastRevision = lastRevision;
	}

	public String getSha256sum() {
		return sha256sum;
	}

	public void setSha256sum(String sha256sum) {
		this.sha256sum = sha256sum;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	@XmlTransient
	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}

	public Boolean getHasThumbnail() {
		return hasThumbnail;
	}

	public void setHasThumbnail(Boolean hasThumbnail) {
		this.hasThumbnail = hasThumbnail;
	}

	public String getHumanMimeType() {
		return humanMimeType;
	}

	public void setHumanMimeType(String humanMimeType) {
		this.humanMimeType = humanMimeType;
	}

	@Override
	public String toString() {
		return "WorkGroupDocument [size=" + size + ", mimeType=" + mimeType + ", documentUuid=" + documentUuid
				+ ", hasRevision=" + hasRevision + ", lastRevision=" + lastRevision + ", sha256sum=" + sha256sum
				+ ", uploadDate=" + uploadDate + ", ciphered=" + ciphered + ", hasThumbnail=" + hasThumbnail + "]";
	}

}
