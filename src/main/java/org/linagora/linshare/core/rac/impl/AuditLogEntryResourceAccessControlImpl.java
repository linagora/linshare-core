package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.rac.AuditLogEntryResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

public class AuditLogEntryResourceAccessControlImpl extends
		AbstractResourceAccessControlImpl<Account, Account, AuditLogEntry> implements
        AuditLogEntryResourceAccessControl {
    public AuditLogEntryResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
        super(functionalityService);
    }

    @Override
    protected boolean hasReadPermission(Account authUser, Account account, AuditLogEntry entry, Object... opt) {
        if (authUser.hasSuperAdminRole()) {
            return true;
        }
        if (isUserRelated(authUser, entry)) {
            return true;
        }
        if (authUser.hasAdminRole() && account != null) {
            return account.getDomain().isManagedBy(authUser);
        }
        if (authUser.hasDelegationRole()) {
            return hasPermission(authUser, TechnicalAccountPermissionType.AUDIT_LIST);
        }
        return false;
    }

    @Override
    protected boolean hasListPermission(Account authUser, Account account, AuditLogEntry entry, Object... opt) {
        if (authUser.hasSuperAdminRole()) {
            return true;
        }
        if (isUserRelated(authUser, entry)) {
            return true;
        }
        if (authUser.hasAdminRole() && account != null) {
            return account.getDomain().isManagedBy(authUser);
        }
        if (authUser.hasDelegationRole()) {
            return hasPermission(authUser, TechnicalAccountPermissionType.AUDIT_LIST);
        }
        return false;
    }

    private static boolean isUserRelated(Account authUser, AuditLogEntry entry) {
        boolean authUserIsOwner = entry.getAuthUser().getUuid().equals(authUser.getLsUuid());
        boolean authUserIsRelated = entry instanceof AuditLogEntryUser && ((AuditLogEntryUser) entry).getRelatedAccounts().contains(authUser.getLsUuid());
        return authUserIsOwner || authUserIsRelated;
    }

    @Override
    protected boolean hasDeletePermission(Account authUser, Account account, AuditLogEntry entry, Object... opt) {
        return false;
    }

    @Override
    protected boolean hasCreatePermission(Account authUser, Account account, AuditLogEntry entry, Object... opt) {
        return false;
    }

    @Override
    protected boolean hasUpdatePermission(Account authUser, Account account, AuditLogEntry entry, Object... opt) {
        return false;
    }

    @Override
    protected String getTargetedAccountRepresentation(Account targetedAccount) {
        return targetedAccount.getAccountRepresentation();
    }

    @Override
    protected Account getOwner(AuditLogEntry entry, Object... opt) {
        return null;
    }

    @Override
    protected String getEntryRepresentation(AuditLogEntry entry) {
        return entry.toString();
    }
}
