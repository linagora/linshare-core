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
package org.linagora.linshare.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.domain.entities.LDAPConnection;
import org.linagora.linshare.core.domain.entities.LdapUserProvider;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DomainPatternRepository;
import org.linagora.linshare.core.repository.LDAPConnectionRepository;
import org.linagora.linshare.core.repository.UserProviderRepository;
import org.linagora.linshare.core.service.LDAPQueryService;
import org.linagora.linshare.core.service.UserProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserProviderServiceImpl implements UserProviderService {

    private static final Logger logger = LoggerFactory.getLogger(UserProviderServiceImpl.class);

    private final LDAPConnectionRepository ldapConnectionRepository;
    private final DomainPatternRepository domainPatternRepository;
    private final LDAPQueryService ldapQueryService;
    private final UserProviderRepository userProviderRepository;


    public UserProviderServiceImpl(LDAPConnectionRepository ldapConnectionRepository,
            DomainPatternRepository domainPatternRepository,
            LDAPQueryService ldapQueryService,
            UserProviderRepository userProviderRepository) {
        this.ldapConnectionRepository = ldapConnectionRepository;
        this.domainPatternRepository = domainPatternRepository;
        this.ldapQueryService = ldapQueryService;
        this.userProviderRepository = userProviderRepository;
    }

    @Override
    public DomainPattern createDomainPattern(DomainPattern domainPattern) throws BusinessException {
        DomainPattern createdDomain = domainPatternRepository.create(domainPattern);
        return createdDomain;
    }

    @Override
    public LDAPConnection createLDAPConnection(LDAPConnection ldapConnection) throws BusinessException {
        LDAPConnection createdLDAPConnection = ldapConnectionRepository.create(ldapConnection);
        return createdLDAPConnection;
    }

    @Override
    public LDAPConnection retrieveLDAPConnection(String identifier) throws BusinessException {
        return ldapConnectionRepository.findById(identifier);
    }

    @Override
    public DomainPattern retrieveDomainPattern(String identifier) throws BusinessException {
        return domainPatternRepository.findById(identifier);
    }

    @Override
    public void deleteConnection(String connectionToDelete) throws BusinessException {
        if (!connectionIsDeletable(connectionToDelete)) {
            throw new BusinessException("Cannot delete connection because still used by domains");
        }

        LDAPConnection conn = retrieveLDAPConnection(connectionToDelete);
        if(conn == null) {
            logger.error("Ldap connexion not found: " + connectionToDelete);
        } else {
            logger.debug("delete ldap connexion : " + connectionToDelete);
            ldapConnectionRepository.delete(conn);
        }
    }

    @Override
    public boolean connectionIsDeletable(String connectionToDelete) {
        List<LdapUserProvider> list = userProviderRepository.findAll();
        boolean used = false;
        for (LdapUserProvider ldapUserProvider : list) {
            if (ldapUserProvider.getLdapconnexion().getIdentifier().equals(connectionToDelete)) {
                used = true;
                break;
            }
        }
        return (!used);
    }

    @Override
    public void deletePattern(String patternToDelete) throws BusinessException {

        if (!patternIsDeletable(patternToDelete)) {
            throw new BusinessException("Cannot delete pattern because still used by domains");
        }

        DomainPattern pattern = retrieveDomainPattern(patternToDelete);
        domainPatternRepository.delete(pattern);
    }

    @Override
    public boolean patternIsDeletable(String patternToDelete) {
        List<LdapUserProvider> list = userProviderRepository.findAll();
        boolean used = false;
        for (LdapUserProvider ldapUserProvider : list) {
            if (ldapUserProvider.getPattern().getIdentifier().equals(patternToDelete)) {
                used = true;
                break;
            }
        }
        return (!used);
    }


    @Override
    public List<DomainPattern> findAllDomainPatterns() throws BusinessException {
        return domainPatternRepository.findAll();
    }

    @Override
    public List<DomainPattern> findAllUserDomainPattern() throws BusinessException {
        return domainPatternRepository.findAllUserDomainPattern();
    }

    @Override
    public List<DomainPattern> findAllSystemDomainPattern() throws BusinessException {
        return domainPatternRepository.findAllSystemDomainPattern();
    }

    @Override
    public List<LDAPConnection> findAllLDAPConnections() throws BusinessException {
        return ldapConnectionRepository.findAll();
    }

    @Override
    public void updateLDAPConnection(LDAPConnection ldapConnection) throws BusinessException {
        LDAPConnection ldapConn = ldapConnectionRepository.findById(ldapConnection.getIdentifier());
        ldapConn.setProviderUrl(ldapConnection.getProviderUrl());
        ldapConn.setSecurityAuth(ldapConnection.getSecurityAuth());
        ldapConn.setSecurityCredentials(ldapConnection.getSecurityCredentials());
        ldapConn.setSecurityPrincipal(ldapConnection.getSecurityPrincipal());
        ldapConnectionRepository.update(ldapConn);
    }

    @Override
    public void updateDomainPattern(DomainPattern domainPattern) throws BusinessException {
        DomainPattern pattern = domainPatternRepository.findById(domainPattern.getIdentifier());
        pattern.setDescription(domainPattern.getDescription());
        pattern.setAuthCommand(domainPattern.getAuthCommand());
        pattern.setSearchUserCommand(domainPattern.getSearchUserCommand());
        pattern.getAttributes().get(DomainPattern.USER_FIRST_NAME).setAttribute(domainPattern.getAttributes().get(DomainPattern.USER_FIRST_NAME).getAttribute());
        pattern.getAttributes().get(DomainPattern.USER_LAST_NAME).setAttribute(domainPattern.getAttributes().get(DomainPattern.USER_LAST_NAME).getAttribute());
        pattern.getAttributes().get(DomainPattern.USER_MAIL).setAttribute(domainPattern.getAttributes().get(DomainPattern.USER_MAIL).getAttribute());
        pattern.getAttributes().get(DomainPattern.USER_UID).setAttribute(domainPattern.getAttributes().get(DomainPattern.USER_UID).getAttribute());
        domainPatternRepository.update(pattern);
    }

    @Override
    public void create(LdapUserProvider userProvider) throws BusinessException {
        userProviderRepository.create(userProvider);
    }

    @Override
    public void delete(LdapUserProvider userProvider) throws BusinessException {
        userProviderRepository.delete(userProvider);
    }

    @Override
    public void update(LdapUserProvider userProvider) throws BusinessException {
        userProviderRepository.update(userProvider);
    }

    @Override
    public List<User> searchUser(LdapUserProvider userProvider, String mail) throws BusinessException, NamingException, IOException {
        return searchUser(userProvider, mail, "", "");
    }

    @Override
    public List<User> searchUser(LdapUserProvider userProvider, String mail, String firstName, String lastName) throws BusinessException, NamingException, IOException {
        return ldapQueryService.searchUser(userProvider.getLdapconnexion(), userProvider.getBaseDn(), userProvider.getPattern(), mail, firstName, lastName);
    }

    @Override
    public List<User> autoCompleteUser(LdapUserProvider userProvider, String pattern) throws BusinessException, NamingException, IOException {
        return ldapQueryService.completeUser(userProvider.getLdapconnexion(), userProvider.getBaseDn(), userProvider.getPattern(), pattern);
    }
    
    @Override
    public List<User> autoCompleteUser(LdapUserProvider userProvider, String firstName, String lastName) throws BusinessException, NamingException, IOException {
    	return ldapQueryService.completeUser(userProvider.getLdapconnexion(), userProvider.getBaseDn(), userProvider.getPattern(), firstName, lastName);
    }
    
    @Override
    public User auth(LdapUserProvider userProvider, String mail, String userPasswd)	throws BusinessException, NamingException, IOException {
        LdapUserProvider p = userProvider;
        if(p == null) {
            return null;
        }
        return ldapQueryService.auth(p.getLdapconnexion(), p.getBaseDn(), p.getPattern(), mail,userPasswd);
    }

    @Override
    public List<String> findAllDomainPatternIdentifiers() {
        List<String> list = new ArrayList<String>();
        for (DomainPattern d : domainPatternRepository.findAll()) {
            list.add(d.getIdentifier());
        }
        return list;
    }

    @Override
    public List<String> findAllUserDomainPatternIdentifiers() {
        List<String> list = new ArrayList<String>();
        for (DomainPattern d : domainPatternRepository.findAllUserDomainPattern()) {
            list.add(d.getIdentifier());
        }
        return list;
    }

    @Override
    public List<String> findAllSystemDomainPatternIdentifiers() {
        List<String> list = new ArrayList<String>();
        for (DomainPattern d : domainPatternRepository.findAllSystemDomainPattern()) {
            list.add(d.getIdentifier());
        }
        return list;
    }

    @Override
    public List<String> findAllLDAPConnectionIdentifiers() {
        List<String> list = new ArrayList<String>();
        for (LDAPConnection c : ldapConnectionRepository.findAll()) {
            list.add(c.getIdentifier());
        }
        return list;
    }

}


