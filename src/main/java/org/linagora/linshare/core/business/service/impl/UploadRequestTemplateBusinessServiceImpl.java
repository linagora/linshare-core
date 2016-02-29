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

import org.linagora.linshare.core.business.service.UploadRequestTemplateBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequestTemplate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestTemplateRepository;

public class UploadRequestTemplateBusinessServiceImpl implements
		UploadRequestTemplateBusinessService {

	private final UploadRequestTemplateRepository uploadRequestTemplateRepository;

	public UploadRequestTemplateBusinessServiceImpl(
			final UploadRequestTemplateRepository uploadRequestTemplateRepository) {
		super();
		this.uploadRequestTemplateRepository = uploadRequestTemplateRepository;
	}

	@Override
	public UploadRequestTemplate findByUuid(String uuid) {
		return uploadRequestTemplateRepository.findByUuid(uuid);
	}

	@Override
	public UploadRequestTemplate create(Account actor, UploadRequestTemplate template)
			throws BusinessException {
		template.setOwner(actor);
		return uploadRequestTemplateRepository.create(template);
	}

	@Override
	public UploadRequestTemplate update(UploadRequestTemplate template, UploadRequestTemplate object)
			throws BusinessException {
		template.setBusinessDayBeforeNotification(object.getDayBeforeNotification());
		template.setBusinessDepositMode(object.getDepositMode());
		template.setBusinessDescription(object.getDescription());
		template.setBusinessDurationBeforeActivation(object.getDurationBeforeActivation());
		template.setBusinessDurationBeforeExpiry(object.getDurationBeforeExpiry());
		template.setBusinessLocale(object.getLocale());
		template.setBusinessMaxDepositSize(object.getMaxDepositSize());
		template.setBusinessMaxFile(object.getMaxFile());
		template.setBusinessMaxFileSize(object.getMaxFileSize());
		template.setBusinessUnitBeforeActivation(object.getUnitBeforeActivation());
		template.setBusinessUnitBeforeExpiry(object.getUnitBeforeExpiry());
		template.setBusinessName(object.getName());
		template.setBusinessSecured(object.getSecured());
		template.setBusinessProlongationMode(object.getProlongationMode());
		template.setBusinessGroupMode(object.getGroupMode());
		return uploadRequestTemplateRepository.update(template);
	}

	@Override
	public void delete(UploadRequestTemplate template) throws BusinessException {
		uploadRequestTemplateRepository.delete(template);
	}
}
