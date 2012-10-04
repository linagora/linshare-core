package org.linagora.linshare.view.tapestry.pages.thread;

import org.linagora.linshare.core.domain.vo.TagVo;

public class DummyViewTagVo {
	public static final int MAX_DEPTH = 3;
	private TagVo tagVo;
	private int depth;

	public DummyViewTagVo(TagVo tagVo, int order) {
		super();
		this.tagVo = tagVo;
		this.depth = order;
	}
	
	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public TagVo getTagVo() {
		return tagVo;
	}

	public void setTagVo(TagVo tagVo) {
		this.tagVo = tagVo;
	}
}
