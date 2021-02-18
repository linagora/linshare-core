/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.business.service.impl;

import java.util.Set;

import org.linagora.linshare.core.business.service.MimePolicyBusinessService;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.MimePolicyRepository;
import org.linagora.linshare.core.repository.MimeTypeRepository;

import com.google.common.collect.Sets;

public class MimePolicyBusinessServiceImpl implements MimePolicyBusinessService {

	private final MimePolicyRepository mimePolicyRepository;

	private final MimeTypeRepository mimeTypeRepository;

	private final MimeTypeMagicNumberDao mimeTypeMagicNumberDao;

	private final AbstractDomainRepository domainRepository;

	public MimePolicyBusinessServiceImpl(
			final MimePolicyRepository mimePolicyRepository,
			final MimeTypeRepository mimeTypeRepository,
			final MimeTypeMagicNumberDao mimeTypeMagicNumberDao,
			final AbstractDomainRepository domainRepository) {
		this.mimePolicyRepository = mimePolicyRepository;
		this.mimeTypeRepository = mimeTypeRepository;
		this.mimeTypeMagicNumberDao = mimeTypeMagicNumberDao;
		this.domainRepository = domainRepository;
	}

	@Override
	public MimePolicy create(MimePolicy mimePolicy) throws BusinessException {
		mimePolicyRepository.create(mimePolicy);
		for (MimeType mimeType : mimeTypeMagicNumberDao.getAllMimeType()) {
			mimeType.setMimePolicy(mimePolicy);
			mimeTypeRepository.create(mimeType);
		}
		return mimePolicy;
	}

	@Override
	public void delete(MimePolicy mimePolicy) throws BusinessException {
		AbstractDomain domain = mimePolicy.getDomain();
		Set<MimePolicy> mimePolicies = domain.getMimePolicies();
		mimePolicies.remove(mimePolicy);
		domainRepository.update(domain);
		mimePolicyRepository.delete(mimePolicy);
	}

	@Override
	public MimePolicy find(String uuid) throws BusinessException {
		MimePolicy mimePolicy = mimePolicyRepository.findByUuid(uuid);
		if (mimePolicy == null) {
			throw new BusinessException(BusinessErrorCode.MIME_NOT_FOUND,
					"Can not find mimePolicy with uuid : " + uuid + ".");
		}
		return mimePolicy;
	}

	@Override
	public Set<MimePolicy> findAll() throws BusinessException {
		Set<MimePolicy> res = Sets.newHashSet();
		res.addAll(mimePolicyRepository.findAll());
		return res;
	}

	@Override
	public MimePolicy load(MimePolicy mimePolicy) throws BusinessException {
		if (mimePolicy.getMimeTypes() != null
				&& mimePolicy.getMimeTypes().size() == 0) {
			for (MimeType mimeType : mimeTypeMagicNumberDao.getAllMimeType()) {
				mimeType.setMimePolicy(mimePolicy);
				mimeTypeRepository.create(mimeType);
			}
		}
		return mimePolicy;
	}

	@Override
	public MimePolicy update(MimePolicy mimePolicy) throws BusinessException {
		MimePolicy entity = mimePolicyRepository.findByUuid(mimePolicy
				.getUuid());
		entity.setDisplayable(mimePolicy.getDisplayable());
		entity.setMode(mimePolicy.getMode());
		entity.setName(mimePolicy.getName());
		return mimePolicyRepository.update(entity);
	}

	@Override
	public MimePolicy enableAll(MimePolicy mimePolicy) throws BusinessException {
		MimePolicy entity = mimePolicyRepository.findByUuid(mimePolicy
				.getUuid());
		return mimePolicyRepository.enableAll(entity);
	}

	@Override
	public MimePolicy disableAll(MimePolicy mimePolicy)
			throws BusinessException {
		MimePolicy entity = mimePolicyRepository.findByUuid(mimePolicy
				.getUuid());
		return mimePolicyRepository.disableAll(entity);
	}
}
