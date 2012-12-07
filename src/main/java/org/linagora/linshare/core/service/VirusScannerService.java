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
package org.linagora.linshare.core.service;

import java.io.File;
import java.io.InputStream;

import org.linagora.linshare.core.exception.TechnicalException;

/**
 * Interface to VirusScanner
 */
public interface VirusScannerService {
	
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
}
