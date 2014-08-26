package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.entities.UploadRequestTemplate;

public class UploadRequestTemplateVo {

	private String uuid;

	private String name;

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

	public UploadRequestVo toValue() {
		UploadRequestVo ret = new UploadRequestVo();
		return ret;
	}

	/*
	 * used as label for tapestry'select
	 */
	@Override
	public String toString() {
		return name;
	}
}
