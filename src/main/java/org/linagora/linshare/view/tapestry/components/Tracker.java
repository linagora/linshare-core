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

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.view.tapestry.beans.TrackerObject;



/**
 * The tracker is a component which permits to display a list of page link in a page.
 * This component is useful when the application required a trace: 
 * For example:
 * Home > Configuration > Firewall .
 * 
 * The labels are based on a convention.
 * For set a label it has to add new key in your app.properties.
 * With this format:
 * tracker.pages.mydir.mypages=myLabel
 * For example the page is test/Index, the key will be:
 * tracker.pages.test.Index=Test page . 
 *  
 * Limitation: this component works only with pageLink, not external link. 
 * @author ngapaillard
 *
 */
public class Tracker {

	private final static String SEPARATOR="->";


	@Inject
	private ComponentResources componentResources;

	@Inject
	private Messages messages;

	/**
	 * A string separated by "-&gt;" for each pagelinks will be displayed.
	 */
	@Parameter(required=false,defaultPrefix=BindingConstants.LITERAL)
	private String pagelinks;

	/**
	 * The separator used to display links.
	 */
	@SuppressWarnings("unused")
	@Parameter(required=false,value=">",defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String separator;
	
	/**
	 * The label displayed before links.
	 */
	@SuppressWarnings("unused")
	@Parameter(required=false,value="",defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String trackerTitle;
	
	
	@Property
	private String pageName;

	@Property
	private List<Object> listTracker;
	
	private final static String PREFIX="tracker.pages.";


	
	@SuppressWarnings("unused")
	@Property
	private TrackerObject object;
	
	@SetupRender
	public void initTracker(){

		listTracker=new ArrayList<Object>();
		String[]pages;
		if(null!=pagelinks){
			pages=pagelinks.trim().split(SEPARATOR);
			
		}else{
			pages=new String[0];
		}
		this.pageName=componentResources.getPage().getComponentResources().getCompleteId();
		
		initMessages(pages);
		
		
	
	}


	private void initMessages(String pages[]){
		for(String currentPage:pages){
			listTracker.add(new TrackerObject(currentPage, messages.get(PREFIX+currentPage.replaceAll("/", ".")),false));
		}
		listTracker.add(new TrackerObject(this.pageName,messages.get(PREFIX+this.pageName.replaceAll("/", ".")),true));
	}
	
	
	
	
}
