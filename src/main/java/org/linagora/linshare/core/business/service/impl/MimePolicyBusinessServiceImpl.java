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
package org.linagora.linshare.core.business.service.impl;

import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class MimePolicyBusinessServiceImpl implements MimePolicyBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(MimePolicyBusinessServiceImpl.class);

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
		mimePolicy.setVersion(1);
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
		} else if (mimePolicy.getVersion() != 1) {
			// The main purpose of this code is to upgrade database with all new mime types
			// available in Apache Tika. Each version of Tika adds and/or removes some mime types.
			Set<MimeType> mimeTypes = mimeTypeMagicNumberDao.getAllMimeType();
			Set<String> ref = Sets.newHashSet();
			for (MimeType mimeType : mimeTypes) {
				ref.add(mimeType.getMimeType());
				MimeType type = mimeTypeRepository.findByMimeType(mimePolicy, mimeType.getMimeType());
				if (type == null) {
					mimeType.setMimePolicy(mimePolicy);
					mimeTypeRepository.create(mimeType);
					mimePolicy.getMimeTypes().add(mimeType);
				}
			}
			List<MimeType> findAll = mimeTypeRepository.findAll(mimePolicy);
			for (MimeType mimeType : findAll) {
				if (!ref.contains(mimeType.getMimeType())) {
					mimeTypeRepository.delete(mimeType);
					mimePolicy.getMimeTypes().remove(mimeType);
				}
			}
			mimePolicy.setVersion(1);
			mimePolicy = mimePolicyRepository.update(mimePolicy);
			logger.debug("mime_policies size : " + mimePolicy.getMimeTypes().size());
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
