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

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.linagora.linShare.view.tapestry.beans.MenuEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * <P>
 * Menu is a simple component class which permits to create a menu easily.
 * </P>
 * 
 * @author ngapaillard
 *
 */

@SupportsInformalParameters
public class Menu implements ClientElement{

	private static Logger logger = LoggerFactory.getLogger(Menu.class);
	
	/**
	 * Define if the style of the menu entries can change.
	 * When this value is "true" two cases. 
	 * Even line, uses these styles: 
	 * .menu-label0
	 * .menu-link0
	 * Odd line, uses these styles:
	 * .menu-label1
	 * .menu-link1  
	 * the default value is false.
	 */
	@Parameter(value="false",defaultPrefix=BindingConstants.LITERAL)
	private Boolean alternateStyle;
	
	/**
	 * The title of the menu.
	 * this parameter is required.
	 */
	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix = BindingConstants.LITERAL)
	@Property
	private String title;
	
	/**
	 * The current highlight we want to have
	 */
	@Parameter(required=false,value="",defaultPrefix=BindingConstants.LITERAL)
	private String currentHighlight;

	@Property
	private MenuEntry object;
	
	@SuppressWarnings("unused")
	@Inject
	@Property
	private Request request;
	
	@Environmental
    private RenderSupport renderSupport;
	
	@Inject
	private ComponentResources componentResources;
	
	@Property
	private boolean image=false;
	
	@SuppressWarnings("unused")
	@Property
	private boolean label=false;
	
	@SuppressWarnings("unused")
	@Property
	private boolean target=false;

	@Property
	private boolean highlight=false;
	
	@Property
	private ArrayList<MenuEntry> listMenuEntry = new ArrayList<MenuEntry>(); ;
	
	private Integer cpt = 0;
	
	@SetupRender
	public void initList(){	
	}

	public String getClientId() {
		
		return renderSupport.allocateClientId(componentResources);
	}
	
	public String getStyleValue(){
		
		if ((highlight)&& (currentHighlight!=null) &&(currentHighlight.equals(object.getHighlight()))) {
			return "highlight";
		}
		
		return (!alternateStyle || object.getId()%2==0)?"0":"1";
	}

	public boolean isImageDisplay(){
		return null!=object.getImage() && image;
	}

	public void clearMenuEntry() {
		listMenuEntry.clear();
		label=true;
		highlight=true;
		cpt=0;
	}
	public void addMenuEntry(MenuEntry menu) {
		cpt++;
		menu.setId(cpt);
		listMenuEntry.add(menu);
	}
}
