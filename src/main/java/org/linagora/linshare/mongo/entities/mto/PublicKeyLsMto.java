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
package org.linagora.linshare.mongo.entities.mto;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.PublicKeyFormat;
import org.linagora.linshare.mongo.entities.PublicKeyLs;

public class PublicKeyLsMto {

	protected String uuid;

	protected String domainUuid;

	protected String issuer;

	protected String publicKey;

	protected Date creationDate;

	protected PublicKeyFormat format;

	public PublicKeyLsMto() {
		super();
	}

	public PublicKeyLsMto(PublicKeyLs publicKey) {
		this.issuer = publicKey.getIssuer();
		this.publicKey = publicKey.getPublicKey();
		this.format = publicKey.getFormat();
		this.creationDate = publicKey.getCreationDate();
		this.domainUuid = publicKey.getDomainUuid();
		this.uuid = publicKey.getUuid();
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

	public PublicKeyFormat getFormat() {
		return format;
	}

	public void setFormat(PublicKeyFormat format) {
		this.format = format;
	}
}
