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

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import org.linagora.linshare.core.domain.constants.LdapBatchMetaDataType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.WorkSpaceProvider;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.LDAPWorkSpaceProviderDto;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.LdapGroupsBatchResultContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.GroupLdapPatternService;
import org.linagora.linshare.core.service.LDAPGroupSyncService;
import org.linagora.linshare.core.service.impl.LdapConnectionServiceImpl;

public class SynchronizeLDAPGroupsInWorkSpaceBatchImpl extends GenericBatchImpl {

	private final AbstractDomainRepository domainRepository;

	private final LdapConnectionServiceImpl connectionService;

	private final GroupLdapPatternService patternService;

	private final LDAPGroupSyncService syncService;

	public SynchronizeLDAPGroupsInWorkSpaceBatchImpl(
			AccountRepository<Account> accountRepository,
			AbstractDomainRepository domainRepository,
			LdapConnectionServiceImpl connectionService,
			GroupLdapPatternService patternService,
			LDAPGroupSyncService syncService) {
		super(accountRepository);
		this.domainRepository = domainRepository;
		this.connectionService = connectionService;
		this.patternService = patternService;
		this.syncService = syncService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("{} job starting ...", getClass().toString());
		return domainRepository.findAllDomainIdentifiersWithWorkSpaceProviders();
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		SystemAccount systemAccount = accountRepository.getBatchSystemAccount();
		AbstractDomain domain = domainRepository.findById(identifier);
		logger.info("Synchronizing the LDAP groups for the domain: {}", domain.toString());
		LdapGroupsBatchResultContext context = new LdapGroupsBatchResultContext(domain);
		WorkSpaceProvider workSpaceProvider = domain.getWorkSpaceProvider();
		if (null != workSpaceProvider) {
			LDAPWorkSpaceProviderDto dto = workSpaceProvider.toLDAPWorkSpaceProviderDto();
			LdapConnection ldapConnection = connectionService.find(dto.getConnection().getUuid());
			GroupLdapPattern groupPattern = patternService.find(dto.getPattern().getUuid());
			String baseDn = dto.getBaseDn();
			try {
				syncService.executeBatch(systemAccount, domain, ldapConnection, baseDn, groupPattern, context);
				context.setProcessed(true);
			} catch (NamingException e) {
				logger.error("NamingException : Failure during the synchro of workSpace group of the domain with uuid: {}", identifier, e);
				context.setProcessed(false);
			} catch (IOException e) {
				logger.error("IOException : Failure during the synchro of workSpace group of the domain with uuid: {}", identifier, e);
				context.setProcessed(false);
			} catch (Exception e) {
				logger.error("Failure during the synchro of workSpace group of the domain with uuid: {}", identifier, e);
				context.setProcessed(false);
			}
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		LdapGroupsBatchResultContext renameContext = (LdapGroupsBatchResultContext) context;
		AbstractDomain domain = renameContext.getResource();
		console.logInfo(batchRunContext, total, position,
				"The LDAP workSpaces of the domain " + domain.getUuid() + " have been synchronized");
		console.logInfo(batchRunContext, total, position, renameContext.getResultStats().get(LdapBatchMetaDataType.CREATED_WORKSPACES) + " workSpaces(s) created");
		console.logInfo(batchRunContext, total, position, renameContext.getResultStats().get(LdapBatchMetaDataType.UPDATED_WORKSPACES) + " workSpaces(s) updated");
		console.logInfo(batchRunContext, total, position, renameContext.getResultStats().get(LdapBatchMetaDataType.DELETED_WORKSPACES) + " workSpaces(s) deleted");
		console.logInfo(batchRunContext, total, position, renameContext.getResultStats().get(LdapBatchMetaDataType.CREATED_WORKSPACES_MEMBERS) + " Member(s) created");
		console.logInfo(batchRunContext, total, position, renameContext.getResultStats().get(LdapBatchMetaDataType.UPDATED_WORKSPACES_MEMBERS) + " Member(s) updated");
		console.logInfo(batchRunContext, total, position, renameContext.getResultStats().get(LdapBatchMetaDataType.DELETED_WORKSPACES_MEMBERS) + " Member(s) deleted");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		LdapGroupsBatchResultContext domainContext = (LdapGroupsBatchResultContext) exception.getContext();
		AbstractDomain domain = domainContext.getResource();
		console.logError(batchRunContext, total, position,
				"Failure of the LDAP Groups synchronization in workSpaces of the domain: {} ", domain.getUuid());
	}

}
