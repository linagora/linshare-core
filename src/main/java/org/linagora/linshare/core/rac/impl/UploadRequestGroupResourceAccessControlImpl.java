package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.constants.TechnicalAccountPermissionType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.rac.UploadRequestGroupResourceAccessControl;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class UploadRequestGroupResourceAccessControlImpl
		extends AbstractResourceAccessControlImpl<Account, Account, UploadRequestGroup>
		implements UploadRequestGroupResourceAccessControl {

	public UploadRequestGroupResourceAccessControlImpl(FunctionalityReadOnlyService functionalityService) {
		super(functionalityService);
	}

	@Override
	protected boolean hasReadPermission(Account actor, Account account, UploadRequestGroup entry, Object... opt) {
		if (isEnable(actor)) {
			return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_GET);
		}
		return false;
	}

	@Override
	protected boolean hasListPermission(Account actor, Account account, UploadRequestGroup entry, Object... opt) {
		if (isEnable(actor)) {
			return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_LIST);
		}
		return false;
	}

	@Override
	protected boolean hasDeletePermission(Account actor, Account account, UploadRequestGroup entry, Object... opt) {
		if (isEnable(actor)) {
			return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_DELETE);
		}
		return false;
	}

	@Override
	protected boolean hasCreatePermission(Account actor, Account account, UploadRequestGroup entry, Object... opt) {
		if (isEnable(actor)) {
			return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_CREATE);
		}
		return false;
	}

	@Override
	protected boolean hasUpdatePermission(Account actor, Account account, UploadRequestGroup entry, Object... opt) {
		if (isEnable(actor)) {
			return defaultPermissionCheck(actor, account, entry, TechnicalAccountPermissionType.UPLOAD_REQUEST_UPDATE);
		}
		return false;
	}

	@Override
	protected String getTargetedAccountRepresentation(Account targetedAccount) {
		return targetedAccount.getAccountReprentation();
	}

	@Override
	protected String getEntryRepresentation(UploadRequestGroup entry) {
		return entry.getUuid();
	}

	private boolean isEnable(Account actor) {
		Functionality func = functionalityService.getUploadRequestFunctionality(actor.getDomain());
		if (func.getActivationPolicy().getStatus()) {
			Functionality groupFunc = functionalityService.getUploadRequestGroupedFunctionality(actor.getDomain());
			return groupFunc.getActivationPolicy().getStatus();
		}
		return false;
	}
}
