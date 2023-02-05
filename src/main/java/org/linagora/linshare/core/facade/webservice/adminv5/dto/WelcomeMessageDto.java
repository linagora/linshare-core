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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.domain.entities.WelcomeMessagesEntry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
	name = "WelcomeMessage",
	description = "Welcome message"
)
public class WelcomeMessageDto {

	@Schema(name = "Domain", description = "A LinShare's domain")
	public static class DomainDto {

		@Schema(description = "Domain's uuid",
				required = true)
		private String uuid;

		@Schema(description = "Domain's name")
		private String name;

		public DomainDto() {
			super();
		}

		public DomainDto(AbstractDomain domain) {
			this.setUuid(domain.getUuid());
			this.setName(domain.getLabel());
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

		@Override
		public final boolean equals(Object other) {
			if (other instanceof  DomainDto) {
				DomainDto domainDto = (DomainDto) other;
				return Objects.equals(uuid, domainDto.getUuid())
					&& Objects.equals(name, domainDto.getName());
			}
			return false;
		}

		@Override
		public final int hashCode() {
			return Objects.hash(uuid, name);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("uuid", uuid)
					.add("name", name)
					.toString();
		}
	}

	@Schema(description = "Welcome message's uuid",
			required = false)
	protected String uuid;

	@Schema(description = "Welcome message's name (updatable)",
			required = true)
	protected String name;

	@Schema(description = "Welcome message's creation date",
			required = false)
	protected Date creationDate;

	@Schema(description = "Welcome message's description (updatable)",
			required = false)
	protected String description;

	@Schema(description = "Welcome message's modification date",
			required = false)
	protected Date modificationDate;

	@Schema(description = "Domain",
			required = true)
	protected DomainDto domain;

	@Schema(description = "Is welcome message assigned to current domain")
	protected boolean assignedToCurrentDomain;

	@Schema(description = "Is welcome message read only")
	protected boolean readOnly;

	@Schema(description = "Welcome message entries (updatable)",
			required = true)
	protected Map<SupportedLanguage, String> entries;

	public static WelcomeMessageDto from(WelcomeMessages welcomeMessage, AbstractDomain domain, boolean readOnly) {
		WelcomeMessageDto welcomeMessageDto = new WelcomeMessageDto();
		welcomeMessageDto.setUuid(welcomeMessage.getUuid());
		welcomeMessageDto.setName(welcomeMessage.getName());
		welcomeMessageDto.setCreationDate(welcomeMessage.getCreationDate());
		welcomeMessageDto.setDescription(welcomeMessage.getDescription());
		welcomeMessageDto.setModificationDate(welcomeMessage.getModificationDate());
		welcomeMessageDto.setDomain(new DomainDto(welcomeMessage.getDomain()));
		welcomeMessageDto.setAssignedToCurrentDomain(isAssignedToCurrentDomain(welcomeMessage, domain));
		welcomeMessageDto.setReadOnly(readOnly);
		welcomeMessageDto.setEntries(entries(welcomeMessage.getWelcomeMessagesEntries()));
		return welcomeMessageDto;
	}

	private static boolean isAssignedToCurrentDomain(WelcomeMessages welcomeMessage, AbstractDomain domain) {
		WelcomeMessages currentWelcomeMessage = domain.getCurrentWelcomeMessage();
		if (currentWelcomeMessage == null) {
			return false;
		}
		return currentWelcomeMessage.getUuid().equals(welcomeMessage.getUuid());
	}

	private static Map<SupportedLanguage, String> entries(Map<SupportedLanguage, WelcomeMessagesEntry> entries) {
		Map<SupportedLanguage, String> map = new HashMap<>();
		for (Map.Entry<SupportedLanguage, WelcomeMessagesEntry> entry : entries.entrySet()) {
			map.put(entry.getKey(), entry.getValue().getValue());
		}
		return map;
	}

	public WelcomeMessageDto() {
		super();
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public DomainDto getDomain() {
		return domain;
	}

	public void setDomain(DomainDto domain) {
		this.domain = domain;
	}

	public boolean isAssignedToCurrentDomain() {
		return assignedToCurrentDomain;
	}

	public void setAssignedToCurrentDomain(boolean assignedToCurrentDomain) {
		this.assignedToCurrentDomain = assignedToCurrentDomain;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Map<SupportedLanguage, String> getEntries() {
		return entries;
	}

	public void setEntries(Map<SupportedLanguage, String> entries) {
		this.entries = entries;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("uuid", uuid)
				.add("name", name)
				.add("creationDate", creationDate)
				.add("description", description)
				.add("modificationDate", modificationDate)
				.add("domain", domain)
				.add("assignedToCurrentDomain", assignedToCurrentDomain)
				.add("readOnly", readOnly)
				.toString();
	}
}
