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

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Thread")
@ApiModel(value = "Thread", description = "A thread is a shared space for users to deposit files.")
public class WorkGroupDto extends AccountDto {

	@ApiModelProperty(value = "Name")
	protected String name;

	@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
	@ApiModelProperty(value = "Workgroup's quota uuid, only available in v2.")
	protected String quotaUuid;

	@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
	@ApiModelProperty(value = "Members")
	protected Set<WorkGroupMemberDto> members;

	public WorkGroupDto(Thread thread) {
		super(thread, true);
		this.name = thread.getName();
	}

	public WorkGroupDto(Thread thread, List<ThreadMember> members) {
		super(thread, true);
		this.name = thread.getName();
		this.members = Sets.newHashSet();
		for (ThreadMember member : members) {
			this.members.add(new WorkGroupMemberDto(member));
		}
	}

	public WorkGroupDto() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<WorkGroupMemberDto> getMembers() {
		return members;
	}

	public void setMembers(Set<WorkGroupMemberDto> members) {
		this.members = members;
	}

	public String getQuotaUuid() {
		return quotaUuid;
	}

	public void setQuotaUuid(String quotaUuid) {
		this.quotaUuid = quotaUuid;
	}

	/*
	 * Transformers
	 */
	public static Function<Thread, WorkGroupDto> toDto() {
		return new Function<Thread, WorkGroupDto>() {
			@Override
			public WorkGroupDto apply(Thread arg0) {
				return new WorkGroupDto(arg0);
			}
		};
	}
}
