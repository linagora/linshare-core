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
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.dao.JcloudObjectStorageFileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.io.Files;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {
		"classpath:springContext-test.xml",
		"classpath:OPTIONAL-springContext-storage-jcloud.xml" })
//@TestPropertySource("/linshare-storage-swift.properties")
public class JcloudObjectStorageFileDataStoreTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());


	@Autowired
	protected JcloudObjectStorageFileDataStore jcloudFileDataStore;

	@Value( "${linshare.documents.storage.providers}" )
	private String supportedProviders;

	@Value( "${linshare.documents.storage.mode}" )
	private String provider;

	@BeforeEach
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		logger.info("supportedProviders : " + supportedProviders);
		logger.info("Testing provider : " + provider);
		logger.info("fileDataStore : " + jcloudFileDataStore.toString());
		logger.debug(LinShareTestConstants.END_SETUP);
	}

	@AfterEach
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	@Test
	public void testUpload() throws BusinessException, URISyntaxException, IOException {
		logger.debug(LinShareTestConstants.BEGIN_TEST);
		String fileName = "CAS.bin.export";
		Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
		File file = path.toFile();
		FileMetaData metaData = new FileMetaData(FileMetaDataKind.DATA, "application/octet-stream", file.length(),
				fileName);
		FileMetaData data = jcloudFileDataStore.add(Files.asByteSource(file), metaData);
		Assertions.assertNotNull(data);
		logger.info("FileMetaData : " + data.toString());
		logger.debug(LinShareTestConstants.END_TEST);
	}
}
