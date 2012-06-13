/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.auth;

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
