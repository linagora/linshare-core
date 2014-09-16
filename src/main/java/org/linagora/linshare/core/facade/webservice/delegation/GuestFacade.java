package org.linagora.linshare.core.facade.webservice.delegation;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.GuestDto;

public interface GuestFacade extends DelegationGenericFacade {

	GuestDto find(String ownerUuid, String uuid) throws BusinessException;

	/**
	 * 
	 * @param ownerUuid
	 * @param domain : optional. owner domain will be used to find guest domain.
	 * @param mail
	 * @return
	 * @throws BusinessException
	 */
	GuestDto find(String ownerUuid, String domain, String mail) throws BusinessException;

	List<GuestDto> findAll(String ownerUuid) throws BusinessException;

	GuestDto create(String ownerUuid, GuestDto guest) throws BusinessException;

	GuestDto update(String ownerUuid, GuestDto guest) throws BusinessException;

	void delete(String ownerUuid, GuestDto guest) throws BusinessException;

	void delete(String ownerUuid, String uuid) throws BusinessException;

}
