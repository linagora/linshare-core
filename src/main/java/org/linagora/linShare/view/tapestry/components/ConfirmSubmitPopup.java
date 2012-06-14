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
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * Used to confirm in a form
 * @author ncharles
 *
 */
public class ConfirmSubmitPopup {


	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
	
	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String messageLabel;
	
	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	private String fieldName;
	
	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	private String formName;
	
	@Component(parameters = {"style=bluelighting", "show=false","width=500", "height=100"})
	private WindowWithEffects window_confirm_submit;
	
	
	
    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
		
	@Inject
    private JavaScriptSupport renderSupport;
	
	
	
	
    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
	/**
	 * Initialize the JS value
	 */
    @AfterRender
    public void afterRender() {
    	renderSupport.addScript(String.format("Event.observe('confirmsubmitPopupYes', 'click', function(event) { document.forms['%s'].%s.value='true'; document.forms['%s'].submit();});", formName, fieldName, formName));
    	renderSupport.addScript(String.format("Event.observe('confirmsubmitPopupNo', 'click', function(event) { document.forms['%s'].%s.value='false'; Windows.close('%s'); });", formName, fieldName,window_confirm_submit.getJSONId()));
    }

    public void onActionFromConfirmsubmitPopupYes() {
		return;
	}
    
	public void onActionFromConfirmsubmitPopupNo() {
		return;
	}
	
	public String getJSONId() {
    	return window_confirm_submit.getJSONId();
    }
}
