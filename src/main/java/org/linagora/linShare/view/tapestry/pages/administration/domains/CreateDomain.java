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
package org.linagora.linShare.view.tapestry.pages.administration.domains;

import java.util.List;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.DomainFacade;
import org.linagora.linShare.core.domain.vo.DomainPatternVo;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.LDAPConnectionVo;
import org.linagora.linShare.core.exception.BusinessException;

public class CreateDomain {
	
	@Property
	@Persist
	private DomainVo domain;
	
	@Inject
	private DomainFacade domainFacade;
	
	@Persist
	@Property
	private List<DomainPatternVo> patterns;
	
	@Persist
	@Property
	private List<LDAPConnectionVo> connections;
	
	@Persist
	@Property
	private boolean inModify;
	
	public void onActivate(String identifier) throws BusinessException {
		if (identifier != null) {
			inModify = true;
			domain = domainFacade.retrieveDomain(identifier);
		} else {
			inModify = false;
			domain = null;
		}
		
	}
	
	@SetupRender
	public void init() throws BusinessException {
		if (domain == null) {
			domain = new DomainVo();
		}
		patterns = domainFacade.findAllDomainPatterns();
    	connections = domainFacade.findAllLDAPConnections();
	}
	
	public Object onActionFromCancel() {
		inModify = false;
		domain = null;
		return Index.class;
	}
	
	public Object onSubmit() {
		try {
			if (inModify) {
				domainFacade.updateDomain(domain);
			} else {
				domainFacade.createDomain(domain);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inModify = false;
		domain = null;
		return Index.class;
	}
    
    public ValueEncoder<DomainPatternVo> getPatternValueEncoder() {
    	return new ValueEncoder<DomainPatternVo>() {
    		public String toClient(DomainPatternVo value) {
    			return value.getIdentifier();
    		}
    		public DomainPatternVo toValue(String clientValue) {
    			for (DomainPatternVo domainPattern : patterns) {
    	    		if (domainPattern.getIdentifier().equals(clientValue)) {
    	    			return domainPattern;
    	    		}
    			}
    			return null;
    		}
		};
    }
    
    public ValueEncoder<LDAPConnectionVo> getConnectionValueEncoder() {
    	return new ValueEncoder<LDAPConnectionVo>() {
    		public String toClient(LDAPConnectionVo value) {
    			return value.getIdentifier();
    		}
    		public LDAPConnectionVo toValue(String clientValue) {
    			for (LDAPConnectionVo conn : connections) {
    	    		if (conn.getIdentifier().equals(clientValue)) {
    	    			return conn;
    	    		}
    			}
    			return null;
    		}
		};
    }

}
