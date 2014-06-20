package org.linagora.linshare.core.business.service;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface GuestBusinessService {

	Guest findByLsUuid(String lsUuid) throws BusinessException;

	List<Guest> findAll();

	List<Guest> findOutdatedGuests();

	Guest create(Guest guest, User owner, GuestDomain domain, Date expiryDate)
			throws BusinessException;

	Guest update(Guest guest, Account owner, AbstractDomain domain)
			throws BusinessException;

	void delete(Guest guest) throws BusinessException;

	boolean exist(String domainId, String mail);

	void purgeRestriction(Guest guest) throws BusinessException;

	void addRestrictedContact(Guest guest, User contact) throws BusinessException;

	/**
	 * Remove restriction on contacts for a guest and delete all his contacts
	 * @param guest guest lsUuid
	 * @return updated guest
	 * @throws BusinessException
	 */
	Guest removeContactRestriction(Guest guest) throws BusinessException;

	Guest enableContactRestriction(Guest guest) throws BusinessException;

	List<AllowedContact> getRestrictedContacts(Guest guest);

	void resetPassword(Guest guest) throws BusinessException;
}
