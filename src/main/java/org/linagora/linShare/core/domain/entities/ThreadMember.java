package org.linagora.linShare.core.domain.entities;

import java.util.Date;

public class ThreadMember {

	private long id;
	
	private boolean canUpload;
	
	private boolean admin;
	
	private Date creationDate;
	
	private Date modificationDate;
	
	private Account account;
	
	private Thread thread;
	
	
	@SuppressWarnings("unused")
	private void setId(long value) {
		this.id = value;
	}
	
	public long getId() {
		return id;
	}
	
	public void setCanUpload(boolean value) {
		this.canUpload = value;
	}
	
	public boolean getCanUpload() {
		return canUpload;
	}
	
	public void setAdmin(boolean value) {
		this.admin = value;
	}
	
	public boolean getAdmin() {
		return admin;
	}
	
	public void setCreationDate(Date value) {
		this.creationDate = value;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setModificationDate(Date value) {
		this.modificationDate = value;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}
	
	public Thread getThread() {
		return thread;
	}
	
}
