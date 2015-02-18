package org.linagora.linshare.core.domain.entities;

import java.util.Date;

public abstract class UserProvider {

	protected long id;

	protected String uuid;

	protected Date creationDate;

	protected Date modificationDate;

	@Override
	public String toString() {
		return "UserProvider [uuid=" + uuid + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

}
