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
import org.linagora.linshare.core.domain.vo.DomainPatternVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateDomainPattern {

	private static Logger logger = LoggerFactory.getLogger(CreateDomainPattern.class);
	
	@Property
	@Persist
	private DomainPatternVo domainPattern;

	@Inject
	private AbstractDomainFacade domainFacade;

	@SessionState
    private UserVo loginUser;

	@Persist
	@Property
	private boolean inModify;
	
    private boolean resetFields = true;

	public void onActivate(String identifier) throws BusinessException {
        logger.debug("domainPatternIdentifier:" + identifier);
		if (identifier != null) {
			inModify = true;
			domainPattern = domainFacade.retrieveDomainPattern(identifier);
		} else {
			inModify = false;
			domainPattern = null;
		}
    }
	
    // Workaround in order to reset fields. See Bug #444
    public void onActivate(){
    	if (resetFields) {
    		logger.debug("Reset the fields");
            inModify = false;
            domainPattern = null;	
    	}
    }

	@SetupRender
	public void init() {
		if (domainPattern == null) {
			try {
				domainPattern = domainFacade.findAllSystemDomainPatterns().get(0);
				domainPattern.setIdentifier(null);
				domainPattern.setSystem(false);
				domainPattern.setPatternDescription("");
			} catch (BusinessException e) {
				domainPattern = null;
				logger.debug(e.toString());
			}
		}
	}

	public Object onActionFromCancel() {
		inModify = false;
		domainPattern = null;
		return Index.class;
	}

	public Object onSubmit() {
		try {
			if (inModify) {
				domainFacade.updateDomainPattern(loginUser, domainPattern);
			} else {
				domainFacade.createDomainPattern(loginUser, domainPattern);
			}
		} catch (BusinessException e) {
			logger.error("Can not create or update domain pattern : " + e.getMessage());
			logger.debug(e.toString());
		}
		inModify = false;
		domainPattern = null;
		return Index.class;
	}

}
