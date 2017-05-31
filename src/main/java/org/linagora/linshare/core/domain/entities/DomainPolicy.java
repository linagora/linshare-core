/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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

import java.util.UUID;

import org.linagora.linshare.core.facade.webservice.admin.dto.DomainPolicyDto;

public class DomainPolicy {

	/**
	 * Database persistence identifier
	 */
	private long persistenceId;

	private String uuid;

	private String label;

	private DomainAccessPolicy domainAccessPolicy;

	private String description;

	public DomainPolicy() {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.domainAccessPolicy = new DomainAccessPolicy();
	}

	public DomainPolicy(DomainPolicyDto dto) {
		super();
		this.uuid = dto.getIdentifier();
		this.label = dto.getLabel();
		this.description = dto.getDescription();
		this.domainAccessPolicy = new DomainAccessPolicy();
	}

	public DomainPolicy(String label) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.label = label;
		this.domainAccessPolicy = new DomainAccessPolicy();
	}

	public DomainPolicy(String label, DomainAccessPolicy policy) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.label = label;
		this.domainAccessPolicy = policy;
	}

	public DomainPolicy(String label, String description,
			DomainAccessPolicy domainAccessPolicy) {
		this.uuid = UUID.randomUUID().toString();
		this.label = label;
		this.description = description;
		this.domainAccessPolicy = domainAccessPolicy;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DomainAccessPolicy getDomainAccessPolicy() {
		return domainAccessPolicy;
	}

	public void setDomainAccessPolicy(DomainAccessPolicy domainAccessPolicy) {
		this.domainAccessPolicy = domainAccessPolicy;
	}

	public long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	@Override
	public String toString() {
		return "DomainPolicy [uuid=" + uuid + ", label=" + label + "]";
	}

}
