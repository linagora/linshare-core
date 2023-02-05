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
