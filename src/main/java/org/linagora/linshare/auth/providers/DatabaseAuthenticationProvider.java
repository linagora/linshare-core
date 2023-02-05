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
package org.linagora.linshare.auth.providers;

import org.jboss.aerogear.security.otp.Totp;
import org.linagora.linshare.auth.LinShareWebAuthenticationDetails;
import org.linagora.linshare.auth.exceptions.TOTPBadFormatException;
import org.linagora.linshare.auth.exceptions.TOTPInvalidValueException;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class DatabaseAuthenticationProvider extends DaoAuthenticationProvider {

	protected UserRepository<User> userRepository;

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		super.additionalAuthenticationChecks(userDetails, authentication);
		User account = userRepository.findByLsUuid(userDetails.getUsername());
		checkTOTP(account, authentication);
	}

	protected void checkTOTP(User account, UsernamePasswordAuthenticationToken authentication) {
		if (account.isUsing2FA()) {
			String verificationCode = ((LinShareWebAuthenticationDetails) authentication.getDetails())
					.getSecondFactorAuthCode();
			final Totp totp = new Totp(account.getSecondFASecret());
			if (!isValidPin(verificationCode)) {
				String msg = "Missing or Invalid format for TOTP code. See X-LinShare-2fa-pin header.";
				logger.info(msg + " for account " + account.getLsUuid());
				throw new TOTPBadFormatException(msg);
			}
			if (!totp.verify(verificationCode)) {
				String msg = "Invalid or Expired TOTP code . See X-LinShare-2fa-pin header.";
				logger.info(msg + " for account " + account.getLsUuid());
				throw new TOTPInvalidValueException(msg);
			}
		}
	}

	protected boolean isValidPin(String strPin) {
		try {
			long pin = Long.parseLong(strPin);
			// Only 6 digits.
			if (pin >= 1000000) {
				return false;
			}
		} catch (final NumberFormatException e) {
			return false;
		}
		return true;
	}

	public UserRepository<User> getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository<User> userRepository) {
		this.userRepository = userRepository;
	}
}
