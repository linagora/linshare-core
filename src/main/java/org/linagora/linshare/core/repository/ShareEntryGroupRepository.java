package org.linagora.linshare.core.repository;

import java.util.List;

import org.linagora.linshare.core.domain.entities.ShareEntryGroup;

public interface ShareEntryGroupRepository extends AbstractRepository<ShareEntryGroup>{

	ShareEntryGroup findById(long id);

	ShareEntryGroup findByUuid(String uuid);

	List<ShareEntryGroup> findAllToNotify();

	List<ShareEntryGroup> findAllToPurge();
}
