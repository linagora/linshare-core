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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.domain.vo.DomainAccessRuleVo;
import org.linagora.linshare.core.domain.vo.DomainPolicyVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.DomainPolicyFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Import(library = {"../../../components/jquery/jquery-1.7.2.js","../../../components/jquery/jquery.ui.core.js","../../../components/jquery/jquery.ui.widget.min.js","../../../components/jquery/jquery.ui.mouse.min.js","../../../components/jquery/jquery.ui.sortable.min.js","ManageDomainPolicy.js"}, stylesheet={"../../../components/jquery/jquery-ui-1.8.21.custom.css","ManageDomainPolicy.css"})
public class ManageDomainPolicy {
	
	private static Logger logger = LoggerFactory.getLogger(ManageDomainPolicy.class);
	
	@SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;
	
    @Inject
    private DomainPolicyFacade domainPolicyFacade;
	
	@SessionState(create=false)
	@Property
	private DomainPolicyVo domainPolicy;

	@SessionState
    private UserVo loginUser;
	
    @Property
    private String _rules;
	
    @Property
    private DomainAccessRuleVo _rule;
    
    @Property
    private long _ruleIdentifier;
    
    @Property 
    private int indexRule;

    @Property 
    private boolean cancel;
    
    @Inject
    private Messages messages;
    
	@Property
	private String tabPos;
    
	
	
	
	public List<DomainAccessRuleVo> getRulesList(){
		
		List<DomainAccessRuleVo> rulesVo = new ArrayList<DomainAccessRuleVo>();
		for(DomainAccessRule rules : domainPolicy.getDomainAccessPolicy().getRules())
		{
			rulesVo.add(new DomainAccessRuleVo(rules));
		}
		return rulesVo;
	}
	
	
	public void onActivate(String identifier) throws BusinessException {
		logger.debug("domainPolicyIdentifier:" + identifier);
		domainPolicy = domainPolicyFacade.retrieveDomainPolicy(identifier);
	}

   public Object onRemove(long _ruleIdentifier) {
    	Iterator<DomainAccessRule> it =domainPolicy.getDomainAccessPolicy().getRules().iterator();
    	DomainAccessRuleVo deleteRule=new DomainAccessRuleVo();
    	boolean delete=false;
    	while(it.hasNext()){
    		DomainAccessRule rule=it.next();
    		if(rule.getPersistenceId() == _ruleIdentifier && delete == false)
    		{
    			deleteRule.setPersistenceId(rule.getPersistenceId());
    			it.remove();
    	    	try {
    				domainPolicyFacade.deleteDomainAccessRule(deleteRule,domainPolicy);
    			} catch (BusinessException e) {
    				e.printStackTrace();
    			}
    			delete=true;
    		}
    	}
    	return null;
    }
	
    void onSelectedFromCancel() { cancel = true; }
	
	public Object onSuccess() throws BusinessException{

		if(cancel==true){
		 domainPolicy=null;
		}
		else{
			
			if(tabPos != null){
				
				String[] domainIdentifiers = tabPos.split(";");
				List<DomainAccessRuleVo> rulesVo = new ArrayList<DomainAccessRuleVo>();
				DomainAccessRuleVo ruleVo; 
				
				for (String domainIdentifier : domainIdentifiers) {
					if(!domainIdentifier.isEmpty()){
						
						ruleVo =domainPolicyFacade.retrieveDomainAccessRule(Long.parseLong(domainIdentifier));
						rulesVo.add(ruleVo);
					}
				}
				domainPolicy.getDomainAccessPolicy().getRules().clear();
				domainPolicy.getDomainAccessPolicy().setRules(domainPolicyFacade.sortDomainAccessRules(rulesVo));
			}
			
			try {
				domainPolicyFacade.updateDomainPolicy(loginUser,domainPolicy);
			} catch (BusinessException e) {
				e.printStackTrace();
			}
				domainPolicy=null;	
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
