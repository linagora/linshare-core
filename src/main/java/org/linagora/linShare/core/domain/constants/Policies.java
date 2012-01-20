package org.linagora.linShare.core.domain.constants;

public enum Policies {
	MANDATORY(0), ALLOWED(1), FORBIDDEN(2);

    private int value;

    private Policies(int value) {
        this.value = value;
    }

    public int toInt() {
        return value;
    }

    public static Policies fromInt(int value) {
        switch(value) {
            case 0 : return MANDATORY;
            case 1 : return ALLOWED;
            case 2 : return FORBIDDEN;
            default : throw new IllegalArgumentException("Doesn't match an existing Policy");
        }
    }
}
