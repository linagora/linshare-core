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
package org.linagora.linShare.view.tapestry.components;

import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.domain.constants.Policies;
import org.linagora.linShare.core.domain.vo.PolicyVo;
import org.slf4j.Logger;

@IncludeJavaScriptLibrary(value = {"FunctionalityPolicyConfigurer.js"})
public class FunctionalityPolicyConfigurer {
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private List<PolicyVo> policies;
    
    @Property
    private PolicyVo policyRow;
     
    @Inject
	private Messages messages;
    
    @Inject
    private Logger logger;
    
//    @Property
//    @Persist
//    private String identifier;
//    
//    @Property
//    @Persist
//    private boolean status;
//    
    @SetupRender
    void setupRender() {
    }
    
    public Policies getAllowedPolicy() { 
    	return Policies.ALLOWED; 
	}
    
    public Policies getForbiddenPolicy() { 
    	return Policies.FORBIDDEN; 
	}
    
    public Policies getMandatoryPolicy() { 
    	return Policies.MANDATORY; 
	}
    
    public boolean getReadOnlyStatus() {
    	if(policyRow.getPolicy().equals(Policies.ALLOWED)) {
    		return false; 
    	}
    	return true;
    }
    

    public String getFunctionalityIdentifierLabel() {
    	return messages.get("pages.administration.functionality." + policyRow.getFunctionalityIdentifier());
    }
    
    
    @Property
	private PolicyEncoder policyEncoder;

    private List<PolicyVo> policyEdited;

    
    private class PolicyEncoder implements ValueEncoder<PolicyVo> {

		@Override
		public String toClient(PolicyVo value) {
			return value.getFunctionalityIdentifier();
		}

		@Override
		public PolicyVo toValue(String clientValue) {
			PolicyVo p = findPolicyVo(clientValue);
			policyEdited.add(p);
			return p;
		}

		private PolicyVo findPolicyVo(String id) {
			for (PolicyVo p : policies) {
				if(p.getFunctionalityIdentifier().equals(id)) {
					return p;
				}
			}
			return null;
		}
	};
	
}
