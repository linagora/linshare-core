/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 *
 * Copyright (C) 2022 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */

package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.batches.impl.gdpr.AnonymizeUploadRequestAuditLogEntry;
import org.linagora.linshare.core.batches.impl.gdpr.AnonymizeUploadRequestGroupAuditLogEntry;
import org.linagora.linshare.core.batches.impl.gdpr.AnonymizeUploadRequestUrlAuditLogEntry;
import org.linagora.linshare.core.batches.impl.gdpr.GDPRConstants;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ContactRepository;
import org.linagora.linshare.core.repository.UploadRequestRepository;
import org.linagora.linshare.core.repository.UploadRequestUrlRepository;
import org.linagora.linshare.core.service.TimeService;
import org.springframework.data.mongodb.core.MongoTemplate;

public class GDPRUploadRequestBatchImpl extends GenericBatchImpl {

	private final TimeService timeService;
	private final UploadRequestRepository uploadRequestRepository;
	private final UploadRequestUrlRepository uploadRequestUrlRepository;
	private final ContactRepository contactRepository;
	private final MongoTemplate mongoTemplate;

	public GDPRUploadRequestBatchImpl(
			AccountRepository<Account> accountRepository,
			TimeService timeService,
			UploadRequestRepository uploadRequestRepository,
			UploadRequestUrlRepository uploadRequestUrlRepository,
			ContactRepository contactRepository,
			MongoTemplate mongoTemplate) {
		super(accountRepository);
		this.timeService = timeService;
		this.uploadRequestRepository = uploadRequestRepository;
		this.uploadRequestUrlRepository = uploadRequestUrlRepository;
		this.contactRepository = contactRepository;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		return uploadRequestRepository.findAllArchivedOrPurgedOlderThan(timeService.previousYear());
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position) throws BatchBusinessException, BusinessException {
		UploadRequest uploadRequest = uploadRequestRepository.findByUuid(identifier);
		ResultContext context = new BatchResultContext<UploadRequest>(uploadRequest);

		Contact anonymizedContact = getAnonymizedContact();
		uploadRequest.getUploadRequestURLs()
			.stream()
			.forEach(uploadRequestURL -> {
				uploadRequestURL.setContact(anonymizedContact);
				uploadRequestUrlRepository.update(uploadRequestURL);
			});

		new AnonymizeUploadRequestAuditLogEntry(mongoTemplate).process(identifier);
		new AnonymizeUploadRequestGroupAuditLogEntry(mongoTemplate).process(identifier);
		new AnonymizeUploadRequestUrlAuditLogEntry(mongoTemplate).process(identifier);

		context.setProcessed(true);
		return context;
	}

	private Contact getAnonymizedContact() {
		Contact contact = contactRepository.findByMail(GDPRConstants.MAIL_ANONYMIZATION);
		if (contact != null) {
			return contact;
		}
		Contact anonymizedContact = new Contact(GDPRConstants.MAIL_ANONYMIZATION);
		contactRepository.create(anonymizedContact);
		return anonymizedContact;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		BatchResultContext<UploadRequest> batchResultContext = (BatchResultContext<UploadRequest>) context;
		UploadRequest uploadRequest = batchResultContext.getResource();
		console.logInfo(batchRunContext, total, position, "The Upload Request " + uploadRequest.getUuid() + " has been anonymized processed.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		BatchResultContext<UploadRequest> batchResultContext = (BatchResultContext<UploadRequest>) exception.getContext();
		UploadRequest uploadRequest = batchResultContext.getResource();
		console.logError(batchRunContext, total, position, "Anonymization for upload request has failed : " + uploadRequest.getUuid());
	}
}
