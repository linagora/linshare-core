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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * 
 * PBE with AES
 * the encrypted file is salt[16 bytes] + iterations int [4 bytes] + CBC encrypted file
 *
 */
public class SymmetricEnciphermentPBEwithAES {
	
	// PBE, AES CONFIG
	private final static int ITERATIONS = 20;
	private final static int SALT_NUMBER_BITES = 16; //16*8=128 bit
	/**
	 *ALGO_AES can be many conf like
	 *PBEWITHSHAAND192BITAES-CBC-BC
	 *PBEWITHSHA256AND128BITAES-CBC-BC
	 *PBEWITHSHA256AND192BITAES-CBC-BC
	 *PBEWITHSHAAND128BITAES-CBC-BC
	 *PBEWITHSHAAND256BITAES-CBC-BC
	 *PBEWITHSHA256AND256BITAES-CBC-BC    etc ...
	 */
	private final static String SECRETKEYFACTORY_ALGO = "PBEWITHSHA256AND256BITAES-CBC-BC";
	private final static String CIPHER_ALGO="AES/CBC/PKCS5Padding";
	
	
	private byte[] salt;
	private int iterations;
	private Cipher cipher;
	private DataInputStream in;
	private OutputStream out;
	
	/**
	 * can only be Cipher.DECRYPT_MODE or Cipher.ENCRYPT_MODE
	 * @see javax.crypto.Cipher
	 */
	private int cipherMode;
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	
	public SymmetricEnciphermentPBEwithAES(String password,byte[] dataToProcess,int cipherMode) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException{
		this(password,new ByteArrayInputStream(dataToProcess),new ByteArrayOutputStream(),cipherMode);
	}
	
	public SymmetricEnciphermentPBEwithAES(String password,InputStream is, OutputStream out,int cipherMode) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IOException {
		
		this.in = new DataInputStream(is);
		this.out= out;
		this.cipherMode=cipherMode;
		
		
		if(cipherMode==Cipher.DECRYPT_MODE){
			
			//read the salt 16 bytes
			salt = new byte[SALT_NUMBER_BITES];
			in.read(salt,0,SALT_NUMBER_BITES);
			SecretKey secret_key = getSecretKey(password);
			//read the iterations
			iterations = in.readInt(); //encoded in four bytes
			
			AlgorithmParameterSpec param_spec = getPBEParameterSpec(salt,iterations);
			cipher = Cipher.getInstance(CIPHER_ALGO);
			cipher.init(Cipher.DECRYPT_MODE, secret_key, param_spec);
		}
		else if(cipherMode==Cipher.ENCRYPT_MODE){
			salt = generateSalt(); // create new salt (IV)
			this.iterations=ITERATIONS;
			SecretKey secret_key = getSecretKey(password);
			AlgorithmParameterSpec param_spec = getPBEParameterSpec(salt,iterations);
			cipher = Cipher.getInstance(CIPHER_ALGO);
			cipher.init(Cipher.ENCRYPT_MODE, secret_key, param_spec);
		}
	}

	
	/**
	 * To create the bytes for the initialization vector IV, we should use java.security.SecureRandom to generate
	 * a byte array equivalent to the block size for the cipher we are using. Most block ciphers have a block size of 64 bits
	 *(8 bytes). AES has a variable block size, either 128, 192, or 256 bits, but is typically set to 128-bit (16 bytes).
	 */
	private static byte[] generateSalt() {
		byte[] salt = new byte[SALT_NUMBER_BITES];
		SecureRandom random = new SecureRandom();
		random.nextBytes(salt);
		return salt;
	}
	
	
	public static void main(String[] args) throws Exception {

		/* File encrypt / decrypt test */
		SymmetricEnciphermentPBEwithAES aes = new SymmetricEnciphermentPBEwithAES("12345678",new FileInputStream("/test/test1.pdf"),new FileOutputStream("/test/xxxxx.pdf"),Cipher.ENCRYPT_MODE);
		aes.encryptStream();
		aes = new SymmetricEnciphermentPBEwithAES("12345678",new FileInputStream("/test/xxxxx.pdf"),new FileOutputStream("/test/decryption.pdf"),Cipher.DECRYPT_MODE);
		aes.decryptStream();
		
		/* String in memory encrypt / decrypt test */
		SymmetricEnciphermentPBEwithAES aes2 = new SymmetricEnciphermentPBEwithAES("12345678","only a test with cipher when you use a string".getBytes(),Cipher.ENCRYPT_MODE);
		byte[] encStr = aes2.encryptString();
		System.out.println(new String(encStr));
		aes2 = new SymmetricEnciphermentPBEwithAES("12345678",encStr,Cipher.DECRYPT_MODE);
		System.out.println(new String(aes2.decryptString()));
	}
	
	/**
	 * PBE specification (pkcs5 standard)
	 * @param salt 
	 * @return
	 */
	private static AlgorithmParameterSpec getPBEParameterSpec(byte[] salt, int iterations){
		
		PBEParameterSpec param_spec = new PBEParameterSpec(salt, iterations);
		return param_spec;
	}
	
	private static SecretKey getSecretKey(String pw) throws NoSuchAlgorithmException, InvalidKeySpecException{
		
		PBEKeySpec key_spec = new PBEKeySpec(pw.toCharArray());
		SecretKeyFactory key_factory = SecretKeyFactory.getInstance(SECRETKEYFACTORY_ALGO);
		SecretKey secret_key = key_factory.generateSecret(key_spec);
		
		return secret_key;
	}
	
	
	public void encryptStream() throws IOException {

		if(cipherMode!=Cipher.ENCRYPT_MODE) throw new IllegalStateException("can not call encrypt, check cipher mode");
		
		out.write(salt,0,SALT_NUMBER_BITES);
		
		byte v[] = new byte[4];
		//*** writeInt
		v[0] = (byte)(0xff & (iterations >> 24));
		v[1] = (byte)(0xff & (iterations >> 16));
		v[2] = (byte)(0xff & (iterations >> 8));
		v[3] = (byte)(0xff & iterations);
		out.write(v);
		out.flush();
				
		CipherOutputStream cos = new CipherOutputStream(out, cipher);

		// Read from the input and write to the encrypting output stream
		byte[] buffer = new byte[2048];
		int bytesRead;

		while ((bytesRead = in.read(buffer)) != -1) {
			cos.write(buffer, 0, bytesRead);
		}
		
		cos.flush();
		cos.close();
		out.close();
		in.close();
	}
	
	public void decryptStream() throws IOException {
		
		if(cipherMode!=Cipher.DECRYPT_MODE) throw new IllegalStateException("can not call decrypt, check cipher mode");

		CipherInputStream cis = new CipherInputStream(in, cipher);
		
		// Read from encrypted input and write to output stream
		byte[] buffer = new byte[2048];
		int bytesRead;
		
		while ((bytesRead = cis.read(buffer)) !=-1) {
			out.write(buffer, 0, bytesRead);
			//System.out.println(bytesRead);
		}
		
		out.flush();
		out.close();
		cis.close();
		in.close();
		
	}
	
	
	public byte[] encryptString() throws IOException {
		encryptStream();
		return ((ByteArrayOutputStream) out).toByteArray();
	}
	public byte[] decryptString() throws IOException {
		decryptStream();
		return ((ByteArrayOutputStream) out).toByteArray();
	}
	
	/**
	 * give a CipherInputStream to decrypt data, if you want to decrypt data yourself
	 * in must be given in class constructor
	 * @return
	 */
	public CipherInputStream getCipherInputStream(){
		if(in == null) throw new IllegalStateException("can not give intialised CipherInputStream, check inputstream");
		return  new CipherInputStream(in, cipher);
	}
	
	/**
	 * give a CipherOutputStream to encrypt data, if you want to encrypt data yourself
	 * out must be given in class constructor
	 * @return
	 */
	public CipherOutputStream getCipherOutputStream(){
		if(out == null) throw new IllegalStateException("can not give intialised CipherOutputStream, check outputstream");
		return new CipherOutputStream(out, cipher);
	}
	
}
