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

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Retain;
import org.apache.tapestry5.corelib.components.Zone;

/**
 * display a warning in a popup
 * the message is given as a parameter 
 * <pre>
 * 	<t:WarningDisplayer t:id="..." t:warningMessage="${message:components.listDocument.warningDisplayer.message}"/>
 * </pre>
 */
public class WarningDisplayer {

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    
	
	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String warningMessage;
	
	
	@SuppressWarnings("unused")
    @Component(parameters = {"style=bluelighting", "show=false", "width=350", "height=150"})
    private WindowWithEffectsComponent warningWindow;
    
    @InjectComponent
    private Zone warningTemplateZone;
    
    @Retain
    private String _assignedZoneClientId;
    
    @Environmental
    private RenderSupport _pageRenderSupport;
    
    
    /**
     * set explicitly the id for the component
     */
    
    @Parameter(required=true,value = "prop:componentResources.id", defaultPrefix = BindingConstants.LITERAL)
    private String id;
    
    
    
    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    public Zone getShowWarning() {
        return warningTemplateZone;
    }
    
    public String getJSONId(){
    	return warningWindow.getJSONId();
    }
    
    /**
     * give the id of the window
     * use it to open the window
     * @return client id
     */
    public String getJavascriptOpenPopup(){
    	return warningWindow.getJavascriptOpenPopup();
    }
    
    
    
    public String getZoneClientId()
    {
    	if(_assignedZoneClientId==null)
    		_assignedZoneClientId = "zone"+_pageRenderSupport.allocateClientId( id ); 
    	
    	return _assignedZoneClientId;
    } 
    
}