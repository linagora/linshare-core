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
package org.linagora.linshare.view.tapestry.pages.administration.domains.subdomains;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.GuestDomainVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.DomainPolicyFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.models.impl.SimpleSelectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateGuestDomain {

    private static Logger logger = LoggerFactory.getLogger(CreateGuestDomain.class);

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    @SessionState
    private UserVo loginUser;

    @Persist
    @Property
    private AbstractDomainVo domain;

    @Inject
    private AbstractDomainFacade domainFacade;

    @Inject
    private DomainPolicyFacade domainPolicyFacade;

    @Persist
    @Property
    private List<String> patterns;

    @Persist
    @Property
    private List<String> connections;


    @Persist
    @Property
    private List<String> policies;

    @Persist
    @Property
    private boolean inModify;


    @SuppressWarnings("unused")
    @Persist
    @Property
    private SimpleSelectModel<String> model;

    @Property
    private List<String> locales;

    @Inject
    private Messages messages;

    @Inject
    private SymbolSource symbolSource;

    @Property
    @Persist
    private String currentTopDomainIdentifier;

    public void onActivate(Object[] parameters) throws BusinessException {
        logger.debug("onActivate");

        if(currentTopDomainIdentifier==null) {
            String domainIdentifier = null;
            int cpt=0;
            for (Object string : parameters) {
                cpt++;
                if(cpt == 1) {
                    currentTopDomainIdentifier=(String) string;
                } else if (cpt == 2) {
                    domainIdentifier=(String)string;
                }
            }
            logger.debug("currentTopDomainIdentifier:" + currentTopDomainIdentifier);
            logger.debug("domainIdentifier:" + domainIdentifier);

            if (domainIdentifier != null) {
                logger.debug("update ?");
                inModify = true;
                domain = domainFacade.retrieveDomain(domainIdentifier);
            } else {
                logger.debug("create ?");
                inModify = false;
                domain = null;
            }
        }
    }

    @SetupRender
    public void init() throws BusinessException {
        if (domain == null) {
            domain = new GuestDomainVo();
            ((GuestDomainVo)domain).setParentDomainIdentifier(currentTopDomainIdentifier);
            logger.debug("GuestDomain creation with parent : " + currentTopDomainIdentifier);
            logger.debug("domainVo class:" + domain.getClass().toString());
            logger.debug("domainVo :" + domain.toString());
        }

        patterns = domainFacade.findAllUserDomainPatternIdentifiers();
        connections = domainFacade.findAllLDAPConnectionIdentifiers();
        policies = domainPolicyFacade.findAllDomainPoliciesIdentifiers();

        if(null==locales || locales.size()==0){
            if(null!=symbolSource.valueForSymbol(SymbolConstants.SUPPORTED_LOCALES)){
                String stringLocales=symbolSource.valueForSymbol(SymbolConstants.SUPPORTED_LOCALES);
                String[]listLocales=stringLocales.split(",");
                locales=this.getSupportedLocales(listLocales);		
            }
        }

        model = new SimpleSelectModel<String>(locales, messages, "pages.administration.userconfig.select");
    }

    private List<String> getSupportedLocales(String[]locales){
        ArrayList<String> newLocales=new ArrayList<String>();
        for(String currentLocale: locales){
            newLocales.add(currentLocale);
        }
        return newLocales;
    }

    public Object onActionFromCancel() {
        inModify = false;
        domain = null;
        currentTopDomainIdentifier=null;
        return Index.class;
    }

    public Object onSubmit() throws BusinessException  {
        logger.debug("domainVo class:" + domain.getClass().toString());
        logger.debug("domainVo :" + domain.toString());
        try {
            if (inModify) {
                domainFacade.updateDomain(loginUser, domain);
            } else {
                domainFacade.createDomain(loginUser, domain);
            }
            inModify = false;
            domain = null;
            currentTopDomainIdentifier=null;
        } catch (BusinessException e) {
            if(e.getErrorCode().equals(BusinessErrorCode.DOMAIN_ID_ALREADY_EXISTS)) {
                shareSessionObjects.addError(messages.get("error.code.domain.alreadyExist"));
                return this;
            } else {
                throw e;
            }
        }


        return Index.class;
    }

    Object onException(Throwable cause) {
        shareSessionObjects.addError(messages.get("global.exception.message"));
        logger.error(cause.getMessage());
        cause.printStackTrace();
        return this;
    }

}
