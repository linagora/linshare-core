package org.linagora.linshare.core.domain.vo;

import java.util.Date;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.entities.UploadRequestTemplate;

public class UploadRequestTemplateVo {

	private String uuid;

	private String name;

	private Date creationDate;

	private Date modificationDate;

	@Inject
	public UploadRequestTemplateVo() {
		super();
	}

	public UploadRequestTemplateVo(UploadRequestTemplate t) {
		uuid = t.getUuid();
		name = t.getName();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	/*
	 * used as label for tapestry' select
	 */
	@Override
	public String toString() {
		return name;
	}

	public UploadRequestTemplate toEntity() {
		UploadRequestTemplate ret = new UploadRequestTemplate();

		ret.setUuid(uuid);
		ret.setName(name);
		// TODO other fields
		return ret;
	}
}
