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
package org.linagora.linshare.auth;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.collect.Maps;

public class PasswordEncoderFactory {

	/**
	 * Returns an instance of password encoder. The password are encoded in using
	 * {@link BCryptPasswordEncoder} encoding.
	 * @return a password encoder.
	 */
	public PasswordEncoder getInstance() {
		String defaultEncoderId = "bcrypt";
		return new DelegatingPasswordEncoder(defaultEncoderId, getSupportedEncoders());
	}

	private Map<String, PasswordEncoder> getSupportedEncoders() {
		Map<String, PasswordEncoder> encoders = Maps.newHashMap();
		encoders.put("bcrypt", new BCryptPasswordEncoder());
		MessageDigestPasswordEncoder shaEncoder = new MessageDigestPasswordEncoder("SHA-256");
		shaEncoder.setEncodeHashAsBase64(true);
		encoders.put("sha", shaEncoder);
		return encoders;
	}
}
