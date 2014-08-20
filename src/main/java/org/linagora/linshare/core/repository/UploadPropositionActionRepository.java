package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.UploadPropositionAction;

public interface UploadPropositionActionRepository extends
		AbstractRepository<UploadPropositionAction> {

	UploadPropositionAction find(String uuid);

}
