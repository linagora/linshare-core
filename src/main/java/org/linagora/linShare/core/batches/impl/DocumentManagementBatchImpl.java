/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.batches.impl;

import java.io.InputStream;
import java.util.List;

import org.linagora.linShare.core.batches.DocumentManagementBatch;
import org.linagora.linShare.core.dao.FileSystemDao;
import org.linagora.linShare.core.domain.constants.Reason;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Batch for document management.
 *
 */
public class DocumentManagementBatchImpl implements DocumentManagementBatch {

    Logger logger = LoggerFactory.getLogger(DocumentManagementBatchImpl.class);

    private final DocumentRepository documentRepository;
    private final DocumentService documentService;
    private final FileSystemDao fileSystemDao;

    public DocumentManagementBatchImpl(DocumentRepository documentRepository, DocumentService documentService,
        FileSystemDao fileSystemDao) {
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.fileSystemDao = fileSystemDao;
    }

    public void removeMissingDocuments() {
        List<Document> documents = documentRepository.findAll();

        logger.info("Remove missing documents batch launched.");

        for (Document document : documents) {
            InputStream stream = fileSystemDao.getFileContentByUUID(document.getIdentifier());

            if (stream == null) {
                try {
                    logger.info("Removing file with UID = {} because of inconsistency", document.getIdentifier());
                    documentService.deleteFile(document.getOwner().getLogin(), document.getIdentifier(),
                        Reason.INCONSISTENCY);
                } catch (BusinessException ex) {
                    logger.error("Error when processing cleaning of document whith UID = {} during consistency check " +
                        "process", document.getIdentifier());
                }
            }
        }
        logger.info("Remove missing documents batch ended.");
    }
}
