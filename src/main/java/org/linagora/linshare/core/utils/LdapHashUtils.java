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
