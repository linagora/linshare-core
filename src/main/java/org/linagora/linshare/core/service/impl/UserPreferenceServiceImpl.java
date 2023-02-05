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

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.UserPreferenceResourceAccessControl;
import org.linagora.linshare.core.service.UserPreferenceService;
import org.linagora.linshare.mongo.entities.UserPreference;
import org.linagora.linshare.mongo.entities.logs.UserPreferenceAuditLogEntry;
import org.linagora.linshare.mongo.repository.AuditUserMongoRepository;
import org.linagora.linshare.mongo.repository.UserPreferenceMongoRepository;

public class UserPreferenceServiceImpl extends GenericServiceImpl<Account, UserPreference> implements UserPreferenceService {

	protected UserPreferenceMongoRepository repository;

	protected AuditUserMongoRepository mongoRepository;

	public UserPreferenceServiceImpl(UserPreferenceMongoRepository repository,
			AuditUserMongoRepository mongoRepository,
			UserPreferenceResourceAccessControl rac,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.repository = repository;
		this.mongoRepository = mongoRepository;
	}

	@Override
	public UserPreference findByKey(Account actor, Account owner, String key) {
		preChecks(actor, owner);
		UserPreference entry = repository.findByAccountUuidAndKey(owner.getLsUuid(), key);
		checkReadPermission(actor, owner, UserPreference.class, BusinessErrorCode.USER_PREFERENCE_FORBIDDEN, entry);
		return entry;
	}

	@Override
	public List<UserPreference> findByAccount(Account actor, Account owner) {
		preChecks(actor, owner);
		List<UserPreference> list = repository.findByAccountUuid(owner.getLsUuid());
		checkListPermission(actor, owner, UserPreference.class, BusinessErrorCode.USER_PREFERENCE_FORBIDDEN, null);
		return list;
	}

	@Override
	public List<UserPreference> findByDomain(Account actor, AbstractDomain domain) {
		List<UserPreference> list = repository.findByDomainUuid(domain.getUuid());
		return list;
	}

	@Override
	public UserPreference findByUuid(Account actor, Account owner, String uuid) {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "Missing uuid");
		UserPreference entry = repository.findByUuid(uuid);
		if (entry == null) {
			logger.error("Current actor " + actor.getAccountRepresentation()
					+ " is looking for a misssing user preference (" + uuid
					+ ") owned by : " + owner.getAccountRepresentation());
			String message = "Can not find user preference with uuid : " + uuid;
			throw new BusinessException(
					BusinessErrorCode.USER_PREFERENCE_NOT_FOUND, message);
		}
		checkReadPermission(actor, owner, UserPreference.class, BusinessErrorCode.USER_PREFERENCE_FORBIDDEN, entry);
		return entry;
	}

	@Override
	public UserPreference create(Account actor, Account owner, UserPreference dto) throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(dto, "Missing user preference object");
		dto.validate();
		dto.setAccountUuid(owner.getLsUuid());
		dto.setDomainUuid(owner.getDomainId());
		dto.setUuid(null);
//		dto.setUuid(UUID.randomUUID().toString());
		// Check if it a valid key ?
		UserPreference res = repository.insert(dto);
		UserPreferenceAuditLogEntry log = new UserPreferenceAuditLogEntry(actor, owner, LogAction.CREATE,
				AuditLogEntryType.USER_PREFERENCE, res);
		mongoRepository.insert(log);
		return res;
	}

	@Override
	public UserPreference update(Account actor, Account owner, UserPreference dto) throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(dto, "Missing user preference object");
		dto.validate();
		UserPreference entry = findByUuid(actor, owner, dto.getUuid());
		UserPreferenceAuditLogEntry log = new UserPreferenceAuditLogEntry(actor, owner, LogAction.CREATE,
				AuditLogEntryType.USER_PREFERENCE, entry);
		Validate.isTrue(entry.getKey().equals(dto.getKey()), "Key in dto does not match key in entity object.");
		dto.setAccountUuid(entry.getAccountUuid());
		dto.setDomainUuid(entry.getDomainUuid());
		entry = repository.save(dto);
		log.setResourceUpdated(entry);
		mongoRepository.insert(log);
		return entry;
	}

	@Override
	public UserPreference delete(Account actor, Account owner, String uuid) throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "Missing user preference uuid");
		UserPreference entry = findByUuid(actor, owner, uuid);
		repository.delete(entry);
		UserPreferenceAuditLogEntry log = new UserPreferenceAuditLogEntry(actor, owner, LogAction.CREATE,
				AuditLogEntryType.USER_PREFERENCE, entry);
		mongoRepository.insert(log);
		return entry;
	}

	@Override
	public void deleteAll(Account actor, Account owner) throws BusinessException {
		preChecks(actor, owner);
		repository.deleteByAccountUuid(owner.getLsUuid());
	}
}
