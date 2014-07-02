/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.core.domain.vo;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AllowAllDomain;
import org.linagora.linshare.core.domain.entities.AllowDomain;
import org.linagora.linshare.core.domain.entities.DenyAllDomain;
import org.linagora.linshare.core.domain.entities.DenyDomain;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;

public class DomainAccessPolicyVo {

	private List<DomainAccessRuleVo> rules;
	private long id;

	public DomainAccessPolicyVo() {
	}

	public DomainAccessPolicyVo(DomainAccessPolicy policy) {

		rules = new ArrayList<DomainAccessRuleVo>();
		for (DomainAccessRule rule : policy.getRules()) {
			
			if(rule instanceof AllowDomain){
				rules.add(new AllowDomainVo(((AllowDomain) rule).getDomain().getIdentifier(),rule.getPersistenceId()));
				
			} else if(rule instanceof AllowAllDomain){
				rules.add(new AllowAllDomainVo(rule.getPersistenceId()));
				
			}else if(rule instanceof DenyDomain){
				rules.add(new DenyDomainVo(((DenyDomain) rule).getDomain().getIdentifier(),rule.getPersistenceId()));
				
			} else if(rule instanceof DenyAllDomain){
				rules.add(new DenyAllDomainVo(rule.getPersistenceId()));
			}	
		}
		this.id = policy.getPersistenceId();
	}

	public List<DomainAccessRuleVo> getRules() {
		return rules;
	}

	public void setRules(List<DomainAccessRuleVo> rules) {
		this.rules = rules;
	}

	public void addRule(DomainAccessRuleVo rule) {
		if (this.rules == null) {
			this.rules = new ArrayList<DomainAccessRuleVo>();
		}
		this.rules.add(rule);
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}