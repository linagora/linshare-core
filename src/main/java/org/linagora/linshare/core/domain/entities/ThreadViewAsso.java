package org.linagora.linshare.core.domain.entities;

public class ThreadViewAsso {

	private Long id;
	
	private Tag tag;
	
	private int depth;


	public ThreadViewAsso() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
}
