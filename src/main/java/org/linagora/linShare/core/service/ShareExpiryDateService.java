package org.linagora.linShare.core.service;

import java.util.Calendar;

import org.linagora.linShare.core.domain.entities.Document;

public interface ShareExpiryDateService {
	public Calendar computeShareExpiryDate(Document doc);
}
