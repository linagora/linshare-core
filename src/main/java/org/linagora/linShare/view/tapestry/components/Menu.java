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
import org.apache.tapestry5.services.Response;
import org.linagora.linShare.view.tapestry.beans.MenuEntry;



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

	/**
	 * The delimitor which uses for separate images,labels and links.
	 */
	private final static String DELIMITOR=";";
	
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
	 * The links in the menu.
	 * this parameter is required.
	 */
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	private String links;
	
	/**
	 * The targets of the link.
	 * 
	 */
	@Parameter(required=false,defaultPrefix=BindingConstants.LITERAL)
	private String targets;
	
	/**
	 * The highlighting directory of the link.
	 * 
	 */
	@Parameter(required=false,defaultPrefix=BindingConstants.LITERAL)
	private String highlights;
	
	/**
	 * The labels displayed in the menu.
	 * this parameter is not required.
	 * But if there are only links and no labels and images 
	 * only the title will be displayed.  
	 */
	@Parameter(required=false,value="",defaultPrefix=BindingConstants.LITERAL)
	private String labels;
	
	/**
	 * The images paths displayed in the menu.
	 * this parameter is not required.
	 * But if there are only links and no labels and images 
	 * only the title will be displayed.  
	 */
	@Parameter(required=false,value="",defaultPrefix=BindingConstants.LITERAL)
	private String images;
	
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
	
	@SuppressWarnings("unused")
	@Inject
	private Response response;

	
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

	@SuppressWarnings("unused")
	@Property
	private boolean highlight=false;
	
	private String[] listLinks;
	
	private String[] listImages;
	
	private String[] listLabels;
	
	private String[] listTargets;
	
	private String[] listHighlights;
	
	@Property
	private ArrayList<MenuEntry> listMenuEntry;
	
	@SetupRender
	public void initList(){	
	/*	if (currentHighlight == null) {
			currentHighlight = "";
		}
		*/
		listMenuEntry=new ArrayList<MenuEntry>();
		
		listMenuEntry.clear();
		
		listLinks=links.trim().split(DELIMITOR);

		if(null!=images){ 

			listImages=images.trim().split(DELIMITOR);
			if(null!=listImages && listImages.length>0){
				image=true;
			}
		}
		if(null!=labels){


			listLabels=labels.trim().split(DELIMITOR);
			if(null!=listLabels && listLabels.length>0){
				label=true;
			}
		}
		if(null!=targets){


			listTargets=targets.trim().split(DELIMITOR);
			if(null!=listTargets && listTargets.length>0){
				target=true;
			}
		}
		
		if((currentHighlight != null) && (null!=highlights)){ // no highlight on the root
			listHighlights=highlights.trim().split(DELIMITOR);
			if(null!=listHighlights && listHighlights.length>0){
				highlight=true;
			}
		}
		
		initListMenuEntry(listLinks, listLabels, listImages, listTargets, listHighlights);
	}

	public String getClientId() {
		
		return renderSupport.allocateClientId(componentResources);
	}
	
	public String getStyleValue(){
		
		if ((highlight)&&(currentHighlight.equals(object.getHighlight()))) {
			return "highlight";
		}
		
		return (!alternateStyle || object.getId()%2==0)?"0":"1";
	}


	public boolean isImageDisplay(){
		return null!=object.getImage() && image;
	}

	private void initListMenuEntry(String[]links,String[]labels,String[]images,String[]targets,String[] highlights){

		
		for(int i=0;i<links.length;i++){
			String label=null;
			String image=null;
			String target=null;
			String highlight=null;
			if(null!=labels && i<labels.length){
				label=labels[i];
			}
			if(null!=images && i<images.length){
				image=images[i];
			}
			if(null!=targets && i<targets.length){
				target=targets[i];
			}
			if(null!=highlights && i<highlights.length){
				highlight=highlights[i];
			}
			String url=response.encodeURL(links[i]);
			
			
			listMenuEntry.add(new MenuEntry(url,label,image,target,highlight,i));
		}
		
	}

	



}
