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
package org.linagora.linShare.core.service;

import java.io.InputStream;
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
	public TimeStampResponse getTimeStamp(InputStream inToTimeStamp) throws TSPException;
	
	
	/**
	 * disabled if no configuration in file properties
	 * @return
	 */
	public boolean isDisabled();
	
	
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
