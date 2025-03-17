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
package org.linagora.linshare.core.business.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.linagora.linshare.core.business.service.VirusScannerBusinessService;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.ClamavException;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import javax.annotation.Nonnull;

/**
 * This class is a Clamav implementation of VirusScannerService To use this
 * implementation you must run the ClamavDaemon and configure it to be opened on
 * a TCP port by adding "TCPSocket 3310" to your clamad.conf
 */
public class ClamavVirusScannerBusinessServiceImpl implements VirusScannerBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(ClamavVirusScannerBusinessServiceImpl.class);

	private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 90 * 1000;

	private boolean disabled = false;

	private String clamdHost = "127.0.0.1";

	private Integer clamdPort = 3310;

	private ClamavClient clamavClient;

	private final Object lock = new Object();

	public ClamavVirusScannerBusinessServiceImpl(@NonNull final String clamdHost, @NonNull final int clamdPort) {
		this.clamdHost = clamdHost;
		this.clamdPort = clamdPort;
		if (clamdHost.length() == 0) {
			this.disabled = true;
		}
		else{
			this.clamavClient = new ClamavClient(clamdHost, clamdPort);
		}
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	@Override
	public boolean check(@Nonnull final File fileToCheck) throws TechnicalException {
		try (final InputStream fileAsStream = new FileInputStream(fileToCheck)) {
			return this.check(fileAsStream);
		} catch (final FileNotFoundException e) {
			throw new TechnicalException(TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED,
					String.format("File to scan not found: '%s' !!", fileToCheck.getAbsolutePath()), e);
		} catch (final IOException e) {
			throw new TechnicalException(TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED,
					String.format("An error occurs closing the file '%s'. Ignored", fileToCheck.getAbsolutePath()), e);
		}
	}

	public boolean check(@Nonnull final InputStream streamToCheck) throws TechnicalException{
		if (this.disabled)
			throw new TechnicalException(TechnicalErrorCode.VIRUS_SCANNER_IS_DISABLED, "VirusScanner is disabled");
		try {
			return this.clamavClient.scan(streamToCheck) instanceof ScanResult.OK;
		} catch (final ClamavException e) {
			logger.error("Error during ClamAV scan: {}", e.getMessage(), e);
			throw new TechnicalException(
					TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED, e
							.getMessage(), e.getCause());
		}
	}

	@Override
	public String getHost() {
		return this.clamdHost;
	}

	@Override
	public void setHost(String host) {
		logger.info("Reconfiguring Clamav current host ...");
		synchronized (this.lock) {
				this.clamdHost = host;
				logger.info("Clamav current host reconfigured to {}", this.clamdHost);

		}
	}

	@Override
	public Integer getPort() {
		return this.clamdPort;
	}

	@Override
	public void setPort(Integer port) throws Exception {
		logger.warn("Reconfiguring Clamav current port ...");
		if (port == 0) {
			throw new TechnicalException(
					TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED,
					"Invalid port value: " + port
			);
		}
		synchronized (this.lock) {
				this.clamdPort = port;
				logger.warn("Clamav current port reconfigured to {} " ,this.clamdPort);
		}
	}

}
