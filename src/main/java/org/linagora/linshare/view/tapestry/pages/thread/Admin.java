package org.linagora.linshare.view.tapestry.pages.thread;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Admin {

	private static final Logger logger = LoggerFactory.getLogger(Admin.class);

	/*
	 * Tapestry properties
	 */

	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	@SessionState
	@Property
	private UserVo userVo;

	@Persist
	@Property
	private ThreadVo thread;

	/*
	 * Injected beans
	 */

	@Inject
	private ThreadEntryFacade threadEntryFacade;

	@Inject
	private Messages messages;

	public Object onActivate() {
		if (thread == null) {
			logger.info("No thread selected, abort");
			return ThreadContent.class;
		}
		try {
			if (!threadEntryFacade.userIsAdmin(userVo, thread)) {
				logger.info("Unauthorized");
				return ThreadContent.class;
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			return ThreadContent.class;
		}
		return null;
	}

	public void setSelectedThread(ThreadVo thread) {
		this.thread = thread;
	}

	public void onActionFromDelete() throws BusinessException {
		threadEntryFacade.deleteThread(userVo, thread);
	}

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
