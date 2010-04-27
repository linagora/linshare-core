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
package org.linagora.linShare.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.taldius.clamav.ScannerException;
import net.taldius.clamav.impl.NetworkScanner;

import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.service.VirusScannerService;

/**
 * This class is a Clamav implementation of VirusScannerService To use this
 * implementation you must run the ClamavDaemon and configure it to be opened on
 * a TCP port by adding "TCPSocket 3310" to your clamad.conf
 */
public class ClamavVirusScannerServiceImpl implements VirusScannerService {

	private static final int defaultConnectionTimeout = 90;

	private NetworkScanner clamavScanner;
	private boolean disabled = false;

	public ClamavVirusScannerServiceImpl(String clamdHost, int clamdPort) {
		this(clamdHost, clamdPort, defaultConnectionTimeout);
	}

	public ClamavVirusScannerServiceImpl(String clamdHost, int clamdPort,
			int connectionTimeout) {
		if (clamdHost.length() == 0) {
			disabled = true;
		} else {
			clamavScanner = new NetworkScanner();
			clamavScanner.setClamdHost(clamdHost);
			clamavScanner.setClamdPort(clamdPort);
			clamavScanner.setConnectionTimeout(connectionTimeout);
		}

	}

	public boolean isDisabled() {
		return disabled;
	}

	public boolean check(File fileToCheck) throws TechnicalException {
		try {
			InputStream fileAsStream = new FileInputStream(fileToCheck);
			return check(fileAsStream);
		} catch (FileNotFoundException e) {
			throw new TechnicalException(
					TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED, e
							.getMessage(), e.getCause());
		}

	}

	public boolean check(InputStream steamToCheck) throws TechnicalException{
		if (disabled) 
			throw new TechnicalException(TechnicalErrorCode.VIRUS_SCANNER_IS_DISABLED, "VirusScanner is disabled");
		try {
			// Check if the streamToCheck contains virus
			boolean isSafe = clamavScanner.performScan(steamToCheck);
			// consume the messages
			clamavScanner.reset();
			return isSafe;
		} catch (ScannerException e) {
			throw new TechnicalException(
					TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED, e
							.getMessage(), e.getCause());
		}
	}

}
