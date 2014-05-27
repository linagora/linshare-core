package org.linagora.linshare.core.service.impl;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.MimePolicyBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.MimePolicyService;

import com.google.common.collect.Sets;

public class MimePolicyServiceImpl implements MimePolicyService {

	final private MimePolicyBusinessService mimePolicyBusinessService;

	final private DomainPermissionBusinessService domainPermissionService;

	final private DomainBusinessService domainBusinessService;

	public MimePolicyServiceImpl(
			MimePolicyBusinessService mimePolicyBusinessService,
			DomainPermissionBusinessService domainPermissionService,
			final DomainBusinessService domainBusinessService
			) {
		super();
		this.mimePolicyBusinessService = mimePolicyBusinessService;
		this.domainPermissionService = domainPermissionService;
		this.domainBusinessService = domainBusinessService;
	}

	@Override
	public MimePolicy create(Account actor, MimePolicy mimePolicy)
			throws BusinessException {
		Validate.notNull(actor);
		Validate.notNull(mimePolicy);
		Validate.notEmpty(mimePolicy.getUuid());
		Validate.notNull(mimePolicy.getDomain());
		Validate.notEmpty(mimePolicy.getDomain().getIdentifier());
		Validate.notEmpty(mimePolicy.getName());

		// check actor is admin of mimePolicy.getDomain();
		if (!mimePolicy.getDomain().isManagedBy(actor)) {
			String msg = "The current actor " + actor.getAccountReprentation()
					+ " does not have the right to create a MimePolicy.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}
		return mimePolicyBusinessService.create(mimePolicy);
	}

	@Override
	public MimePolicy delete(Account actor, MimePolicy mimePolicy)
			throws BusinessException {
		Validate.notNull(actor);
		Validate.notNull(mimePolicy);
		Validate.notEmpty(mimePolicy.getUuid());
		if (!isAdminFor(actor, mimePolicy.getUuid())) {
			String msg = "The current actor " + actor.getAccountReprentation()
					+ " does not have the right to delete this MimePolicy.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}
		return mimePolicyBusinessService.create(mimePolicy);
	}

	@Override
	public MimePolicy find(Account actor, String uuid, boolean full)
			throws BusinessException {
		Validate.notNull(actor);
		Validate.notEmpty(uuid);
		if (!isAdminFor(actor, uuid)) {
			String msg = "The current actor " + actor.getAccountReprentation()
					+ " does not have the right to get this MimePolicy.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}
		MimePolicy ret = mimePolicyBusinessService.find(uuid);
		if (full) {
			mimePolicyBusinessService.load(ret);
		}
		return ret;
	}

	@Override
	public Set<MimePolicy> findAll(Account actor, String domainIdentifier,
			boolean onlyCurrentDomain) throws BusinessException {
		Validate.notNull(actor);
		Validate.notNull(domainIdentifier);

		AbstractDomain domain = domainBusinessService.findById(domainIdentifier);
		if (!domainPermissionService.isAdminforThisDomain(actor, domain)) {
			String msg = "The current actor " + actor.getAccountReprentation()
					+ " does not have the right to get all MimePolicies.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}
		if (onlyCurrentDomain) {
			return domain.getMimePolicies();
		} else {
			return getAllUsable(domain);
		}
	}

	private Set<MimePolicy> getAllUsable(AbstractDomain domain) {
		Set<MimePolicy> res = Sets.newHashSet();
		if (domain != null) {
			res.addAll(domain.getMimePolicies());
			res.addAll(getAllUsable(domain.getParentDomain()));
		}
		return res;
	}

	@Override
	public MimePolicy update(Account actor, MimePolicy mimePolicyDto)
			throws BusinessException {
		Validate.notNull(actor);
		Validate.notNull(mimePolicyDto);
		Validate.notEmpty(mimePolicyDto.getUuid());
		Validate.notEmpty(mimePolicyDto.getName());
		if (!isAdminFor(actor, mimePolicyDto.getUuid())) {
			String msg = "The current actor " + actor.getAccountReprentation()
					+ " does not have the right to update this MimePolicy.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}
		return mimePolicyBusinessService.update(mimePolicyDto);
	}

	private boolean isAdminFor(Account actor, String uuid)
			throws BusinessException {
		MimePolicy mimePolicy = mimePolicyBusinessService.find(uuid);
		// we check if the current actor is admin of the domain which belongs
		// the MimePolicy
		return domainPermissionService.isAdminforThisDomain(actor,
				mimePolicy.getDomain());
	}
}
