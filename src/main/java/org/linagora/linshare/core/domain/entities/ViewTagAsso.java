package org.linagora.linshare.core.domain.entities;

public class ViewTagAsso {

	private long id;
	
	private Tag tag;
	
	private View view;
	
	private int depth;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public org.linagora.linshare.core.domain.entities.Tag getTag() {
		return tag;
	}

	public void setTag(org.linagora.linshare.core.domain.entities.Tag tag) {
		this.tag = tag;
	}

	public org.linagora.linshare.core.domain.entities.View getView() {
		return view;
	}

	public void setView(org.linagora.linshare.core.domain.entities.View view) {
		this.view = view;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
}