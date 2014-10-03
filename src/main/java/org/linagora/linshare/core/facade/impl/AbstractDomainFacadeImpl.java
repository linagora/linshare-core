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
package org.linagora.linshare.core.facade.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.MessagesConfiguration;
import org.linagora.linshare.core.domain.entities.ShareExpiryRule;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WelcomeText;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.GuestDomainVo;
import org.linagora.linshare.core.domain.vo.SubDomainVo;
import org.linagora.linshare.core.domain.vo.TopDomainVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.UserAndDomainMultiService;
import org.linagora.linshare.core.utils.AESCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDomainFacadeImpl implements AbstractDomainFacade {

	private final AbstractDomainService abstractDomainService;
	private final FunctionalityReadOnlyService functionalityReadOnlyService;
	private final UserAndDomainMultiService userAndDomainMultiService;
	// Dirty hack. Will be removed with tapestry ! :)
	private final AbstractDomainRepository abstractDomainRepository;

    private static final Logger logger = LoggerFactory.getLogger(AbstractDomainFacadeImpl.class);

    public AbstractDomainFacadeImpl(AbstractDomainService abstractDomainService, FunctionalityReadOnlyService functionalityReadOnlyService,
            UserAndDomainMultiService userAndDomainMultiService, AbstractDomainRepository abstractDomainRepository) {
        super();
        this.abstractDomainService = abstractDomainService;
        this.functionalityReadOnlyService = functionalityReadOnlyService;
        this.userAndDomainMultiService = userAndDomainMultiService;
        this.abstractDomainRepository = abstractDomainRepository;
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
    public List<AbstractDomainVo> findAllTopAndSubDomain() {
        List<AbstractDomainVo> res = new ArrayList<AbstractDomainVo>();
        for (AbstractDomain abstractDomain : abstractDomainService.getAllTopAndSubDomain()) {
            res.add(new AbstractDomainVo(abstractDomain));
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
            abstractDomainRepository.update(domain);
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
            User actor = userAndDomainMultiService.findOrCreateUser(actorVo.getMail(),actorVo.getDomainIdentifier());
            abstractDomainService.updateDomain(actor, domain);
        } else {
            throw new BusinessException("You are not authorized to update shareExpiryRules.");
        }
    }

    @Override
    public List<String> getAllDomainIdentifiers(UserVo actorVo) throws BusinessException {
        return abstractDomainService.getAllMyDomainIdentifiers(actorVo.getDomainIdentifier());
    }


	@Override
	public Set<WelcomeText> getWelcomeMessages() {
		return abstractDomainService.getUniqueRootDomain().getMessagesConfiguration().getWelcomeTexts();
	}
}
