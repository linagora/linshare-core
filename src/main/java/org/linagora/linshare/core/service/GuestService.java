package org.linagora.linshare.core.service;

import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface GuestService {

	/**
	 * Find a guest by is lsUuid
	 * 
	 * @param actor
	 *            who trigger the action
	 * @param lsUuid
	 *            guest lsUuid
	 * @return guest found otherwise return null
	 * @throws BusinessException
	 */
	Guest findByLsUuid(User actor, String lsUuid) throws BusinessException;

	/**
	 * 
	 * @param lsUuid
	 * @return
	 * @throws BusinessException
	 */
	boolean exist(String lsUuid) throws BusinessException;

	/**
	 * Create a guest
	 * 
	 * @param actor
	 *            who triggered the action
	 * @param guest
	 *            guest to create
	 * @param ownerLsUuid
	 * @return created guest
	 * @throws BusinessException
	 */
	Guest create(User actor, Guest guest, String ownerLsUuid)
			throws BusinessException;

	/**
	 * Update a guest
	 * 
	 * @param actor
	 *            who triggered the action
	 * @param guest
	 *            guest to update
	 * @param ownerLsUuid
	 *            optional. Required only if owner needs to be updated
	 * @return updated guest
	 * @throws BusinessException
	 */
	Guest update(User actor, Guest guest, String ownerLsUuid)
			throws BusinessException;

	/**
	 * 
	 * @param actor
	 *            who triggered the action
	 * @param lsUuid
	 *            guest lsUuid
	 * @throws BusinessException
	 */
	void delete(User actor, String lsUuid) throws BusinessException;

	/**
	 * Clean outdated guest accounts
	 * 
	 * @param systemAccount
	 */
	void cleanExpiredGuests(SystemAccount systemAccount);

//	/**
//	 * Add a contact for a restricted guest
//	 * 
//	 * @param actor
//	 *            who trigger the action
//	 * @param lsUuid
//	 *            guest lsUuid
//	 * @param contactLsUuid
//	 * @throws BusinessException
//	 */
//	void addRestrictedContact(User actor, String lsUuid, String contactLsUuid)
//			throws BusinessException;
//
//	/**
//	 * Reset all restricted contacts of a guest
//	 * 
//	 * @param actor
//	 *            who trigger the action
//	 * @param lsUuid
//	 *            guest lsUuid
//	 * @param mailContacts
//	 *            new contact mail list
//	 * @throws BusinessException
//	 */
//	void resetContactRestrictions(User actor, String lsUuid,
//			List<String> mailContacts) throws BusinessException;
//
//	/**
//	 * Get all restricted contacts of a guest
//	 * 
//	 * @param actor
//	 *            who trigger the action
//	 * @param lsUuid
//	 *            guest lsUuid
//	 * @return contacts
//	 * @throws BusinessException
//	 */
//	List<AllowedContact> getRestrictedContacts(User actor, String lsUuid)
//			throws BusinessException;

	/**
	 * Reset guest password
	 * 
	 * @param lsUuid
	 *            guest lsUuid
	 * @throws BusinessException
	 */
	void resetPassword(String lsUuid) throws BusinessException;

//	/**
//	 * Remove restriction on contacts for a guest and delete all his contacts
//	 * 
//	 * @param actor
//	 *            who trigger the action
//	 * @param lsUuid
//	 *            guest lsUuid
//	 * @return updated guest
//	 * @throws BusinessException
//	 */
//	Guest removeContactRestriction(User actor, String lsUuid)
//			throws BusinessException;
}
