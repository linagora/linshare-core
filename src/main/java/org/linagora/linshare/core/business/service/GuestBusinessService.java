package org.linagora.linshare.core.business.service;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.impl.GuestBusinessServiceImpl.GuestWithMetadata;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface GuestBusinessService {

	Guest findByLsUuid(String lsUuid) throws BusinessException;

	List<Guest> findAll();

	List<Guest> findOutdatedGuests();

	GuestWithMetadata create(Guest guest, User owner, GuestDomain domain, Date expiryDate)
			throws BusinessException;

	Guest update(Guest guest, Account owner, AbstractDomain domain)
			throws BusinessException;

	void delete(Guest guest) throws BusinessException;

	boolean exist(String domainId, String mail);

	GuestWithMetadata resetPassword(Guest guest) throws BusinessException;
}
