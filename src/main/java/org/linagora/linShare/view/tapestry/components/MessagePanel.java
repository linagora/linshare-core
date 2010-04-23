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

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;

public class MessagePanel {
    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;
	@Property
	private String currentMessage;
    
    public List<String> getMessagesInfo() {
    	List<String> messages = shareSessionObjects.getMessages();
    	shareSessionObjects.setMessages(new ArrayList<String>());
    	return messages;
    }
    
    public List<String> getMessagesError() {
    	List<String> messages = shareSessionObjects.getErrors();
    	shareSessionObjects.setErrors(new ArrayList<String>());
    	return messages;
    }
    
    public List<String> getMessagesWarning() {
    	List<String> messages = shareSessionObjects.getWarnings();
    	shareSessionObjects.setWarnings(new ArrayList<String>());
    	return messages;
    }
    
    @SetupRender
    public void setupRender() {
    	if (shareSessionObjects==null) {
    		shareSessionObjects = new ShareSessionObjects();
    	}
    }
}
