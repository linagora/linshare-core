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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AuditLogEntryResourceAccessControl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.springframework.data.domain.Sort;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AuditLogEntryServiceImpl extends GenericServiceImpl<Account, AuditLogEntryUser> implements AuditLogEntryService {

	private static final String CREATION_DATE = "creationDate";

	private AuditAdminMongoRepository auditMongoRepository;

	private AuditUserMongoRepository userMongoRepository;

	private UserService userService;

	private AbstractDomainService domainService;

	public AuditLogEntryServiceImpl(AuditAdminMongoRepository auditMongoRepository,
			AuditUserMongoRepository userMongoRepository,
			UserService userService,
			AbstractDomainService domainService,
			final AuditLogEntryResourceAccessControl rac) {
		super(rac);
		this.auditMongoRepository = auditMongoRepository;
		this.userMongoRepository = userMongoRepository;
		this.userService = userService;
		this.domainService = domainService;
	}

	@Override
	public Set<AuditLogEntryUser> findAll(Account actor, Account owner, List<String> action, List<String> type,
			boolean forceAll, String beginDate, String endDate) {
		Validate.notNull(actor);
		Validate.notNull(owner);
		Set<AuditLogEntryUser> res = Sets.newHashSet();
		List<LogAction> actions = getActions(action);
		List<AuditLogEntryType> types = getEntryTypes(type, null);
		if (forceAll) {
			res = userMongoRepository.findForUser(owner.getLsUuid(), actions, types);
		} else {
			Date end = getEndDate(endDate);
			Date begin = getBeginDate(beginDate, end);
			res = userMongoRepository.findForUser(owner.getLsUuid(), actions, types, begin, end);
		}
//		checkListPermission(actor, owner, AuditLogEntryUser.class, BusinessErrorCode.BAD_REQUEST,
//				res.iterator().next());
		return res;
	}

	@Override
	public Set<AuditLogEntry> findAll(Account actor, List<String> action, List<String> type,
			boolean forceAll, String beginDate, String endDate) {
		Validate.notNull(actor);
		if (!actor.hasSuperAdminRole()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not allowed to use this api."); 
		}
		Set<AuditLogEntry> res = Sets.newHashSet();
		List<LogAction> actions = getActions(action);
		List<AuditLogEntryType> types = getEntryTypes(type, null);
		if (actor.hasSuperAdminRole()) {
			if (forceAll) {
				res = auditMongoRepository.findAll(actions, types);
			} else {
				Date end = getEndDate(endDate);
				Date begin = getBeginDate(beginDate, end);
				res = auditMongoRepository.findAll(actions, types, begin, end);
			}
		}
		return res;
	}

	@Override
	public Set<AuditLogEntryUser> findAll(Account actor, Account owner, Thread workGroup, WorkGroupNode workGroupNode,
			List<String> action, List<String> type, String beginDate, String endDate) {
		Validate.notNull(actor);
		Validate.notNull(owner);
		Validate.notNull(workGroup);
		Set<AuditLogEntryUser> res = Sets.newHashSet();
		List<AuditLogEntryType> supportedTypes = Lists.newArrayList();
		supportedTypes.add(AuditLogEntryType.WORKGROUP);
		supportedTypes.add(AuditLogEntryType.WORKGROUP_DOCUMENT);
		supportedTypes.add(AuditLogEntryType.WORKGROUP_FOLDER);
		supportedTypes.add(AuditLogEntryType.WORKGROUP_MEMBER);
		List<AuditLogEntryType> types = getEntryTypes(type, supportedTypes);
		List<LogAction> actions = getActions(action);
		Date end = getEndDate(endDate);
		Date begin = getBeginDate(beginDate, end);
		// TODO:workgroups: use limit (Pageable query).
		if (workGroupNode != null) {
			res = userMongoRepository.findWorgGroupNodeHistoryForUser(
					workGroup.getLsUuid(), workGroupNode.getUuid(),
					actions, types,
					begin, end,
					new Sort(Sort.Direction.DESC, CREATION_DATE));
		} else {
			res = userMongoRepository.findWorgGroupHistoryForUser(
					workGroup.getLsUuid(),
					actions, types,
					begin, end,
					new Sort(Sort.Direction.DESC, CREATION_DATE));
		}
		return res;
	}

	@Override
	public Set<AuditLogEntryUser> findAllContactLists(Account actor, Account owner, String contactListUuid) {
		Validate.notNull(actor);
		Validate.notNull(owner);
		Validate.notNull(contactListUuid);
		Set<AuditLogEntryUser> res = Sets.newHashSet();
		List<AuditLogEntryType> supportedTypes = Lists.newArrayList();
		supportedTypes.add(AuditLogEntryType.CONTACTS_LISTS);
		supportedTypes.add(AuditLogEntryType.CONTACTS_LISTS_CONTACTS);
		res = userMongoRepository.findContactListsActivity(
				contactListUuid,
				supportedTypes,
				new Sort(Sort.Direction.DESC, CREATION_DATE));
		return res;
	}

	@Override
	public Set<AuditLogEntryUser> findAll(Account actor, Account owner, String entryUuid, List<String> action,
			List<String> type, String beginDate, String endDate) {
		Validate.notNull(actor);
		Validate.notNull(owner);
		Validate.notNull(entryUuid);
		Set<AuditLogEntryUser> res = Sets.newHashSet();
		List<AuditLogEntryType> supportedTypes = Lists.newArrayList();
		supportedTypes.add(AuditLogEntryType.DOCUMENT_ENTRY);
		supportedTypes.add(AuditLogEntryType.SHARE_ENTRY);
		supportedTypes.add(AuditLogEntryType.ANONYMOUS_SHARE_ENTRY);
		List<AuditLogEntryType> types = getEntryTypes(type, supportedTypes);
		List<LogAction> actions = getActions(action);
		res = userMongoRepository.findDocumentHistoryForUser(
				owner.getLsUuid(), entryUuid,
				actions, types,
				new Sort(Sort.Direction.DESC, CREATION_DATE));
		return res;
	}

	protected List<AuditLogEntryType> getEntryTypes(List<String> type, List<AuditLogEntryType> supportedTypes) {
		List<AuditLogEntryType> types = Lists.newArrayList();
		if (type != null && !type.isEmpty()) {
			for (String t : type) {
				AuditLogEntryType entryType = AuditLogEntryType.fromString(t);
				if (supportedTypes != null) {
					if (supportedTypes.contains(entryType)) {
						types.add(entryType);
					}
				} else {
					types.add(entryType);
				}
			}
		} else {
			if (supportedTypes != null) {
				types = supportedTypes;
			} else {
				types = Lists.newArrayList(AuditLogEntryType.class.getEnumConstants());
			}
		}
		return types;
	}

	protected Date getBeginDate(String beginDate, Date end) {
		Date begin = null;
		if (beginDate == null) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(end);
			cal.add(Calendar.DAY_OF_MONTH, -7);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			begin = cal.getTime();
		} else {
			try {
				begin = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(beginDate);
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
				throw new BusinessException(BusinessErrorCode.BAD_REQUEST, "Can not convert begin date.");
			}
		}
		return begin;
	}

	protected Date getEndDate(String endDate) {
		Date end = null;
		if (endDate == null) {
			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.add(Calendar.SECOND, 1);
			end = cal.getTime();
		} else {
			try {
				end = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(endDate);
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
				throw new BusinessException(BusinessErrorCode.BAD_REQUEST, "Can not convert end date.");
			}
		}
		return end;
	}

	protected List<LogAction> getActions(List<String> action) {
		List<LogAction> actions = Lists.newArrayList();
		if (action != null && !action.isEmpty()) {
			for (String a : action) {
				actions.add(LogAction.fromString(a));
			}
		} else {
			actions = Lists.newArrayList(LogAction.class.getEnumConstants());
		}
		return actions;
	}
}