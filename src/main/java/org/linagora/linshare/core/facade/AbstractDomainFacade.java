/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
 */
package org.linagora.linshare.core.facade;

import java.util.List;

import org.linagora.linshare.core.domain.entities.MessagesConfiguration;
import org.linagora.linshare.core.domain.entities.ShareExpiryRule;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.DomainPatternVo;
import org.linagora.linshare.core.domain.vo.GuestDomainVo;
import org.linagora.linshare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface AbstractDomainFacade {

    public void createDomain(UserVo actorVo, AbstractDomainVo domainVo) throws BusinessException;
    public AbstractDomainVo retrieveDomain(String identifier) throws BusinessException;
    public void deleteDomain(String identifier, UserVo actorVo) throws BusinessException;
    public List<String> getAllDomainIdentifiers() throws BusinessException;
    public List<String> getAllDomainIdentifiers(UserVo actorVo) throws BusinessException;
    public boolean userCanCreateGuest(UserVo userVo) throws BusinessException;
    public boolean canCreateGuestDomain(String domainIdentifier) throws BusinessException;
    public boolean guestDomainAllowed(String domainIdentifier) throws BusinessException;

    public List<AbstractDomainVo> findAllDomain();
    public List<AbstractDomainVo> findAllTopDomain();
    public List<AbstractDomainVo> findAllTopAndSubDomain();
    public List<AbstractDomainVo> findAllSubDomainWithoutGuestDomain(String topDomainIdentifier);
    public GuestDomainVo findGuestDomain(String topDomainIdentifier);
    public void updateDomain(UserVo actorVo, AbstractDomainVo domain) throws BusinessException;

    /**
     * Update the display order of all domain.
     * @param actorVo
     * @param domainsVo
     * @throws BusinessException
     */
    public void updateAllDomainForAuthShowOrder(UserVo actorVo,List<AbstractDomainVo> domainsVo) throws BusinessException;

    public List<String> findAllDomainIdentifiers();
    public List<String> findAllDomainPatternIdentifiers();
    public List<String> findAllUserDomainPatternIdentifiers();
    public List<String> findAllSystemDomainPatternIdentifiers();
    public List<DomainPatternVo> findAllDomainPatterns() throws BusinessException ;	
    public List<DomainPatternVo> findAllSystemDomainPatterns() throws BusinessException;
    public List<DomainPatternVo> findAllUserDomainPatterns() throws BusinessException;
    public void createDomainPattern(UserVo actorVo, DomainPatternVo domainPatternVo) throws BusinessException ;
    public DomainPatternVo retrieveDomainPattern(String identifier) throws BusinessException ;
    public void updateDomainPattern(UserVo actorVo, DomainPatternVo domainPatternVo) throws BusinessException ;
    public void deletePattern(String patternToDelete, UserVo actorVo) throws BusinessException ;
    public boolean patternIsDeletable(String patternToDelete, UserVo actor) ;


    public List<String> findAllLDAPConnectionIdentifiers();
    public List<LDAPConnectionVo> findAllLDAPConnections() throws BusinessException ;
    public LDAPConnectionVo createLDAPConnection(UserVo actorVo, LDAPConnectionVo ldapConnectionVo) throws BusinessException ;
    public LDAPConnectionVo retrieveLDAPConnection(String identifier)throws BusinessException ;
    public void updateLDAPConnection(UserVo actorVo, LDAPConnectionVo ldapConn) throws BusinessException ;
    public void deleteConnection(String connectionToDelete, UserVo actorVo) throws BusinessException ;
    public boolean connectionIsDeletable(String connectionToDelete, UserVo actor) ;

    public boolean isCustomLogoActive(UserVo actorVo) throws BusinessException;
	public boolean isCustomLogoActiveByDefault() throws BusinessException;
    public String getCustomLogoUrl(UserVo actorVo) throws BusinessException;
	public String getCustomLogoUrlByDefault() throws BusinessException;

    public MessagesConfiguration getMessages(String domainIdentifier) throws BusinessException;
    public void updateMessages(UserVo actorVo, String domainIdentifier, MessagesConfiguration messages) throws BusinessException;

    public List<ShareExpiryRule> getShareExpiryRules(String domainIdentifier) throws BusinessException;
    public void updateShareExpiryRules(UserVo actorVo, String domainIdentifier, List<ShareExpiryRule> shareExpiryRules) throws BusinessException;

    /**
     * return the current used space of this domain
     * @param domainIdentifier
     * @return
     * @throws BusinessException
     */
    public Long getUsedSpace(String domainIdentifier) throws BusinessException;

    /**
     * check that encrypt, signature is possible on the server side
     * just do a call to crypt function.
     */
    public boolean checkPlatformEncryptSupportedAlgo() ;

    /**
     * This method return true if the current domain support type Mime type filter.
     * @param domainIdentifier
     * @return
     */
    public boolean isMimeTypeFilterEnableFor(String domainIdentifier, UserVo actorVo);

}
