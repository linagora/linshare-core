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

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.MailFooterLang;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainLightDto;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "MailConfig")
@Schema(name = "MailConfig", description = "")
public class MailConfigDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Domain")
	private DomainLightDto domain;

	@Schema(description = "Name")
	private String name;

	@Schema(description = "Visible")
	private boolean visible;

	@Schema(description = "Readonly")
	private boolean readonly;

	@Schema(description = "CreationDate")
	private Date creationDate;

	@Schema(description = "ModificationDate")
	private Date modificationDate;

	@Schema(description = "MailLayout")
	private String mailLayout;

	@Schema(description = "MailFooters")
	private Map<Language, MailFooterLangDto> mailFooterLangs = Maps.newHashMap();

	@Schema(description = "MailContentLangs")
	private Set<MailContentLangDto> mailContentLangs = Sets.newHashSet();

	public MailConfigDto() {
	}

	public MailConfigDto(MailConfig config) {
		this(config, false);
	}

	public MailConfigDto(MailConfig config, boolean overrideReadonly) {
		super();
		this.uuid = config.getUuid();
		this.domain = new DomainLightDto(config.getDomain());
		this.name = config.getName();
		this.visible = config.isVisible();
		this.readonly = config.isReadonly();
		if (overrideReadonly) {
			readonly = false;
		}
		this.creationDate = new Date(config.getCreationDate().getTime());
		this.modificationDate = new Date(config.getModificationDate().getTime());
		this.mailLayout = config.getMailLayoutHtml().getUuid();

		Set<MailContentLang> mcls = config.getMailContentLangs();
		Map<Integer, MailFooterLang> mfls = config.getMailFooters();

		for (MailContentLang mcl : mcls) {
			this.mailContentLangs.add(new MailContentLangDto(mcl, overrideReadonly));
		}
		for (Entry<Integer, MailFooterLang> e : mfls.entrySet()) {
			this.mailFooterLangs.put(Language.fromInt(e.getKey()),
					new MailFooterLangDto(e.getValue()));
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public DomainLightDto getDomain() {
		return domain;
	}

	public void setDomain(DomainLightDto domain) {
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public String getMailLayout() {
		return mailLayout;
	}

	public void setMailLayout(String mailLayout) {
		this.mailLayout = mailLayout;
	}

	public Map<Language, MailFooterLangDto> getMailFooterLangs() {
		return mailFooterLangs;
	}

	public void setMailFooterLangs(Map<Language, MailFooterLangDto> mailFooterLangs) {
		this.mailFooterLangs = mailFooterLangs;
	}

	public Set<MailContentLangDto> getMailContentLangs() {
		return mailContentLangs;
	}

	public void setMailContentLangs(Set<MailContentLangDto> mailContentLangs) {
		this.mailContentLangs = mailContentLangs;
	}
}
