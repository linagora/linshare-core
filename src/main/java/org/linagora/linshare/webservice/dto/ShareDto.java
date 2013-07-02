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
package org.linagora.linshare.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;


@XmlRootElement(name = "Share")
public class ShareDto extends EntryDto {

	private Long downloaded;
	
	private UserDto recipient;
	
	private DocumentDto documentDto;
	
	private int secured;
	
	private String message;

	public ShareDto(ShareEntry shareEntry) {
		super(shareEntry);
		this.documentDto = new DocumentDto(shareEntry.getDocumentEntry());
		this.downloaded = shareEntry.getDownloaded();
		this.recipient = new UserDto(shareEntry.getRecipient());
		this.owner = new UserDto((User) shareEntry.getEntryOwner());
	}
	
	public ShareDto() {
		super();
	}

	public Long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(Long downloaded) {
		this.downloaded = downloaded;
	}

	public UserDto getRecipient() {
		return recipient;
	}

	public void setRecipient(UserDto recipient) {
		this.recipient = recipient;
	}

	public DocumentDto getDocumentDto() {
		return documentDto;
	}

	public void setDocumentDto(DocumentDto documentDto) {
		this.documentDto = documentDto;
	}

	public String getType() {
		return this.documentDto.getType();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getSecured() {
		return secured;
	}

	public void setSecured(int secured) {
		this.secured = secured;
	}
}
