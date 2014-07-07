package org.linagora.linshare.view.tapestry.components;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.BeanDisplay;

@Import(library = { "bootstrap/js/bootstrap.js" })
@SupportsInformalParameters
public class BSBeanDisplay extends BeanDisplay {

	@Import(stylesheet = { "bootstrap/css/bootstrap.css" })
	void cleanupRender() {
	}
}
