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
import org.linagora.linshare.core.Facade.AbstractDomainFacade;
import org.linagora.linshare.core.Facade.DomainPolicyFacade;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.SubDomainVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.beans.SelectableRole;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.models.impl.SimpleSelectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateDomain {

	private static Logger logger = LoggerFactory.getLogger(CreateDomain.class);

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
	
	@Property
    private SelectableRole role;
	
	
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
			domain = new SubDomainVo();
			((SubDomainVo)domain).setParentDomainIdentifier(currentTopDomainIdentifier);
			logger.debug("SubDomain creation with parent : " + currentTopDomainIdentifier);
			logger.debug("domainVo class:" + domain.getClass().toString());
			logger.debug("domainVo :" + domain.toString());
		}
			
		patterns = domainFacade.findAllDomainPatternIdentifiers();
    	connections = domainFacade.findAllLDAPConnectionIdentifiers();
    	policies = domainPolicyFacade.getAllDomainPolicyIdentifiers();
    	
    	if(null==locales || locales.size()==0){
			if(null!=symbolSource.valueForSymbol(SymbolConstants.SUPPORTED_LOCALES)){
				String stringLocales=symbolSource.valueForSymbol(SymbolConstants.SUPPORTED_LOCALES);
				String[]listLocales=stringLocales.split(",");
				locales=this.getSupportedLocales(listLocales);		
			}
		}
		
		model = new SimpleSelectModel<String>(locales, messages, "pages.administration.userconfig.select");
		role = SelectableRole.fromRole(domain.getDefaultRole());
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
			domain.setDefaultRole(SelectableRole.fromSelectableRole(role));
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
