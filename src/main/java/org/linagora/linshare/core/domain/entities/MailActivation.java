package org.linagora.linshare.core.domain.entities;

public class MailActivation extends AbstractFunctionality {

	protected boolean value;

	public MailActivation() {
		super();
	}

	public MailActivation(boolean value) {
		super();
		this.value = value;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
}
