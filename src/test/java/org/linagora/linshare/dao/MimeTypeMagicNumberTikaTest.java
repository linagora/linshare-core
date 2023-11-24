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
		Assertions.assertEquals(1653, allSupportedMimeType.size());
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
