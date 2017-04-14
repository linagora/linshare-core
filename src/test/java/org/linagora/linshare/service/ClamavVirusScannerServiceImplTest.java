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
package org.linagora.linshare.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
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

	@BeforeClass
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
		Integer clamavPort = new Integer((String) properties.get("test.virusscanner.clamav.port"));
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
		Assert.assertTrue(hasFailed);
		logger.debug(LinShareTestConstants.END_TEST);
	}


	@Test
	public void checkSteam() {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		try{

			boolean flag=virusScannerService.check(this.getClass().getResourceAsStream("/linshare-test.properties"));
			Assert.assertTrue(flag);
		}catch(TechnicalException co){
			if(co.getErrorCode().equals(TechnicalErrorCode.VIRUS_SCANNER_IS_DISABLED)){
				Assert.assertTrue("WARNING ! The virus scan is disabled", true);
			}else if(co.getErrorCode().equals(TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED)){
				Assert.assertTrue("WARNING ! The communication to the virus scanner is failed", true);
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
				Assert.assertTrue("WARNING you haven't set a correct path to the file to test the anti virus in linshare-test.properties",true);
			}

			boolean flag=virusScannerService.check(f);
			Assert.assertTrue(flag);
		}catch(TechnicalException co){
			if(co.getErrorCode().equals(TechnicalErrorCode.VIRUS_SCANNER_IS_DISABLED)){
				Assert.assertTrue("WARNING ! The virus scan is disabled", true);
			}else 
				if(co.getErrorCode().equals(TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED)){
					Assert.assertTrue("WARNING ! The communication to the virus scanner is failed", true);
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
		Assert.assertTrue(hasFailed);	
		logger.debug(LinShareTestConstants.END_TEST);
	}

}
