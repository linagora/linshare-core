package org.linagora.linshare.core.domain.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.linagora.linshare.core.domain.entities.ThreadView;
import org.linagora.linshare.core.domain.entities.ThreadViewAsso;


public class ThreadViewVo {
	
	private String name;
	
	private int depth;

	private List<ThreadViewAssoVo> threadViewAssos;


	public ThreadViewVo(ThreadView currentThreadView) {
		this.name = currentThreadView.getName();
		this.threadViewAssos = new ArrayList<ThreadViewAssoVo>();
		for (ThreadViewAsso threadViewAsso : currentThreadView.getThreadViewAssos()) {
			this.threadViewAssos.add(new ThreadViewAssoVo(threadViewAsso));
		}
		if (!this.threadViewAssos.isEmpty()) {
			Collections.sort(this.threadViewAssos);
			this.depth = this.threadViewAssos.get(this.threadViewAssos.size() - 1).getDepth();
		}
		else
			this.depth = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ThreadViewAssoVo> getThreadViewAssos() {
		return threadViewAssos;
	}

	public void setThreadViewAssos(List<ThreadViewAssoVo> threadViewAssos) {
		this.threadViewAssos = threadViewAssos;
	}

	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
}
