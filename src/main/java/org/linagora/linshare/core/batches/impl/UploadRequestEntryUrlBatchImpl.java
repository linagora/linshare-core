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

package org.linagora.linshare.core.batches.impl;

import java.util.Set;

import org.linagora.linshare.core.batches.UploadRequestEntryUrlBatch;
import org.linagora.linshare.core.batches.generics.impl.GenericBatchImpl;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.Context;
import org.linagora.linshare.core.job.quartz.ResourceContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.UploadRequestEntryUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadRequestEntryUrlBatchImpl extends
		GenericBatchImpl<UploadRequestEntryUrl> implements
		UploadRequestEntryUrlBatch {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadRequestEntryUrlBatch.class);

	protected UploadRequestEntryUrlService service;

	public UploadRequestEntryUrlBatchImpl(
			UploadRequestEntryUrlService uploadRequestEntryUrlService,
			AccountRepository<Account> accountRepository) {
		super(accountRepository);
		this.service = uploadRequestEntryUrlService;
	}

	@Override
	public Set<UploadRequestEntryUrl> getAll() {
		logger.info("UploadRequestEntryUrlBatchImpl job starting ...");
		Set<UploadRequestEntryUrl> allExpired = service
				.findAllExpired(getSystemAccount());
		logger.info("The system has found " + allExpired.size()
				+ " expired upload request entrie(s) url");
		return allExpired;
	}

	@Override
	public BatchResultContext<UploadRequestEntryUrl> execute(Context c,
			long total, long position) throws BatchBusinessException,
			BusinessException {
		UploadRequestEntryUrl resource = getResource(c);
		BatchResultContext<UploadRequestEntryUrl> context = new BatchResultContext<UploadRequestEntryUrl>(
				resource);
		try {
			logInfo(total, position,
					"processing uREUrl : " + resource.getUuid());
			service.deleteUploadRequestEntryUrl(getSystemAccount(), resource);
		} catch (BusinessException businessException) {
			String msg = "Error while trying to delete outdated upload request entry url ";
			logError(total, position, msg);
			logger.error(msg, businessException);
			BatchBusinessException exception = new BatchBusinessException(
					context, msg);
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchResultContext<UploadRequestEntryUrl> context,
			long total, long position) {
		logInfo(total, position, "Outdated uREUrl was successfully removed : "
				+ context.getResource().getUuid());
	}

	@Override
	public void notifyError(BatchBusinessException exception,
			UploadRequestEntryUrl resource, long total,
			long position) {
		logError(total, position, "cleaning eREUrl has failed : " + resource.getUuid());
		logger.error(
				"An error occured while cleaning outdated upload request entry url "
						+ resource.getUuid()
						+ ". A BatchBusinessException has been thrown ",
				exception);
	}

	@Override
	public void terminate(Set<UploadRequestEntryUrl> all,
			long errors, long unhandled_errors, long total) {
		long success = total - errors - unhandled_errors;
		logger.info(success
				+ " upload request entrie(s) url have been removed.");
		if (errors > 0) {
			logger.error(errors
					+ " upload request entrie(s) url failed to be removed.");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors
					+ " upload request entrie(s) url failed to be removed (unhandled error).");
		}
		logger.info("UploadRequestEntryUrlBatchImpl job terminated.");
	}

	@Override
	public UploadRequestEntryUrl getResource(Context c) {
		@SuppressWarnings("unchecked")
		ResourceContext<UploadRequestEntryUrl> rc = (ResourceContext<UploadRequestEntryUrl>)c;
		return rc.getRessource();
	}
}
