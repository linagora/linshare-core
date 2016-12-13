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

import java.util.List;
import java.util.Map;

import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericBatchImpl implements GenericBatch {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final AccountRepository<Account> accountRepository;

	public GenericBatchImpl(AccountRepository<Account> accountRepository) {
		super();
		this.accountRepository = accountRepository;
	}

	protected String getStringPosition(long total, long position) {
		return position + "/" + total + ":";
	}

	@Override
	public void logDebug(long total, long position, String message,
			Object... args) {
		logger.debug(getStringPosition(total, position) + message, args);
	}

	protected void logInfo(long total, long position, String message,
			Object... args) {
		logger.info(getStringPosition(total, position) + message, args);
	}

	protected void logWarn(long total, long position, String message,
			Object... args) {
		logger.warn(getStringPosition(total, position) + message, args);
	}

	protected void logError(long total, long position, String message,
			Object... args) {
		logger.error(getStringPosition(total, position) + message, args);
	}

	protected SystemAccount getSystemAccount() {
		return accountRepository.getBatchSystemAccount();
	}

	@Override
	public String getBatchClassName() {
		return this.getClass().toString().split(" ")[1];
	}

	@Override
	public boolean needToRun() {
		return true;
	}

	@Override
	public void fail(Map<String, List<String>> context, Exception exception) {
		logger.error("Batch error in " + this.getClass().getName());
		logger.error(exception.getMessage(), exception);
	}
}
