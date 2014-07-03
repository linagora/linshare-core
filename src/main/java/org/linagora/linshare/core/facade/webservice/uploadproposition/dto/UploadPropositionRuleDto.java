package org.linagora.linshare.core.facade.webservice.uploadproposition.dto;

import org.linagora.linshare.core.domain.constants.UploadPropositionRuleFieldType;
import org.linagora.linshare.core.domain.constants.UploadPropositionRuleOperatorType;
import org.linagora.linshare.core.domain.entities.UploadPropositionRule;

public class UploadPropositionRuleDto {

	protected String uuid;

	protected String operator;

	protected String field;

	protected String value;

	// Tests only
	public UploadPropositionRuleDto(String uuid, UploadPropositionRuleOperatorType operator, UploadPropositionRuleFieldType field,
			String value) {
		super();
		this.uuid = uuid;
		this.operator = operator.name();
		this.field = field.name();
		this.value = value;
	}

	public UploadPropositionRuleDto(UploadPropositionRule entity) {
		super();
		this.uuid = entity.getUuid();
		this.operator = entity.getOperator().name();
		this.field = entity.getField().name();
		this.value = entity.getValue();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
