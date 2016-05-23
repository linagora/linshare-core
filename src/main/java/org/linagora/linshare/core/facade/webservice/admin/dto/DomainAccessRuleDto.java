/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.facade.webservice.admin.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.DomainAccessRuleType;
import org.linagora.linshare.core.domain.entities.AllowDomain;
import org.linagora.linshare.core.domain.entities.DenyDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainDto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "DomainAccessRule")
@ApiModel(value = "DomainAccessRule", description = "Access rule of a domain")
public class DomainAccessRuleDto {

	@ApiModelProperty(value = "Access rule type")
	private DomainAccessRuleType type;

	@ApiModelProperty(value = "Domain being allowed / denied")
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
