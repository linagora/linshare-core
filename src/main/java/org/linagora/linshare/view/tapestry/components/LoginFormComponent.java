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
package org.linagora.linshare.view.tapestry.components;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Request;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Import(library = {"LoginFormComponent.js"})
public class LoginFormComponent {

	private static final Logger logger = LoggerFactory.getLogger(LoginFormComponent.class);
	
	/* ***********************************************************
     *                         Parameters
     ************************************************************ */

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @Inject
    @Property
    private Request request;

	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
	@SessionState
	private UserVo userDetailsVo;

	private boolean userDetailsVoExists;

    @Property
    private String login;
    
    @Property
    private String password;

    @Property
    @Persist
    private String domain;
    
	@Inject @Symbol("sso.button.hide")
	@Property
	private boolean ssoButtonHide;
    
	@Inject @Symbol("linshare.domain.visible")
	@Property
	private boolean domainVisible;

	@Inject @Symbol("linshare.display.licenceTerm")
	@Property
	private boolean linshareLicenceTerm;
	
	@Inject
	private HttpServletRequest httpServletRequest;
	
	@Inject
	private AbstractDomainFacade domainFacade;
	
	@Persist
	@Property
	private List<String> availableDomains;

    @Property
    private String availableDomain;
    

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
	
	@SetupRender
	public void init() throws BusinessException {
		if (!isUserLoggedIn() && domainVisible) {
			availableDomains = domainFacade.getAllDomainIdentifiers();
		}
	}
	
	public boolean isUserLoggedIn() {
		return userDetailsVoExists;
	}

	public String getUserName() throws BusinessException {
		return userDetailsVo.getFirstName() + " " + userDetailsVo.getLastName();
	}

    public boolean isBadCredentials() {
    	String param = request.getParameter("login_error");
        return (param != null && param.equals("1"));
    }

    public boolean isBadDomain() {
    	String param = request.getParameter("login_error");
        return (param != null && param.equals("2"));
    }
    
    public String getSpringLogoutLink() {
    	return (httpServletRequest.getContextPath()+"/j_spring_security_logout");
    }

}
