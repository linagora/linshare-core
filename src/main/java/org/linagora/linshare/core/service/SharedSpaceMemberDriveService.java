package org.linagora.linshare.core.service;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceMemberContext;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;

public interface SharedSpaceMemberDriveService {

	SharedSpaceMember create(Account authUser, Account actor, SharedSpaceNode foundSharedSpaceNode,
			SharedSpaceMemberContext context, SharedSpaceAccount account);

	SharedSpaceMember createWithoutCheckPermission(Account authUser, Account actor, SharedSpaceNode node,
			SharedSpaceRole role, SharedSpaceRole nestedRole, SharedSpaceAccount account) throws BusinessException;

	SharedSpaceMember update(Account authUser, Account actor, SharedSpaceMember memberToUpdate);

}
