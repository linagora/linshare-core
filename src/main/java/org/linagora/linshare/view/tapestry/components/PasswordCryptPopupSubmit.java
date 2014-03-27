/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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


import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Popup that is openable wait for a password, and if it is not suitable, shake the window
 * This is not conveniently integrated for the moment
 * 
 * The right way to use it : 
 *  * in the opener page, have something like "window1.showCenter(true)" (window1 is the window component name here)
 *  * t:title and t:content t:errorBanner are expected to customize the window
 *  * all the mechanics about form submit isn't done here. the opener should validate the form, and
 *    regarding the result, either return formFail or formSuccess
 *    
 * Probably we should use another parameter to put the link to open the window, and have a more suitable component
 * @author ncharles
 *
 */
public class PasswordCryptPopupSubmit {

	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
	// Title of the window
	@Parameter("title")
	private String title;
	
	// Content of the window
	@SuppressWarnings("unused")
	@Parameter("content")
	@Property
	private String content;
	
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String formId;
	
	
	// error banner
	@Parameter("errorBanner")
	@Property
	private String errorBanner;
    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
	
    @Component(parameters = {"style=bluelighting", "show=false", "title=title", "width=450", "height=150"})
    private WindowWithEffects window_passwordCryptPopupSubmit;
    
    
	// The form that holds the password request
	@InjectComponent
	private Form formPassword;
	
	// The zone that contains the action to be thrown on success 
	@InjectComponent
	private Zone onSuccess;
	
	// The form zone
	@InjectComponent
	private Zone formZone2;
	
	@Inject
	private Messages messages;
	
	@Property
	private String errormessage;
	
    private String password;
    private String confirm;
    
    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SetupRender
    public void cleanError() {
    	formPassword.clearErrors();
    	errormessage = "";
    }
    

	// When the form has a failure, we throw this
	public Zone formFail() {
		errormessage = messages.get("components.listDocument.passwordCryptPopup.error.message");
		return formZone2;
	}
	
	// When the form has a success, we throw this
	public Zone formSuccess() {
		return onSuccess;
	}
	
    /* ***********************************************************
     *                      Getters & Setters
     ************************************************************ */ 
	
	
	public Form getFormPassword() {
		return formPassword;
	}

	public String getPassword() {
		return password;
	}

	public String getConfirm() {
		return confirm;
	}


	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getJSONId() {
		return window_passwordCryptPopupSubmit.getJSONId();
	}
	
	public String getTitle() {
		return title;
	}
}
