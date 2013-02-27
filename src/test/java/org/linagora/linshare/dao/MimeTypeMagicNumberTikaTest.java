/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.dao;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AllowedMimeType;
import org.linagora.linshare.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:springContext-dao.xml", "classpath:springContext-test.xml" })
public class MimeTypeMagicNumberTikaTest extends AbstractJUnit4SpringContextTests {

	private static Logger logger = LoggerFactory.getLogger(MimeTypeMagicNumberTikaTest.class);

	@Qualifier("mimeTypeMagicNumberDao2")
	@Autowired
	private MimeTypeMagicNumberDao mimeTypeService;

	@Before
	public void setUp() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@After
	public void tearDown() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testMimeTypeCount() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		List<AllowedMimeType> allSupportedMimeType = mimeTypeService.getAllSupportedMimeType();
		logger.debug("allSupportedMimeType size : " + allSupportedMimeType.size());
		// old library : 161, new one : 1385
		Assert.assertEquals(1385, allSupportedMimeType.size());
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMimeTypeDetection1() {

		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource("fichier.test.1.ods");
			File f = new File(url.getPath());
			logger.debug("filename " + f.getName());
			String mime = mimeTypeService.getMimeType(f);
			Assert.assertEquals("application/vnd.oasis.opendocument.spreadsheet", mime);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getCause().toString());
			Assert.assertEquals(true, false);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMimeTypeDetection2() {

		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {

			URL url = Thread.currentThread().getContextClassLoader().getResource("fichier.test.1.png");
			File f = new File(url.getPath());
			logger.debug("filename " + f.getName());
			String mime = mimeTypeService.getMimeType(f);
			Assert.assertEquals("image/png", mime);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getCause().toString());
			Assert.assertEquals(true, false);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMimeTypeDetection3() {

		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource("fichier.test.1.docx");
			File f = new File(url.getPath());
			logger.debug("filename " + f.getName());
			String mime = mimeTypeService.getMimeType(f);
			Assert.assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", mime);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getCause().toString());
			Assert.assertEquals(true, false);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMimeTypeDetection4() {

		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {

			URL url = Thread.currentThread().getContextClassLoader().getResource("fichier.test.1.py");
			File f = new File(url.getPath());
			logger.debug("filename " + f.getName());
			String mime = mimeTypeService.getMimeType(f);
			Assert.assertEquals("text/x-python", mime);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getCause().toString());
			Assert.assertEquals(true, false);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

}
