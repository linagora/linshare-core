package org.linagora.linshare.view.tapestry.components;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.BeanEditForm;

@Import(library = { "bootstrap/js/bootstrap.js" })
@SupportsInformalParameters
public class BSBeanEditForm extends BeanEditForm {

	@Import(stylesheet = { "bootstrap/css/bootstrap.css" })
	void cleanupRender() {
	}
}
