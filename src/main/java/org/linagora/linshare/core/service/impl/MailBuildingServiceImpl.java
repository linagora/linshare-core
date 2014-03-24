/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailConfigRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailBuildingServiceImpl implements MailBuildingService {

	private final static Logger logger = LoggerFactory
			.getLogger(MailBuildingServiceImpl.class);

	private final boolean displayLogo;

	private final boolean insertLicenceTerm;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final MailConfigRepository mailConfigRepository;

	class ContactRepresentation {
		private String mail;
		private String firstName;
		private String lastName;

		public ContactRepresentation(String mail, String firstName,
				String lastName) {
			super();
			this.mail = StringUtils.trimToNull(mail);
			this.firstName = StringUtils.trimToNull(firstName);
			this.lastName = StringUtils.trimToNull(lastName);
		}

		public ContactRepresentation(String mail) {
			this.mail = StringUtils.trimToNull(mail);
			this.firstName = null;
			this.lastName = null;
		}

		public ContactRepresentation(User user) {
			this.mail = StringUtils.trimToNull(user.getMail());
			this.firstName = StringUtils.trimToNull(user.getFirstName());
			this.lastName = StringUtils.trimToNull(user.getLastName());
		}

		public ContactRepresentation(Account account) {
			this.mail = StringUtils.trimToNull(account.getAccountReprentation());
			if (account instanceof User) {
				User user = (User) account;
				this.firstName = StringUtils.trimToNull(user.getFirstName());
				this.lastName = StringUtils.trimToNull(user.getLastName());
				this.mail = StringUtils.trimToNull(user.getMail());
			}
		}

		public String getContactRepresntation() {
			if (this.firstName == null || this.lastName == null)
				return this.mail;
			return firstName + ' ' + lastName + " (" + mail + ')';
		}
	}

	public MailBuildingServiceImpl(boolean displayLogo,
			final AbstractDomainService abstractDomainService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final MailConfigRepository mailConfigRepository,
			boolean insertLicenceTerm) throws BusinessException {
		this.displayLogo = displayLogo;
		this.abstractDomainService = abstractDomainService;
		this.insertLicenceTerm = insertLicenceTerm;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mailConfigRepository = mailConfigRepository;
	}

}
