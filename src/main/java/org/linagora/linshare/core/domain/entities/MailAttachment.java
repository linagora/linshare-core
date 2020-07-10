/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2019-2020 LINAGORA
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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.UUID;

import org.linagora.linshare.core.domain.constants.Language;

public class MailAttachment {

	private long id;

	private String uuid;

	private String ressourceUuid;

	private Boolean enable;

	private Boolean enableForAll;

	private Language language;

	private String description;

	private String name;

	private Long size;

	private Date creationDate;

	private Date modificationDate;

	private String mimeType;

	private String sha256sum;

	private MailConfig mailConfig;

	private String cid;

	private String bucketUuid;

	public MailAttachment() {
		super();
	}

	public MailAttachment(Boolean enable, Boolean enableForAll, Language language, String description, String name,
			MailConfig mailConfig, String cid) {
		super();
		this.enable = enable;
		this.enableForAll = enableForAll;
		this.language = language;
		this.description = description;
		this.name = name;
		this.mailConfig = mailConfig;
		this.cid = cid;
	}

	public MailAttachment(Boolean enable, Boolean enableForAll, Language language, String description, String name,
			Long size, String mimeType, String sha256sum, MailConfig mailConfig, String cid) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.ressourceUuid = UUID.randomUUID().toString();
		this.enable = enable;
		this.enableForAll = enableForAll;
		this.language = language;
		this.description = description;
		this.name = name;
		this.size = size;
		this.creationDate = new Date();
		this.modificationDate = new Date();
		this.mimeType = mimeType;
		this.sha256sum = sha256sum;
		this.mailConfig = mailConfig;
		this.cid = cid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Boolean getEnableForAll() {
		return enableForAll;
	}

	public void setEnableForAll(Boolean enableForAll) {
		this.enableForAll = enableForAll;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MailConfig getMailConfig() {
		return mailConfig;
	}

	public void setMailConfig(MailConfig mailConfig) {
		this.mailConfig = mailConfig;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getRessourceUuid() {
		return ressourceUuid;
	}

	public void setRessourceUuid(String ressourceUuid) {
		this.ressourceUuid = ressourceUuid;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
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

	public String getSha256sum() {
		return sha256sum;
	}

	public void setSha256sum(String sha256sum) {
		this.sha256sum = sha256sum;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getBucketUuid() {
		return bucketUuid;
	}

	public void setBucketUuid(String bucketUuid) {
		this.bucketUuid = bucketUuid;
	}

	@Override
	public String toString() {
		return "MailAttachment [uuid=" + uuid + ", ressourceUuid=" + ressourceUuid + ", enable=" + enable
				+ ", enableForAll=" + enableForAll + ", language=" + language
				+ ", description=" + description + ", name=" + name + ", size=" + size + ", creationDate="
				+ creationDate + ", modificationDate=" + modificationDate + ", mimeType="
				+ mimeType + ", sha256sum=" + sha256sum + ", mailConfig=" + mailConfig + ", cid=" + cid
				+ "]";
	}
}
