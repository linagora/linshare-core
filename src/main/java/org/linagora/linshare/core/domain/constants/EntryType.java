package org.linagora.linshare.core.domain.constants;

public enum EntryType {
	
	
	DOCUMENT(1), SHARE(2), ANONYMOUS_SHARE(3),THREAD(4);

	private int value;

	private EntryType(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static EntryType fromInt(int value) {
        switch (value) {
            case 1: return EntryType.DOCUMENT;
            case 2: return EntryType.SHARE;
            case 3: return EntryType.ANONYMOUS_SHARE;
            case 4: return EntryType.THREAD;
            default : throw new IllegalArgumentException("Doesn't match an existing EntryType");
        }
	}
	
	
}
