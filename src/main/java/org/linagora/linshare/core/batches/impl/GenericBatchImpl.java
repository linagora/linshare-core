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
package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.batches.GenericBatch;
import org.linagora.linshare.core.batches.utils.BatchConsole;
import org.linagora.linshare.core.batches.utils.OperationKind;
import org.linagora.linshare.core.batches.utils.impl.BatchConsoleImpl;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericBatchImpl implements GenericBatch {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final AccountRepository<Account> accountRepository;

	protected BatchConsole console;

	protected OperationKind operationKind;

	public GenericBatchImpl(AccountRepository<Account> accountRepository) {
		super();
		this.accountRepository = accountRepository;
		this.console = new BatchConsoleImpl(this.getClass());
		this.operationKind = OperationKind.UPDATED;
	}

	@Override
	public BatchConsole getConsole() {
		return console;
	}

	@Override
	public void setConsole(BatchConsole console) {
		this.console = console;
	}

	protected String getStringPosition(long total, long position) {
		return position + "/" + total + ":";
	}

	// legacy wrappers.
	protected void logDebug(BatchRunContext batchRunContext, long total, long position,
			String message, Object... args) {
		console.logDebug(batchRunContext, total, position, message, args);
	}

	protected void logInfo(BatchRunContext batchRunContext, long total, long position,
			String message, Object... args) {
		console.logInfo(batchRunContext, total, position, message, args);
	}

	protected void logWarn(long total, long position, String message,
			BatchRunContext batchRunContext, Object... args) {
		console.logWarn(batchRunContext, total, position, message, args);
	}

	protected void logError(long total, long position, String message,
			BatchRunContext batchRunContext, Object... args) {
		console.logError(batchRunContext, total, position, message, args);
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
	public void start(BatchRunContext batchRunContext) {
		console.logInfo(batchRunContext, "Task is starting ...");
	}

	@Override
	public void fail(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total, long processed) {
		console.logError(batchRunContext, "Critical error for the current task. Task stopped.");
		long skipped = total - processed - errors -unhandled_errors;
		String msg = String.format("Summary : success: %d, skipped: %d, errors: %d, unhandled errors: %d",
				processed, skipped, errors, unhandled_errors);
		console.logError(batchRunContext, msg);
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors, long total, long processed) {
		String msg = String.format("Total resource(s) : %1$d", total);
		console.logInfo(batchRunContext, msg);
		String operation = operationKind.toString().toLowerCase();
		msg = String.format("%1$d resource(s) have been %2$s.", processed, operation);
		console.logInfo(batchRunContext, msg);
		long skipped = total - processed - errors -unhandled_errors;
		if (skipped > 0) {
			msg = String.format("%1$d resource(s) have been skipped.", skipped);
			console.logInfo(batchRunContext, msg);
		}
		if (errors > 0) {
			msg = String.format("%1$d resource(s) can not be %2$s. Look for more information into tomcat server logs.", errors, operation);
			console.logError(batchRunContext, msg);
		}
		if (unhandled_errors > 0) {
			msg = String.format("There were %1$d unhandled error durring the task processing which stop it.", unhandled_errors);
			console.logError(batchRunContext, msg);
		}
		console.logInfo(batchRunContext, "Task complete.");
		msg = String.format("Summary : success: %d, skipped: %d, errors: %d, unhandled errors: %d",
				processed, skipped, errors, unhandled_errors);
		console.logInfo(batchRunContext, msg);
	}
}
