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
package org.linagora.linshare.core.business.service;

import java.io.File;
import java.io.InputStream;

import org.linagora.linshare.core.exception.TechnicalException;

/**
 * Interface to VirusScanner
 */
public interface VirusScannerBusinessService {
	
	/**
	 * @return true if the virusScanner is disabled
	 */
	boolean isDisabled();
	
	/**
	 * Check if a file is safe
	 * @param fileToCheck a file to check
	 * @return return true if the file is safe. return false if the file contains a virus
	 */
	boolean check(File fileToCheck) throws TechnicalException;
	
	/**
	 * Check if a stream is safe
	 * @param steamToCheck a stream to check
	 * @return return true if the stream is safe. return false if the stream contains a virus
	 */
	boolean check(InputStream steamToCheck);

	/**
	 * For JMX purpose.
	 */

	String getHost();

	void setHost(String host);

	Integer getPort();

	void setPort(Integer port) throws Exception;
}
