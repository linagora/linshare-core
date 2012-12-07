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

import java.util.List;

import org.apache.tapestry5.Block;
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
	
	@Inject
	private Block adminBlock, userBlock, restrictedUserBlock;
	
	@SetupRender
	public void init() {
		try {
			members = threadEntryFacade.getThreadMembers(thread);
		} catch (BusinessException e) {
			logger.error("Cannot retrieve thread members : " + e.getMessage());
			logger.debug(e.toString());
		}
	}
	
	/*
	 * Handle page layout with Tapestry Blocks
	 */
	public Object getType() {
		return (member.isAdmin() ? adminBlock : member.isCanUpload() ? userBlock : restrictedUserBlock);
	}
	
}
