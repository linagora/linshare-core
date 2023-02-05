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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.utils.Version;

import com.google.common.base.Function;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "Share entry group")
@Schema(name = "Share entry group", description = "A Share entry group")
public class ShareEntryGroupDto {

	@Schema(description = "Owner")
	private AccountDto owner;

	@Schema(description = "Uuid")
	private String uuid;

	@Schema(description = "Subject")
	private String subject;

	@Schema(description = "Notification date")
	private Date notificationDate;

	@Schema(description = "Creation date")
	private Date creationDate;

	@Schema(description = "Modification date")
	private Date modificationDate;

	@Schema(description = "Notified")
	private Boolean notified = false;

	@Schema(description = "Processed")
	private Boolean processed = false;

	@Schema(description = "Expiration date")
	private Date expirationDate;

	@Schema(description = "List of share and anonymous share entries")
	private List<ShareDto> shareEntriesDto;

	public ShareEntryGroupDto() {
		super();
	}

	public ShareEntryGroupDto(AccountDto owner, String subject) {
		super();
		this.setOwner(owner);
		this.setSubject(subject);
	}

	public ShareEntryGroupDto(Version version, ShareEntryGroup shareEntryGroup, boolean full) {
		super();
		this.setOwner(new AccountDto(shareEntryGroup.getOwner(), false));
		this.setCreationDate(shareEntryGroup.getCreationDate());
		this.setModificationDate(shareEntryGroup.getModificationDate());
		this.setUuid(shareEntryGroup.getUuid());
		this.setSubject(shareEntryGroup.getSubject());
		this.setNotified(shareEntryGroup.getNotified());
		this.setProcessed(shareEntryGroup.getProcessed());
		this.setExpirationDate(shareEntryGroup.getExpirationDate());
		List<ShareDto> seDto = new ArrayList<ShareDto>();
		if (full) {
			for (ShareEntry se : shareEntryGroup.getShareEntries()) {
				seDto.add(ShareDto.getSentShare(version, se));
			}
			for (AnonymousShareEntry ase : shareEntryGroup.getAnonymousShareEntries()) {
				seDto.add(ShareDto.getReceivedShare(version, ase));
			}
			this.setShareEntriesDto(seDto);
		}
	}

	public ShareEntryGroup toObject() {
		ShareEntryGroup e = new ShareEntryGroup();
		e.setModificationDate(getModificationDate());
		e.setSubject(getSubject());
		e.setExpirationDate(getExpirationDate());
		e.setProcessed(getProcessed());
		e.setNotified(getNotified());
		return e;
	}

	public AccountDto getOwner() {
		return owner;
	}

	public void setOwner(AccountDto owner) {
		this.owner = owner;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
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

	public Boolean getNotified() {
		return notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public Boolean getProcessed() {
		return processed;
	}

	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public List<ShareDto> getShareEntriesDto() {
		return shareEntriesDto;
	}

	public void setShareEntriesDto(List<ShareDto> shareEntriesDto) {
		this.shareEntriesDto = shareEntriesDto;
	}

	/*
	 * Transformers
	 */

	public static Function<ShareEntryGroup, ShareEntryGroupDto> toDto(Version version, final boolean full) {
		return new Function<ShareEntryGroup, ShareEntryGroupDto>() {
			@Override
			public ShareEntryGroupDto apply(ShareEntryGroup arg0) {
				return new ShareEntryGroupDto(version, arg0, full);
			}
		};
	}
}
