package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.LogAction;

/**
 * @author nbertrand
 *
 */
public class ThreadLogEntry extends FileLogEntry {

	private static final long serialVersionUID = 4082338337251529658L;

	private String threadName;
	
	/*
	 * Default constructor for Hibernate
	 */
	protected ThreadLogEntry() {
		super();
		this.threadName = null;
	}
	
	public ThreadLogEntry(Account actor, Thread thread, LogAction logAction, String description) {
		super(actor, logAction, description, null, null, null);
		this.threadName = thread.getName();
	}
	
	public ThreadLogEntry(Account actor, ThreadEntry threadEntry, LogAction logAction, String description) {
		super(actor, logAction, description, threadEntry.getName(), threadEntry.getSize(), threadEntry.getType());
		this.threadName = ((Thread) threadEntry.getEntryOwner()).getName();
	}

	public ThreadLogEntry(Account actor, ThreadMember threadMember, LogAction logAction, String description) {
		super(actor, logAction, description, null, null, null);
		this.threadName = threadMember.getThread().getName();
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
}
