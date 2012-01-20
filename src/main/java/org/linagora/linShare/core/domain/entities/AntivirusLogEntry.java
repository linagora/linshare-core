package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.LogAction;

public class AntivirusLogEntry extends LogEntry {

	private static final long serialVersionUID = -5035754068121031915L;
	
	public AntivirusLogEntry() {
	}
	
	public AntivirusLogEntry(LogAction logAction, String description) {
		super(null, null, null, null, logAction, description);
	}
	
	public AntivirusLogEntry(String actorMail,
			String actorFirstname, String actorLastname, String actorDomain,
			LogAction logAction, String description) {
		super(actorMail, actorFirstname, actorLastname, actorDomain, logAction, description);
	}

}
