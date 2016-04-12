/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
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

package org.linagora.linshare.core.facade.webservice.user.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "UploadRequestGroup")
public class UploadRequestGroupDto {

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Subject")
	private String subject;

	@ApiModelProperty(value = "Body")
	private String body;

	@ApiModelProperty(value = "creationDate")
	private Date creationDate;

	@ApiModelProperty(value = "modificationDate")
	private Date modifiationDate;

	@ApiModelProperty(value = "UploadRequest")
	private List<UploadRequestDto> uploadRequestDtos = Lists.newArrayList();

	public UploadRequestGroupDto() {
	}

	public UploadRequestGroupDto(UploadRequestGroup group) {
		this.uuid = group.getUuid();
		this.subject = group.getSubject();
		this.body = group.getBody();
		this.creationDate = group.getCreationDate();
		this.modifiationDate = group.getModificationDate();
		this.uploadRequestDtos = ImmutableList.copyOf(Lists.transform(Lists.newArrayList(group.getUploadRequests()), UploadRequestDto.toDto(false)));
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

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModifiationDate() {
		return modifiationDate;
	}

	public void setModifiationDate(Date modifiationDate) {
		this.modifiationDate = modifiationDate;
	}

	public List<UploadRequestDto> getUploadrequestDtos() {
		return uploadRequestDtos;
	}

	public void setUploadrequestDtos(List<UploadRequestDto> uploadrequestDtos) {
		this.uploadRequestDtos = uploadrequestDtos;
	}

	/*
	 * Transformers
	 */
	public static Function<UploadRequestGroup, UploadRequestGroupDto> toDto() {
		return new Function<UploadRequestGroup, UploadRequestGroupDto>() {
			@Override
			public UploadRequestGroupDto apply(UploadRequestGroup arg0) {
				return new UploadRequestGroupDto(arg0);
			}
		};
	}
}
