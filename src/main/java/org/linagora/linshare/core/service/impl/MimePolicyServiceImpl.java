/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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

import java.util.Set;

import org.apache.commons.lang.Validate;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class MimePolicyServiceImpl implements MimePolicyService {

	private static final Logger logger = LoggerFactory.getLogger(MimePolicyServiceImpl.class);

	final private MimePolicyBusinessService mimePolicyBusinessService;

	final private MimeTypeBusinessService mimeTypeBusinessService;

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
		this.mimeTypeBusinessService = mimeTypeBusinessService;
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
		if (!isAdminFor(actor, uuid)) {
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
		if (!domainPermissionService.isAdminforThisDomain(actor, domain)) {
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

	private boolean isAdminFor(Account actor, String uuid)
			throws BusinessException {
		MimePolicy mimePolicy = mimePolicyBusinessService.find(uuid);
		return isAdminFor(actor, mimePolicy);
	}

	private boolean isAdminFor(Account actor, MimePolicy mimePolicy) {
		// we check if the current actor is admin of the domain which belongs
		// the MimePolicy
		return domainPermissionService.isAdminforThisDomain(actor,
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
}
