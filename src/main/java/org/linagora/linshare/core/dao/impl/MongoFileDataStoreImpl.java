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
		metadata.setUuid(UUID.randomUUID().toString());
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
