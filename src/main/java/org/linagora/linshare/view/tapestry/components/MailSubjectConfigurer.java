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
import org.linagora.linshare.core.domain.entities.MailSubject;
import org.slf4j.Logger;

public class MailSubjectConfigurer {
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private Set<MailSubject> mailSubjects;
    
    @Property
    private MailSubject mailSubject;
    
    @Property
    @Persist
    private MailSubject mailSubjectToEdit;
    
    @Inject
    private Logger logger;
    
    @Property
    @Persist
    private String value;

    @SetupRender
    void setupRender() {
    }
    
    public boolean getInSubjectModificationState() {
    	if (mailSubjectToEdit == null) {
    		return false;
    	}
    	return mailSubjectToEdit.toString().equals(mailSubject.toString());
    }
    
    void onActionFromSwitchToSubjectModificationState(String rowValue) {
    	for (MailSubject mailSubjectObj : mailSubjects) {
			if (mailSubjectObj.toString().equals(rowValue)) {
				mailSubjectToEdit = mailSubjectObj;
	        	break;
			}
		}
    	
    }
    
    void onActionFromSwitchFromSubjectModificationState() {
    	mailSubjectToEdit = null;
    }
    
    void onSuccess() {
    	mailSubjectToEdit = null;
    }
}