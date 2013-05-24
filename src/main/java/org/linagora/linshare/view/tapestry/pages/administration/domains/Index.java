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
package org.linagora.linshare.view.tapestry.pages.administration.domains;

import java.util.List;

import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.DomainPatternVo;
import org.linagora.linshare.core.domain.vo.DomainPolicyVo;
import org.linagora.linshare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.DomainPolicyFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Index {

    private static Logger logger = LoggerFactory.getLogger(Index.class);

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    @Inject
    private Messages messages;

    @Inject
    private AbstractDomainFacade domainFacade;
    
    @Inject
    private DomainPolicyFacade domainPolicyFacade;
    
    @SessionState
    private UserVo loginUser;

    @Persist
    @Property
    private List<AbstractDomainVo> domains;

    @Persist
    @Property
    private List<DomainPatternVo> domainPatterns;

    @Persist
    @Property
    private List<LDAPConnectionVo> ldapConnections;
    
    @Persist
    @Property
    private List<DomainPolicyVo> policies;
    
    @Property
    private DomainPolicyVo domainPolicy;

    @Property
    private AbstractDomainVo domain;

    @Property
    private DomainPatternVo domainPattern;

    @Property
    private LDAPConnectionVo ldapConnection;

    @Persist
    @Property
    private AbstractDomainVo selectedDomain;

    @Property
    @Persist
    private boolean superadmin;

    @Property
    @Persist(value="flash")
    private String domainToDelete;

    @Property
    @Persist(value="flash")
    private String patternToDelete;

    @Property
    @Persist(value="flash")
    private String connectionToDelete;
    
    @Property
    @Persist(value="flash")
    private String policyToDelete;

    @SetupRender
    public void init() throws BusinessException {
        domains = domainFacade.findAllTopDomain();
        domainPatterns = domainFacade.findAllUserDomainPatterns();
        ldapConnections = domainFacade.findAllLDAPConnections();
        policies = domainFacade.findAllDomainPolicies();
        /*for(DomainPolicyVo current : policies){
		logger.debug("id:" + current.getDomainAccessPolicy().getPersistenceId());
		logger.debug("rules:" + current.getDomainAccessPolicy().getRules());
        }*/
        
        List<DomainAccessPolicy> accesses=domainFacade.findAllDomainAccessPolicy();
        for(DomainAccessPolicy current : accesses){
    		logger.debug("id:" + current.getPersistenceId());
    		logger.debug("rules:" + current.getRules());
            }
        
    }

    @OnEvent(value="domainDeleteEvent")
    public void deleteDomain() throws BusinessException {
        domainFacade.deleteDomain(domainToDelete, loginUser);
        domains = domainFacade.findAllTopDomain();
    }

    @OnEvent(value="patternDeleteEvent")
    public void deletePattern() throws BusinessException {
        domainFacade.deletePattern(patternToDelete, loginUser);
        domains = domainFacade.findAllTopDomain();
    }

    @OnEvent(value="connectionDeleteEvent")
    public void deleteConnection() throws BusinessException {
        domainFacade.deleteConnection(connectionToDelete, loginUser);
        ldapConnections = domainFacade.findAllLDAPConnections();
    }
    
    @OnEvent(value="policyDeleteEvent")
    public void deletePolicy() throws BusinessException {
        domainFacade.deletePolicy(policyToDelete, loginUser);
        policies = domainFacade.findAllDomainPolicies();
    }
    
    public boolean getConnectionIsDeletable() throws BusinessException {
        return domainFacade.connectionIsDeletable(ldapConnection.getIdentifier(), loginUser);
    }

    public boolean getPatternIsDeletable() throws BusinessException {
        return domainFacade.patternIsDeletable(domainPattern.getIdentifier(), loginUser);
    }

    public boolean getPolicyIsDeletable() throws BusinessException {
        return domainFacade.policyIsDeletable(domainPolicy.getIdentifier(), loginUser);
    }
    
    public String getConnectionIdentifier() {
        return domain.getLdapIdentifier();
    }

    public String getPatternIdentifier() {
        return domain.getPatternIdentifier();
    }
    
    public String getPolicyIdentifier() {
        return domain.getPolicyIdentifier();
    }
    
    public void onActionFromDeleteDomain(String domain) {
        this.domainToDelete = domain;
    }

    public void onActionFromDeletePattern(String pattern) {
        this.patternToDelete = pattern;
    }

    public void onActionFromDeleteConnection(String connection) {
        this.connectionToDelete = connection;
    }
    
    public void onActionFromDeletePolicy(String policy) {
        this.policyToDelete = policy;
    }

    Object onException(Throwable cause) {
        shareSessionObjects.addError(messages.get("global.exception.message"));
        logger.error(cause.getMessage());
        cause.printStackTrace();
        return this;
    }


}
