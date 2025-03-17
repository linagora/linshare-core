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
package org.linagora.linshare.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.linagora.linshare.core.business.service.VirusScannerBusinessService;
import org.linagora.linshare.core.business.service.impl.ClamavVirusScannerBusinessServiceImpl;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClamavVirusScannerServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(ClamavVirusScannerServiceImplTest.class);

	private VirusScannerBusinessService virusScannerService;

	private String fileNameToCheck;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		final Properties properties = loadProperties();
		final String clamavHost = properties.getProperty("test.virusscanner.clamav.host");
		int clamavPort = Integer.parseInt(properties.getProperty("test.virusscanner.clamav.port"));
		fileNameToCheck = properties.getProperty("test.virusscanner.clamav.filetocheck");
		virusScannerService = new ClamavVirusScannerBusinessServiceImpl(clamavHost, clamavPort);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@Test
	void checkCommunicationFailed() {
		VirusScannerBusinessService wrongVirusScannerService = new ClamavVirusScannerBusinessServiceImpl("localhost",
				1234);
		final TechnicalException exception = assertThrows(TechnicalException.class, () -> {
			wrongVirusScannerService.check(this.getClass().getResourceAsStream("/linshare-test.properties"));
		});
		assertEquals(TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED, exception.getErrorCode());

	}

	@Test
	void checkSteam() {
		assertDoesNotThrow(() -> {
			final boolean flag = virusScannerService.check(this.getClass().getResourceAsStream("/linshare-test.properties"));
			assertTrue(flag, "The stream should be considered safe by the antivirus");
		});
	}

	@Test
	void checkFile() {
		try {
			final File f = new File(fileNameToCheck);
			if (!f.exists()) {
				assertTrue(true,
						"WARNING you haven't set a correct path to the file to test the anti virus in linshare-test.properties");
			}

			final boolean flag = virusScannerService.check(f);
			assertTrue(flag);
		} catch (final TechnicalException co) {
			if (co.getErrorCode().equals(TechnicalErrorCode.VIRUS_SCANNER_IS_DISABLED)) {
				assertTrue(true, "WARNING ! The virus scan is disabled");
			} else if (co.getErrorCode().equals(TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED)) {
				assertTrue(true, "WARNING ! The communication to the virus scanner is failed");
			}
		}
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "invalidHost" })
	void testCheckStreamWhenDisabledOrInvalidHost(final String host) {
		final VirusScannerBusinessService service = new ClamavVirusScannerBusinessServiceImpl(host, 1234);
		final TechnicalException exception = assertThrows(TechnicalException.class, () -> {
			service.check(this.getClass().getResourceAsStream("/linshare-test.properties"));
		});
		assertTrue(TechnicalErrorCode.VIRUS_SCANNER_IS_DISABLED.equals(exception.getErrorCode())
				|| TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED.equals(exception.getErrorCode()));
	}

	private static Properties loadProperties() throws Exception {
		final Properties properties = new Properties();
		try (final InputStream is = ClamavVirusScannerServiceImplTest.class.getResourceAsStream("/linshare-test.properties")){
			if (is == null)
				throw new Exception("Impossible to parse linshare-test.properties");
			properties.load(is);
		} catch (final IOException e) {
			throw new Exception("Impossible to parse linshare-test.properties");
		}
		return properties;
	}

}
