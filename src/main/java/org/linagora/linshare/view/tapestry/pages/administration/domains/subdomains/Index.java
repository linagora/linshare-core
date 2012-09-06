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

import java.util.List;

import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.GuestDomainVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
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
    
    @SessionState
    private UserVo loginUser;
	
	@Persist
	@Property
	private List<AbstractDomainVo> domains;
	
	@Property
	private AbstractDomainVo domain;
	
	@Property
	private GuestDomainVo guestDomain;
	
	@Persist
	@Property
	private AbstractDomainVo currentTopDomain;

    @Property
    @Persist(value="flash")
	private String domainToDelete;

	@SetupRender
    public void init() throws BusinessException {
    	domains = domainFacade.findAllSubDomainWithoutGuestDomain(currentTopDomain.getIdentifier());
    	guestDomain = domainFacade.findGuestDomain(currentTopDomain.getIdentifier());
	}
    
	@OnEvent(value="domainDeleteEvent")
    public void deleteDomain() throws BusinessException {
		domainFacade.deleteDomain(domainToDelete, loginUser);
		domains = domainFacade.findAllSubDomainWithoutGuestDomain(currentTopDomain.getIdentifier());
		guestDomain = domainFacade.findGuestDomain(currentTopDomain.getIdentifier());
    }
    
	public String getConnectionIdentifier() {
		return domain.getLdapIdentifier();
	}
	
	public String getPatternIdentifier() {
		return domain.getPatternIdentifier();
	}

    public void onActionFromDeleteDomain(String domain) {
        this.domainToDelete = domain;
    }
    
    public String getTopDomainName() {
    	return currentTopDomain.getLabel();
    }
    
    public boolean getCanCreateGuestDomain() throws BusinessException {
    	return domainFacade.canCreateGuestDomain(currentTopDomain.getIdentifier());
    }
    
    public boolean getGuestDomainAllowed() throws BusinessException {
    	return domainFacade.guestDomainAllowed(currentTopDomain.getIdentifier());
    }

    public void onActivate(String identifier) throws BusinessException {
		if (identifier != null) {
			currentTopDomain = domainFacade.retrieveDomain(identifier);
		}
	}

    public Object[] getContextParams()
    {
      return new String[]{currentTopDomain.getIdentifier(), domain.getIdentifier()};
    }
    
    public Object[] getGuestContextParams()
    {
    	logger.debug("guestContextParams call");
    	if(guestDomain == null) {
    		logger.debug("Guest domain is null");
    		return new String[]{currentTopDomain.getIdentifier(), null};
    	} else {
    		logger.debug("Guest domain is not null");
    		return new String[]{currentTopDomain.getIdentifier(), guestDomain.getIdentifier()};
    	}
    } 
    
    public String getGuestDomainLabel() {
    		return guestDomain.getLabel();
    }
    
    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
}
