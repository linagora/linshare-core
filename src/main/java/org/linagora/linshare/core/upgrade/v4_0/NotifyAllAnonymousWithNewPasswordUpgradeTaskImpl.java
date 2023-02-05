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
import java.util.stream.Collectors;

import org.linagora.linshare.core.batches.impl.GenericUpgradeTaskImpl;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.Recipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.BatchResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.ShareAnonymousResetPasswordEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.AnonymousUrlRepository;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

/**
 * Upgrade task to notify external users with deprecated passwords
 *
 */
public class NotifyAllAnonymousWithNewPasswordUpgradeTaskImpl extends GenericUpgradeTaskImpl {

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	private final AnonymousUrlRepository anonymousUrlRepository;

	private final PasswordService passwordService;

	public NotifyAllAnonymousWithNewPasswordUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			AnonymousUrlRepository anonymousUrlRepository,
			MailBuildingService mailBuildingService,
			NotifierService notifierService,
			PasswordService passwordService) {
		super(accountRepository, upgradeTaskLogMongoRepository);
		this.anonymousUrlRepository = anonymousUrlRepository;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.passwordService = passwordService;
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_0_PASSWORD_ENCODING_STRATEGY_CHANGES_FOR_ANONYMOUS;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("{} job is starting ...", getClass().toString());
		List<String> uuids = anonymousUrlRepository.findAllMyAnonymousUuids();
		logger.info("{} anonymous url(s) has been found.", uuids.size());
		return uuids;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AnonymousUrl anonymousUrl = anonymousUrlRepository.findByUuid(identifier);
		BatchResultContext<AnonymousUrl> batchResultContext = new BatchResultContext<>(anonymousUrl);
		if (anonymousUrl == null) {
			batchResultContext.setProcessed(false);
			return batchResultContext;
		}
		String password = passwordService.generatePassword();
		anonymousUrl.setTemporaryPlainTextPassword(password);
		anonymousUrl.setPassword(passwordService.encode(password));
		List<String> documents = anonymousUrl.getAnonymousShareEntries().stream()
					.map(anon -> anon.getDocumentEntry().getUuid()).collect(Collectors.toList());
		ShareContainer sc = new ShareContainer();
		ContactListContact contact = new ContactListContact();
		contact.setMail(anonymousUrl.getContact().getMail());
		sc.addContact(contact);
		sc.addDocumentUuid(documents);
		sc.addAnonymousShareRecipient(new Recipient(contact));
		EmailContext emailContext = new ShareAnonymousResetPasswordEmailContext((User)anonymousUrl.getOwner(), anonymousUrl, sc);
		MailContainerWithRecipient mail = mailBuildingService.build(emailContext);
		notifierService.sendNotification(mail);
		batchResultContext.setProcessed(true);
		return batchResultContext;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		@SuppressWarnings("unchecked")
		BatchResultContext<AnonymousUrl> batchResultContext = (BatchResultContext<AnonymousUrl>) context;
		AnonymousUrl resource = batchResultContext.getResource();
		if (batchResultContext.getProcessed()) {
			logInfo(batchRunContext, total, position,
					"Anonymous shares has been successfully notified with new passwords : " + resource.toString());
		} 
		if(!batchResultContext.getProcessed()) {
			logInfo(batchRunContext, total, position,
					"No Anonymous shares was found with depreacted passwords, any mail was sent");
		}
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position,
			BatchRunContext batchRunContext) {
		@SuppressWarnings("unchecked")
		BatchResultContext<Guest> res = (BatchResultContext<Guest>) exception.getContext();
		Guest resource = res.getResource();
		logError(total, position, exception.getMessage(), batchRunContext);
		logger.error("Error occured while processing external users notify : {} . BatchBusinessException", resource, exception);
	}
}
