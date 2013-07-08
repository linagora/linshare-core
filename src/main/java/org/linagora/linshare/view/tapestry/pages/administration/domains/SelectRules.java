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

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.linagora.linshare.core.domain.constants.DomainAccessRuleType;
import org.linagora.linshare.core.domain.vo.AllowAllDomainVo;
import org.linagora.linshare.core.domain.vo.DenyAllDomainVo;
import org.linagora.linshare.core.domain.vo.DomainAccessRuleVo;
import org.linagora.linshare.core.domain.vo.DomainPolicyVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.DomainPolicyFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectRules {


	@SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

	private static Logger logger = LoggerFactory.getLogger(SelectRules.class);
    
	@SessionState(create=false)
	@Property
    private DomainPolicyVo domainPolicy;

    @Inject
    private DomainPolicyFacade domainPolicyFacade;
	
    @Property
    private DomainAccessRuleVo ruleVo;
    
    @Inject
    private PersistentLocale persistentLocale;
	
	@Property
	@Validate("required")
    private DomainAccessRuleType rule;
	
    @SessionState
    private UserVo loginUser;
	
    @InjectPage
    private org.linagora.linshare.view.tapestry.pages.administration.domains.SelectDomain selectDomainpage;
    
    @InjectPage
    private org.linagora.linshare.view.tapestry.pages.administration.domains.ManageDomainPolicy manageDomainpage;
    
    @Property
    private boolean onTop;
    
    @Inject
    private Messages messages;

	
    
	@SetupRender
	public void init() {
		if (domainPolicy == null) {
			domainPolicy = new DomainPolicyVo();
		}
	}
	
    public Object onActionFromCancel() {
        return ManageDomainPolicy.class;
    }

    public Object onSuccess() throws BusinessException {  
    		if(rule.toInt() == 2 || rule.toInt() == 3 ){
    			selectDomainpage.set(rule);
    			selectDomainpage.setChoice(onTop);
    			return selectDomainpage;
    			}
    		else{ 
    	    		if(rule.toInt() ==0){	
    	    			ruleVo=new AllowAllDomainVo();
    	    		} else {
    	    			ruleVo=new DenyAllDomainVo();
    	    			}
    	    		if(onTop == true)
    	    		{
    	    			domainPolicyFacade.insertOnTop(domainPolicy, ruleVo);
    	    		}else{
    	    				domainPolicy.getDomainAccessPolicy().addRule(ruleVo);
    	    		}
    	    			domainPolicyFacade.updateDomainPolicy(loginUser,domainPolicy);
    	    			domainPolicy = domainPolicyFacade.retrieveDomainPolicy(domainPolicy.getIdentifier());
    	    			for(DomainAccessRuleVo current : domainPolicy.getDomainAccessPolicy().getRules()){
    	    				logger.debug("rule ID: "+current.getPersistenceId());
    	    				current.setDescription(current.toDisplay(persistentLocale));
    	    			}
    		}
        	return manageDomainpage;
    }
    
    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }

    
}
