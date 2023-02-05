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
package org.linagora.linshare.core.business.service;

import java.util.Map;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;

public interface PasswordService {

	/**
	 * generate the password of a guest or password for a secure URL (SHA1PRNG
	 * algorithm)
	 * 
	 * @return password in plain text
	 */
	public String generatePassword();

	public String encode(CharSequence rawPassword);

	public boolean matches(CharSequence rawPassword, String encodedPassword);

	public void changePassword(User actor, String oldPassword, String newPassword)
			throws BusinessException;

	void validatePassword(String password);
	
	Map<String, Integer> getPasswordRules();

	void validateAndStorePassword(User user, String newPassword);

	void verifyPasswordMatches(String newPassword, String oldPassword, BusinessErrorCode error, String errorMsg);
}