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

package org.linagora.linshare.core.dao.jackRabbit;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.objects.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class JackRabbitFileSystemMock implements FileSystemDao {

	private Map<String, String> files;

	final private static Logger logger = LoggerFactory
			.getLogger(JackRabbitFileSystemMock.class);

	public JackRabbitFileSystemMock() {
		super();
		this.files = Maps.newConcurrentMap();
//		files.put("b5edf244-8b39-4f6a-8caa-559bec48407a", "linshare-default.properties");
	}

	@Override
	public void removeFileByUUID(String uuid) {
		logger.debug("mock method");
		files.remove(uuid);
	}

	@Override
	public String insertFile(String path, InputStream file, long size,
			String fileName, String mimeType) {
		logger.debug("mock method");
		String uuid = UUID.randomUUID().toString();
		files.put(uuid, fileName);
		return uuid;
	}

	@Override
	public FileInfo getFileInfoByUUID(String uuid) {
		logger.debug("mock method");
		return null;
	}

	@Override
	public InputStream getFileContentByUUID(String uuid) {
		logger.debug("mock method");
		String name = files.get(uuid);
		return Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(name);
	}

}
