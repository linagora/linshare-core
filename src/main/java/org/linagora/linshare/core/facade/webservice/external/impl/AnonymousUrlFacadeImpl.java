/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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
package org.linagora.linshare.core.facade.webservice.external.impl;

import java.io.InputStream;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.facade.webservice.external.AnonymousUrlFacade;
import org.linagora.linshare.core.facade.webservice.external.dto.AnonymousUrlDto;
import org.linagora.linshare.core.facade.webservice.external.dto.ShareEntryDto;
import org.linagora.linshare.core.service.AnonymousUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnonymousUrlFacadeImpl implements AnonymousUrlFacade{

	private static final Logger logger = LoggerFactory.getLogger(AnonymousUrlFacade.class);

	private final AnonymousUrlService anonymousUrlService;

	public AnonymousUrlFacadeImpl(
			AnonymousUrlService anonymousUrlService) {
		super();
		this.anonymousUrlService = anonymousUrlService;
	}

	@Override
	public AnonymousUrlDto find(String uuid, String password) {
		logger.debug("getting anonymousurl with uuid : " + uuid);
		SystemAccount actor = anonymousUrlService.getAnonymousURLAccount();
		Account owner = null;
		AnonymousUrl url = anonymousUrlService.find(actor, actor, uuid, password);
		/**
		 * It is impossible to get directly the owner of an anonymous url
		 * This workaround is to get the owner
		 * TODO Add property in Anonymous url object for the owner
		 */
		for (AnonymousShareEntry ase : url.getAnonymousShareEntries()) {
			owner = ase.getEntryOwner();
			break;
		}
		return new AnonymousUrlDto(owner, url);
	}

	@Override
	public InputStream download(String anonymousUrlUuid,
			String anonymousShareEntryUuid, String password) {
		logger.debug("Downloading a file with anonymousShareEntryUuid :"
				+ anonymousShareEntryUuid + "from anonymousUrl : "
				+ anonymousUrlUuid);
		SystemAccount actor = anonymousUrlService.getAnonymousURLAccount();
		return anonymousUrlService.downloadDocument(actor,
				actor, anonymousUrlUuid, anonymousShareEntryUuid, password);
	}

	@Override
	public InputStream getThumbnail(String anonymousUrlUuid,
			String anonymousShareEntryUuid, String password) {
		Validate.notEmpty(anonymousUrlUuid,
				"Missing required anonymousUrl uuid");
		Validate.notEmpty(anonymousShareEntryUuid,
				"Missing required anonymousShareEntry uuid");
		SystemAccount actor = anonymousUrlService.getAnonymousURLAccount();
		return anonymousUrlService.downloadThumbnail(actor,
				actor, anonymousUrlUuid, anonymousShareEntryUuid, password);
	}

	@Override
	public ShareEntryDto getShareEntry(String anonymousUrlUuid,
			String anonymousShareEntryUuid, String password) {
		Validate.notEmpty(anonymousUrlUuid,
				"Missing required anonymousUrl uuid");
		Validate.notEmpty(anonymousShareEntryUuid,
				"Missing required anonymousShareEntry uuid");
		SystemAccount actor = anonymousUrlService.getAnonymousURLAccount();
		AnonymousShareEntry ase = anonymousUrlService.getAnonymousShareEntry(
				actor, actor, anonymousUrlUuid, anonymousShareEntryUuid,
				password);
		return new ShareEntryDto(ase.getUuid(), ase.getDocumentEntry());
	}
}
