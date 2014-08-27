package org.linagora.linshare.view.tapestry.pages.uploadrequest.template;

import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.UploadRequestTemplateVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.UploadRequestFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;

public class Create {

	/*
	 * Tapestry properties
	 */

	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	@SessionState
	@Property
	private UserVo userVo;

	@Property
	private UploadRequestTemplateVo current;

	/*
	 * Injected beans
	 */

	@Inject
	private Logger logger;

	@Inject
	private Messages messages;

	@Inject
	private BusinessMessagesManagementService businessMessagesManagementService;

	@Inject
	private FunctionalityFacade functionalityFacade;

	@Inject
	private UploadRequestFacade uploadRequestFacade;

	public Object onActivate() {
		if (!functionalityFacade.isEnableUploadRequest(userVo
				.getDomainIdentifier())) {
			return org.linagora.linshare.view.tapestry.pages.Index.class;
		}
		return null;
	}

	public void setupRender() {
		current = new UploadRequestTemplateVo();
	}

	@Log
	public Object onSuccess() throws BusinessException {
		uploadRequestFacade.createTemplate(userVo, current);
		return Index.class;
	}

	@Log
	public Object onCanceled() throws BusinessException {
		return Index.class;
	}

	/*
	 * Models + ValueEncoder
	 */

	/*
	 * Exception Handling
	 */

	public Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}

}
