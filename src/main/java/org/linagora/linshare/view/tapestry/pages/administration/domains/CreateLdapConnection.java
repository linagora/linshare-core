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

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateLdapConnection {
	
	private static Logger logger = LoggerFactory.getLogger(CreateLdapConnection.class);
	
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
        logger.debug("ldapConnectionIdentifier:" + identifier);
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
			logger.error("Can not create or update ldap connection : " + e.getMessage());
			logger.debug(e.toString());
		}
		inModify = false;
		ldapConn = null;
		return Index.class;
	}

}
