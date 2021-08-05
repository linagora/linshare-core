/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
package org.linagora.linshare.core.domain.constants;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.RootDomain;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainDto;

@SuppressWarnings( "deprecation" )
public enum DomainType {

	ROOTDOMAIN(0) {
		@Override
		public RootDomain getDomain(DomainDto domainDto, AbstractDomain parent) {
			return new RootDomain(domainDto);
		}

		@Override
		public AbstractDomain createDomain(String name, AbstractDomain parent) {
			return new RootDomain(name);
		}

		@Override
		public AbstractDomain toDomain(org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto dto) {
			RootDomain domain = new RootDomain(dto.getName());
			domain.setDescription(dto.getDescription());
			domain.setDefaultRole(dto.getDefaultUserRole());
			domain.setExternalMailLocale(dto.getDefaultEmailLanguage());
			return domain;
		}
	},
	TOPDOMAIN(1) {
		@Override
		public TopDomain getDomain(DomainDto domainDto, AbstractDomain parent) {
			return new TopDomain(domainDto, parent);
		}

		@Override
		public TopDomain createDomain(String name, AbstractDomain parent) {
			return new TopDomain(name, parent);
		}

		@Override
		public AbstractDomain toDomain(org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto dto) {
			TopDomain domain = new TopDomain(dto.getName());
			domain.setDescription(dto.getDescription());
			domain.setDefaultRole(dto.getDefaultUserRole());
			domain.setExternalMailLocale(dto.getDefaultEmailLanguage());
			return domain;
		}
	},
	SUBDOMAIN(2) {
		@Override
		public SubDomain getDomain(DomainDto domainDto, AbstractDomain parent) {
			return new SubDomain(domainDto, parent);
		}

		@Override
		public SubDomain createDomain(String name, AbstractDomain parent) {
			if (!parent.getDomainType().equals(DomainType.TOPDOMAIN)) {
				throw new BusinessException(BusinessErrorCode.DOMAIN_INVALID_TYPE,
						"You must create a sub domain inside a TopDomain.");
			}
			return new SubDomain(name, parent);
		}

		@Override
		public AbstractDomain toDomain(org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto dto) {
			SubDomain domain = new SubDomain(dto.getName());
			domain.setDescription(dto.getDescription());
			domain.setDefaultRole(dto.getDefaultUserRole());
			domain.setExternalMailLocale(dto.getDefaultEmailLanguage());
			return domain;
		}
	},
	GUESTDOMAIN(3) {
		@Override
		public GuestDomain getDomain(DomainDto domainDto, AbstractDomain parent) {
			if (!parent.getDomainType().equals(DomainType.TOPDOMAIN)) {
				throw new BusinessException(BusinessErrorCode.DOMAIN_INVALID_TYPE,
						"You must create a guest domain inside a TopDomain.");
			}
			return new GuestDomain(domainDto, parent);
		}

		@Override
		public GuestDomain createDomain(String name, AbstractDomain parent) {
			return new GuestDomain(name, parent);
		}

		@Override
		public AbstractDomain toDomain(org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto dto) {
			GuestDomain domain = new GuestDomain(dto.getName());
			domain.setDescription(dto.getDescription());
			domain.setDefaultRole(dto.getDefaultUserRole());
			domain.setExternalMailLocale(dto.getDefaultEmailLanguage());
			return domain;
		}
	};

	private int value;

	private DomainType(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static DomainType fromInt(int value) {
		for (DomainType type : values()) {
			if (type.value == value) {
				return type;
			}
		}
		throw new IllegalArgumentException("Doesn't match an existing DomainType");
	}

	@Deprecated
	public abstract AbstractDomain getDomain(DomainDto domainDto, AbstractDomain parent);

	public abstract AbstractDomain createDomain(String name, AbstractDomain parent);

	public abstract AbstractDomain toDomain(org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto dto);
}
