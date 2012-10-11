package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.ThreadViewAsso;


public class ThreadViewAssoVo implements Comparable<ThreadViewAssoVo> {
	
	private TagVo tagVo;

	private int depth;
	
	public ThreadViewAssoVo(ThreadViewAsso threadViewAsso) {
		this.depth = threadViewAsso.getDepth();
		this.tagVo = new TagVo(threadViewAsso.getTag().getName());
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

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * Sortable by ascendant order
	 */
	@Override
	public int compareTo(ThreadViewAssoVo o) {
		return depth == o.depth ? 0 : depth < o.depth ? -1 : 1;
	}
}
