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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "DomainAccessPolicy")
@Schema(name = "DomainAccessPolicy", description = "Access policy of a domain, defining it's communication rules")
public class DomainAccessPolicyDto {

	@Schema(description = "Communication rules")
	private List<DomainAccessRuleDto> rules;

	public DomainAccessPolicyDto() {
	}

	public DomainAccessPolicyDto(DomainAccessPolicy dap) {
		this.rules = Lists.newArrayList();
		for (DomainAccessRule rule : dap.getRules()) {
			// FIXME : the list can contain a null rule object
			/**
			 * If you delete a domain referenced by a domain access policy,
			 * it seems the proxy keep a null object in the dap list.
			 * May be because the rule is deleted by cascade instead of being deleted by the repository.
			 */
			if (rule != null) {
				this.rules.add(new DomainAccessRuleDto(rule));
			}
		}
	}

	public List<DomainAccessRuleDto> getRules() {
		return rules;
	}

	public void setRules(List<DomainAccessRuleDto> rules) {
		this.rules = rules;
	}
}
