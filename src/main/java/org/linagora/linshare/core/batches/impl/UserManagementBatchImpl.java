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

import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.batches.UserManagementBatch;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.service.GuestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Batch for user management.
 */
public class UserManagementBatchImpl implements UserManagementBatch {

	private static final Logger logger = LoggerFactory
			.getLogger(UserManagementBatchImpl.class);

	private final GuestService service;

	public UserManagementBatchImpl(final GuestService guestService) {
		this.service = guestService;
	}

	@Override
	public Set<Guest> getAll(SystemAccount systemAccount) {
		logger.info("UserManagementBatchImpl job starting ...");
		HashSet<Guest> allGuests = new HashSet<>(
				service.findOudatedGuests(systemAccount));
		logger.info(allGuests.size()
				+ " guest(s) have been found to be removed");
		return allGuests;
	}

	@Override
	public BatchResultContext<Guest> execute(SystemAccount systemAccount,
			Guest resource, long total, long position) throws BatchBusinessException, BusinessException {
		BatchResultContext<Guest> context = new BatchResultContext<Guest>(
				resource);
		try {
			logger.info("processing guest : " + resource.getAccountReprentation());
			service.deleteUser(systemAccount, resource.getLsUuid());
			logger.info("Removed expired user : " + resource.getAccountReprentation());
		} catch (BusinessException businessException) {
			logger.info("Error occured while cleaning outdated guests ",
					businessException);
			BatchBusinessException exception = new BatchBusinessException(
					context, "Error while trying to delete expired guest");
			exception.setBusinessException(businessException);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(SystemAccount systemAccount,
			BatchResultContext<Guest> context, long total, long position) {
		logger.info("Outdated guest was successfully removed ",
				context.getResource().getAccountReprentation());
	}

	@Override
	public void notifyError(SystemAccount systemAccount,
			BatchBusinessException exception, Guest resource, long total, long position) {
		logger.error(
				"Error occured while cleaning outdated guest "
						+ resource.getAccountReprentation()
						+ ". BatchBusinessException ", exception);
	}

	@Override
	public void terminate(SystemAccount systemAccount, Set<Guest> all) {
		terminate(systemAccount, all, errors, unhandled_errors, total);
	}

	@Override
	public void terminate(SystemAccount systemAccount, Set<Guest> all, long errors, long unhandled_errors, long total) {
		logger.info(all.size() + " guests have been removed.");
		logger.info("UserManagementBatchImpl job terminated.");

	}
}
