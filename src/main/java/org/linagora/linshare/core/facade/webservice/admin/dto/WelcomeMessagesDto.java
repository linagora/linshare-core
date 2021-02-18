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

package org.linagora.linshare.core.facade.webservice.admin.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.domain.entities.WelcomeMessagesEntry;
import org.linagora.linshare.core.facade.webservice.common.dto.DomainLightDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Sets;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "WelcomeMessages")
@Schema(name = "WelcomeMessages", description = "")
public class WelcomeMessagesDto {

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Name")
	private String name;

	@Schema(description = "Description")
	private String description;

	@Schema(description = "CreationDate")
	private Date creationDate;

	@Schema(description = "ModificationDate")
	private Date modificationDate;

	@Schema(description = "MyDomain")
	private DomainLightDto myDomain;

	@Schema(description = "Domains")
	private Set<DomainLightDto> domains;

	@Schema(description = "WelcomeMessagesEntries")
	private Map<SupportedLanguage, String> welcomeMessagesEntries;

	public WelcomeMessagesDto() {
	}

	public WelcomeMessagesDto(WelcomeMessages welcomeMessage, boolean extended) {
		this.uuid = welcomeMessage.getUuid();
		this.name = welcomeMessage.getName();
		this.description = welcomeMessage.getDescription();
		this.creationDate = welcomeMessage.getCreationDate();
		this.modificationDate = welcomeMessage.getModificationDate();
		if (extended) {
			this.welcomeMessagesEntries = new HashMap<SupportedLanguage, String>();
			for (WelcomeMessagesEntry entry : welcomeMessage
					.getWelcomeMessagesEntries().values()) {
				welcomeMessagesEntries.put(entry.getLang(), entry.getValue());
			}
			this.myDomain = new DomainLightDto(welcomeMessage.getDomain());
			this.domains = Sets.newHashSet();
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(java.util.Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(java.util.Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public DomainLightDto getMyDomain() {
		return myDomain;
	}

	public void setMyDomain(DomainLightDto myDomain) {
		this.myDomain = myDomain;
	}

	public Map<SupportedLanguage, String> getWelcomeMessagesEntries() {
		return welcomeMessagesEntries;
	}

	public void setWelcomeMessagesEntries(
			Map<SupportedLanguage, String> wlcmEntries) {
		this.welcomeMessagesEntries = wlcmEntries;
	}

	public Set<DomainLightDto> getDomains() {
		return domains;
	}

	public void addDomain(AbstractDomain domain) {
		if (domains == null) {
			domains = Sets.newHashSet();
		}
		domains.add(new DomainLightDto(domain));
	}

	public void setDomains(Set<DomainLightDto> domains) {
		this.domains = domains;
	}

	@Override
	public String toString() {
		return "WelcomeMessagesDto [uuid=" + uuid + ", name=" + name + "]";
	}

	/**
	 * Helpers
	 */
	public WelcomeMessages toObject() {
		WelcomeMessages wlcm = new WelcomeMessages();
		wlcm.setUuid(uuid);
		wlcm.setDescription(description);
		wlcm.setName(name);
		if (welcomeMessagesEntries != null) {
			for (SupportedLanguage supportedLanguage : welcomeMessagesEntries
					.keySet()) {
				wlcm.addWelcomeMessagesEntry(supportedLanguage,
						welcomeMessagesEntries.get(supportedLanguage));
			}
		}
		return wlcm;
	}
}
