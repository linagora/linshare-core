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

import java.util.Iterator;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.entities.DomainAccessRule;
import org.linagora.linshare.core.domain.vo.DomainPolicyVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Import(library = {"../../../components/jquery/jquery-1.7.2.js","../../../components/jquery/jquery.ui.core.js","../../../components/jquery/jquery.ui.widget.min.js","../../../components/jquery/jquery.ui.mouse.min.js","../../../components/jquery/jquery.ui.sortable.min.js","ManageDomainPolicy.js"}, stylesheet={"../../../components/jquery/jquery-ui-1.8.21.custom.css","ManageDomainPolicy.css"})
public class ManageDomainPolicy {
	
	private static Logger logger = LoggerFactory.getLogger(ManageDomainPolicy.class);
	
    @Inject
    private AbstractDomainFacade domainFacade;
	
	@Property
	@Persist
    private DomainPolicyVo domainPolicy;

	@SessionState
    private UserVo loginUser;
	
    @Property
    private String _rules;
	
    @Property
    private String _ruleIdentifier;
    
    @Property 
    private int indexRule;

    @Property
    private long id; 
    
    @Property 
    private boolean cancel;
    
    
    public String[] getRuleNames(){
/*
    	List<DomainAccessRule> rules = domainPolicy.getDomainAccessPolicy().getRules();
    	List<String> ruleNames=new ArrayList<String>();
    	for (DomainAccessRule rule : rules) {
    		ruleNames.add(rule.toString());
    	}
    	if(ruleNames!=null){ return ruleNames.toArray(new String[ruleNames.size()]);}
    	*/
    	return null;
    }

	public void onActivate(String identifier) throws BusinessException {
        logger.debug("domainPolicyIdentifier:" + identifier);
		domainPolicy = domainFacade.retrieveDomainPolicy(identifier);
		id=domainPolicy.getDomainAccessPolicy().getPersistenceId();
		logger.debug("domainPolicyIdentifier:" + id);
	}


    public Object onRemove(String _ruleIdentifier) {
    	Iterator<DomainAccessRule> it =domainPolicy.getDomainAccessPolicy().getRules().iterator();
    	while(it.hasNext()){
    		DomainAccessRule rule=it.next();
    		if(rule.toString().equals(_ruleIdentifier)){ it.remove();}
    	}
    	
    	try {
    	domainFacade.updateDomainAccessPolicy(loginUser, domainPolicy.getDomainAccessPolicy());
		} catch (BusinessException e) {
			logger.error("Can not update domain policy : " + e.getMessage());
			logger.debug(e.toString());}

    	return null;
    }
	
    void onSelectedFromCancel() { cancel = true; }
	
	public Object onSuccess(){
		 
		if(cancel==true){
		 domainPolicy=null;
		}
		else{
			try {
				domainFacade.updateDomainPolicy(loginUser,domainPolicy);
		} catch (BusinessException e) {
			logger.error("Can not update domain policy : " + e.getMessage());
			logger.debug(e.toString());}
		}
		return Index.class;
	}
}
