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
package org.linagora.linshare.dao;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {
		"classpath:springContext-dao.xml",
		"classpath:springContext-test.xml" })
public class MimeTypeMagicNumberTikaTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static Logger logger = LoggerFactory.getLogger(MimeTypeMagicNumberTikaTest.class);

	@Qualifier("mimeTypeMagicNumberDao")
	@Autowired
	private MimeTypeMagicNumberDao mimeTypeService;

	@BeforeEach
	public void setUp() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testMimeTypeCount() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Set<MimeType> allSupportedMimeType = mimeTypeService.getAllMimeType();
		logger.debug("allSupportedMimeType size : " + allSupportedMimeType.size());
		Assertions.assertEquals(1552, allSupportedMimeType.size());
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
			Assertions.assertEquals("application/vnd.oasis.opendocument.spreadsheet", mime);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getCause().toString());
			Assertions.assertEquals(true, false);
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
			Assertions.assertEquals("image/png", mime);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getCause().toString());
			Assertions.assertEquals(true, false);
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
			Assertions.assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", mime);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getCause().toString());
			Assertions.assertEquals(true, false);
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
			Assertions.assertEquals("text/x-python", mime);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getCause().toString());
			Assertions.assertEquals(true, false);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMimeTypeDetection5() throws BusinessException {

		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource("fichier.test.1.pdf");
			File f = new File(url.getPath());
			logger.debug("filename " + f.getName());
			String mime = mimeTypeService.getMimeType(f);
			Assertions.assertEquals("application/pdf", mime);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getCause().toString());
			Assertions.assertEquals(true, false);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}

	@Test
	public void testMimeTypeDetection6() {

		logger.info(LinShareTestConstants.BEGIN_TEST);
		try {

			URL url = Thread.currentThread().getContextClassLoader().getResource("fichier.test.1.jpg");
			File f = new File(url.getPath());
			logger.debug("filename " + f.getName());
			String mime = mimeTypeService.getMimeType(f);
			Assertions.assertEquals("image/jpeg", mime);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.debug(e.getCause().toString());
			Assertions.assertEquals(true, false);
		}
		logger.debug(LinShareTestConstants.END_TEST);
	}


}
