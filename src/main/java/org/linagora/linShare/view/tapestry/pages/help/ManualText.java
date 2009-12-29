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
package org.linagora.linShare.view.tapestry.pages.help;

import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.components.Help;
import org.linagora.linShare.view.tapestry.objects.HelpsASO;
import org.slf4j.Logger;

/**
 * Help pages: Text version
 * @author dcarella
 *
 */
public class ManualText {
	@Inject 
	private Logger logger;

    @ApplicationState
    @Property
    private ShareSessionObjects shareSessionObjects;
	
	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */

	@Inject
	private Messages messages;

	@InjectComponent
	private Help help;

	@Persist
	private String uuid;

	@ApplicationState
	private HelpsASO helpsASO;
	
	@SuppressWarnings("unused")
	@Property
	private String roleLabel;
	
	
	@Inject
	private Messages message;
	
	@SetupRender
	public void init(){
		String role=helpsASO.getHelpVO(uuid).getRole();
		roleLabel=message.get("pages.help.manual."+role+".title");
		help.setUuid(uuid);
		help.setVideo(false);
	}

	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid=uuid;
	}

	public void setIdSection(String idSection) {
		this.uuid = idSection;
	}

    Object onException(Throwable cause) {
    	shareSessionObjects.addMessage(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
}
