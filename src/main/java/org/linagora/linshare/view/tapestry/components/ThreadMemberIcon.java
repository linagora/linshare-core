package org.linagora.linshare.view.tapestry.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.ThreadMemberVo;

public class ThreadMemberIcon {

	@Parameter(required = true, defaultPrefix = BindingConstants.PROP)
	@Property
	private ThreadMemberVo member;

	@Inject
	private Block adminBlock, userBlock, restrictedUserBlock;

	/*
	 * Handle page layout with Tapestry Blocks
	 */
	public Object getType() {
		return (member.isAdmin() ? adminBlock
				: member.isCanUpload() ? userBlock : restrictedUserBlock);
	}
}
