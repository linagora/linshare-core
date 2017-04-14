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
package org.linagora.linshare.core.business.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import net.taldius.clamav.ScannerException;
import net.taldius.clamav.impl.NetworkScanner;

import org.linagora.linshare.core.business.service.VirusScannerBusinessService;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a Clamav implementation of VirusScannerService To use this
 * implementation you must run the ClamavDaemon and configure it to be opened on
 * a TCP port by adding "TCPSocket 3310" to your clamad.conf
 */
public class ClamavVirusScannerBusinessServiceImpl implements VirusScannerBusinessService {
	
	private static final Logger logger = LoggerFactory.getLogger(ClamavVirusScannerBusinessServiceImpl.class);

	private static final int defaultConnectionTimeout = 90;

	private boolean disabled = false;

	private String clamdHost = "127.0.0.1";

	private Integer clamdPort = 3310;

	public ClamavVirusScannerBusinessServiceImpl(String clamdHost, int clamdPort) {
		this.clamdHost = clamdHost;
		this.clamdPort = clamdPort;
		if (clamdHost.length() == 0) {
			disabled = true;
		}
	}

	private NetworkScanner getNewClamavScanner(String clamdHost, int clamdPort,
			int connectionTimeout) {
		NetworkScanner clamavScan = new NetworkScanner();
		clamavScan.setClamdHost(clamdHost);
		clamavScan.setClamdPort(clamdPort);
		clamavScan.setConnectionTimeout(connectionTimeout);
		return clamavScan;
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
			NetworkScanner clamavScanner = getNewClamavScanner(clamdHost, clamdPort, defaultConnectionTimeout);
			// Check if the streamToCheck contains virus
			boolean isSafe = clamavScanner.performScan(steamToCheck);
			// consume the messages
			clamavScanner.reset();
			return isSafe;
		} catch (ScannerException e) {
			Throwable ioException = e.getCause();
			logger.error(ioException.getMessage());
			logger.debug(ioException.toString());
			throw new TechnicalException(
					TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED, e
							.getMessage(), e.getCause());
		}
	}

	@Override
	public String getHost() {
		return clamdHost;
	}

	@Override
	public void setHost(String host) {
		logger.warn("Reconfiguring Clamav current host ...");
		synchronized (clamdHost) {
			try {
				clamdHost = host;
				logger.warn("Clamav current host reconfigured to " + clamdHost);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Clamav reconfiguration failed ! ");
			}
		}
	}

	@Override
	public Integer getPort() {
		return clamdPort;
	}

	@Override
	public void setPort(Integer port) throws Exception {
		logger.warn("Reconfiguring Clamav current port ...");
		if (port.equals(0)) {
			throw new Exception("invalid port value : " + port);
		}
		synchronized (clamdPort) {
			try {
				clamdPort = port;
				logger.warn("Clamav current port reconfigured to " + clamdPort);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Clamav reconfiguration failed ! ");
			}
		}
	}

}
