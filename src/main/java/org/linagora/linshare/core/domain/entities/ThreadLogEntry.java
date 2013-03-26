package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.LogAction;

/**
 * @author nbertrand
 *
 */
public class ThreadLogEntry extends FileLogEntry {

	private static final long serialVersionUID = 4082338337251529658L;

	private String threadName;
	
	private String uuid;
	
	/*
	 * Default constructor for Hibernate
	 */
	protected ThreadLogEntry() {
		super();
		this.threadName = null;
		this.uuid = null;
	}
	
	public ThreadLogEntry(Account actor, Thread thread, LogAction logAction, String description) {
		super(actor, logAction, description, null, null, null);
		this.threadName = thread.getName();
		this.uuid = thread.getLsUuid();
	}
	
	public ThreadLogEntry(Account actor, ThreadEntry threadEntry, LogAction logAction, String description) {
		super(actor, logAction, description, threadEntry.getName(), threadEntry.getSize(), threadEntry.getType());
		this.threadName = ((Thread) threadEntry.getEntryOwner()).getName();
		this.uuid = ((Thread) threadEntry.getEntryOwner()).getLsUuid();
	}

	public ThreadLogEntry(Account actor, ThreadMember threadMember, LogAction logAction, String description) {
		super(actor, logAction, description, null, null, null);
		this.threadName = threadMember.getThread().getName();
		this.uuid = threadMember.getThread().getLsUuid();
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
