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

package org.linagora.linshare.core.batches.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.linagora.linshare.core.batches.ShaSumBatch;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.DocumentBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentRepository;

public class ShaSumBatchImpl extends GenericBatchImpl implements ShaSumBatch {

	private final DocumentRepository documentRepository;
	private final FileSystemDao fileSystemDao;
	private final DocumentEntryBusinessService docEntryBusinessService;

	public ShaSumBatchImpl(
			AccountRepository<Account> accountRepository,
			final DocumentRepository documentRepository,
			final FileSystemDao fileSystemDao,
			final DocumentEntryBusinessService docEntryBusinessService) {
		super(accountRepository);
		this.documentRepository = documentRepository;
		this.fileSystemDao = fileSystemDao;
		this.docEntryBusinessService = docEntryBusinessService;
	}

	@Override
	public List<String> getAll() {
		logger.info("Sha256SumBatchImpl job starting ...");
		List<String> list = documentRepository.findAllSha256CheckNeededDocuments();
		list.addAll(documentRepository.findAllSha1CheckNeededDocuments());
		logger.info(list.size() + " document(s) have been found to treated.");
		return list;
	}

	@Override
	public Context execute(String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		Document doc = documentRepository.findByUuid(identifier);
		if (doc == null) {
			return null;
		}
		logInfo(total, position, "processing document : "
				+ doc.getUuid());
		Context context = new DocumentBatchResultContext(doc);
		logger.debug("retrieve from JackRabbit : " + doc.getUuid());
		try (InputStream fileContentByUUID = fileSystemDao.getFileContentByUUID(doc.getUuid())) {
			doc.setSha256sum(docEntryBusinessService.SHA256CheckSumFileStream(fileContentByUUID));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new BatchBusinessException(context, e.getMessage());
		};
		try (InputStream fileContentByUUID = fileSystemDao.getFileContentByUUID(doc.getUuid())) {
			doc.setSha1sum(docEntryBusinessService.SHA1CheckSumFileStream(fileContentByUUID));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new BatchBusinessException(context, e.getMessage());
		};
		documentRepository.update(doc);
		return context;
	}

	@Override
	public void notify(Context context, long total, long position) {
		DocumentBatchResultContext documentContext = (DocumentBatchResultContext) context;
		Document doc = documentContext.getResource();
		logInfo(total, position, "The document : "
				+ doc.getUuid() +
				" has been successfully updated ");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position) {
		DocumentBatchResultContext documentContext = (DocumentBatchResultContext) exception.getContext();
		Document doc = documentContext.getResource();
		logError(total, position, 
				"Updating document has failed : "
				+ doc.getUuid());
		logger.error("Error occured while updating the document : "
				+ doc.getUuid() + 
				". BatchBusinessException", exception);
	}

}
