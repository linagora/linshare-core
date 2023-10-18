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

import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.TechnicalAccountBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.TechnicalAccount;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.TechnicalAccountPermissionService;
import org.linagora.linshare.core.service.TechnicalAccountService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.mongo.entities.logs.UserAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.UserMto;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;


public class TechnicalAccountServiceImpl implements TechnicalAccountService {

    private final TechnicalAccountBusinessService technicalAccountBusinessService;

    private final TechnicalAccountPermissionService technicalAccountPermissionService;

    private final PasswordService passwordService;

    private final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService;

    private final UserService userService;

    private final AuditAdminMongoRepository auditMongoRepository;

    public TechnicalAccountServiceImpl(
            final TechnicalAccountBusinessService technicalAccountBusinessService,
            final TechnicalAccountPermissionService technicalAccountPermissionService,
            final SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
            final PasswordService passwordService,
            final UserService userService,
            final AuditAdminMongoRepository auditMongoRepository) {
        super();
        this.technicalAccountBusinessService = technicalAccountBusinessService;
        this.technicalAccountPermissionService = technicalAccountPermissionService;
        this.sanitizerInputHtmlBusinessService = sanitizerInputHtmlBusinessService;
        this.passwordService = passwordService;
        this.userService = userService;
        this.auditMongoRepository = auditMongoRepository;
    }

    @Override
    public TechnicalAccount create(Account actor, TechnicalAccount account) throws BusinessException {
        Validate.notNull(actor, "actor must be set.");
        Validate.notNull(account, "account must be set.");
        Validate.notEmpty(account.getLastName(), "last name must be set.");
        // TODO : check right
        // Check role : only uploadprop or delegation
        // mail unicity ?
        TechnicalAccountPermission accountPermission = technicalAccountPermissionService.create(actor, new TechnicalAccountPermission());
        account.setPermission(accountPermission);
        account.setLastName(sanitize(account.getLastName()));
        account = technicalAccountBusinessService.create(actor.getDomainId(), account);
        passwordService.validateAndStorePassword(account, account.getPassword());

        UserAuditLogEntry log = new UserAuditLogEntry(actor, account, LogAction.CREATE, AuditLogEntryType.USER, account);
        auditMongoRepository.insert(log);

        return account;
    }

    private String sanitize(String input) {
        return sanitizerInputHtmlBusinessService.strictClean(input);
    }

    @Override
    public void delete(Account actor, TechnicalAccount account)
            throws BusinessException {
        // TODO : check rights
        TechnicalAccountPermission permission = account.getPermission();
        account.setPermission(null);
        technicalAccountBusinessService.update(account);
        if (permission != null) {
            technicalAccountPermissionService.delete(actor, permission);
        }
        technicalAccountBusinessService.delete(account);

        UserAuditLogEntry log = new UserAuditLogEntry(actor, account, LogAction.DELETE, AuditLogEntryType.USER, account);
        auditMongoRepository.insert(log);
    }

    @Override
    public TechnicalAccount find(Account actor, String uuid)
            throws BusinessException {
        // TODO : check rights
        TechnicalAccount account = technicalAccountBusinessService.find(uuid);
        if (account == null) {
            throw new BusinessException(BusinessErrorCode.TECHNICAL_ACCOUNT_NOT_FOUND,
                    "The technical account does not exist : " + uuid);
        }
        return account;
    }

    @Override
    public Set<TechnicalAccount> findAll(Account actor)
            throws BusinessException {
        // TODO : check rights
        return technicalAccountBusinessService.findAll(LinShareConstants.rootDomainIdentifier);
    }

    @Override
    public TechnicalAccount update(Account actor, TechnicalAccount updatedAccount)
            throws BusinessException {
        return update(actor, updatedAccount, null);
    }

    @Override
    public TechnicalAccount update(Account actor, TechnicalAccount account, Boolean unlock)
            throws BusinessException {
        // TODO : check rights
        TechnicalAccount entity = find(actor, account.getLsUuid());
        checkAccountPermission(actor, entity, account);
        entity.setLastName(sanitize(account.getLastName()));
        entity.setMail(account.getMail());
        entity.setEnable(account.isEnable());

        if (entity.isLocked() && unlock) {
            entity = (TechnicalAccount) userService.unlockUser(actor, entity);
        }
        TechnicalAccount updatedUser = technicalAccountBusinessService.update(entity);

        UserAuditLogEntry log = new UserAuditLogEntry(actor, entity, LogAction.UPDATE, AuditLogEntryType.USER, entity);
        log.setResource(new UserMto(entity));
        log.setResourceUpdated(new UserMto(updatedUser));
        auditMongoRepository.insert(log);

        return updatedUser;
    }

    private void checkAccountPermission(Account actor, TechnicalAccount foundAccount, TechnicalAccount account) {
        if (Role.DELEGATION.equals(foundAccount.getRole())) {
            TechnicalAccountPermission permissionDto = account.getPermission();
            permissionDto.setUuid(foundAccount.getPermission().getUuid());
            technicalAccountPermissionService.update(actor, permissionDto);
        }
    }

    @Override
    public void changePassword(User authUser, User actor, String oldPassword, String newPassword)
            throws BusinessException {
        userService.changePassword(authUser, actor, oldPassword, newPassword);
    }
}
