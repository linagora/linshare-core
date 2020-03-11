/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
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

import com.google.common.io.ByteSource;

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
		SystemAccount authUser = anonymousUrlService.getAnonymousURLAccount();
		Account actor = null;
		AnonymousUrl url = anonymousUrlService.find(authUser, authUser, uuid, password);
		/**
		 * It is impossible to get directly the actor of an anonymous url
		 * This workaround is to get the actor
		 * TODO Add property in Anonymous url object for the actor
		 */
		for (AnonymousShareEntry ase : url.getAnonymousShareEntries()) {
			actor = ase.getEntryOwner();
			break;
		}
		return new AnonymousUrlDto(actor, url);
	}

	@Override
	public ByteSource download(String anonymousUrlUuid,
			String anonymousShareEntryUuid, String password) {
		logger.debug("Downloading a file with anonymousShareEntryUuid :"
				+ anonymousShareEntryUuid + "from anonymousUrl : "
				+ anonymousUrlUuid);
		SystemAccount authUser = anonymousUrlService.getAnonymousURLAccount();
		return anonymousUrlService.downloadDocument(authUser,
				authUser, anonymousUrlUuid, anonymousShareEntryUuid, password);
	}

	@Override
	public ByteSource getThumbnail(String anonymousUrlUuid,
			String anonymousShareEntryUuid, String password, ThumbnailType kind) {
		Validate.notEmpty(anonymousUrlUuid,
				"Missing required anonymousUrl uuid");
		Validate.notEmpty(anonymousShareEntryUuid,
				"Missing required anonymousShareEntry uuid");
		SystemAccount authUser = anonymousUrlService.getAnonymousURLAccount();
		if (kind == null) {
			kind = ThumbnailType.MEDIUM;
		}
		return anonymousUrlService.downloadThumbnail(authUser,
				authUser, anonymousUrlUuid, anonymousShareEntryUuid, password, kind);
	}

	@Override
	public ShareEntryDto getShareEntry(String anonymousUrlUuid,
			String anonymousShareEntryUuid, String password) {
		Validate.notEmpty(anonymousUrlUuid,
				"Missing required anonymousUrl uuid");
		Validate.notEmpty(anonymousShareEntryUuid,
				"Missing required anonymousShareEntry uuid");
		SystemAccount authUser = anonymousUrlService.getAnonymousURLAccount();
		AnonymousShareEntry ase = anonymousUrlService.getAnonymousShareEntry(
				authUser, authUser, anonymousUrlUuid, anonymousShareEntryUuid,
				password);
		return new ShareEntryDto(ase.getUuid(), ase.getDocumentEntry());
	}
}
