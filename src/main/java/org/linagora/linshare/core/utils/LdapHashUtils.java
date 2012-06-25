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
package org.linagora.linshare.core.utils;

import org.apache.commons.lang.ArrayUtils;


public class LdapHashUtils {

	private LdapHashUtils() {
	}
	public static final String SHA1 = "SHA";
	public static final String SSHA = "SSHA";
	public static final String MD5 = "MD5";

	
	public static String LdapHashSha1withBase64(String password) {
		return "{"+SHA1+"}"+HashUtils.hashSha1withBase64(password.getBytes());
	}
	public static String LdapHashMd5withBase64(String password) {
		return "{"+MD5+"}"+HashUtils.hashMd5withBase64(password.getBytes());
	}
	public static String LdapHashSshawithBase64(String password, byte[] salt) {
		if(salt==null) salt = HashUtils.getSalt();
		return "{"+SSHA+"}"+HashUtils.hashSshawithBase64(password.getBytes(), salt);
	}
	
	/**
	 * compose a ldap hash
	 * @param password clear passsword
	 * @param algoHashLdap SHA MD5 or SSHA
	 * @param salt is neeeded only for SSHA. it can be null. if null it will be generated.
	 * @return {ALGO}base64password
	 */
	public static String LdapHashwithBase64(String password,String algoHashLdap,byte[] salt) {
		if(algoHashLdap.equals(SHA1)) return LdapHashSha1withBase64(password);
		else if (algoHashLdap.equals(MD5)) return LdapHashMd5withBase64(password);
		else if (algoHashLdap.equals(SSHA)) return LdapHashSshawithBase64(password,salt);
		else throw new UnsupportedOperationException("this algorythm is not supported "+algoHashLdap);
	}
	
	
	/**
	 * get the salt from a user login if exists.
	 * @param userPassword userPassword comes from ldap. It is base 64 encoded form with {algo} in the prefix
	 * @return salt last four bytes
	 */
	public static byte[] getSaltFromUserPassword(String userPassword) {
		String pass = userPassword.substring(userPassword.indexOf("}")+1, userPassword.length());
		
		byte[] passBytes = Base64Utils.decode(pass);
		byte[] salt = ArrayUtils.subarray(passBytes, passBytes.length-4, passBytes.length);
		
		return salt;
	}
	
	/**
	 * get the salt from a user login
	 * @param userPassword userPassword comes from ldap. It is base 64 encoded form with {algo} in the prefix
	 * @return salt last four bytes
	 */
	public static byte[] getSaltFromUserPassword(byte[] userPassword) {
		return getSaltFromUserPassword(new String (userPassword));
	}
	
	
}
