package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;

public interface UploadRequestEntryUrlRepository extends
		AbstractRepository<UploadRequestEntryUrl> {

	/**
	 * Find a UploadRequestEntryUrl using its uuid.
	 * 
	 * @param uuid
	 * @return found UploadRequestEntryUrl (null if no uploadRequestEntry found).
	 */
	public UploadRequestEntryUrl findByUuid(String uuid);
}
