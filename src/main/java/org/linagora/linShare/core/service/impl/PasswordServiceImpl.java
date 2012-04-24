package org.linagora.linShare.core.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordServiceImpl implements PasswordService {

	final private static Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);
	
	@Override
	public String generatePassword() {
		SecureRandom sr = null;
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
        	logger.error("Algorithm \"SHA1PRNG\" not supported");
            throw new TechnicalException("Algorithm \"SHA1PRNG\" not supported");
        }

        return Long.toString(sr.nextLong() & Long.MAX_VALUE , 36 );
	}
}
