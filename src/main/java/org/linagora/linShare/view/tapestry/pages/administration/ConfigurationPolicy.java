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
package org.linagora.linShare.view.tapestry.pages.administration;

import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.AbstractDomainFacade;
import org.linagora.linShare.core.Facade.FunctionalityFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.vo.PolicyVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigurationPolicy {
	
	private static Logger logger = LoggerFactory.getLogger(ConfigurationPolicy.class);

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

	@Inject
	private Messages messages;
    @Inject
    private AbstractDomainFacade abstractDomainFacade;
    
    @Inject
    private FunctionalityFacade functionalityFacade;
    
    @Inject
    private UserFacade userFacade;
    
    @SessionState
    private UserVo loginUser;
    
    @Persist
	@Property
	private String selectedDomain;
	
	@Persist
	@Property
	private List<String> domains;
	
	@Property
	@Persist
	private boolean superadmin;
	
	@Property
	@Persist
	private boolean admin;

	@Property
	private boolean noDomain;
	
	
	@InjectComponent
	private Form policyForm;
	
	@Property
	@Persist
	private List<PolicyVo> configurationPolicies;
	

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SetupRender
    public void init() throws BusinessException {
    	superadmin = loginUser.isSuperAdmin();
    	admin = loginUser.isAdministrator();
    }   
    

	public Object onActivate(String identifier) throws BusinessException {
		logger.debug("domainIdentifier:" + identifier);
		selectedDomain = identifier;

		domains = abstractDomainFacade.getAllDomainIdentifiers(loginUser);
		if(!domains.contains(selectedDomain)) {
			shareSessionObjects.addError(messages.get("pages.error.badAuth.message"));
			return org.linagora.linShare.view.tapestry.pages.administration.Index.class;
    	}
		
		configurationPolicies = functionalityFacade.getAllConfigurationPolicy(identifier);
		Collections.sort(configurationPolicies);
		return null;
	}
    
	public Object onSuccessFromPolicyForm() throws BusinessException {
		logger.debug("onSuccessFromPolicyForm");
		functionalityFacade.updateConfigurationPolicies(loginUser, configurationPolicies);
		return org.linagora.linShare.view.tapestry.pages.administration.Index.class;
	}
	
    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
    
}
