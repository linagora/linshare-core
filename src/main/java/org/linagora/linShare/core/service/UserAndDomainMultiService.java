package org.linagora.linShare.core.service;

import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;

public interface UserAndDomainMultiService {
	

	/** Find a  user (based on mail address).
     * Search first in database, then on ldap if not found.
     * If the user isn't found on DB, then it is created from the ldap info.
     * If the user isn't found in the ldap, an exception is raised.
     * @param mail user mail.
     * @param domainId domain identifier.
     * @return founded user.
     * @throws BusinessException if the user could not be found
	 */
	public User findOrCreateUser(String mail, String domainId) throws BusinessException ;
	 
    /**
     * Delete a all users from a domain (and all the related data )
     * @param actor
     * @param domainIdentifier 
     * @throws BusinessException 
     */
	void deleteDomainAndUsers(User actor, String domainIdentifier) throws BusinessException;
}
