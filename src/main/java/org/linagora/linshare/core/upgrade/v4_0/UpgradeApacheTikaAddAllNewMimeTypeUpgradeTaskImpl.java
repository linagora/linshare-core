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
package org.linagora.linshare.core.upgrade.v4_0;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.MimePolicyRepository;
import org.linagora.linshare.core.repository.MimeTypeRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

import com.google.common.collect.Sets;

public class UpgradeApacheTikaAddAllNewMimeTypeUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	protected MimePolicyRepository mimePolicyRepository;

	protected MimeTypeMagicNumberDao mimeTypeMagicNumberDao;

	protected MimeTypeRepository mimeTypeRepository;

	public UpgradeApacheTikaAddAllNewMimeTypeUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MimePolicyRepository mimePolicyRepository,
			MimeTypeMagicNumberDao mimeTypeMagicNumberDao,
			MimeTypeRepository mimeTypeRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.mimeTypeMagicNumberDao = mimeTypeMagicNumberDao;
		this.mimePolicyRepository = mimePolicyRepository;
		this.mimeTypeRepository = mimeTypeRepository;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_0_ADD_ALL_NEW_MIME_TYPE;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		List<String> mimePolicyIdentifiers = mimePolicyRepository.findAllUuid();
		return mimePolicyIdentifiers;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		MimePolicy mimePolicy = mimePolicyRepository.findByUuid(identifier);
		BatchResultContext<MimePolicy> res = new BatchResultContext<MimePolicy>(mimePolicy);
		console.logDebug(batchRunContext, total, position, "Processing MimePolicy : " + mimePolicy.toString());
		Set<MimeType> mimeTypes = mimeTypeMagicNumberDao.getAllMimeType();
		Set<String> ref = Sets.newHashSet();
		for(MimeType mimeType : mimeTypes) {
			ref.add(mimeType.getMimeType());
			MimeType type = mimeTypeRepository.findByMimeType(mimePolicy, mimeType.getMimeType());
			if (type == null) {
				mimeType.setMimePolicy(mimePolicy);
				mimeType.setEnable(false);
				mimeTypeRepository.create(mimeType);
				mimePolicy.getMimeTypes().add(mimeType);
			}
		}
		List<MimeType> findAll = mimeTypeRepository.findAll(mimePolicy);
		for (MimeType mimeType : findAll) {
			if (!ref.contains(mimeType.getMimeType())) {
				mimeTypeRepository.delete(mimeType);
				mimePolicy.getMimeTypes().remove(mimeType);
			}
		}
		mimePolicy = mimePolicyRepository.update(mimePolicy);
		logger.debug("mime_policies size : " + mimePolicy.getMimeTypes().size());
		res.setProcessed(true);
		return res;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<MimePolicy> res = (BatchResultContext<MimePolicy>) context;
		MimePolicy resource = res.getResource();
		if (res.getProcessed()) {
			logInfo(batchRunContext, total, position, resource + " has been updated.");
		} else {
			logInfo(batchRunContext, total, position, resource + " has been skipped.");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<MimePolicy> res = (BatchResultContext<MimePolicy>) exception.getContext();
		MimePolicy resource = res.getResource();
		console.logError(batchRunContext, total, position, "The upgrade task : " + resource + " failed.", batchRunContext);
		logger.error("Error occured while updating the MimePolicy : "
				+ resource +
				". BatchBusinessException", exception);
	}
}
