package org.linagora.linshare.core.domain.entities;

import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.LinShareConstants;

public class ThreadView {

	private Long id;
	
	private Thread thread;
	
	private String name;

	private Set<ThreadViewAsso> threadViewAssos = new HashSet<ThreadViewAsso>();


	public ThreadView() {
		super();
	}

	public ThreadView(Thread thread) {
		super();
		this.thread = thread;
		this.name = LinShareConstants.defaultThreadView;
	}

	public ThreadView(Thread thread, String name, Set<ThreadViewAsso> threadViewAssos) {
		super();
		this.thread = thread;
		this.name = name;
		this.threadViewAssos = threadViewAssos;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<ThreadViewAsso> getThreadViewAssos() {
		return threadViewAssos;
	}

	public void setThreadViewAssos(Set<ThreadViewAsso> threadViewAssos) {
		this.threadViewAssos = threadViewAssos;
	}
}
