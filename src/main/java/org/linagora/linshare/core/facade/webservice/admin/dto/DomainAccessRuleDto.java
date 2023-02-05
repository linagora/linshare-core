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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.DomainAccessRuleType;
import org.linagora.linshare.core.domain.entities.AllowDomain;
import org.linagora.linshare.core.domain.entities.DenyDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainDto;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "DomainAccessRule")
@Schema(name = "DomainAccessRule", description = "Access rule of a domain")
public class DomainAccessRuleDto {

	@Schema(description = "Access rule type")
	private DomainAccessRuleType type;

	@Schema(description = "Domain being allowed / denied")
	private DomainDto domain;

	public DomainAccessRuleDto() {
	}

	public DomainAccessRuleDto(DomainAccessRule rule) {
		this.type = rule.getDomainAccessRuleType();
		switch (type) {
		case ALLOW:
			this.domain = DomainDto.getSimple(((AllowDomain) rule).getDomain());
			break;
		case DENY:
			this.domain = DomainDto.getSimple(((DenyDomain) rule).getDomain());
			break;
		default:
			this.domain = null;
			break;
		}
	}

	public DomainAccessRuleType getType() {
		return type;
	}

	public void setType(DomainAccessRuleType type) {
		this.type = type;
	}

	public DomainDto getDomain() {
		return domain;
	}

	public void setDomain(DomainDto domain) {
		this.domain = domain;
	}

	@Override
	public String toString() {
		if (domain==null) {
			return type.toString();
		} else {
			return type.toString() + " : " + domain.getIdentifier();
		}
	}

}
