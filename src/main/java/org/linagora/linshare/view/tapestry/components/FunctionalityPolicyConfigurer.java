/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.view.tapestry.components;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.vo.PolicyVo;
import org.slf4j.Logger;

@Import(library = {"FunctionalityPolicyConfigurer.js"})
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
		for (PolicyVo policy : policies) {
			policy.setName(messages.get("pages.administration.functionality."+ policy.getFunctionalityIdentifier().toLowerCase()));
		}
		Collections.sort(policies, new Comparator<PolicyVo>() {
			@Override
			public int compare(PolicyVo o1, PolicyVo o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
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
