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
package org.linagora.linshare.view.tapestry.pages.administration.domains;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;

public class CreateLdapConnection {
	
	@SessionState
    private UserVo loginUser;
    
	@Property
	@Persist
	private LDAPConnectionVo ldapConn;
	
	@Inject
	private AbstractDomainFacade domainFacade;
	
	@Persist
	@Property
	private boolean inModify;
	
	public void onActivate(String identifier) throws BusinessException {
		if (identifier != null) {
			inModify = true;
			ldapConn = domainFacade.retrieveLDAPConnection(identifier);
		} else {
			inModify = false;
			ldapConn = null;
		}
		
	}
	
	@SetupRender
	public void init() {
		if (ldapConn == null) {
			ldapConn = new LDAPConnectionVo();
		}
	}
	
	public Object onActionFromCancel() {
		inModify = false;
		ldapConn = null;
		return Index.class;
	}
	
	public Object onSubmit() {
		ldapConn.setSecurityAuth("simple"); //TODO support another auth in the future
		try {
			if (inModify) {
				domainFacade.updateLDAPConnection(loginUser, ldapConn);
			} else {
				domainFacade.createLDAPConnection(loginUser, ldapConn);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inModify = false;
		ldapConn = null;
		return Index.class;
	}

}
