package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.Signature;

public interface SignatureRepository extends AbstractRepository<Signature> {

	 /** Find a signature using its uuid.
     * @param id
     * @return found document (null if no Signature found).
     */
	public Signature findByUuid(String identifier);
	  
}