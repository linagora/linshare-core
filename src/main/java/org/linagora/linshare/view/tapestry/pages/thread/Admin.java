package org.linagora.linshare.view.tapestry.pages.thread;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Admin {

	private static final Logger logger = LoggerFactory.getLogger(Admin.class);

	@SessionState
	@Property
	private UserVo userVo;

	@Persist
	@Property
	private ThreadVo thread;
	
	@Inject
	private ThreadEntryFacade threadEntryFacade;

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
			e.printStackTrace();
			return ThreadContent.class;
		}
		return null;
	}

	public void setSelectedThread(ThreadVo thread) {
		this.thread = thread;
	}
}
