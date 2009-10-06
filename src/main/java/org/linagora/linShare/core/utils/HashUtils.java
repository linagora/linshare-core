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
package org.linagora.linShare.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.lang.ArrayUtils;

public class HashUtils {

	private HashUtils() {
	}
	
	public static String hashSha1withBase64(byte[] b) {
		return hashWithBase64(b,"SHA1");
	}
	
	public static String hashMd5withBase64(byte[] b) {
		return hashWithBase64(b,"MD5");
	}
	
	public static String hashSshawithBase64(byte[] b,byte[] salt) {
		return hashWithBase64(b,salt,"SHA1");
	}
	
	/**
	 * generate salt (32 bits/4 octets)
	 * @return
	 */
	public static byte[] getSalt() {
		// Get 32 random bits
		byte[] mybytes;
		try {
			// Create a secure random number generator
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");

			// Create secure number generator with seed
			int seedByteCount = 10;
			byte[] seed = sr.generateSeed(seedByteCount);

			sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(seed);

			mybytes = new byte[32 / 8];
			sr.nextBytes(mybytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return mybytes;
	}
	
	
	/**
	 * hash an array of byte with a given salt
	 * @param b array of byte to digest
	 * @param salt can be null. if not it is appended to the array of bytes before doing the digest.
	 * @param HashAlgo may be MD5, SHA1 ...
	 * @return a base 64 string, with the encoded salt at the end if exist.
	 */
	private static String hashWithBase64(byte[] b, byte[] salt,String HashAlgo) {
		byte[] res = null;

		MessageDigest md;
		try {
			md = MessageDigest.getInstance(HashAlgo);
			md.reset();
			md.update(b);
			if (salt!=null) md.update(salt);
			res = md.digest();
			
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		if (salt==null){
			return Base64Utils.encodeBytes(res);
		} else {		
			res = ArrayUtils.addAll(res,salt);
			return Base64Utils.encodeBytes(res);
		}
	}
	
	
	private static String hashWithBase64(byte[] b, String HashAlgo) {
		return hashWithBase64(b,null,HashAlgo);
	}

	public static byte[] giveBytesHashSha1withBase64(byte[] b) {

		byte[] res = null;

		try {
			res = hashSha1withBase64(b).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return res;
	}
	
	/**
	 * give sha1 fingerprint for a file
	 * @param file
	 * @return array of sha1 bytes
	 */
	public static byte[] hashSha1(File file)
		{

	  	byte[] mdbytes = null;

	  	
	  	try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
					FileInputStream fis = new FileInputStream(file);
					byte[] dataBytes = new byte[1024];
					int nread = fis.read(dataBytes);
					while (nread > 0) {
					  md.update(dataBytes, 0, nread);
					  nread = fis.read(dataBytes);
					}
					mdbytes = md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
	    
	    return mdbytes;
	   }
	
	
	/**
	 * compute sha1 with base 64 from a argument
	 * usefull to compute hash to insert in sql script
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length>0){
			System.out.println(args[0]+":"+HashUtils.hashSha1withBase64(args[0].trim().getBytes()));
		}
	}

}
