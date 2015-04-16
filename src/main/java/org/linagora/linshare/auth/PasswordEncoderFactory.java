/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.auth;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

/** 
 * Returns an instance of password encoder.
 */
public class PasswordEncoderFactory {

    private static final String MD5 = "MD5";
    private static final String SHA = "SHA";
    private static final String SSHA = "SSHA";
    private static final String PLAIN = "PLAIN";

    private String passwordEncoderName;

    public PasswordEncoderFactory(String passwordEncoderName) {
        if (!passwordEncoderName.equalsIgnoreCase(MD5) && !passwordEncoderName.equalsIgnoreCase(SHA)
            && !passwordEncoderName.equalsIgnoreCase(SSHA) && !passwordEncoderName.equalsIgnoreCase(PLAIN)) {
            throw new RuntimeException("Unkown password encoder name : " + passwordEncoderName);
        }
        this.passwordEncoderName = passwordEncoderName;
    }

    /** Returns an instance of password encoder.
     * The password are encoded in base64.
     * @return a password encoder.
     */
    public PasswordEncoder getInstance() {
        if (passwordEncoderName.equalsIgnoreCase(MD5)) {
            Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
            passwordEncoder.setEncodeHashAsBase64(true);
            return passwordEncoder;
        } else if (passwordEncoderName.equalsIgnoreCase(SHA) || passwordEncoderName.equalsIgnoreCase(SSHA)) {
            ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder();
            passwordEncoder.setEncodeHashAsBase64(true);
            return passwordEncoder;
        } else {
            return new PlaintextPasswordEncoder();
        }
    }
}
