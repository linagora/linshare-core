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
package org.linagora.linshare.core.business.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.MailAttachmentBusinessService;
import org.linagora.linshare.core.business.service.ThumbnailGeneratorBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailAttachment;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.MailAttachmentRepository;
import org.linagora.linshare.core.service.TimeStampingService;
import org.linagora.linshare.core.service.impl.AbstractDocumentBusinessServiceImpl;

import com.google.common.base.Strings;
import com.google.common.io.Files;

public class MailAttachmentBusinessServiceImpl extends AbstractDocumentBusinessServiceImpl implements MailAttachmentBusinessService{

	protected final MailAttachmentRepository attachmentRepository;

	private final MimeTypeMagicNumberDao mimeTypeIdentifier;

	public MailAttachmentBusinessServiceImpl(
			FileDataStore fileDataStore,
			TimeStampingService timeStampingService,
			DocumentRepository documentRepository,
			ThumbnailGeneratorBusinessService thumbnailGeneratorBusinessService,
			boolean deduplication,
			MailAttachmentRepository attachmentRepository,
			MimeTypeMagicNumberDao mimeTypeIdentifier) {
		super(fileDataStore, timeStampingService, documentRepository,
				thumbnailGeneratorBusinessService, deduplication);
		this.attachmentRepository = attachmentRepository;
		this.mimeTypeIdentifier = mimeTypeIdentifier;
	}

	@Override
	public MailAttachment create(Account authUser, boolean enable, String fileName, boolean enableForAll,
			MailConfig mailConfig, String description, String cid, Language language, File tempFile,
			String metaData) {
		String mimeType = mimeTypeIdentifier.getMimeType(tempFile);
		String sha256sum = SHA256CheckSumFileStream(tempFile);
		cid = Strings.isNullOrEmpty(cid) ? LinShareConstants.defaultMailAttachmentCid : cid;
		if(language == null && (!enableForAll)) {
			language = Language.ENGLISH;
		} else if (enableForAll) {
			language = null;
		}
		description = description == null ? "" : description;
		MailAttachment mailAttachment = new MailAttachment(enable, enableForAll, language, description, fileName,
				tempFile.length(), mimeType, sha256sum, mailConfig, cid);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.MAIL_ATTACHMENT, mailAttachment);
		try {
			metadata = fileDataStore.add(Files.asByteSource(tempFile), metadata);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_INSERT_DOCUMENT, "Can not store file when creating mail attachment.");
		}
		mailAttachment.setBucketUuid(metadata.getBucketUuid());
		return attachmentRepository.create(mailAttachment);
	}

	@Override
	public MailAttachment findByUuid(String uuid) {
		return attachmentRepository.findByUuid(uuid);
	}

	@Override
	public List<MailAttachment> findAllByMailConfig(MailConfig config) {
		return attachmentRepository.findAllByMailConfig(config);
	}

	@Override
	public MailAttachment update(MailAttachment mailAttachment) {
		mailAttachment.setModificationDate(new Date());
		return attachmentRepository.update(mailAttachment);
	}

	@Override
	public void delete(MailAttachment mailAttachment) {
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.MAIL_ATTACHMENT, mailAttachment);
		fileDataStore.remove(metadata);
		attachmentRepository.delete(mailAttachment);
	}
}
