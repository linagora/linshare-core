package org.linagora.linshare.core.domain.objects;

public class FunctionalityPermissions {

	protected boolean parentAllowAPUpdate;

	protected boolean parentAllowCPUpdate;

	protected boolean parentAllowDPUpdate;

	protected boolean parentAllowParametersUpdate;

	public FunctionalityPermissions(boolean parentAllowAPUpdate,
			boolean parentAllowCPUpdate, boolean parentAllowDPUpdate,
			boolean parentAllowParametersUpdate) {
		super();
		this.parentAllowAPUpdate = parentAllowAPUpdate;
		this.parentAllowCPUpdate = parentAllowCPUpdate;
		this.parentAllowDPUpdate = parentAllowDPUpdate;
		this.parentAllowParametersUpdate = parentAllowParametersUpdate;
	}

	public boolean isParentAllowAPUpdate() {
		return parentAllowAPUpdate;
	}

	public void setParentAllowAPUpdate(boolean parentAllowAPUpdate) {
		this.parentAllowAPUpdate = parentAllowAPUpdate;
	}

	public boolean isParentAllowCPUpdate() {
		return parentAllowCPUpdate;
	}

	public void setParentAllowCPUpdate(boolean parentAllowCPUpdate) {
		this.parentAllowCPUpdate = parentAllowCPUpdate;
	}

	public boolean isParentAllowDPUpdate() {
		return parentAllowDPUpdate;
	}

	public void setParentAllowDPUpdate(boolean parentAllowDPUpdate) {
		this.parentAllowDPUpdate = parentAllowDPUpdate;
	}

	public boolean isParentAllowParametersUpdate() {
		return parentAllowParametersUpdate;
	}

	public void setParentAllowParametersUpdate(
			boolean parentAllowParametersUpdate) {
		this.parentAllowParametersUpdate = parentAllowParametersUpdate;
	}
}
