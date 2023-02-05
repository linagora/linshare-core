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
package org.linagora.linshare.core.service;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Date;

import org.bouncycastle.cms.SignerId;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;


/**
 * Services timestamp for a doc.
 */
public interface TimeStampingService {
	
	
	/**
	 * get timestamp on a file
	 * @param inToTimeStamp inpustream to a file to timestamp
	 * @return TimeStampResponse
	 * @throws TSPException any problems with the tsa
	 */
	public TimeStampResponse getTimeStamp(String urlTSA, InputStream inToTimeStamp) throws TSPException, URISyntaxException ;
	
	
    /**
     * get signer info
     * Signer ID serial is signerId.getSerialNumber()
     * Signer ID issuer is signerId.getIssuerAsString()
     * @param response Tsp response
     * @return signerId of a tsa response
     */
	public SignerId getSignerID(TimeStampResponse response);
	
	/**
	 * get time of timestamp
	 * @param response Tsp response
	 * @return date
	 */
	public Date getGenerationTime(TimeStampResponse response);
}
