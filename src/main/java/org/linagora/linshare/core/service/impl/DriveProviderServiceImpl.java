/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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

import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.DriveProvider;
import org.linagora.linshare.core.domain.entities.LdapDriveProvider;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DriveProviderRepository;
import org.linagora.linshare.core.repository.LdapDriveProviderRepository;
import org.linagora.linshare.core.service.DriveProviderService;

public class DriveProviderServiceImpl extends GenericAdminServiceImpl implements DriveProviderService {

	private DriveProviderRepository driveProviderRepository;

	private LdapDriveProviderRepository ldapDriveProviderRepository;

	protected DriveProviderServiceImpl(
			LdapDriveProviderRepository ldapDriveProviderRepository,
			DriveProviderRepository driveProviderRepository,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(sanitizerInputHtmlBusinessService);
		this.ldapDriveProviderRepository = ldapDriveProviderRepository;
		this.driveProviderRepository = driveProviderRepository;
	}

	@Override
	public LdapDriveProvider find(String uuid) throws BusinessException {
		LdapDriveProvider provider = ldapDriveProviderRepository.findByUuid(uuid);
		if (provider == null) {
			throw new BusinessException(
					BusinessErrorCode.DRIVE_LDAP_FILTER_NOT_FOUND,
					"Drive provider identifier no found.");
		}
		return provider;
	}

	@Override
	public LdapDriveProvider create(LdapDriveProvider ldapDriveProvider) throws BusinessException {
		return ldapDriveProviderRepository.create(ldapDriveProvider);
	}

	@Override
	public boolean exists(String uuid) {
		LdapDriveProvider provider = ldapDriveProviderRepository.findByUuid(uuid);
		return provider != null;
	}

	@Override
	public LdapDriveProvider update(LdapDriveProvider ldapDriveProvider) throws BusinessException {
		return ldapDriveProviderRepository.update(ldapDriveProvider);
	}

	@Override
	public void delete(DriveProvider driveProvider) throws BusinessException {
		driveProviderRepository.delete(driveProvider);
	}

}
