package org.linagora.linshare.core.domain.constants;

public enum VisibilityType {

	Private(0), Public(1);

	private int value;

	
	private VisibilityType(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public VisibilityType fromInt(int value) {
        switch (value) {
            case 0: return VisibilityType.Private;
            case 1: return VisibilityType.Public;
            default : throw new IllegalArgumentException("Doesn't match an existing DomainAccessRuleType");
        }
	}
	
}
