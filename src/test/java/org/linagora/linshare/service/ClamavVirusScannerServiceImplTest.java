/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.linagora.linshare.core.business.service.VirusScannerBusinessService;
import org.linagora.linshare.core.business.service.impl.ClamavVirusScannerBusinessServiceImpl;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClamavVirusScannerServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(ClamavVirusScannerServiceImplTest.class);
	
	private static VirusScannerBusinessService virusScannerService;

	private static String fileNameToCheck;

	@BeforeAll
	public static void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		
		Properties properties = new Properties();
		try {
			InputStream is = ClamavVirusScannerServiceImplTest.class.getResourceAsStream("/linshare-test.properties");
			if (is ==  null)
				throw new Exception("Impossible to parse linshare-test.properties");
			properties.load(is);
		} catch (IOException e) {
			throw new Exception("Impossible to parse linshare-test.properties");
		}
		String clamavHost = (String) properties.get("test.virusscanner.clamav.host");
		Integer clamavPort = Integer.valueOf((String) properties.get("test.virusscanner.clamav.port"));
		fileNameToCheck = properties.getProperty("test.virusscanner.clamav.filetocheck");
		virusScannerService = new ClamavVirusScannerBusinessServiceImpl(clamavHost,clamavPort.intValue());
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@Test
	public void checkCommunicationFailed() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		boolean hasFailed=false;
		try {
			VirusScannerBusinessService wrongVirusScannerService = new ClamavVirusScannerBusinessServiceImpl("localhost",1234);
			wrongVirusScannerService.check(this.getClass().getResourceAsStream("/linshare-test.properties"));
		} catch (TechnicalException e) {
			hasFailed = true;
		}
		Assertions.assertTrue(hasFailed);
		logger.debug(LinShareTestConstants.END_TEST);
	}


	@Test
	public void checkSteam() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		try{

			boolean flag=virusScannerService.check(this.getClass().getResourceAsStream("/linshare-test.properties"));
			Assertions.assertTrue(flag);
		}catch(TechnicalException co){
			if(co.getErrorCode().equals(TechnicalErrorCode.VIRUS_SCANNER_IS_DISABLED)){
				Assertions.assertTrue(true, "WARNING ! The virus scan is disabled");
			}else if(co.getErrorCode().equals(TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED)){
				Assertions.assertTrue(true, "WARNING ! The communication to the virus scanner is failed");
			}
		}
		logger.debug(LinShareTestConstants.END_TEST);

	}

	@Test
	public void checkFile() {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		try{
			File f = new File(fileNameToCheck);
			if(!f.exists()){
				Assertions.assertTrue(true, "WARNING you haven't set a correct path to the file to test the anti virus in linshare-test.properties");
			}

			boolean flag=virusScannerService.check(f);
			Assertions.assertTrue(flag);
		}catch(TechnicalException co){
			if(co.getErrorCode().equals(TechnicalErrorCode.VIRUS_SCANNER_IS_DISABLED)){
				Assertions.assertTrue(true, "WARNING ! The virus scan is disabled");
			}else 
				if(co.getErrorCode().equals(TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED)){
					Assertions.assertTrue(true, "WARNING ! The communication to the virus scanner is failed");
				}
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void checkStreamWhenDisabled() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		boolean hasFailed=false;
		try {
			VirusScannerBusinessService disabledVirusScannerService = new ClamavVirusScannerBusinessServiceImpl("",1234);
			disabledVirusScannerService.check(this.getClass().getResourceAsStream("/linshare-test.properties"));
		} catch (TechnicalException e) {
			if (TechnicalErrorCode.VIRUS_SCANNER_IS_DISABLED.equals(e.getErrorCode()))
				hasFailed = true;
		}
		Assertions.assertTrue(hasFailed);	
		logger.debug(LinShareTestConstants.END_TEST);
	}

}
