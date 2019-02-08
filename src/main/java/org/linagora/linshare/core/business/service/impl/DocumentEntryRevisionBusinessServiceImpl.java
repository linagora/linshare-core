/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.business.service.impl;

import java.io.File;

import org.linagora.linshare.core.business.service.DocumentEntryRevisionBusinessService;
import org.linagora.linshare.core.business.service.SignatureBusinessService;
import org.linagora.linshare.core.business.service.ThumbnailGeneratorBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestEntryBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.ThumbnailRepository;
import org.linagora.linshare.core.service.TimeStampingService;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollectorMongoRepository;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentEntryRevisionBusinessServiceImpl extends DocumentEntryBusinessServiceImpl
		implements DocumentEntryRevisionBusinessService {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(DocumentEntryRevisionBusinessServiceImpl.class);

	public DocumentEntryRevisionBusinessServiceImpl(
			final FileDataStore fileSystemDao,
			final TimeStampingService timeStampingService,
			final DocumentEntryRepository documentEntryRepository,
			final DocumentRepository documentRepository,
			final SignatureBusinessService signatureBusinessService,
			final UploadRequestEntryBusinessService uploadRequestEntryBusinessService,
			final ThumbnailGeneratorBusinessService thumbnailGeneratorBusinessService,
			final boolean deduplication,
			final WorkGroupNodeMongoRepository repository,
			final DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository,
			final ThumbnailRepository thumbnailRepository) {
		super(fileSystemDao, timeStampingService, documentEntryRepository, documentRepository, signatureBusinessService,
				uploadRequestEntryBusinessService, thumbnailGeneratorBusinessService, deduplication, repository,
				documentGarbageCollectorRepository, thumbnailRepository);
	}

	@Override
	public WorkGroupDocumentRevision createWorkGroupDocumentRevision(Account actor, WorkGroup workGroup, File myFile,
			Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType,
			WorkGroupNode parentNode) throws BusinessException {
		Document document = createDocument(workGroup, myFile, size, fileName, timeStampingUrl, mimeType);
		WorkGroupDocumentRevision node = new WorkGroupDocumentRevision(actor, fileName, document, workGroup, parentNode);
		setDocumentProperties(actor, node, fileName, parentNode, myFile, checkIfIsCiphered);
		node = repository.insert(node);
		return node;
	}
}
