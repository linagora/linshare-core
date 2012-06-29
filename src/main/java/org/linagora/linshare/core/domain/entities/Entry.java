package org.linagora.linshare.core.domain.entities;

import java.util.Calendar;
import java.util.Date;

public class Entry {

	protected long id;
	
	protected Account entryOwner;
	
	protected Calendar creationDate;
	
	protected Calendar modificationDate;
	
	protected Calendar expirationDate;
	
	protected String name;
	
	protected String comment;
	
	public Entry() {
	}
	
	public Entry(Account entryOwner, String name, String comment) {
		this.entryOwner = entryOwner;
		this.name = name;
		this.comment = comment;
	}
	
	
	public long getId() {
		return id;
	}

	
	public void setId(long id) {
		this.id = id;
	}

	public Account getEntryOwner() {
		return entryOwner;
	}

	public void setEntryOwner(Account entryOwner) {
		this.entryOwner = entryOwner;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public Calendar getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Calendar modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
