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


import java.util.UUID;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Retain;
import org.apache.tapestry5.json.JSONObject;
import org.linagora.linshare.view.tapestry.objects.JSONRaw;

/**
 * Extends the Window to allow for easy effects on the object
 * The main feature is to be able to set the javascript id
 * @author ncharles
 *
 */
public class WindowWithEffects extends Window {
	

	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
    @Parameter(value="true", defaultPrefix=BindingConstants.PROP)
    private boolean closable;

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */

	// the javascript id of the component
	@Retain
	private String JSONId; 
	
	
    /* ***********************************************************
     *                       Phase processing
     ************************************************************ */
     
    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */

	
	
	
	    
	public WindowWithEffects() {
		super();
		// create a unique javascript id

		this.JSONId="window_" + UUID.randomUUID().toString();
	}
	
	
	protected void configure(JSONObject options) {
		options.put("id", getJSONId());
		options.put("draggable", true);
		options.put("minimizable", false);
		options.put("maximizable", false);
		options.put("showEffect", new JSONRaw("Element.show"));
		options.put("hideEffect", new JSONRaw("Element.hide"));
		options.put("destroyOnClose", false);
        options.put("closable", closable);
	}


	
    /* ***********************************************************
     *                      Getters & Setters
     ************************************************************ */ 
	
	public String getJSONId() {
		return JSONId;
	}


	
}
