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
package org.linagora.linshare.core.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import org.bouncycastle.cms.SignerId;
import org.bouncycastle.tsp.TSPAlgorithms;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.linagora.linshare.core.service.TimeStampingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TimeStampingServiceImpl implements TimeStampingService {

    private static final Logger logger = LoggerFactory.getLogger(TimeStampingService.class);

	public TimeStampingServiceImpl() {
	}
	
	private URI getUriFromUrl(String urlTSA) throws URISyntaxException, TSPException  {
		URI uriTSA;
		
		if (urlTSA == null || urlTSA.equals("")) {
			throw new TSPException("no TSA url");
		} else { 
			try {
				uriTSA = new URI(urlTSA); //check url syntax
			} catch (URISyntaxException e) {
				throw e;
			}
		}
		
		return uriTSA;
	}
	
	
	/**
	 * method to timestamp a file (inputstream)
	 * @throws URISyntaxException 
	 */
	public TimeStampResponse getTimeStamp(String urlTSA, InputStream inToTimeStamp) throws TSPException, URISyntaxException {
		URI uriTSA = getUriFromUrl(urlTSA);
		byte[] hash = computeDigest(inToTimeStamp);
		return getTimeStamp(uriTSA, hash);
	} 
	

	private TimeStampResponse getTimeStamp(URI uriTSA, byte[] sha1Digest) throws TSPException {
		TimeStampResponse response = null;
		
		ByteArrayInputStream bis = null;
		OutputStream out = null;
		
		
		try {
			
			TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
			
			SecureRandom randomGenerator =  SecureRandom.getInstance("SHA1PRNG");
			long nonce = randomGenerator.nextLong();
			
			// request with digestAlgorithmOID, byte[] digest, java.math.BigInteger nonce
			TimeStampRequest request = reqGen.generate(TSPAlgorithms.SHA1, sha1Digest, BigInteger.valueOf(nonce));

			byte[] reqData = request.getEncoded();
			
			HttpURLConnection conn = (HttpURLConnection) uriTSA.toURL().openConnection();
			
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false); 
			
			conn.setRequestProperty("Content-Type","application/timestamp-query"); 
			conn.setRequestProperty("Content-Length", Long.toString(reqData.length));
			conn.setRequestMethod("POST");
			
			out = conn.getOutputStream();
			bis = new ByteArrayInputStream(reqData);
			
			byte[] tab = new byte[1024];
			
			int lu = bis.read(tab);
			
			while(lu>=0) {
				out.write(tab, 0, lu);
			    lu = bis.read(tab);
			}
			
			out.flush();

			int returnCode = conn.getResponseCode();
			
			if (returnCode == HttpURLConnection.HTTP_OK){
			    
			    InputStream in = conn.getInputStream();
				response = new TimeStampResponse(in);
			    
				response.validate (request);    // if it fails a TSPException is raised
			} else {
				//404 or 500 ...
				throw new TSPException("service TSA is not available");
			}
		} catch (ProtocolException e) {
			throw new TSPException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TSPException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new TSPException(e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.toString());
				}
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					logger.error(e.toString());
				}
			}
		}
		
		return response;
	}
	
	
	private byte[] computeDigest(InputStream is) throws TSPException {
		byte[] result = null;
		MessageDigest dig;
		
		try {
			dig = MessageDigest.getInstance("SHA-1");
			byte[] bytes = new byte[2048];
			int numBytes;

			while ((numBytes = is.read(bytes)) != -1) {
				dig.update(bytes, 0, numBytes);
			}
			result = dig.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new TSPException(e.getMessage(), e);
		} catch (IOException e) {
			throw new TSPException(e.getMessage(), e);
		}
        
        return result;
	}
	
	
	public Date getGenerationTime(TimeStampResponse response) {
	    TimeStampToken  tsToken = response.getTimeStampToken();
	    TimeStampTokenInfo tsInfo = tsToken.getTimeStampInfo();
	    
	    return tsInfo.getGenTime();
	}
	
	
    public SignerId getSignerID(TimeStampResponse response) {
	    TimeStampToken  tsToken = response.getTimeStampToken();
		
	    return tsToken.getSID();
	}
}
