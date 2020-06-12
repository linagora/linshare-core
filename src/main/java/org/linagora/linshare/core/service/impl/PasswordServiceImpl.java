/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.linagora.linshare.core.domain.entities.PasswordHistory;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.PasswordHistoryRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.PasswordService;
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
		updatePassword(actor, newPassword);
	}

	private void updatePassword(User user, String newPassword) {
		validatePassword(newPassword);
		List<PasswordHistory> histories = passwordHistoryRepository.findAllByAccount(user);
		for (PasswordHistory password : histories) {
			verifyPasswordMatches(newPassword, password.getPassword());
		}
		if (histories.size() >= maxSavedPasswordsNumber) {
			PasswordHistory oldest = passwordHistoryRepository.findOldestByAccount(user);
			passwordHistoryRepository.delete(oldest);
		}
		PasswordHistory passwordHistory = new PasswordHistory(user.getPassword(), new Date(), user);
		passwordHistoryRepository.create(passwordHistory);
		user.setPassword(encode(newPassword));
		userRepository.update(user);
	}

	private void verifyPasswordMatches(String newPassword, String oldPassword) {
		if (matches(newPassword, oldPassword)) {
			throw new BusinessException(BusinessErrorCode.RESET_ACCOUNT_PASSWORD_ALREADY_USED,
					"The new password you entered is the same as your old passwords, Enter a different password please");
		}
	}
}
