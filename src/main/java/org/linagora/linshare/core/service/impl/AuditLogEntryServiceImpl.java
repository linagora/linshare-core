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
package org.linagora.linshare.core.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.AuditGroupLogEntryType;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.AuditEntryField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AuditLogEntryResourceAccessControl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.TimeService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.MailAttachmentAuditLogEntry;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AuditLogEntryServiceImpl extends GenericServiceImpl<Account, AuditLogEntry> implements AuditLogEntryService {

	private static final Logger logger = LoggerFactory
			.getLogger(AuditLogEntryServiceImpl.class);

	private static final String CREATION_DATE = "creationDate";

	protected final AuditAdminMongoRepository adminMongoRepository;

	protected final AuditUserMongoRepository userMongoRepository;

	protected final DomainPermissionBusinessService permissionService;

	protected final AbstractDomainService domainService;

	protected final TimeService timeService;

	protected final MongoTemplate mongoTemplate;

	public AuditLogEntryServiceImpl(
			AuditAdminMongoRepository auditMongoRepository,
			AuditUserMongoRepository userMongoRepository,
			DomainPermissionBusinessService permissionService,
			TimeService timeService,
			MongoTemplate mongoTemplate,
			AbstractDomainService domainService,
			AuditLogEntryResourceAccessControl rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.adminMongoRepository = auditMongoRepository;
		this.userMongoRepository = userMongoRepository;
		this.permissionService = permissionService;
		this.timeService = timeService;
		this.mongoTemplate = mongoTemplate;
		this.domainService = domainService;
	}

	/**
	 * UserRestService
	 */
	@Override
	public Set<AuditLogEntryUser> findAllForUsers(Account authUser, Account actor, List<LogAction> action, List<AuditLogEntryType> type,
			boolean forceAll, String beginDate, String endDate) {
		Validate.notNull(authUser);
		Validate.notNull(actor);
		Set<AuditLogEntryUser> res = Sets.newHashSet();
		List<LogAction> actions = getActions(action);
		List<AuditLogEntryType> types = getEntryTypes(type, null, true);
		if (forceAll) {
			res = userMongoRepository.findForUser(actor.getLsUuid(), actions, types);
		} else {
			Date end = getEndDate(endDate);
			Date begin = getBeginDate(beginDate, end);
			res = userMongoRepository.findForUser(actor.getLsUuid(), actions, types, begin, end);
		}
		checkListPermission(authUser, actor, AuditLogEntryUser.class, BusinessErrorCode.BAD_REQUEST,
				res.iterator().next());
		return res;
	}

	/**
	 * For administrators only.
	 */
	@Override
	public Set<AuditLogEntry> findAllForAdmins(Account actor, List<LogAction> action, List<AuditLogEntryType> type,
			boolean forceAll, String beginDate, String endDate) {
		Validate.notNull(actor);
		if (!(actor.hasSuperAdminRole() || actor.hasAdminRole())) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not allowed to use this api."); 
		}
		List<LogAction> actions = getActions(action);
		List<AuditLogEntryType> types = getEntryTypes(type, null, true);
		if (actor.hasSuperAdminRole()) {
			if (forceAll) {
				return adminMongoRepository.findAll(actions, types);
			} else {
				Date end = getEndDate(endDate);
				Date begin = getBeginDate(beginDate, end);
				return adminMongoRepository.findAll(actions, types, begin, end);
			}
		} else {
			List<String> domains = permissionService.getAdministratedDomainsIdentifiers(actor, actor.getDomainId());
			Date end = getEndDate(endDate);
			Date begin = getBeginDate(beginDate, end);
			return adminMongoRepository.findAll(actions, types, begin, end, domains);
		}
	}

	@Override
	public Set<AuditLogEntryUser> findAllSharedSpaceAudits(Account authUser, User actor, String sharedSpaceUuid,
			String resourceUuid, List<LogAction> actions, List<AuditLogEntryType> types, String beginDate, String endDate) {
		Validate.notNull(authUser);
		Validate.notNull(actor);
		List<AuditLogEntryType> supportedTypes = Lists.newArrayList();
		supportedTypes.add(AuditLogEntryType.WORK_GROUP);
		supportedTypes.add(AuditLogEntryType.WORKGROUP_DOCUMENT);
		supportedTypes.add(AuditLogEntryType.WORKGROUP_FOLDER);
		supportedTypes.add(AuditLogEntryType.WORKGROUP_MEMBER);
		supportedTypes.add(AuditLogEntryType.WORKGROUP_DOCUMENT_REVISION);
		supportedTypes.add(AuditLogEntryType.WORK_SPACE);
		supportedTypes.add(AuditLogEntryType.WORK_SPACE_MEMBER);
		Date end = getEndDate(endDate);
		Date begin = getBeginDate(beginDate, end);
		if (Objects.nonNull(resourceUuid)) {
			return userMongoRepository.findWorkGroupNodeHistoryForUser(
					sharedSpaceUuid, resourceUuid,
					getActions(actions),
					getEntryTypes(types, supportedTypes, true),
					begin, end,
					Sort.by(Sort.Direction.DESC, CREATION_DATE));
		} else {
			return userMongoRepository.findAllSharedSpaceAuditsForUser(
					sharedSpaceUuid,
					getActions(actions),
					getEntryTypes(types, supportedTypes, true),
					begin, end,
					Sort.by(Sort.Direction.DESC, CREATION_DATE));
		}
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
				Sort.by(Sort.Direction.DESC, CREATION_DATE));
		return res;
	}

	@Override
	public Set<AuditLogEntryUser> findAll(Account actor, Account owner, String entryUuid, List<LogAction> action,
			List<AuditLogEntryType> type, String beginDate, String endDate) {
		Validate.notNull(actor);
		Validate.notNull(owner);
		Validate.notNull(entryUuid);
		Set<AuditLogEntryUser> res = Sets.newHashSet();
		List<AuditLogEntryType> supportedTypes = Lists.newArrayList();
		supportedTypes.add(AuditLogEntryType.DOCUMENT_ENTRY);
		supportedTypes.add(AuditLogEntryType.SHARE_ENTRY);
		supportedTypes.add(AuditLogEntryType.ANONYMOUS_SHARE_ENTRY);
		List<AuditLogEntryType> types = getEntryTypes(type, supportedTypes, true);
		List<LogAction> actions = getActions(action);
		res = userMongoRepository.findDocumentHistoryForUser(
				owner.getLsUuid(), entryUuid,
				actions, types,
				Sort.by(Sort.Direction.DESC, CREATION_DATE));
		return res;
	}

	protected List<AuditLogEntryType> getEntryTypes(List<AuditLogEntryType> entryTypes, List<AuditLogEntryType> supportedTypes, boolean defaultType) {
		List<AuditLogEntryType> types = Lists.newArrayList();
		if (entryTypes != null && !entryTypes.isEmpty()) {
			for (AuditLogEntryType type : entryTypes) {
				if (supportedTypes != null) {
					if (supportedTypes.contains(type)) {
						types.add(type);
					}
				} else {
					types.add(type);
				}
			}
		} else {
			if (defaultType) {
				if (supportedTypes != null) {
					types = supportedTypes;
				} else {
					types = Lists.newArrayList(AuditLogEntryType.class.getEnumConstants());
				}
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

	protected List<LogAction> getActions(List<LogAction> action) {
		if (action == null || action.isEmpty()) {
			return Lists.newArrayList(LogAction.class.getEnumConstants());
		}
		return action;
	}

	@Override
	public Set<AuditLogEntryUser> findAllAuditsOfGroup(Account authUser, Account actor, String uploadRequestGroupUuid, boolean all,
			List<LogAction> action, List<AuditLogEntryType> types) {
		Validate.notNull(authUser);
		Validate.notNull(actor);
		Set<AuditLogEntryUser> res = Sets.newHashSet();
		List<AuditLogEntryType> supportedTypes = Lists.newArrayList();
		supportedTypes.add(AuditLogEntryType.UPLOAD_REQUEST_GROUP);
		supportedTypes.add(AuditLogEntryType.UPLOAD_REQUEST);
		supportedTypes.add(AuditLogEntryType.UPLOAD_REQUEST_URL);
		if (all) {
			supportedTypes.add(AuditLogEntryType.UPLOAD_REQUEST_ENTRY);
		}
		res = userMongoRepository.findUploadRequestHistoryForUser(actor.getLsUuid(), uploadRequestGroupUuid, getActions(action),
				getEntryTypes(types, supportedTypes, true), Sort.by(Sort.Direction.DESC, CREATION_DATE));
		return res;
	}

	@Override
	public Set<AuditLogEntryAdmin> findAll(Account actor, String domainUuid, List<LogAction> action) {
		Validate.notNull(actor);
		if (action.isEmpty()) {
			action.add(LogAction.CREATE);
			action.add(LogAction.DELETE);
		}
		return adminMongoRepository.findAll(domainUuid, action, AuditLogEntryType.PUBLIC_KEY,
				Sort.by(Sort.Direction.DESC, CREATION_DATE));
	}

	private List<LogAction> getCreateUpdateDeletetActions(List<LogAction> actions) {
		if (actions == null || actions.isEmpty()) {
			actions.add(LogAction.CREATE);
			actions.add(LogAction.DELETE);
			actions.add(LogAction.UPDATE);
		}
		return actions;
	}

	@Override
	public Set<MailAttachmentAuditLogEntry> findAllAudits(Account authUser, String uuid,
			List<LogAction> actions) {
		List<LogAction> actionsList = getCreateUpdateDeletetActions(actions);
		return adminMongoRepository.findAllAudits(uuid, actionsList, AuditLogEntryType.MAIL_ATTACHMENT,
				Sort.by(Sort.Direction.DESC, CREATION_DATE));
	}

	@Override
	public Set<MailAttachmentAuditLogEntry> findAllAuditsByDomain(Account authUser, List<String> domains,
			List<LogAction> actions) {
		List<LogAction> actionsList = getCreateUpdateDeletetActions(actions);
		return adminMongoRepository.findAllAuditsByDomain(domains, actionsList, AuditLogEntryType.MAIL_ATTACHMENT,
				Sort.by(Sort.Direction.DESC, CREATION_DATE));
	}

	@Override
	public Set<MailAttachmentAuditLogEntry> findAllAuditsByRoot(Account authUser, List<LogAction> actions) {
		Validate.notNull(authUser);
		if (!authUser.hasSuperAdminRole()) {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not allowed to use this api.");
		}
		List<LogAction> actionsList = getCreateUpdateDeletetActions(actions);
		return adminMongoRepository.findAllAuditsByRoot(actionsList, AuditLogEntryType.MAIL_ATTACHMENT,
				Sort.by(Sort.Direction.DESC, CREATION_DATE));
	}

	@Override
	public Set<AuditLogEntryUser> findAllUploadRequestAudits(Account authUser, Account actor, String uploadRequestUuid,
			List<LogAction> actions, List<AuditLogEntryType> types) {
		List<AuditLogEntryType> supportedTypes = Lists.newArrayList();
		supportedTypes.add(AuditLogEntryType.UPLOAD_REQUEST);
		supportedTypes.add(AuditLogEntryType.UPLOAD_REQUEST_URL);
		supportedTypes.add(AuditLogEntryType.UPLOAD_REQUEST_ENTRY);
		return userMongoRepository.findAllUploadRequestAuditTraces(actor.getLsUuid(), uploadRequestUuid,
				getActions(actions), getEntryTypes(types, supportedTypes, true), Sort.by(Sort.Direction.DESC, CREATION_DATE));
	}

	@Override
	public Set<AuditLogEntryUser> findAllUploadRequestEntryAudits(Account authUser, Account actor,
			String uploadRequestEntryUuid, List<LogAction> actions) {
		return userMongoRepository.findAllUploadRequestEntryAuditTraces(actor.getLsUuid(), uploadRequestEntryUuid,
				getActions(actions), Sort.by(Sort.Direction.DESC, CREATION_DATE));
	}

	@Override
	public Set<AuditLogEntryUser> findAllModeratorAudits(Account authUser, Account actor, String ModeratorUuid,
			List<LogAction> actions, List<AuditLogEntryType> types, String beginDate, String endDate) {
		List<AuditLogEntryType> supportedTypes = Lists.newArrayList();
		supportedTypes.add(AuditLogEntryType.GUEST_MODERATOR);
		supportedTypes.add(AuditLogEntryType.GUEST);
		Set<AuditLogEntryUser> audits = userMongoRepository.findAllModeratorTraces(actor.getLsUuid(), ModeratorUuid,
				getCreateUpdateDeletetActions(actions), getEntryTypes(types, supportedTypes, true),
				Sort.by(Sort.Direction.DESC, CREATION_DATE));
		return audits;
	}

	@Override
	public AuditLogEntry find(Account authUser, AbstractDomain domain, String uuid) {
		Validate.notNull(authUser, "authUser must be set.");
		if (!permissionService.isAdminForThisDomain(authUser, domain)) {
			throw new BusinessException(
					BusinessErrorCode.FORBIDDEN,
					"You are not allowed to query this domain");
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("uuid").is(uuid));
		if (!domain.isRootDomain()) {
			List<String> domainUuids = permissionService.getAdministratedDomainsIdentifiers(authUser, domain.getUuid());
			query.addCriteria(Criteria.where("relatedDomains").in(domainUuids));
		}
		AuditLogEntry trace = mongoTemplate.findOne(query, AuditLogEntry.class, "audit_log_entries");
		if (trace == null) {
			throw new BusinessException(
					BusinessErrorCode.AUDIT_LOG_ENTRY_DO_NOT_EXIST,
					"Not found");
		}
		return trace;
	}

	@Override
	public PageContainer<AuditLogEntry> findAll(
			Account authUser,
			AbstractDomain domain, boolean includeNestedDomains, Set<String> domains,
			SortOrder sortOrder, AuditEntryField sortField,
			Set<LogAction> logActions, Set<AuditLogEntryType> resourceTypes,
			Set<AuditGroupLogEntryType> resourceGroups,
			Set<AuditLogEntryType> excludedTypes,
			Optional<String> authUserUuid, Optional<String> actorUuid,
			Optional<String> actorEmail,
			Optional<String> relatedAccount,
			Optional<String> resource,
			Optional<String> relatedResource,
			Optional<String> resourceName,
			Optional<String> beginDate, Optional<String> endDate,
			PageContainer<AuditLogEntry> container) {
		Validate.notNull(authUser, "authUser must be set.");
		checkDomainPermissions(authUser, domain, domains);

		Pair<Optional<LocalDate>, Optional<LocalDate>> period = getPeriod(beginDate, endDate);
		Query query = getQuery(
				authUser, domain, includeNestedDomains, domains, logActions, resourceTypes, resourceGroups,
				excludedTypes, authUserUuid, actorUuid, actorEmail, relatedAccount, resource, relatedResource,
				resourceName, period.getFirst(), period.getSecond());

		long count = mongoTemplate.count(query, AuditLogEntry.class);
		logger.debug("Total of elements returned by the query without pagination: {}", count);
		if (count == 0) {
			return new PageContainer<AuditLogEntry>();
		}

		paginateQuery(sortOrder, sortField, container, query, count);
		return container.loadData(performQuery(query));
	}

	@NotNull
	private List<AuditLogEntry> performQuery(Query query) {
		long startTime = System.currentTimeMillis();
		List<AuditLogEntry> data = mongoTemplate.find(query, AuditLogEntry.class);
		long elapsed = System.currentTimeMillis() - startTime;
		logger.debug("audit query duration : {} ms.", elapsed);
		return data;
	}

	private static void paginateQuery(SortOrder sortOrder, AuditEntryField sortField, PageContainer<AuditLogEntry> container, Query query, long count) {
		Pageable paging = PageRequest.of(container.getPageNumber(), container.getPageSize());
		query.with(paging);
		query.with(Sort.by(SortOrder.getSortDir(sortOrder), sortField.toString()));
		container.validateTotalPagesCount(count);
	}

	@NotNull
	private Query getQuery(Account authUser, AbstractDomain domain, boolean includeNestedDomains, Set<String> domains,
						   Set<LogAction> logActions, Set<AuditLogEntryType> resourceTypes, Set<AuditGroupLogEntryType> resourceGroups,
						   Set<AuditLogEntryType> excludedTypes, Optional<String> authUserUuid, Optional<String> actorUuid,
						   Optional<String> actorEmail, Optional<String> relatedAccount, Optional<String> resource, Optional<String> relatedResource,
						   Optional<String> resourceName, Optional<LocalDate> begin, Optional<LocalDate> end) {
		Query query = new Query();
		//Domains
		if (includeNestedDomains) {
			if (!domain.isRootDomain()) {
				List<String> domainUuids = permissionService.getAdministratedDomainsIdentifiers(authUser, domain.getUuid());
				query.addCriteria(Criteria.where("relatedDomains").in(domainUuids));
			}
		} else {
			if (!domains.isEmpty()) {
				query.addCriteria(Criteria.where("relatedDomains").in(domains));
			} else {
				query.addCriteria(Criteria.where("relatedDomains").is(domain.getUuid()));
			}
		}

		//Log type
		if (!logActions.isEmpty()) {
			query.addCriteria(Criteria.where("action").in(logActions));
		}
		if (!resourceGroups.isEmpty()) {
			for (AuditGroupLogEntryType type : resourceGroups) {
				resourceTypes.addAll(AuditGroupLogEntryType.toAuditLogEntryTypes.get(type));
			}
		}
		if (!resourceTypes.isEmpty()) {
			query.addCriteria(Criteria.where("type").in(resourceTypes));
		} else {
			if (!excludedTypes.isEmpty()) {
				query.addCriteria(Criteria.where("type").nin(excludedTypes));
			}
		}

		// Users
		authUserUuid.ifPresent(s -> query.addCriteria(Criteria.where("authUser.uuid").is(s)));
		actorUuid.ifPresent(s -> query.addCriteria(Criteria.where("actor.uuid").is(s)));
		actorEmail.ifPresent(s -> query.addCriteria(Criteria.where("actor.mail").regex(s, "i")));

		// Period
		if (begin.isPresent() && end.isPresent() ) {
			query.addCriteria(Criteria.where("creationDate").gte(begin.get()).lt(end.get()));
		} else {
			end.ifPresent(localDate -> query.addCriteria(Criteria.where("creationDate").lt(localDate)));
			begin.ifPresent(localDate -> query.addCriteria(Criteria.where("creationDate").gte(localDate)));
		}

		// Resource
		relatedAccount.ifPresent(s -> query.addCriteria(Criteria.where("relatedAccounts").in(s)));
		resource.ifPresent(s -> query.addCriteria(Criteria.where("resourceUuid").is(s)));
		if (relatedResource.isPresent()) {
			String uuid = relatedResource.get();
			query.addCriteria(new Criteria().orOperator(
					Criteria.where("relatedResources").in(uuid),
					Criteria.where("resourceUuid").is(uuid)
					)
			);
		}
		resourceName.ifPresent(s -> query.addCriteria(Criteria.where("resource.name").regex(s, "i")));
		return query;
	}

	@NotNull
	private static Pair<Optional<LocalDate>, Optional<LocalDate>> getPeriod(Optional<String> beginDate, Optional<String> endDate) {
		Optional<LocalDate> begin = Optional.empty();
		Optional<LocalDate> end = Optional.empty();
		try {
			if (beginDate.isPresent()) {
				begin = Optional.of(LocalDate.parse(beginDate.get()));
			}
			if (endDate.isPresent()) {
				end = Optional.of(LocalDate.parse(endDate.get()));
			}
			if (begin.isPresent() && end.isPresent()) {
				if (end.get().isBefore(begin.get())) {
					throw new BusinessException(
							BusinessErrorCode.STATISTIC_DATE_RANGE_ERROR,
							String.format("begin date (%s) must be before end date (%s)", begin.get(), end.get())
					);
				}
			}
		} catch (DateTimeParseException e) {
			throw new BusinessException(BusinessErrorCode.STATISTIC_DATE_PARSING_ERROR, e.getMessage());
		}
		return new Pair<>(begin, end);
	}

	private void checkDomainPermissions(Account authUser, AbstractDomain domain, Set<String> domains) {
		if (!permissionService.isAdminForThisDomain(authUser, domain)) {
			throw new BusinessException(
					BusinessErrorCode.FORBIDDEN,
					"You are not allowed to query this domain");
		}
		if (!domains.isEmpty()) {
			for (String domainUuid : domains) {
				AbstractDomain d = domainService.findById(domainUuid);
				if (!permissionService.isAdminForThisDomain(authUser, d)) {
					throw new BusinessException(
							BusinessErrorCode.FORBIDDEN,
							"You are not allowed to query this domain: " + domainUuid);
				}
			}
		}
	}
}