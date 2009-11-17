package org.linagora.linShare.core.service.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.ShareExpiryRule;
import org.linagora.linShare.core.service.ShareExpiryDateService;
import org.linagora.linShare.core.service.ParameterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareExpiryDateServiceImpl implements ShareExpiryDateService {
	private final ParameterService parameterService;
	private static final Logger logger = LoggerFactory.getLogger(ShareExpiryDateServiceImpl.class);

	public ShareExpiryDateServiceImpl(final ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	public Calendar computeShareExpiryDate(Document doc) {
		List<ShareExpiryRule> shareRules = parameterService.loadConfig()
				.getShareExpiryRules();

		Calendar defaultExpiration = null;
		// set the default exp time
		if (parameterService.loadConfig().getDefaultShareExpiryTime() != null) {
			defaultExpiration = GregorianCalendar.getInstance();
			defaultExpiration.add(parameterService.loadConfig()
					.getDefaultShareExpiryUnit().toCalendarValue(),
					parameterService.loadConfig().getDefaultShareExpiryTime());
		}
		if ((shareRules == null) || (shareRules.size() == 0)) {
			return defaultExpiration;
		}

		// luckily, the shareExpiryRules are ordered according to the size,
		// increasing
		for (ShareExpiryRule shareExpiryRule : shareRules) {
			if (doc.getSize() < shareExpiryRule.getShareSizeUnit()
					.getPlainSize(shareExpiryRule.getShareSize())) {
				Calendar expiration = GregorianCalendar.getInstance();
				expiration.add(shareExpiryRule.getShareExpiryUnit()
						.toCalendarValue(), shareExpiryRule
						.getShareExpiryTime());
				return expiration;
			}
		}

		// nothing has been decided
		return defaultExpiration;
	}

}
