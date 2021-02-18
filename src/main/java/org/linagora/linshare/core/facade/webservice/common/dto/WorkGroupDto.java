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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Function;
import com.google.common.collect.Sets;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "Thread")
@Schema(name = "Thread", description = "A thread is a shared space for users to deposit files.")
public class WorkGroupDto extends AccountDto {

	@Schema(description = "Name")
	protected String name;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "Workgroup's quota uuid, only available in v2.")
	protected String quotaUuid;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Schema(description = "Members")
	protected Set<WorkGroupMemberDto> members;

	public WorkGroupDto(WorkGroup workGroup, SharedSpaceNode sharedSpaceNode) {
		super(workGroup, true);
		this.name = sharedSpaceNode.getName();
	}

	public WorkGroupDto(WorkGroup workGroup) {
		super(workGroup, true);
		this.name = workGroup.getName();
	}

	public WorkGroupDto(WorkGroup workGroup, List<WorkgroupMember> members) {
		super(workGroup, true);
		this.name = workGroup.getName();
		this.members = Sets.newHashSet();
		for (WorkgroupMember member : members) {
			this.members.add(new WorkGroupMemberDto(member));
		}
	}

	public WorkGroupDto(WorkGroup workGroup, SharedSpaceNode node, Set<WorkGroupMemberDto> members) {
		super(workGroup, true);
		this.name = node.getName();
		this.members = members;
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
	public static Function<WorkGroup, WorkGroupDto> toDto() {
		return new Function<WorkGroup, WorkGroupDto>() {
			@Override
			public WorkGroupDto apply(WorkGroup arg0) {
				return new WorkGroupDto(arg0);
			}
		};
	}
}
