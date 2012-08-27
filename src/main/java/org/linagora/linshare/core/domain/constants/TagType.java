package org.linagora.linshare.core.domain.constants;

public enum TagType {

	SIMPLE(0), ENUM(1);

    private int value;

    private TagType(int value) {
        this.value = value;
    }

    public int toInt() {
        return value;
    }

    public static TagType fromInt(int value) {
        switch(value) {
            case 0 : return SIMPLE;
            case 1 : return ENUM;
            default : throw new IllegalArgumentException("Doesn't match an existing type of tag");
        }
    }
}
