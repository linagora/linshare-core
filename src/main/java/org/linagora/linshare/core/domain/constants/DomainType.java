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
package org.linagora.linshare.core.domain.constants;

import java.util.Optional;

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
			if (!parent.getDomainType().equals(DomainType.TOPDOMAIN)) {
				throw new BusinessException(BusinessErrorCode.DOMAIN_INVALID_TYPE,
						"You must create a guest domain inside a TopDomain.");
			}

			Optional<AbstractDomain> otherGuestDomain = parent.getSubdomain().stream()
					.filter(domain -> domain.isGuestDomain() && DomainPurgeStepEnum.IN_USE.equals(domain.getPurgeStep()))
					.findFirst();
			if (otherGuestDomain.isPresent()){
				throw new BusinessException(BusinessErrorCode.DOMAIN_INVALID_OPERATION,
						"Another guest domain already exist : " + otherGuestDomain.get().getLabel());
			}

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
