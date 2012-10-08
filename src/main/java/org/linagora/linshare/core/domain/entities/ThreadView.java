package org.linagora.linshare.core.domain.entities;

import java.util.HashSet;
import java.util.Set;

public class ThreadView {

	private Long id;
	
	private Thread thread;
	
	private String name;
	
	private Set<ThreadViewAsso> threadViewAssos = new HashSet<ThreadViewAsso>();

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
