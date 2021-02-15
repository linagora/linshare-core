/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
package org.linagora.linshare.mongo.entities.mto;

import org.linagora.linshare.core.domain.entities.UploadRequestEntry;

public class UploadRequestEntryMto {

	protected String name;

	protected Long size;

	protected String type;

	protected String sha256sum;

	protected Boolean copied;

	protected Boolean ciphered;

	protected String uploadRequestUuid;

	protected String uploadRequestGroupUuid;

	public UploadRequestEntryMto() {
		super();
	}

	public UploadRequestEntryMto(UploadRequestEntry reqEntry) {
		this.name = reqEntry.getName();
		this.type = reqEntry.getType();
		this.size = reqEntry.getSize();
		this.sha256sum = reqEntry.getSha256sum();
		this.ciphered = reqEntry.getCiphered();
		this.copied = reqEntry.getCopied();
		this.uploadRequestUuid = reqEntry.getUploadRequestUrl().getUploadRequest().getUuid();
		this.uploadRequestGroupUuid = reqEntry.getUploadRequestUrl().getUploadRequest().getUploadRequestGroup()
				.getUuid();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getSha256sum() {
		return sha256sum;
	}

	public void setSha256sum(String sha256sum) {
		this.sha256sum = sha256sum;
	}

	public Boolean getCopied() {
		return copied;
	}

	public void setCopied(Boolean copied) {
		this.copied = copied;
	}

	public Boolean getCiphered() {
		return ciphered;
	}

	public void setCiphered(Boolean ciphered) {
		this.ciphered = ciphered;
	}

	public String getUploadRequestUuid() {
		return uploadRequestUuid;
	}

	public void setUploadRequestUuid(String uploadRequestUuid) {
		this.uploadRequestUuid = uploadRequestUuid;
	}

	public String getUploadRequestGroupUuid() {
		return uploadRequestGroupUuid;
	}

	public void setUploadRequestGroupUuid(String uploadRequestGroupUuid) {
		this.uploadRequestGroupUuid = uploadRequestGroupUuid;
	}
}