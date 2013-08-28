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

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Role;

public class AbstractDomainVo {

	protected String identifier;

	protected String label;
	
	protected String defaultLocale;

	protected Role defaultRole;
	
	protected String domainDescription;

	@NonVisual 
	protected boolean template = false;
	
	@NonVisual
	protected boolean enable = true;
	
	@NonVisual 
	protected Long usedSpace = new Long(0);
	
	@NonVisual 
	protected Long authShowOrder = new Long(1);
	
	protected String differentialKey;
	
	protected String patternIdentifier;
	
	protected String ldapIdentifier;
	
	protected String policyIdentifier;
	
	protected DomainType type;

	public AbstractDomainVo() {
	}
	
	
	public AbstractDomainVo(AbstractDomain entity) {
		this.setDefaultLocale(entity.getDefaultLocale());
		this.setDefaultRole(entity.getDefaultRole());
		this.setDomainDescription(entity.getDescription());
		
		this.setPolicyIdentifier(entity.getPolicy().getIdentifier());
		
		if(entity.getUserProvider() != null) {
			this.setDifferentialKey(entity.getUserProvider().getBaseDn());
			this.setLdapIdentifier(entity.getUserProvider().getLdapconnexion().getIdentifier());
			this.setPatternIdentifier(entity.getUserProvider().getPattern().getIdentifier());
		}
		
		this.setEnable(entity.isEnable());
		this.setIdentifier(entity.getIdentifier());
		this.setLabel(entity.getLabel());
		this.setTemplate(entity.isTemplate());
		this.setUsedSpace(entity.getUsedSpace());
		this.setAuthShowOrder(entity.getAuthShowOrder());
		this.setType(entity.getDomainType());
	}

	public AbstractDomainVo(String identifier, String differentialKey,
			String patternIdentifier, String ldapIdentifier) {
		this.identifier = identifier;
		this.differentialKey = differentialKey;
		this.patternIdentifier = patternIdentifier;
		this.ldapIdentifier = ldapIdentifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Validate("required")
	public String getIdentifier() {
		return identifier;
	}
	
	@Validate("required")
	public String getLabel() {
		return label;
	}
	
	public DomainType getType() {
		return type;
	}
	public void setType(DomainType type) {
		this.type=type;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public String getDifferentialKey() {
		return differentialKey;
	}

	public void setDifferentialKey(String differentialKey) {
		if(differentialKey != null)
			this.differentialKey = differentialKey.trim();
		else
			this.differentialKey = differentialKey;			
	}

	@Override
	public String toString() {
		return identifier;
	}

	@Validate("required")
	public String getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(String defaultLocale) {
		if(defaultLocale != null)
			this.defaultLocale = defaultLocale.trim();
		else
			this.defaultLocale = defaultLocale;			
	}

//	@Validate("required")
	public Role getDefaultRole() {
		return defaultRole;
	}

	public void setDefaultRole(Role defaultRole) {
		this.defaultRole = defaultRole;
	}

	public String getDomainDescription() {
		if(domainDescription == null) return "";
		return domainDescription;
	}

	public void setDomainDescription(String domainDescription) {
		if(domainDescription != null)
			this.domainDescription = domainDescription.trim();
		else
			this.domainDescription = domainDescription;			
	}

	public boolean isTemplate() {
		return template;
	}

	public void setTemplate(boolean template) {
		this.template = template;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getPolicyIdentifier() {
		return policyIdentifier;
	}

	public void setPolicyIdentifier(String policyIdentifier) {
		if(policyIdentifier != null)
			this.policyIdentifier = policyIdentifier.trim();
		else
			this.policyIdentifier = policyIdentifier;				
	}

	public Long getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(Long usedSpace) {
		this.usedSpace = usedSpace;
	}

	public String getPatternIdentifier() {
		return patternIdentifier;
	}

	public void setPatternIdentifier(String patternIdentifier) {
		if(patternIdentifier != null)
			this.patternIdentifier = patternIdentifier.trim();
		else
			this.patternIdentifier = patternIdentifier;		
	}

	public String getLdapIdentifier() {
		return ldapIdentifier;
	}

	public void setLdapIdentifier(String ldapIdentifier) {
		if(ldapIdentifier != null)
			this.ldapIdentifier = ldapIdentifier.trim();
		else
			this.ldapIdentifier = ldapIdentifier;
	}

	public Long getAuthShowOrder() {
		return authShowOrder;
	}

	public void setAuthShowOrder(Long authShowOrder) {
		this.authShowOrder = authShowOrder;
	}

}
