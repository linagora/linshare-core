/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
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
	public boolean isCustomLogoActiveInRootDomain() throws BusinessException;
    public String getCustomLogoUrl(UserVo actorVo) throws BusinessException;
	public String getCustomLogoUrlInRootDomain() throws BusinessException;
    public String getCustomLogoLink(UserVo actorVo) throws BusinessException;
    public String getCustomLogoLinkInRootDomain() throws BusinessException ;

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
