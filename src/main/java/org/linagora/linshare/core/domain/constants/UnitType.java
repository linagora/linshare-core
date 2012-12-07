package org.linagora.linshare.core.domain.constants;

public enum UnitType {
	TIME(0), SIZE(1);

    private int value;

    private UnitType(int value) {
        this.value = value;
    }

    public int toInt() {
        return value;
    }

    public static UnitType fromInt(int value) {
        switch(value) {
            case 0 : return TIME;
            case 1 : return SIZE;
            default : throw new IllegalArgumentException("Doesn't match an existing type of unit");
        }
    }
}
