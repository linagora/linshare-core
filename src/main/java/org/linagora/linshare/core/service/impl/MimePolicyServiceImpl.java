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

import static org.linagora.linshare.core.exception.BusinessErrorCode.DOMAIN_FORBIDDEN;

import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.MimePolicyBusinessService;
import org.linagora.linshare.core.business.service.MimeTypeBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.MimePolicyService;

import com.google.common.collect.Sets;

public class MimePolicyServiceImpl implements MimePolicyService {

	final private MimePolicyBusinessService mimePolicyBusinessService;

	final private DomainPermissionBusinessService domainPermissionService;

	final private DomainBusinessService domainBusinessService;

	public MimePolicyServiceImpl(
			final MimePolicyBusinessService mimePolicyBusinessService,
			final MimeTypeBusinessService mimeTypeBusinessService,
			final DomainPermissionBusinessService domainPermissionService,
			final DomainBusinessService domainBusinessService) {
		super();
		this.mimePolicyBusinessService = mimePolicyBusinessService;
		this.domainPermissionService = domainPermissionService;
		this.domainBusinessService = domainBusinessService;
	}

	@Override
	public MimePolicy create(Account actor, String domainId,
			MimePolicy mimePolicy) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(mimePolicy, "MimePolicy must be set.");
		Validate.notEmpty(domainId, "Domain identifier must be set.");
		Validate.notEmpty(mimePolicy.getName(),
				"Name of MimePolicy must be set.");

		AbstractDomain domain = domainBusinessService.findById(domainId);
		// check actor is admin of mimePolicy.getDomain();
		if (!domain.isManagedBy(actor)) {
			String msg = "The current actor " + actor.getAccountRepresentation()
					+ " does not have the right to create a MimePolicy.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}

		domain.getMimePolicies().add(mimePolicy);
		mimePolicy.setDomain(domain);
		MimePolicy ret = mimePolicyBusinessService.create(mimePolicy);
		domainBusinessService.update(domain);
		return ret;
	}

	@Override
	public void delete(Account actor, MimePolicy mimePolicy)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(mimePolicy, "MimePolicy must be set.");
		Validate.notEmpty(mimePolicy.getUuid(), "MimePolicy uuid must be set");
		if (!isAdminFor(actor, mimePolicy.getUuid())) {
			String msg = "The current actor " + actor.getAccountRepresentation()
					+ " does not have the right to delete this MimePolicy.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}
		mimePolicyBusinessService.delete(mimePolicy);
	}

	@Override
	public MimePolicy find(Account actor, String uuid, boolean full)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notEmpty(uuid, "MimePolicy uuid must be set.");
		if (!isAdminFor(actor, uuid) && !isAllowedToSee(actor, uuid)) {
			String msg = "The current actor " + actor.getAccountRepresentation()
					+ " does not have the right to get this MimePolicy.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}
		MimePolicy ret = mimePolicyBusinessService.find(uuid);
		if (full) {
			ret = mimePolicyBusinessService.load(ret);
		}
		return ret;
	}

	@Override
	public Set<MimeType> findAllMyMimeTypes(Account actor)
			throws BusinessException {
		MimePolicy mimePolicy = actor.getDomain().getMimePolicy();
		mimePolicyBusinessService.load(mimePolicy);
		return mimePolicy.getMimeTypes();
	}

	@Override
	public Set<MimePolicy> findAll(Account actor, String domainIdentifier,
			boolean onlyCurrentDomain) throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(domainIdentifier, "Domain identifier must be set.");

		AbstractDomain domain = domainBusinessService
				.findById(domainIdentifier);
		if (!domainPermissionService.isAdminForThisDomain(actor, domain)) {
			String msg = "The current actor " + actor.getAccountRepresentation()
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
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(mimePolicyDto, "MimePolicy must be set.");
		Validate.notEmpty(mimePolicyDto.getUuid(),
				"MimePolicy uuid must be set.");
		Validate.notEmpty(mimePolicyDto.getName(),
				"MimePolicy name must be set.");
		if (!isAdminFor(actor, mimePolicyDto.getUuid())) {
			String msg = "The current actor " + actor.getAccountRepresentation()
					+ " does not have the right to update this MimePolicy.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}
		return mimePolicyBusinessService.update(mimePolicyDto);
	}

	private boolean isAllowedToSee(Account actor, String uuid)
			throws BusinessException {
		MimePolicy mimePolicy = mimePolicyBusinessService.find(uuid);
		return isAllowedToSee(actor, mimePolicy);
	}

	private boolean isAllowedToSee(Account actor, MimePolicy mimePolicy) {
		return actor.getDomain().isAncestry(mimePolicy.getDomain().getUuid());
	}

	private boolean isAdminFor(Account actor, String uuid)
			throws BusinessException {
		MimePolicy mimePolicy = mimePolicyBusinessService.find(uuid);
		return isAdminFor(actor, mimePolicy);
	}

	private boolean isAdminFor(Account actor, MimePolicy mimePolicy) {
		// we check if the current actor is admin of the domain which belongs
		// the MimePolicy
		return domainPermissionService.isAdminForThisDomain(actor,
				mimePolicy.getDomain());
	}

	@Override
	public MimePolicy enableAllMimeTypes(Account actor, String uuid)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notEmpty(uuid, "MimePolicy uuid must be set.");
		MimePolicy mimePolicy = mimePolicyBusinessService.find(uuid);
		if (!isAdminFor(actor, mimePolicy)) {
			String msg = "The current actor " + actor.getAccountRepresentation()
					+ " does not have the right to update this MimePolicy.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}
		mimePolicyBusinessService.enableAll(mimePolicy);
		return mimePolicy;
	}

	@Override
	public MimePolicy disableAllMimeTypes(Account actor, String uuid)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notEmpty(uuid, "MimePolicy uuid must be set.");
		MimePolicy mimePolicy = mimePolicyBusinessService.find(uuid);
		if (!isAdminFor(actor, mimePolicy)) {
			String msg = "The current actor " + actor.getAccountRepresentation()
					+ " does not have the right to update this MimePolicy.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}
		mimePolicyBusinessService.disableAll(mimePolicy);
		return mimePolicy;
	}

	@Override
	public void assign(Account actor, String domainUuid, String mimePolicyUuid) {
		Validate.notEmpty(domainUuid, "Domain uuid must be set.");
		Validate.notEmpty(mimePolicyUuid, "MimePolicy uuid must be set.");

		AbstractDomain domain = domainBusinessService.find(domainUuid);
		if (!domain.isManagedBy(actor)) {
			throw new BusinessException(DOMAIN_FORBIDDEN, "You are not allowed to manage domain " + domainUuid);
		}
		MimePolicy mimePolicy = mimePolicyBusinessService.find(mimePolicyUuid);
		if (!domain.isAncestry(mimePolicy.getDomain().getUuid())){
			throw new BusinessException("Mime policy " + mimePolicyUuid + " cannot be added to domain " + domainUuid);
		}

		domain.setMimePolicy(mimePolicy);
		domainBusinessService.update(domain);
	}
}
