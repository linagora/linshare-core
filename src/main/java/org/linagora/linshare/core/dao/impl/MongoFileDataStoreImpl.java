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
package org.linagora.linshare.core.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;

import com.google.common.collect.Iterators;
import com.google.common.io.ByteSource;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;

public class MongoFileDataStoreImpl implements FileDataStore {

	private static final Logger logger = LoggerFactory.getLogger(MongoFileDataStoreImpl.class);

	private GridFsOperations gridOperations;

	private SimpleMongoClientDatabaseFactory mongoDbFactory;

	public MongoFileDataStoreImpl(GridFsOperations gridOperations,
			SimpleMongoClientDatabaseFactory mongoDbFactory) {
		super();
		this.gridOperations = gridOperations;
		this.mongoDbFactory = mongoDbFactory;
	}

	@Override
	public void remove(FileMetaData metadata) {
		Query query = new Query().addCriteria(Criteria.where("metadata.uuid").is(metadata.getUuid()));
		GridFSFindIterable find = gridOperations.find(query);
		if (find == null) {
			logger.warn("Can not remove document '{}' in gridfs", metadata.getUuid());
		} else {
			checkNotTooMany(metadata, find);
			gridOperations.delete(query);
		}
	}

	@Override
	public FileMetaData add(ByteSource byteSource, FileMetaData metadata) throws IOException {
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
		gridOperations.store(byteSource.openBufferedStream(), metadata.getFileName(), metadata.getMimeType(), meta);
		return metadata;
	}

	@Override
	public ByteSource get(FileMetaData metadata) {
		Query query = new Query().addCriteria(Criteria.where("metadata.uuid").is(metadata.getUuid()));
		GridFSFindIterable find = gridOperations.find(query);
		if (find == null) {
			logger.error("Can not find document '{}' in gridfs", metadata.getUuid());
			throw new TechnicalException(TechnicalErrorCode.GENERIC,
					"Can not find document in gridfs : " + metadata.getUuid());
		}
		checkNotTooMany(metadata, find);
		return new ByteSource() {
			@Override
			public InputStream openStream() throws IOException {
				GridFSDownloadStream gridFSDownloadStream = getGridFs().openDownloadStream(find.first().getObjectId());
				GridFsResource gridFsResource = new GridFsResource(find.first(), gridFSDownloadStream);
				return gridFsResource.getInputStream();
			}
		};
	}

	private void checkNotTooMany(FileMetaData metadata, GridFSFindIterable find) {
		int size = Iterators.size(find.iterator());
		if (size >= 2) {
			logger.error("Too many results found : {} for document '{}'.", size, metadata.getUuid());
			throw new TechnicalException(TechnicalErrorCode.GENERIC,
					"Too many results found in gridfs : " + metadata.getUuid());
		}
	}

	@Override
	public boolean exists(FileMetaData metadata) {
		Query query = new Query().addCriteria(Criteria.where("metadata.uuid").is(metadata.getUuid()));
		GridFSFile find = gridOperations.findOne(query);
		if (find == null) {
			return false;
		}
		return true;
	}

	private GridFSBucket getGridFs() {
		MongoDatabase db = mongoDbFactory.getMongoDatabase();
		return GridFSBuckets.create(db);
	}
}
