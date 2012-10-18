package org.linagora.linshare.view.tapestry.components;

import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.ThreadMemberVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadMembersComponent {
	
	private static Logger logger = LoggerFactory.getLogger(ThreadMembersComponent.class);
	
	@Parameter
	private ThreadVo thread;
	
	@SessionState
	@Property
	private UserVo user;
	
	@Property
	private List<ThreadMemberVo> members;
	
	@Property
	private ThreadMemberVo member;
	
	@Inject 
	private ThreadEntryFacade threadEntryFacade;
	
	@Inject
	private Messages messages;
	
	@SetupRender
	public void init() {
		try {
			members = threadEntryFacade.getThreadMembers(thread);
		} catch (BusinessException e) {
			logger.error("Cannot retrieve thread members : " + e.getMessage());
			logger.debug(e.toString());
		}
	}
	
}
