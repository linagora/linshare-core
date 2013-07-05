/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
import org.linagora.linshare.core.domain.vo.DomainAccessRuleVo;
import org.linagora.linshare.core.facade.impl.DomainPolicyFacadeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainAccessPolicyVo {

	private static final Logger logger = LoggerFactory
			.getLogger(DomainAccessPolicyVo.class);
	
	private List<DomainAccessRuleVo> rules;
	private long id;

	public DomainAccessPolicyVo() {
	}

	public DomainAccessPolicyVo(DomainAccessPolicy policy) {

		rules = new ArrayList<DomainAccessRuleVo>();
		for (DomainAccessRule current : policy.getRules()) {
			
			if(current instanceof AllowDomain){
				AllowDomain allow = new AllowDomain(((AllowDomain) current).getDomain());
				allow.setPersistenceId(current.getPersistenceId());
				AllowDomainVo allowDomain = new AllowDomainVo(allow);
				rules.add(allowDomain);
				
			} else if(current instanceof AllowAllDomain){
				AllowAllDomainVo allowAllDomain = new AllowAllDomainVo();
				allowAllDomain.setPersistenceId(current.getPersistenceId());
				rules.add(allowAllDomain);
				
			}else if(current instanceof DenyDomain){
				DenyDomainVo denyDomain = new DenyDomainVo(((DenyDomain) current).getDomain().getIdentifier());
				denyDomain.setPersistenceId(current.getPersistenceId());
				rules.add(denyDomain);
				
			} else if(current instanceof DenyAllDomain){
				DenyAllDomainVo denyAllDomain = new DenyAllDomainVo();
				denyAllDomain.setPersistenceId(current.getPersistenceId());
				rules.add(denyAllDomain);
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

	public boolean compareList(List<DomainAccessRuleVo> rules) {
		int ok = 0;
		for (DomainAccessRuleVo rule : rules) {
			for (DomainAccessRuleVo rule2 : this.getRules()) {
				if (rule.toString().equalsIgnoreCase(rule2.toString())) {
					ok++;
				}
			}
		}
		if (ok == rules.size()) {
			return true;
		} else {
			return false;
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}