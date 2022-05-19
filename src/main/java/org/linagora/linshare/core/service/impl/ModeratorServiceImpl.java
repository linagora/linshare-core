/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
package org.linagora.linshare.core.service.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.business.service.ModeratorBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.ModeratorDto;
import org.linagora.linshare.core.notifications.context.GuestModeratorCreationEmailContext;
import org.linagora.linshare.core.notifications.context.GuestModeratorDeletionEmailContext;
import org.linagora.linshare.core.notifications.context.GuestModeratorUpdateEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.ModeratorResourceAccessControl;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.ModeratorService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.ModeratorAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.ModeratorMto;

import com.google.common.collect.Lists;

public class ModeratorServiceImpl extends GenericServiceImpl<Account, Moderator> implements ModeratorService {

	private ModeratorBusinessService moderatorBusinessService;

	private GuestBusinessService guestBusinessService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	private final LogEntryService logEntryService;

	public ModeratorServiceImpl(
			ModeratorResourceAccessControl rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			ModeratorBusinessService moderatorBusinessService,
			GuestBusinessService guestBusinessService,
			NotifierService notifierService,
			MailBuildingService mailBuildingService,
			LogEntryService logEntryService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.moderatorBusinessService = moderatorBusinessService;
		this.guestBusinessService = guestBusinessService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
		this.logEntryService = logEntryService;
	}

	@Override
	public Moderator create(Account authUser, Account actor, Moderator moderator, boolean onGuestCreation) {
		preChecks(authUser, actor);
		Validate.notNull(moderator, "Moderator must be set.");
		Validate.notNull(moderator.getAccount(), "Moderator's account should be set");
		Validate.notNull(moderator.getGuest(), "Moderator's guest should be set");
		Validate.notEmpty(moderator.getGuest().getLsUuid(), "Guest's uuid must be set");
		checkCreatePermission(authUser, actor, Moderator.class, BusinessErrorCode.GUEST_MODERATOR_CANNOT_CREATE, moderator, onGuestCreation);
		Guest guest = guestBusinessService.findByLsUuid(moderator.getGuest().getLsUuid());
		List<Moderator> moderators = moderatorBusinessService.findAllByGuest(guest, null, null);
		if (moderators.contains(moderator)) {
			throw new BusinessException(BusinessErrorCode.GUEST_MODERATOR_ALREADY_EXISTS, "Moderator already exists.");
		}
		moderator = moderatorBusinessService.create(moderator);
		guest.addModerator(moderator);
		guestBusinessService.update(actor, guest, guest, guest.getDomain(), null);
		if (!actor.equals(moderator.getAccount())) {
			GuestModeratorCreationEmailContext mailContext = new GuestModeratorCreationEmailContext(actor, moderator);
			MailContainerWithRecipient mail = mailBuildingService.build(mailContext);
			notifierService.sendNotification(mail);
		}
		ModeratorAuditLogEntry log = new ModeratorAuditLogEntry(authUser, actor, LogAction.CREATE, AuditLogEntryType.GUEST_MODERATOR, moderator, moderator.getGuest().getLsUuid());
		log.addRelatedAccounts(moderatorBusinessService.findAllModeratorUuidsByGuest(moderator.getGuest()));
		logEntryService.insert(log);
		return moderator;
	}

	@Override
	public Moderator find(Account authUser, Account actor, String uuid) {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "Moderator uuid must be set.");
		Moderator moderator = moderatorBusinessService.find(uuid);
		checkReadPermission(authUser, actor, Moderator.class, BusinessErrorCode.GUEST_MODERATOR_CANNOT_GET, moderator);
		return moderator;
	}

	@Override
	public Moderator update(Account authUser, Account actor, Moderator moderator, ModeratorDto dto) {
		preChecks(authUser, actor);
		Validate.notNull(moderator, "Moderator to update must be set.");
		Validate.notNull(moderator.getRole(), "Moderator role must be set.");
		Validate.notNull(moderator.getGuest(), "Moderator's guest should be set");
		Validate.notEmpty(moderator.getGuest().getLsUuid(), "Guest's uuid must be set");
		checkUpdatePermission(authUser, actor, Moderator.class, BusinessErrorCode.GUEST_MODERATOR_CANNOT_UPDATE, moderator);
		ModeratorAuditLogEntry log = new ModeratorAuditLogEntry(authUser, actor, LogAction.UPDATE, AuditLogEntryType.GUEST_MODERATOR, moderator, moderator.getGuest().getLsUuid());
		ModeratorRole oldRole = moderator.getRole();
		moderator.setRole(dto.getRole());
		moderator = moderatorBusinessService.update(moderator);
		if (!actor.equals(moderator.getAccount())) {
			GuestModeratorUpdateEmailContext mailContext = new GuestModeratorUpdateEmailContext(actor, moderator,
					oldRole);
			MailContainerWithRecipient mail = mailBuildingService.build(mailContext);
			notifierService.sendNotification(mail);
		}
		log.setResourceUpdated(new ModeratorMto(moderator));
		log.addRelatedAccounts(moderatorBusinessService.findAllModeratorUuidsByGuest(moderator.getGuest()));
		logEntryService.insert(log);
		return moderator;
	}

	@Override
	public Moderator delete(Account authUser, Account actor, Moderator moderator) {
		preChecks(authUser, actor);
		Validate.notNull(moderator, "Moderator must be set.");
		Validate.notNull(moderator.getGuest(), "Moderator must be set.");
		Validate.notEmpty(moderator.getGuest().getLsUuid(), "Guest's uuid must be set");
		checkDeletePermission(authUser, actor, Moderator.class, BusinessErrorCode.GUEST_MODERATOR_CANNOT_DELETE, moderator);
		Guest guest = guestBusinessService.findByLsUuid(moderator.getGuest().getLsUuid());
		moderatorBusinessService.delete(moderator);
		guest.removeModerator(moderator);
		guestBusinessService.update(actor, guest, guest, guest.getDomain(), null);
		if (!actor.equals(moderator.getAccount())) {
			GuestModeratorDeletionEmailContext mailContext = new GuestModeratorDeletionEmailContext(actor,
					moderator);
			MailContainerWithRecipient mail = mailBuildingService.build(mailContext);
			notifierService.sendNotification(mail);
		}
		ModeratorAuditLogEntry log = new ModeratorAuditLogEntry(authUser, actor, LogAction.DELETE, AuditLogEntryType.GUEST_MODERATOR, moderator, moderator.getGuest().getLsUuid());
		log.addRelatedAccounts(moderatorBusinessService.findAllModeratorUuidsByGuest(moderator.getGuest()));
		logEntryService.insert(log);
		return moderator;
	}

	@Override
	public List<Moderator> findAllByGuest(Account authUser, Account actor, String guestUuid, ModeratorRole role, String pattern) {
		preChecks(authUser, actor);
		Validate.notEmpty(guestUuid, "Guest's uuid must be set.");
		Guest guest = guestBusinessService.findByLsUuid(guestUuid);
		checkListPermission(authUser, actor, Moderator.class, BusinessErrorCode.GUEST_MODERATORS_CANNOT_GET, null, guest);
		if(Objects.isNull(guest)) {
			String errMsg = String.format("Guest with uuid: %1$s not found, cannot get its moderators list", guestUuid);
			logger.debug(errMsg);
			throw new BusinessException(BusinessErrorCode.GUEST_NOT_FOUND, errMsg);
		}
		List<Moderator> moderators = moderatorBusinessService.findAllByGuest(guest, role, pattern);
		return moderators;
	}

	@Override
	public List<Moderator> deleteAllModerators(Account authUser, Account actor, List<Moderator> moderators, Guest guest) {
		preChecks(authUser, actor);
		Validate.notEmpty(moderators, "Moderator list should not be null");
		List<MailContainerWithRecipient> mailContainers = Lists.newArrayList();
		List<AuditLogEntryUser> logs = Lists.newArrayList();
		for (Moderator moderator : moderators) {
			if (!moderator.getAccount().equals(actor)) {
				GuestModeratorDeletionEmailContext mailContext = new GuestModeratorDeletionEmailContext(actor,
						moderator);
				mailContainers.add(mailBuildingService.build(mailContext));
			}
			ModeratorAuditLogEntry log = new ModeratorAuditLogEntry(authUser, actor, LogAction.DELETE, AuditLogEntryType.GUEST_MODERATOR, moderator, moderator.getGuest().getLsUuid());
			log.setCause(LogActionCause.GUEST_DELETION);
			log.addRelatedAccounts(moderatorBusinessService.findAllModeratorUuidsByGuest(moderator.getGuest()));
			logs.add(log);
		}
		moderatorBusinessService.deleteAllModerators(guest);
		notifierService.sendNotification(mailContainers);
		logEntryService.insert(logs);
		return moderators;
	}
}
