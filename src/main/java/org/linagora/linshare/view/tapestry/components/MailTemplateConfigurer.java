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

import java.util.Set;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.entities.MailTemplate;
import org.slf4j.Logger;

public class MailTemplateConfigurer {
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private Set<MailTemplate> mailTemplates;
    
    @Property
    private MailTemplate mailTemplate;
    
    @Property
    @Persist
    private MailTemplate mailTemplateToEdit;
    
    @Inject
    private Logger logger;
    
    @Property
    @Persist
    private String valueTXT;
    
    @Property
    @Persist
    private String valueHTML;

    @SetupRender
    void setupRender() {
    }
    
    public boolean getInModificationState() {
    	if (mailTemplateToEdit == null) {
    		return false;
    	}
    	return mailTemplateToEdit.toString().equals(mailTemplate.toString());
    }
    
    void onActionFromSwitchToModificationState(String rowValue) {
    	for (MailTemplate mailTemplate : mailTemplates) {
			if (mailTemplate.toString().equals(rowValue)) {
				mailTemplateToEdit = mailTemplate;
	        	break;
			}
		}
    	
    }
    
    void onActionFromSwitchFromModificationState() {
    	mailTemplateToEdit = null;
    }
    
    void onSuccess() {
    	mailTemplateToEdit = null;
    }
}