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

import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.view.tapestry.enums.Order;
import org.linagora.linshare.view.tapestry.models.SorterModel;




/**
 * The sorter is a component which is able to reorder a list.
 * It bases on SorterModel which works with the definition of comparators for each property.
 * @author ngapaillard
 *
 * @param <T> the type of the list.
 */
@Import(library = {"Sorter.js"})
public class Sorter<T> {

	private final static String SEPARATOR=",";


	/**
	 * say if the sorter is visible or not if the list is empty.
	 * by default the parameter is true.
	 */
	@Parameter(required=false,defaultPrefix=BindingConstants.LITERAL)
	private Boolean hideIfEmpty;
	
	
	/**
	 * The event to trigger with the list sorted.
	 */
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	private String eventName;
	
	
	/**
	 * do you want a toggleAdvancedSearch ?
	 */
	@Property
	@Parameter(required=false,defaultPrefix=BindingConstants.PROP)
	private Boolean toggleNeeded;
	
	
	/**
	 * The sorter model which permits to sort the list.
	 */
	@SuppressWarnings("unchecked")
	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	private SorterModel<T> sorter;

	
	/**
	 * The labels of elements in the select.
	 */
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String labels;
	
	/**
	 * The properties associated to the label for the sorter.
	 */
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	private String properties;
	
	
	@Persist
	@Property
	private String selected;
	

	private String propertyName;
	
	
	@Persist
	@Property
	/** FILTER: used to launch advanced search file mode **/
	private boolean advancedSearchMode;
	
	@Inject
	private ComponentResources componentResources;

	@Persist
	private SorterModel<T> sorterPersist;
	
	@SetupRender
	public void init(){
		
		sorterPersist=sorter;
		selected=null;
		if(!componentResources.isBound("hideIfEmpty")){
			hideIfEmpty=true;
		}
		
		if(!componentResources.isBound("toggleNeeded")){
			toggleNeeded=false;
		} 
	}
	
	@OnEvent(component = "selectSorter", value = "change")
    public void onChangeEvent(String value) 
    {
		
		propertyName=getPropertyFromLabel(value);
		sortAndsendEvent(propertyName);

    }
	@OnEvent(value="refresh")
	public void onRefresh(){
		
		//do nothing here but if this methd doesn't exist tapestry will throw an exception.
	}
	
    
    private void sortAndsendEvent(String propertyName){
    	List<T> listToSort=sorterPersist.getListToSort();
    	
    	if(null!=sorterPersist.getComparator(propertyName)){
    		Collections.sort(listToSort, sorterPersist.getComparator(propertyName));
    	}
    	
    	if(Order.DESC.equals(sorterPersist.getOrder(propertyName))){
    		Collections.reverse(listToSort);
    	}
    	
    	componentResources.getContainer().getComponentResources().triggerEvent(eventName, new Object[]{listToSort},null); 
 
    }
    private String getPropertyFromLabel(String label){
    	
    	String[]labelsArray=labels.split(SEPARATOR);
    	
    	for(int i=0;i<labelsArray.length;i++){
    		if(labelsArray[i].equals(label)){
    			return properties.split(SEPARATOR)[i];
    		}
    	}
    	return null;
    }

	public boolean isHide() {
		if(sorterPersist.getListToSort()!=null){
			return hideIfEmpty && sorterPersist.getListToSort().isEmpty();
		}else{
			return true;
		}
	}
    
	/**
	 * FILTER: remote control for advanced file search (open the widjet)
	 */
	public void onActionFromToggleAdvancedSearch(){
		advancedSearchMode=!advancedSearchMode;
		
		Object [] ob = { advancedSearchMode };
		
		//componentResources.getContainer().getComponentResources().triggerEvent("eventToggleAdvancedSearchFromListDocument",	ob, null);
		
		//sorter component MUST BE a component of a list of document
		//we want to call the parent of list of document (index)
		componentResources.getContainer().getComponentResources().getContainer().getComponentResources().triggerEvent("eventToggleAdvancedSearchSorterComponent",	ob, null);
	}
	
}
