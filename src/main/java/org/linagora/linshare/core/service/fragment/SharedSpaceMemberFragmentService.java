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
package org.linagora.linshare.core.service.fragment;

import java.util.List;

import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.projections.dto.SharedSpaceNodeNested;

public interface SharedSpaceMemberFragmentService {

	SharedSpaceMember create(Account authUser, Account actor, SharedSpaceNode node, SharedSpaceMemberContext context,
			SharedSpaceAccount account) throws BusinessException;

	SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate, boolean force, Boolean propagate);

	SharedSpaceMember delete(Account authUser, Account actor, String uuid);

	List<SharedSpaceMember> deleteAllMembers(Account authUser, Account actor, SharedSpaceNode node, LogActionCause cause, List<SharedSpaceNodeNested> nodes);

}
