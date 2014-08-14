package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;

public interface UploadPropositionFilterRepository extends AbstractRepository<UploadPropositionFilter> {

	UploadPropositionFilter find(String uuid);

}
