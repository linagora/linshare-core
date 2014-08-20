package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.UploadPropositionRule;

public interface UploadPropositionRuleRepository extends
		AbstractRepository<UploadPropositionRule> {

	UploadPropositionRule find(String uuid);
}
