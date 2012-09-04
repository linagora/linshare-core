package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.LogAction;

public class AntivirusLogEntry extends LogEntry {

	private static final long serialVersionUID = -5035754068121031915L;
	
	public AntivirusLogEntry() {
		super();
	}
	
	public AntivirusLogEntry(Account actor, LogAction logAction, String description) {
		super(actor, logAction, description);
	}

}
