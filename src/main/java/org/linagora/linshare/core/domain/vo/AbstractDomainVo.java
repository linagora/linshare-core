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
package org.linagora.linshare.core.domain.vo;

import java.io.Serializable;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.AbstractDomain;

public class AbstractDomainVo implements Serializable {

	private static final long serialVersionUID = -7310335993606576958L;

	protected String identifier;

	protected String label;

	protected SupportedLanguage defaultLocale;

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

	public AbstractDomainVo() {
	}

	public AbstractDomainVo(AbstractDomain entity) {
		this.setDefaultLocale(entity.getDefaultTapestryLocale());
		this.setDefaultRole(entity.getDefaultRole());
		this.setDomainDescription(entity.getDescription());
		this.setEnable(entity.isEnable());
		this.setIdentifier(entity.getIdentifier());
		this.setLabel(entity.getLabel());
		this.setTemplate(entity.isTemplate());
		this.setUsedSpace(entity.getUsedSpace());
		this.setAuthShowOrder(entity.getAuthShowOrder());
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

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return identifier;
	}

	@Validate("required")
	public SupportedLanguage getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(SupportedLanguage defaultLocale) {
		if (defaultLocale != null)
			this.defaultLocale = defaultLocale;
		else
			this.defaultLocale = SupportedLanguage.ENGLISH;
	}

	// @Validate("required")
	public Role getDefaultRole() {
		return defaultRole;
	}

	public void setDefaultRole(Role defaultRole) {
		this.defaultRole = defaultRole;
	}

	public String getDomainDescription() {
		if (domainDescription == null)
			return "";
		return domainDescription;
	}

	public void setDomainDescription(String domainDescription) {
		if (domainDescription != null)
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

	public Long getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(Long usedSpace) {
		this.usedSpace = usedSpace;
	}

	public Long getAuthShowOrder() {
		return authShowOrder;
	}

	public void setAuthShowOrder(Long authShowOrder) {
		this.authShowOrder = authShowOrder;
	}

}