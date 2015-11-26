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

package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;

import com.google.common.base.Function;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Share entry group")
@ApiModel(value = "Share entry group", description = "A Share entry group")
public class ShareEntryGroupDto {

	@ApiModelProperty(value = "Owner")
	private AccountDto owner;

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Subject")
	private String subject;

	@ApiModelProperty(value = "Notification date")
	private Date notificationDate;

	@ApiModelProperty(value = "Creation date")
	private Date creationDate;

	@ApiModelProperty(value = "Modification date")
	private Date modificationDate;

	@ApiModelProperty(value = "Notified")
	private Boolean notified = false;

	@ApiModelProperty(value = "Processed")
	private Boolean processed = false;

	@ApiModelProperty(value = "Expiration date")
	private Date expirationDate;

	@ApiModelProperty(value = "List of share and anonymous share entries")
	private List<ShareDto> shareEntriesDto;

	public ShareEntryGroupDto() {
		super();
	}

	public ShareEntryGroupDto(AccountDto owner, String subject) {
		super();
		this.setOwner(owner);
		this.setSubject(subject);
	}

	public ShareEntryGroupDto(ShareEntryGroup shareEntryGroup, boolean full) {
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
				seDto.add(ShareDto.getSentShare(se));
			}
			for (AnonymousShareEntry ase : shareEntryGroup.getAnonymousShareEntries()) {
				seDto.add(ShareDto.getReceivedShare(ase));
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

	public static Function<ShareEntryGroup, ShareEntryGroupDto> toDto(final boolean full) {
		return new Function<ShareEntryGroup, ShareEntryGroupDto>() {
			@Override
			public ShareEntryGroupDto apply(ShareEntryGroup arg0) {
				return new ShareEntryGroupDto(arg0, full);
			}
		};
	}
}
