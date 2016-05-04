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


import java.util.UUID;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Retain;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.view.tapestry.objects.JSONRaw;

/**
 * Extends the Window to allow for easy effects on the object
 * The main feature is to be able to set the javascript id
 * 
 * this component is better than WindowWithEffects
 * if you want to use two popup in the same page.
 * you need different id in this case for the zone and for the window
 *  
 * 
 * 
 * @author ncharles
 *
 */
public class WindowWithEffectsComponent extends Window {
	

	/* ***********************************************************
     *                         Parameters
     ************************************************************ */

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */

	// the javascript id of the component
	@Retain
	private String JSONId; 
	
	
    /**
     * The id used to generate a page-unique client-side identifier for the component. If a component renders multiple
     * times, a suffix will be appended to the to id to ensure uniqueness. The uniqued value may be accessed via the
     * {@link #getClientId() clientId property}.
     */
    
	@Parameter(value = "prop:componentResources.id", defaultPrefix = BindingConstants.LITERAL)
    private String id; 
	//id comes from the name of the component (can be explicitly set)
    //private WindowWithEffects warningWindow;    here for example warningWindow
	
    @Parameter(value="true", defaultPrefix=BindingConstants.PROP)
    private boolean closable;
	
    @Retain
    private String _assignedClientId;
    

    @Environmental
    private JavaScriptSupport _pageRenderSupport;

	
	
    /* ***********************************************************
     *                       Phase processing
     ************************************************************ */
     
    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
	
	    
	public WindowWithEffectsComponent() {
		super();
		// create a unique javascript id
		this.JSONId="window_" + UUID.randomUUID().toString();
		
	}
	
	@SetupRender
    void setupRender()
    {
        // By default, use the component id as the (base) client id. If the clientid
        // parameter is bound, then that is the value to use.
        // Often, these controlName and _clientId will end up as the same value. There are many
        // exceptions, including a form that renders inside a loop, or a form inside a component
        // that is used multiple times.


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
	
	@Override
	public String getClientId() {
		
		if(_assignedClientId==null){
				//_assignedClientId = _clientId;
		        _assignedClientId = _pageRenderSupport.allocateClientId(id);
		}
		
		return _assignedClientId;
	}
	
	
    public String getJavascriptOpenPopup(){
    	return getClientId()+ ".showCenter(true)";
    }
	
}
