/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
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

package org.linagora.linshare.mongo.entities;

import java.util.Date;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.linagora.linshare.core.domain.constants.PublicKeysFormatType;
import org.linagora.linshare.core.domain.constants.PublicKeysUsageType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "PublicKeys")
@Document(collection = "public_keys")
public class PublicKeys {

	@JsonIgnore
	@Id
	@GeneratedValue
	protected String id;

	@ApiModelProperty(value = "uuid")
	protected String uuid;

	@ApiModelProperty(value = "domainUuid")
	protected String domainUuid;

	@ApiModelProperty(value = "usage")
	protected PublicKeysUsageType usage;

	@ApiModelProperty(value = "destroyed")
	protected boolean destroyed;

	@ApiModelProperty(value = "issuer")
	@Indexed(unique = true)
	protected String issuer;

	@ApiModelProperty(value = "publicKey")
	protected String publicKey;

	@ApiModelProperty(value = "crationDate")
	protected Date creationDate;

	@ApiModelProperty(value = "format")
	protected PublicKeysFormatType format;

	public PublicKeys() {
		super();
		this.uuid = UUID.randomUUID().toString();
	}

	public PublicKeys(String issuer, String publicKey, PublicKeysFormatType format, String domainUuid) {
		this.issuer = issuer;
		this.publicKey = publicKey;
		this.destroyed = false;
		this.format = format;
		this.creationDate = new Date();
		this.usage = PublicKeysUsageType.JWT;
		this.domainUuid = domainUuid;
		this.uuid = UUID.randomUUID().toString();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDomainUuid() {
		return domainUuid;
	}

	public void setDomainUuid(String domainUuid) {
		this.domainUuid = domainUuid;
	}

	public PublicKeysUsageType getUsage() {
		return usage;
	}

	public void setUsage(PublicKeysUsageType usage) {
		this.usage = usage;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public PublicKeysFormatType getFormat() {
		return format;
	}

	public void setFormat(PublicKeysFormatType format) {
		this.format = format;
	}
}
