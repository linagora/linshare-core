/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.core.facade.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.MessagesConfiguration;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.entities.ShareExpiryRule;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.DomainPatternVo;
import org.linagora.linshare.core.domain.vo.GuestDomainVo;
import org.linagora.linshare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linshare.core.domain.vo.SubDomainVo;
import org.linagora.linshare.core.domain.vo.TopDomainVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.DomainPolicyService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.UserAndDomainMultiService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.core.utils.AESCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDomainFacadeImpl implements AbstractDomainFacade {

    private final AbstractDomainService abstractDomainService;
    private final FunctionalityReadOnlyService functionalityReadOnlyService;
    private final UserAndDomainMultiService userAndDomainMultiService;
    private final UserProviderService userProviderService;
    private final DomainPolicyService domainPolicyService;

    private static final Logger logger = LoggerFactory.getLogger(AbstractDomainFacadeImpl.class);

    public AbstractDomainFacadeImpl(AbstractDomainService abstractDomainService, FunctionalityReadOnlyService functionalityReadOnlyService,
            UserProviderService userProviderService, DomainPolicyService domainPolicyService, UserAndDomainMultiService userAndDomainMultiService) {
        super();
        this.abstractDomainService = abstractDomainService;
        this.functionalityReadOnlyService = functionalityReadOnlyService;
        this.userProviderService = userProviderService;
        this.domainPolicyService = domainPolicyService;
        this.userAndDomainMultiService = userAndDomainMultiService;
    }


    private boolean isAuthorized(UserVo actorVo) throws BusinessException {
        if(actorVo !=null) {
            User actor = userAndDomainMultiService.findOrCreateUser(actorVo.getMail(),actorVo.getDomainIdentifier());
            if(actor != null) {
                if (actor.getRole().equals(Role.SUPERADMIN)
                		|| actor.getRole().equals(Role.SYSTEM)
                		|| actor.getRole().equals(Role.ADMIN)) {
                    return true;
                }
                logger.error("you are not authorised.");
            } else {
                logger.error("isAuthorized:actor object is null.");
            }
        } else {
            logger.error("isAuthorized:actorVo object is null.");
        }
        return false;
    }

    @Override
    public void createDomain(UserVo actorVo, AbstractDomainVo domainVo) throws BusinessException {
        if(isAuthorized(actorVo)) {
            createOrUpdateDomain(domainVo, true);
        }
    }

    @Override
    public void updateDomain(UserVo actorVo, AbstractDomainVo domainVo) throws BusinessException {
        if(isAuthorized(actorVo)) {
            createOrUpdateDomain(domainVo, false);
        }
    }


    @Override
    public void updateAllDomainForAuthShowOrder(UserVo actorVo,List<AbstractDomainVo> domainsVo) throws BusinessException{
        if(isAuthorized(actorVo)) {
            for (AbstractDomainVo domainVo : domainsVo) {
                updateDomainForAuthShowOrder(domainVo);
            }
        }
    }

    /**
     * Update one domain display order value.
     * @param domainVo
     * @throws BusinessException
     */
    private void updateDomainForAuthShowOrder(AbstractDomainVo domainVo) throws BusinessException{
        AbstractDomain abstractDomain = abstractDomainService.retrieveDomain(domainVo.getIdentifier());

        if(abstractDomain == null ) {
            throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,"The domain : " + domainVo.getIdentifier() + " has no existing id.");
        }

        logger.debug("Change domain order, Domain :" + domainVo.getIdentifier() + " old order value :" + abstractDomain.getAuthShowOrder() + ", new order value" + domainVo.getAuthShowOrder());
        abstractDomain.setAuthShowOrder(domainVo.getAuthShowOrder());
        abstractDomainService.updateDomain(abstractDomain);
    }

    private void createOrUpdateDomain(AbstractDomainVo domainVo, boolean create) throws BusinessException {
        logger.debug("domainVo class:" + domainVo.getClass().toString());
        logger.debug("domainVo :" + domainVo.toString());

        DomainPattern domainPattern = userProviderService.retrieveDomainPattern(domainVo.getPatternIdentifier());
        LDAPConnection ldapConn = userProviderService.retrieveLDAPConnection(domainVo.getLdapIdentifier());
        DomainPolicy policy = domainPolicyService.retrieveDomainPolicy(domainVo.getPolicyIdentifier());

        LdapUserProvider provider = null;
        String baseDn = domainVo.getDifferentialKey();
        if (baseDn != null && !baseDn.isEmpty() && domainPattern != null && ldapConn != null) {
            provider = new LdapUserProvider(baseDn, ldapConn, domainPattern);
        }

        if(domainVo instanceof TopDomainVo) {

            TopDomain topDomain = new TopDomain((TopDomainVo)domainVo);

            if(provider !=null) {
                topDomain.setUserProvider(provider);
            }
            topDomain.setPolicy(policy);
            if(create){
                logger.debug("Create linshare Top Domain : " + topDomain.getIdentifier());
                abstractDomainService.createTopDomain(topDomain);
            } else {
                logger.debug("Update linshare Top Domain : " + topDomain.getIdentifier());
                abstractDomainService.updateDomain(topDomain);
            }

        } else if(domainVo instanceof SubDomainVo) {

            AbstractDomain topDomain = abstractDomainService.retrieveDomain(((SubDomainVo) domainVo).getParentDomainIdentifier());
            if(topDomain == null ) {
                throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,"This new sub domain has no parent domain defined.");
            }

            SubDomain subDomain = new SubDomain((SubDomainVo)domainVo);

            subDomain.setParentDomain(topDomain);
            if(provider !=null) {
                subDomain.setUserProvider(provider);
            }
            subDomain.setPolicy(policy);

            if(create){
                logger.debug("Create linshare Sub Domain : " + subDomain.getIdentifier());
                abstractDomainService.createSubDomain(subDomain);
            } else {
                logger.debug("Update linshare Sub Domain : " + subDomain.getIdentifier());
                abstractDomainService.updateDomain(subDomain);
            }

        } else if(domainVo instanceof GuestDomainVo) {
            AbstractDomain topDomain = abstractDomainService.retrieveDomain(((GuestDomainVo) domainVo).getParentDomainIdentifier());
            if(topDomain == null ) {
                throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,"This new guest domain has no parent domain defined.");
            }

            GuestDomain guestDomain = new GuestDomain((GuestDomainVo)domainVo);

            guestDomain.setParentDomain(topDomain);
            guestDomain.setPolicy(policy);

            if(create){
                logger.debug("Create linshare Guest Domain : " + guestDomain.getIdentifier());
                abstractDomainService.createGuestDomain(guestDomain);
            } else {
                logger.debug("Update linshare Guest Domain : " + guestDomain.getIdentifier());
                abstractDomainService.updateDomain(guestDomain);
            }
        } else {
            AbstractDomain domain = abstractDomainService.retrieveDomain(domainVo.getIdentifier());

            if(domain == null ) {
                throw new BusinessException(BusinessErrorCode.DOMAIN_ID_NOT_FOUND,"The domain : " + domainVo.getIdentifier() + " has no existing id.");
            }

            if(domain.getDomainType().equals(DomainType.ROOTDOMAIN)){
                logger.debug("Update linshare Root Domain : " + domain.getIdentifier());
                abstractDomainService.updateDomain(domain);
            }else{
                throw new BusinessException(BusinessErrorCode.DOMAIN_INVALID_TYPE,"Wrong type of domain : not TopDomain, SubDomain, GuestDomain and rootDomain");
            }
        }
    }


    @Override
    public AbstractDomainVo retrieveDomain(String identifier) throws BusinessException {
        AbstractDomain domain = abstractDomainService.retrieveDomain(identifier);

        if(domain instanceof TopDomain) {
            return new TopDomainVo(domain);
        } else if(domain instanceof SubDomain) {
            return new SubDomainVo(domain);
        } else if(domain instanceof GuestDomain) {
            return new GuestDomainVo(domain);
        }
        return new AbstractDomainVo(domain);
    }

    @Override
    public void deleteDomain(String identifier, UserVo actorVo) throws BusinessException {
        if(isAuthorized(actorVo)) {

            User actor = userAndDomainMultiService.findOrCreateUser(actorVo.getMail(), actorVo.getDomainIdentifier());

            userAndDomainMultiService.deleteDomainAndUsers(actor, identifier);
        }
    }

    @Override
    public List<String> getAllDomainIdentifiers() throws BusinessException {
        return abstractDomainService.getAllDomainIdentifiers();
    }

    @Override
    public boolean userCanCreateGuest(UserVo actorVo) throws BusinessException {
        User actor = userAndDomainMultiService.findOrCreateUser(actorVo.getMail(),actorVo.getDomainIdentifier());
        if(actor != null) {
            logger.debug("user found : " + actor.getMail());
            return abstractDomainService.userCanCreateGuest(actor);
        }
        return false;
    }

    @Override
    public boolean canCreateGuestDomain(String domainIdentifier) throws BusinessException {
        if(domainIdentifier != null) {
            AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
            logger.debug("domain found : " + domain.getIdentifier());
            return abstractDomainService.canCreateGuestDomain(domain);
        }
        return false;
    }


    @Override
    public boolean guestDomainAllowed(String domainIdentifier) throws BusinessException {
        if(domainIdentifier != null) {
            AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
            logger.debug("domain found : " + domain.getIdentifier());
            Functionality func = functionalityReadOnlyService.getGuestFunctionality(domain);
            if(func.getActivationPolicy().getStatus()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public List<AbstractDomainVo> findAllDomain() {
        List<AbstractDomainVo> res = new ArrayList<AbstractDomainVo>();
        for (AbstractDomain abstractDomain : abstractDomainService.getAllDomains()) {
            res.add(new AbstractDomainVo(abstractDomain));
        }
        return res;
    }

    @Override
    public List<AbstractDomainVo> findAllTopDomain() {
        List<AbstractDomainVo> res = new ArrayList<AbstractDomainVo>();
        for (AbstractDomain abstractDomain : abstractDomainService.getAllTopDomain()) {
            res.add(new AbstractDomainVo(abstractDomain));
        }
        return res;
    }

    @Override
    public List<AbstractDomainVo> findAllTopAndSubDomain() {
        List<AbstractDomainVo> res = new ArrayList<AbstractDomainVo>();
        for (AbstractDomain abstractDomain : abstractDomainService.getAllTopAndSubDomain()) {
            res.add(new AbstractDomainVo(abstractDomain));
        }
        return res;
    }

    @Override
    public List<AbstractDomainVo> findAllSubDomainWithoutGuestDomain(String topDomainIdentifier) {
        List<AbstractDomainVo> res = new ArrayList<AbstractDomainVo>();

        AbstractDomain topDomain = abstractDomainService.retrieveDomain(topDomainIdentifier);
        if(topDomain == null) {
            logger.error("The top domain " + topDomainIdentifier + " was not found.");
        } else {
            for (AbstractDomain abstractDomain : topDomain.getSubdomain()) {
                if(!abstractDomain.getDomainType().equals(DomainType.GUESTDOMAIN)) {
                    res.add(new AbstractDomainVo(abstractDomain));
                }
            }
        }
        return res;
    }

    @Override
    public GuestDomainVo findGuestDomain(String topDomainIdentifier) {
        GuestDomain g = abstractDomainService.getGuestDomain(topDomainIdentifier);
        if(g==null) {
            // No Guest domain found.
            return null;
        } else {
            return new GuestDomainVo (abstractDomainService.getGuestDomain(topDomainIdentifier));
        }
    }

    @Override
    public List<String> findAllDomainIdentifiers(){
        return abstractDomainService.getAllDomainIdentifiers();
    }

    @Override
    public List<String> findAllDomainPatternIdentifiers() {
        return userProviderService.findAllDomainPatternIdentifiers();
    }
    
    @Override
    public List<String> findAllUserDomainPatternIdentifiers() {
        return userProviderService.findAllUserDomainPatternIdentifiers();
    }

    @Override
    public List<String> findAllSystemDomainPatternIdentifiers() {
        return userProviderService.findAllSystemDomainPatternIdentifiers();
    }

    
    @Override
    public List<DomainPatternVo> findAllUserDomainPatterns() throws BusinessException {
        List<DomainPatternVo> res = new ArrayList<DomainPatternVo>();
        for (DomainPattern domainPattern : userProviderService.findAllUserDomainPattern()) {
            res.add(new DomainPatternVo(domainPattern));
        }
        return res;
    }

    @Override
    public List<DomainPatternVo> findAllSystemDomainPatterns() throws BusinessException {
        List<DomainPatternVo> res = new ArrayList<DomainPatternVo>();
        for (DomainPattern domainPattern : userProviderService.findAllSystemDomainPattern()) {
            res.add(new DomainPatternVo(domainPattern));
        }
        return res;
    }

    @Override
    public void createDomainPattern(UserVo actorVo, DomainPatternVo domainPatternVo) throws BusinessException {
        if(isAuthorized(actorVo)) {
            DomainPattern domainPattern = new DomainPattern(domainPatternVo);
            userProviderService.createDomainPattern(domainPattern);
        } else {
            throw new BusinessException("You are not authorized to create a domain pattern.");
        }
    }

    @Override
    public DomainPatternVo retrieveDomainPattern(String identifier) throws BusinessException {
        DomainPattern pattern = userProviderService.retrieveDomainPattern(identifier);
        return new DomainPatternVo(pattern);
    }

   
    @Override
    public void updateDomainPattern(UserVo actorVo, DomainPatternVo domainPatternVo) throws BusinessException {
        if(isAuthorized(actorVo)) {
            userProviderService.updateDomainPattern(new DomainPattern(domainPatternVo));
        } else {
            throw new BusinessException("You are not authorized to update a domain pattern.");
        }
    }

    @Override
    public void deletePattern(String patternToDelete, UserVo actorVo) throws BusinessException {
        if(isAuthorized(actorVo)) {
            userProviderService.deletePattern(patternToDelete);
        } else {
            throw new BusinessException("You are not authorized to delete a domain pattern.");
        }
    }

    @Override
    public boolean patternIsDeletable(String patternToDelete, UserVo actor) {
        return userProviderService.patternIsDeletable(patternToDelete);
    }

    @Override
    public List<String> findAllLDAPConnectionIdentifiers() {
        return userProviderService.findAllLDAPConnectionIdentifiers();
    }

    @Override
    public List<LDAPConnectionVo> findAllLDAPConnections() throws BusinessException {
        List<LDAPConnectionVo> res = new ArrayList<LDAPConnectionVo>();
        for (LDAPConnection ldap : userProviderService.findAllLDAPConnections()) {
            res.add(new LDAPConnectionVo(ldap));
        }
        return res;
    }

    @Override
    public LDAPConnectionVo createLDAPConnection(UserVo actorVo, LDAPConnectionVo ldapConnectionVo) throws BusinessException {
        if(isAuthorized(actorVo)) {
            LDAPConnection ldapConnection = new LDAPConnection(ldapConnectionVo);
            return new LDAPConnectionVo(userProviderService.createLDAPConnection(ldapConnection));
        } else {
            throw new BusinessException("You are not authorized to create a connection.");
        }
    }

    @Override
    public LDAPConnectionVo retrieveLDAPConnection(String identifier)throws BusinessException {
        LDAPConnection ldap =  userProviderService.retrieveLDAPConnection(identifier);
        return new LDAPConnectionVo(ldap);
    }

    @Override
    public void updateLDAPConnection(UserVo actorVo, LDAPConnectionVo ldapConn) throws BusinessException {
        if(isAuthorized(actorVo)) {
            LDAPConnection ldapConnection = new LDAPConnection(ldapConn);
            userProviderService.updateLDAPConnection(ldapConnection);
        } else {
            throw new BusinessException("You are not authorized to update a connection.");
        }
    }

    @Override
    public void deleteConnection(String connectionToDelete, UserVo actorVo) throws BusinessException {
        if(isAuthorized(actorVo)) {
            userProviderService.deleteConnection(connectionToDelete);
        } else {
            throw new BusinessException("You are not authorized to delete a connection.");
        }
    }

    @Override
    public boolean connectionIsDeletable(String connectionToDelete, UserVo actorVo) {
        if(actorVo == null) {
            logger.error("actor object is null.");
        }
        return userProviderService.connectionIsDeletable(connectionToDelete);
    }

    @Override
    public boolean isCustomLogoActive(UserVo actorVo) throws BusinessException {
        AbstractDomain domain = abstractDomainService.retrieveDomain(actorVo.getDomainIdentifier());
        return functionalityReadOnlyService.getCustomLogoFunctionality(domain).getActivationPolicy().getStatus();
    }

    @Override
    public boolean isCustomLogoActiveInRootDomain() throws BusinessException {
        return functionalityReadOnlyService.isCustomLogoActiveInRootDomain();
    }
    
    @Override
    public String getCustomLogoUrl(UserVo actorVo) throws BusinessException {
        User actor = userAndDomainMultiService.findOrCreateUser(actorVo.getMail(),actorVo.getDomainIdentifier());
        return functionalityReadOnlyService.getCustomLogoFunctionality(actor.getDomain()).getValue();
    }

    @Override
    public String getCustomLogoUrlInRootDomain() throws BusinessException {
        return functionalityReadOnlyService.getCustomLogoUrlInRootDomain();
    }
    
    @Override
    public String getCustomLogoLink(UserVo actorVo) throws BusinessException {
        User actor = userAndDomainMultiService.findOrCreateUser(actorVo.getMail(),actorVo.getDomainIdentifier());
        return functionalityReadOnlyService.getCustomLinkLogoFunctionality(actor.getDomain()).getValue();
    }

    @Override
    public String getCustomLogoLinkInRootDomain() throws BusinessException {
        return functionalityReadOnlyService.getCustomLinkLogoInRootDomain();
    }
    
    @Override
    public Long getUsedSpace(String domainIdentifier) throws BusinessException {
        AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
        return domain.getUsedSpace();
    }

    @Override
    public boolean checkPlatformEncryptSupportedAlgo() {

        //test encrypt aes 256

        boolean res = true;
        AESCrypt aes;

        try {
            aes = new AESCrypt(false, "password");
            aes.encrypt(2, new ByteArrayInputStream("test".getBytes()),	new ByteArrayOutputStream());

        } catch (UnsupportedEncodingException e) {
            res =  false;
            logger.debug(e.toString());
        } catch (GeneralSecurityException e) {
            res =  false;
            logger.debug(e.toString());
        } catch (IOException e) {
            res =  false;
            logger.debug(e.toString());
        } catch (Error err) {
            res = false;
            logger.error(err.toString());
        }

        return res;
    }

    @Override
    public MessagesConfiguration getMessages(String domainIdentifier) throws BusinessException {
        AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
        // Stuff to be compatible with old shit.
        return new MessagesConfiguration(domain.getMessagesConfiguration());
    }

    @Override
    public void updateMessages(UserVo actorVo, String domainIdentifier, MessagesConfiguration messages) throws BusinessException {
        if(isAuthorized(actorVo)) {
            AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);

            // Stuff to be compatible with old shit.
            MessagesConfiguration m = new MessagesConfiguration(messages);
            domain.setMessagesConfiguration(m);

            abstractDomainService.updateDomain(domain);
        } else {
            throw new BusinessException("You are not authorized to update messages.");
        }
    }


    @Override
    public List<ShareExpiryRule> getShareExpiryRules(String domainIdentifier) throws BusinessException {
        AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);

        // TODO : Why ValueObject was not used ?
        //		List<ShareExpiryRuleVo> result = new ArrayList<ShareExpiryRuleVo>();
        //		
        //		if(domain.getShareExpiryRules() != null) {
        //    		for (ShareExpiryRule shareExpiryRule : domain.getShareExpiryRules()) {
        //    			result.add(new ShareExpiryRuleVo(shareExpiryRule));
        //    		}
        //    	}
        return domain.getShareExpiryRules();
    }

    @Override
    public void updateShareExpiryRules(UserVo actorVo, String domainIdentifier, List<ShareExpiryRule> shareExpiryRules) throws BusinessException {
        if(isAuthorized(actorVo)) {
            AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
            domain.setShareExpiryRules(shareExpiryRules);
            abstractDomainService.updateDomain(domain);
        } else {
            throw new BusinessException("You are not authorized to update shareExpiryRules.");
        }
    }

	@Override
	public boolean isMimeTypeFilterEnableFor(String domainIdentifier,
			UserVo actorVo) {
		if (domainIdentifier != null && actorVo != null) {
			if (actorVo.isSuperAdmin()) {
				AbstractDomain domain = abstractDomainService
						.retrieveDomain(domainIdentifier);
				if (domain != null) {
					Functionality mimeTypeFunctionality = functionalityReadOnlyService
							.getMimeTypeFunctionality(domain);
					if (mimeTypeFunctionality.getActivationPolicy().getStatus()) {
						return true;
					}
				}
			}
		}
		return false;
	}

    @Override
    public List<String> getAllDomainIdentifiers(UserVo actorVo) throws BusinessException {
        return abstractDomainService.getAllMyDomainIdentifiers(actorVo.getDomainIdentifier());
    }
}
