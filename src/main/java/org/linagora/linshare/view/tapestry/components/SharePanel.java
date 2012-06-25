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
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

/**
 * This component shows a list of objects and add a link for each for delete it.
 * When the user click on a link. The component trigger an event to the container component.
 * If the user clicks on delete -> the component trigger an event named "deleteFromSharePanel" with the object to delete attached.
 * If the user clicks on reset -> the component trigger an event named "clearListObject".
 * If the user clicks on share -> the component trigger an event named "sharePanel" with the list of objects attached.
 * 
 * @author ngapaillard
 *
 */
public class SharePanel {

	
 	/* ***********************************************************
	 *                         Parameters
	 ************************************************************ */
	
	/**
	 * Read only parameter. If the component must show only the grid.
	 */
	@SuppressWarnings("unused")
	@Property
	@Parameter(required=false,value="false",defaultPrefix=BindingConstants.LITERAL)
	private boolean readOnly;
	
	/**
	 * The label to display in the top of the component.
	 */
	@SuppressWarnings("unused")
	@Parameter(required=false,value="",defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String label;
	
	/**
	 * The columns to display in the grid.
	 */
	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String columns;
	
	
	/**
	 * The list of the objects displayed in the grid.
	 */
	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	@Property
	private List<Object> listObject;

	
	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */
	@Inject
	private ComponentResources componentResources;
	
    @Inject
    private BeanModelSource beanModelSource; 
    
    @Inject
    private Messages messages;
    
	/* ***********************************************************
	 *                Properties & injected symbol, ASO, etc
	 ************************************************************ */
	@SuppressWarnings("unused")
	@Property
	private Object object;
	
	@Property
	@Persist
	private BeanModel shareModel;
	
	@Persist
	private boolean reset;
	

	/**
	 * The indexObject to match the object.
	 */
	private Integer indexObject; 
	
	private String[] columnList;

	
	/* ***********************************************************
	 *                       Phase processing
	 ************************************************************ */
	
	/**
	 * Initialization of indexObject for objects.
	 * Initialisation of the beanModel
	 */
	@SetupRender
	public void init() {
		if(listObject==null){
			listObject=new ArrayList<Object>();
		}
		indexObject=-1;

		if (listObject.size()>0) {
			columnList = columns.split(",");
			
			for (int i=0; i<columnList.length; i++) {
				columnList[i] = columnList[i].trim();
			}
			shareModel = beanModelSource.createDisplayModel(listObject.get(0).getClass(), componentResources.getMessages());

			shareModel.include(columnList);
			
			if (!readOnly) {
				shareModel.add("deleteColumn", null);
			}
			for (String currentcol : columnList) {
				shareModel.get(currentcol).label(messages.get("components.sharePanel.grid."+currentcol));
				if (readOnly) {
					shareModel.get(currentcol).sortable(false);
				}
			}
		}
	}

	/* ***********************************************************
	 *                   Event handlers&processing
	 ************************************************************ */
	
	/**
	 * Send an event to the container of this component.
	 * It's triggered when the reset link is pushed.
	 * The id of this event is "clearListObject"
	 * @param identifier
	 */
//	public void onActionFromReset(){
//		componentResources.getContainer().getComponentResources().triggerEvent("clearListObject",null, null);
//	}

	
	void onSelectedFromReset() {
		reset = true;
	}
	void onSelectedFromShare() {
		reset = false;
	}
	
	/**
	 * Send an event to the container of this component.
	 * It's triggered when the delete link is pushed.
	 * The id of this event is "deleteFromSharePanel"
	 * @param identifier
	 */
	public void onActionFromDelete(Integer identifier){
		
		Object[] objects=new Object[1];
		objects[0]=listObject.get(identifier);
		componentResources.getContainer().getComponentResources().triggerEvent("deleteFromSharePanel", objects, null);
	}
	
	/**
	 * Send an event to the container of this component.
	 * The id of this event is "sharePanel"
	 * The event is triggered when the shared link is pressed.
	 * @param identifier
	 */
	public void onSuccess(){
		if (reset) {
			componentResources.getContainer().getComponentResources().triggerEvent("clearListObject",null, null);
			return;
		}
		
		if(null!=listObject){
			componentResources.getContainer().getComponentResources().triggerEvent("sharePanel", listObject.toArray(), null);
		}else{
			throw new TechnicalException(TechnicalErrorCode.GENERIC,"the parameter list is null !");
		}
	}
	
	/**
	 * Return the indexObject for the object displayed in the grid.
	 * @return indexObject.
	 */
	public Integer getIndexObject(){
		return ++indexObject;
	}
	
	/**
	 * Show the actions list if the listObject is not empty.
	 * @return boolean true if the components is not empty.
	 */
	public boolean isElements(){
		return !listObject.isEmpty();
	}
		
}
