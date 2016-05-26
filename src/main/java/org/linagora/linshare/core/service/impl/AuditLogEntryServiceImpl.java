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
package org.linagora.linshare.core.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.AuditLogEntryAdmin;
import org.linagora.linshare.mongo.entities.AuditLogEntryUser;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;

import com.google.common.collect.Lists;

public class AuditLogEntryServiceImpl implements AuditLogEntryService {

	private AuditAdminMongoRepository auditMongoRepository;

	private AuditUserMongoRepository userMongoRepository;

	private UserService userService;

	private AbstractDomainService domainService;

	public AuditLogEntryServiceImpl(AuditAdminMongoRepository auditMongoRepository,
			AuditUserMongoRepository userMongoRepository, UserService userService,
			AbstractDomainService domainService) {
		this.auditMongoRepository = auditMongoRepository;
		this.userMongoRepository = userMongoRepository;
		this.userService = userService;
		this.domainService = domainService;
	}

	@Override
	public List<AuditLogEntryAdmin> findAll(Account actor) {
		Validate.notNull(actor);
		List<AuditLogEntryAdmin> res = auditMongoRepository.findAll();
		return res;
	}

	@Override
	public List<AuditLogEntryAdmin> findByActor(Account actor, String uuid) {
		Validate.notNull(actor);
		return auditMongoRepository.findByActor(uuid);
	}

	@Override
	public List<AuditLogEntryAdmin> findByAction(Account actor, String action) {
		Validate.notNull(actor);
		Validate.notEmpty(action);
		return auditMongoRepository.findByAction(action);
	}

	@Override
	public List<AuditLogEntryAdmin> findByDomain(Account actor, String uuid) {
		Validate.notNull(actor);
		Validate.notEmpty(uuid);
		domainService.findById(uuid);
		return auditMongoRepository.findByTargetDomainUuid(uuid);
	}

	@Override
	public List<AuditLogEntryAdmin> findByType(Account actor, AuditLogEntryType type) {
		Validate.notNull(actor);
		Validate.notNull(type);
		return auditMongoRepository.findByType(type);
	}

	@Override
	public List<AuditLogEntryUser> userFindAll(Account actor) {
		Validate.notNull(actor);
		return (List<AuditLogEntryUser>) userMongoRepository.findAll();
	}

	@Override
	public List<AuditLogEntryUser> userFindByActor(Account actor, String uuid) {
		Validate.notNull(actor);
		Validate.notEmpty(uuid);
		User user = userService.findByLsUuid(uuid);
		return userMongoRepository.findByActorUuid(user.getLsUuid());
	}

	@Override
	public List<AuditLogEntryUser> userFindByAction(Account actor, String action) {
		Validate.notNull(actor);
		Validate.notEmpty(action);
		return userMongoRepository.findByAction(action);
	}

	@Override
	public List<AuditLogEntryUser> userFindByOwner(Account actor, String uuid) {
		Validate.notNull(actor);
		Validate.notEmpty(uuid);
		User user = userService.findByLsUuid(uuid);
		return userMongoRepository.findByOwnerUuid(user.getLsUuid());
	}

	@Override
	public List<AuditLogEntryUser> userFindByType(Account actor, AuditLogEntryType type) {
		Validate.notNull(actor);
		Validate.notNull(type);
		return userMongoRepository.findByType(type);
	}

	@Override
	public List<AuditLogEntryUser> userFindByActorUuidAndAction(String actorUuid, String action, Account actor) {
		Validate.notEmpty(actorUuid);
		Validate.notEmpty(action);
		Validate.notNull(actor);
		User user = userService.findByLsUuid(actorUuid);
		return userMongoRepository.findByActorUuidAndAction(user.getLsUuid(), action);
	}

	@Override
	public List<AuditLogEntryUser> userFindByActorUuidAndAction(Account actor, String actorUuid, String ownerUuid) {
		Validate.notEmpty(actorUuid);
		Validate.notEmpty(ownerUuid);
		Validate.notNull(actor);
		User owner = userService.findByLsUuid(ownerUuid);
		User user = userService.findByLsUuid(actorUuid);
		return userMongoRepository.findByActorUuidOrOwnerUuid(user.getLsUuid(), owner.getLsUuid());
	}

	@Override
	public List<AuditLogEntryUser> findAll(Account actor, List<String> action, List<String> type, boolean forceAll,
			Date beginDate, Date endDate) {
		Validate.notNull(actor);
		Calendar c = new GregorianCalendar();
		Date d = c.getTime();
		c.add(Calendar.DATE, -10);
		Date n = c.getTime();
		List<LogAction> actions = Lists.newArrayList();
		List<AuditLogEntryType> types = Lists.newArrayList();
		List<AuditLogEntryUser> res = Lists.newArrayList();
		for (String a : action) {
			if (a != null && !a.isEmpty()) {
				actions.add(LogAction.fromString(a));
			} else {
				actions.add(LogAction.CREATE);
				actions.add(LogAction.UPDATE);
				actions.add(LogAction.DELETE);
				actions.add(LogAction.GET);
			}
		}
		for (String t : type) {
			if (t != null && !t.isEmpty()) {
				types.add(AuditLogEntryType.fromString(t));
			} else {
				types.addAll(AuditLogEntryType.getAllUSer());
//				types.add(AuditLogEntryType.DOCUMENT);
//				types.add(AuditLogEntryType.THREAD);
//				types.add(AuditLogEntryType.UPLOAD_REQUEST);
//				types.add(AuditLogEntryType.UPLOAD_REQUEST_GROUP);
//				types.add(AuditLogEntryType.SHARE_ENTRY);
//				types.add(AuditLogEntryType.ANONYMOUS_SHARE_ENTRY);
//				types.add(AuditLogEntryType.GUEST);
//				types.add(AuditLogEntryType.THREAD_MEMBER);
//				types.add(AuditLogEntryType.USER_PREFERENCE);
//				types.add(AuditLogEntryType.USER);
//				types.add(AuditLogEntryType.LIST);
//				types.add(AuditLogEntryType.LIST_CONTACT);
			}
		}
		if (forceAll) {
			res = userMongoRepository.findForUser(actor.getLsUuid(), actions, types);
		} else {
			res = userMongoRepository.findForUser(actor.getLsUuid(), actions, types, n, d);
		}
		return res;
	}
}