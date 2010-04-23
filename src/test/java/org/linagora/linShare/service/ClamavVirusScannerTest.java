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
package org.linagora.linShare.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.service.VirusScannerService;
import org.linagora.linShare.core.service.impl.ClamavVirusScannerServiceImpl;

public class ClamavVirusScannerTest {

	private static VirusScannerService virusScannerService;
	private static String fileNameToCheck;

	@BeforeClass
	public static void setUp() throws Exception {
		Properties properties = new Properties();
		try {
			InputStream is = ClamavVirusScannerTest.class.getResourceAsStream("/linShare-test.properties");
			if (is ==  null)
				throw new Exception("Impossible to parse linShare-test.properties");
			properties.load(is);
		} catch (IOException e) {
			throw new Exception("Impossible to parse linShare-test.properties");
		}
		String clamavHost = (String) properties.get("test.virusscanner.clamav.host");
		Integer clamavPort = new Integer((String) properties.get("test.virusscanner.clamav.port"));
		fileNameToCheck = (String) properties.getProperty("test.virusscanner.clamav.filetocheck");
		virusScannerService = new ClamavVirusScannerServiceImpl(clamavHost,clamavPort.intValue());
	}

	@Test
	public void checkCommunicationFailed() {
		boolean hasFailed=false;
		try {
			VirusScannerService wrongVirusScannerService = new ClamavVirusScannerServiceImpl("localhost",1234);
			wrongVirusScannerService.check(this.getClass().getResourceAsStream("/linShare-test.properties"));
		} catch (TechnicalException e) {
			hasFailed = true;
		}
		Assert.assertTrue(hasFailed);

	}


	@Test
	public void checkSteam() {
		try{

			boolean flag=virusScannerService.check(this.getClass().getResourceAsStream("/linShare-test.properties"));
			Assert.assertTrue(flag);
		}catch(TechnicalException co){
			if(co.getErrorCode().equals(TechnicalErrorCode.VIRUS_SCANNER_IS_DISABLED)){
				Assert.assertTrue("WARNING ! The virus scan is disabled", true);
			}else if(co.getErrorCode().equals(TechnicalErrorCode.VIRUS_SCANNER_COMMUNICATION_FAILED)){
				Assert.assertTrue("WARNING ! The communication to the virus scanner is failed", true);
			}
		}


	}

	@Test
	public void checkFile() {


		try{
			File f = new File(fileNameToCheck);
			if(!f.exists()){
				Assert.assertTrue("WARNING you haven't set a correct path to the file to test the anti virus in linShare-test.properties",true);
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
	}

	@Test
	public void checkStreamWhenDisabled() {
		boolean hasFailed=false;
		try {
			VirusScannerService disabledVirusScannerService = new ClamavVirusScannerServiceImpl("",1234);
			disabledVirusScannerService.check(this.getClass().getResourceAsStream("/linShare-test.properties"));
		} catch (TechnicalException e) {
			if (TechnicalErrorCode.VIRUS_SCANNER_IS_DISABLED.equals(e.getErrorCode()))
				hasFailed = true;
		}
		Assert.assertTrue(hasFailed);	
	}

}
