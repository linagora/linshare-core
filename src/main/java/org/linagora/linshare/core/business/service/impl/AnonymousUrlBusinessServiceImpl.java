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

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.linagora.linshare.core.business.service.AnonymousUrlBusinessService;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.AnonymousUrlRepository;


public class AnonymousUrlBusinessServiceImpl implements AnonymousUrlBusinessService {

	private final AnonymousUrlRepository anonymousUrlRepository;
	private final String baseSecuredUrl;
	private final PasswordService passwordService;
	private final AccountRepository<Account> accountRepository;
	
	public AnonymousUrlBusinessServiceImpl(
			final AnonymousUrlRepository anonymousUrlRepository,
			final String baseSecuredUrl,
			final PasswordService passwordService,
			final AccountRepository<Account> accountRepository) {
		super();
		this.anonymousUrlRepository = anonymousUrlRepository;
		this.baseSecuredUrl = baseSecuredUrl;
		this.passwordService = passwordService;
		this.accountRepository = accountRepository;
	}

	@Override
	public SystemAccount getAnonymousURLAccount() {
		return accountRepository.getBatchSystemAccount();
	}

	@Override
	public AnonymousUrl find(String uuid) {
		return anonymousUrlRepository.findByUuid(uuid);
	}

	@Override
	public AnonymousUrl create(Boolean passwordProtected, Contact contact) throws BusinessException {
		
		AnonymousUrl anonymousUrl = new AnonymousUrl(baseSecuredUrl, contact);
		if(passwordProtected) {
			String password = passwordService.generatePassword();
			// We store it temporary in this object for mail notification.
			anonymousUrl.setTemporaryPlainTextPassword(password);
			anonymousUrl.setPassword(passwordService.encode(password));
		}
		return anonymousUrlRepository.create(anonymousUrl);
	}


	@Override
	public void update(AnonymousUrl anonymousUrl) throws BusinessException {
		anonymousUrlRepository.update(anonymousUrl);
	}

	@Override
	public boolean isValidPassword(AnonymousUrl anonymousUrl, String password) {
		if (anonymousUrl == null) throw new IllegalArgumentException("anonymousUrl url cannot be null");

		// Check password validity
		if(anonymousUrl.isPasswordProtected()) {
			if (password == null) return false;
			return passwordService.matches(password, anonymousUrl.getPassword());
		}
		return true;
	}

	@Override
	public boolean isExpired(AnonymousUrl anonymousUrl) {
		if (anonymousUrl == null)
			throw new IllegalArgumentException("anonymousUrl url cannot be null");
		return CollectionUtils.isEmpty(anonymousUrl.getAnonymousShareEntries());
	}

	@Override
	public List<String> findAllExpiredEntries() {
		return anonymousUrlRepository.findAllExpiredEntries();
	}

	@Override
	public void delete(AnonymousUrl anonymousUrl) {
		anonymousUrlRepository.delete(anonymousUrl);
	}
}
