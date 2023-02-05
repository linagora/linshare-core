/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.business.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.domain.entities.PasswordHistory;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.PasswordHistoryRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.Maps;

public class PasswordServiceImpl implements PasswordService {

	final private static Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);

	private final PasswordEncoder passwordEncoder;

	protected final Map<String, Integer> rules;

	protected PasswordValidator validator;

	protected final UserRepository<User> userRepository;

	private final PasswordHistoryRepository passwordHistoryRepository;

	private Integer maxSavedPasswordsNumber;

	private final static String UpperCaseNumber = "numberUpperCaseCharacters";
	private final static String LowerCaseNumber = "numberLowerCaseCharacters";
	private final static String NumberDigits = "numberDigitsCharacters";
	private final static String NumberSpecialChar = "numberSpecialCharacters";
	private final static String MinLength = "passwordMinLength";
	private final static String MaxLength = "passwordMaxLength";

	public PasswordServiceImpl(PasswordEncoder passwordEncoder,
			Integer numberUpperCaseCharacters,
			Integer numberLowerCaseCharacters,
			Integer numberDigitsCharacters,
			Integer numberSpecialCharacters,
			Integer passwordMinLength,
			Integer passwordMaxLength,
			final PasswordHistoryRepository passwordHistoryRepository,
			Integer maxSavedPasswordsNumber,
			final UserRepository<User> userRepository) {
		super();
		this.passwordEncoder = passwordEncoder;
		this.passwordHistoryRepository = passwordHistoryRepository;
		this.maxSavedPasswordsNumber = maxSavedPasswordsNumber;
		this.userRepository = userRepository;
		this.validator = new PasswordValidator(Arrays.asList(
				new LengthRule(passwordMinLength, passwordMaxLength),
				new CharacterRule(EnglishCharacterData.UpperCase, numberUpperCaseCharacters),
				new CharacterRule(EnglishCharacterData.LowerCase, numberLowerCaseCharacters),
				new CharacterRule(EnglishCharacterData.Digit, numberDigitsCharacters),
				new CharacterRule(EnglishCharacterData.Special, numberSpecialCharacters),
				new WhitespaceRule()));
		rules = Maps.newHashMap();
		rules.put(UpperCaseNumber, numberUpperCaseCharacters);
		rules.put(LowerCaseNumber, numberLowerCaseCharacters);
		rules.put(NumberDigits, numberDigitsCharacters);
		rules.put(NumberSpecialChar, numberSpecialCharacters);
		rules.put(MinLength, passwordMinLength);
		rules.put(MaxLength, passwordMaxLength);
	}

	@Override
	public String encode(CharSequence rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	public Integer setMaxSavedPasswordNumber (Integer enteredValue) {
		return this.maxSavedPasswordsNumber = enteredValue;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	@Override
	public String generatePassword() {
		SecureRandom sr = null;
		try {
			sr = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Algorithm \"SHA1PRNG\" not supported");
			throw new TechnicalException("Algorithm \"SHA1PRNG\" not supported");
		}
		return Long.toString(sr.nextLong() & Long.MAX_VALUE, 36);
	}

	@Override
	public void validatePassword(String password) {
		RuleResult result = validator.validate(new PasswordData(password));
		if (!result.isValid()) {
			throw new BusinessException(BusinessErrorCode.RESET_ACCOUNT_PASSWORD_INVALID_PASSWORD,
					validator.getMessages(result).toString());
		}
	}

	@Override
	public Map<String, Integer> getPasswordRules() {
		return rules;
	}

	@Override
	public void changePassword(User actor, String oldPassword, String newPassword) throws BusinessException {
		if (!matches(oldPassword, actor.getPassword())) {
			throw new BusinessException(BusinessErrorCode.AUTHENTICATION_ERROR, "The supplied password is invalid");
		}
		validateAndStorePassword(actor, newPassword);
	}

	@Override
	public void validateAndStorePassword(User user, String newPassword) {
		validatePassword(newPassword);
		List<PasswordHistory> histories = passwordHistoryRepository.findAllByAccount(user);
		String errorMsg = "The new password you entered is the same as your old passwords, Enter a different password please";
		for (PasswordHistory password : histories) {
			verifyPasswordMatches(newPassword, password.getPassword(), BusinessErrorCode.RESET_ACCOUNT_PASSWORD_ALREADY_USED, errorMsg);
		}
		if (histories.size() >= maxSavedPasswordsNumber) {
			PasswordHistory oldest = passwordHistoryRepository.findOldestByAccount(user);
			passwordHistoryRepository.delete(oldest);
		}
		user.setPassword(encode(newPassword));
		PasswordHistory passwordHistory = new PasswordHistory(user.getPassword(), new Date(), user);
		passwordHistoryRepository.create(passwordHistory);
		userRepository.update(user);
	}

	@Override
	public void verifyPasswordMatches(String newPassword, String oldPassword, BusinessErrorCode error,
			String errorMsg) {
		if (matches(newPassword, oldPassword)) {
			throw new BusinessException(error, errorMsg);
		}
	}
}
