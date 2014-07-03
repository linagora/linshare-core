package org.linagora.linshare.core.facade.webservice.uploadproposition.dto;

import org.linagora.linshare.core.domain.constants.UploadPropositionActionType;
import org.linagora.linshare.core.domain.entities.UploadPropositionAction;


public class UploadPropositionActionDto {

	protected String uuid;

	protected String actionType;

	protected String data;

	//	Tests only
	public UploadPropositionActionDto(String uuid, UploadPropositionActionType actionType,
			String data) {
		super();
		this.uuid = uuid;
		this.actionType = actionType.name();
		this.data = data;
	}

	public UploadPropositionActionDto(UploadPropositionAction entity) {
		super();
		this.uuid = entity.getUuid();
		this.actionType = entity.getActionType().name();
		this.data = entity.getData();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
