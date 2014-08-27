package org.linagora.linshare.core.repository;

import java.util.List;

import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;

public interface UploadPropositionFilterRepository extends AbstractRepository<UploadPropositionFilter> {

	UploadPropositionFilter find(String uuid);

	List<UploadPropositionFilter> findAllEnabledFilters();

}
