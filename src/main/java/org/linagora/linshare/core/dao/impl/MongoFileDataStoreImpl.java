/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
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

package org.linagora.linshare.core.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

public class MongoFileDataStoreImpl implements FileDataStore {

	private static final Logger logger = LoggerFactory.getLogger(MongoFileDataStoreImpl.class);

	private GridFsOperations gridOperations;

	public MongoFileDataStoreImpl(GridFsOperations gridOperations) {
		super();
		this.gridOperations = gridOperations;
	}

	@Override
	public void remove(FileMetaData metadata) {
		Query query = new Query().addCriteria(Criteria.where("metadata.uuid").is(metadata.getUuid()));
		List<GridFSDBFile> find = gridOperations.find(query);
		if (find == null || find.isEmpty()) {
			logger.warn("Can not remove document '{}' in gridfs", metadata.getUuid());
		} else {
			checkNotTooMany(metadata, find);
			gridOperations.delete(query);
		}
	}

	@Override
	public FileMetaData add(File file, FileMetaData metadata) {
		try (FileInputStream fis = new FileInputStream(file)) {
			return metadata = add(fis, metadata);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Can not add a new file : " + e.getMessage());
		}
	}

	@Override
	public FileMetaData add(InputStream inputStream, FileMetaData metadata) {
		DBObject meta = new BasicDBObject();
		// It is not used/useful for mongo.
		// meta.put("bucketUuid", metadata.getBucketUuid());
		if (metadata.getUuid() == null) {
			metadata.setUuid(UUID.randomUUID().toString());
		}
		meta.put("uuid", metadata.getUuid());
		// Mongo does not support empty file name.
		if (metadata.getFileName() == null) {
			metadata.setFileName(UUID.randomUUID().toString());
		}
		// this throws MongoException
		gridOperations.store(inputStream, metadata.getFileName(), metadata.getMimeType(), meta);
		return metadata;
	}

	@Override
	public InputStream get(FileMetaData metadata) {
		Query query = new Query().addCriteria(Criteria.where("metadata.uuid").is(metadata.getUuid()));
		List<GridFSDBFile> find = gridOperations.find(query);
		if (find == null || find.isEmpty()) {
			logger.error("Can not find document '{}' in gridfs", metadata.getUuid());
			throw new TechnicalException(TechnicalErrorCode.GENERIC,
					"Can not find document in gridfs : " + metadata.getUuid());
		}
		checkNotTooMany(metadata, find);
		return find.get(0).getInputStream();
	}

	private void checkNotTooMany(FileMetaData metadata, List<GridFSDBFile> find) {
		if (find.size() >= 2) {
			logger.error("Too many results found : {} for document '{}'.", find.size(), metadata.getUuid());
			throw new TechnicalException(TechnicalErrorCode.GENERIC,
					"Too many results found in gridfs : " + metadata.getUuid());
		}
	}

	@Override
	public boolean exists(FileMetaData metadata) {
		Query query = new Query().addCriteria(Criteria.where("metadata.uuid").is(metadata.getUuid()));
		List<GridFSDBFile> find = gridOperations.find(query);
		if (find == null || find.isEmpty()) {
			return false;
		}
		checkNotTooMany(metadata, find);
		return true;
	}

}
