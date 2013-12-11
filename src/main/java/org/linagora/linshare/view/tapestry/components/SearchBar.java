package org.linagora.linshare.view.tapestry.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;

@Import(library = { "jquery/jquery-1.7.2.js",
					"bootstrap/js/bootstrap.js" })
public class SearchBar implements ClientElement {
	
	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
	@Property
	private String value;

	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	@Property
	private String placeholder;

	@Component(parameters = "validationId=componentResources.id",
			   publishParameters = "clientValidation,autofocus,zone")
	private Form form;

	@SetupRender
	public void init() {
	}

	@Import(stylesheet = { "bootstrap/css/bootstrap.css" })
	@AfterRender
	public void cssInit() {
	}

	@Override
	public String getClientId() {
		return form.getClientId();
	}
}
