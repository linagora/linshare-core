/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.core.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.AnonymousUrlBusinessService;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.AnonymousUrlService;
import org.linagora.linshare.core.utils.ArchiveZipStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

public class AnonymousUrlServiceImpl implements AnonymousUrlService {

	private static final Logger logger = LoggerFactory.getLogger(AnonymousUrlService.class);
	
	private final AnonymousUrlBusinessService anonymousUrlBusinessService;
	
	private final AnonymousShareEntryService anonymousShareEntryService;
	
	
	public AnonymousUrlServiceImpl(AnonymousUrlBusinessService anonymousUrlBusinessService, AnonymousShareEntryService anonymousShareEntryService) {
		super();
		this.anonymousUrlBusinessService = anonymousUrlBusinessService;
		this.anonymousShareEntryService = anonymousShareEntryService;
	}

	@Override
	public boolean isProtectedByPassword(Account actor, AnonymousUrl anonymousUrl) throws BusinessException {
		return !StringUtils.isEmpty(anonymousUrl.getPassword());
	}

	private boolean isValid(AnonymousUrl anonymousUrl, String password) {
		if(!anonymousUrlBusinessService.isExpired(anonymousUrl)) {
			if(password != null) {
				return anonymousUrlBusinessService.isValidPassword(anonymousUrl, password);
			} else {
				return !isProtectedByPassword(null, anonymousUrl);
			}
		}
		return false;
	}

	@Override
	public ByteSource downloadDocument(Account actor, Account owner, String anonymousUrlUuid, String anonymousShareEntryUuid, String password) throws BusinessException {
		AnonymousUrl anonymousUrl = find(actor, owner, anonymousUrlUuid, password);
		if(isValid(anonymousUrl, password)) {
			// anonymous share are not made with a thousand contacts, performance will not be poor most of the time.
			for (AnonymousShareEntry anonymousShareEntry : anonymousUrl.getAnonymousShareEntries()) {
				if(anonymousShareEntry.getUuid().equals(anonymousShareEntryUuid)) {
					return anonymousShareEntryService.getAnonymousShareEntryByteSource(actor, anonymousShareEntry.getUuid());
				}
			}
			String msg = "anonymousShareEntryUuid not found : " + anonymousShareEntryUuid;
			logger.debug(msg);
			throw new BusinessException(BusinessErrorCode.ANONYMOUS_URL_NOT_FOUND, msg);
		}
		String msg = "anonymousUrlUuid not valid : " + anonymousUrlUuid;
		logger.debug(msg);
		throw new BusinessException(msg);
	}


	@Override
	public InputStream retrieveArchiveZipStream(Account actor, Account owner, String anonymousUrlUuid, String password) throws BusinessException {
		AnonymousUrl anonymousUrl = find(actor, owner, anonymousUrlUuid, password);
		if(isValid(anonymousUrl, password)) {
			
			Map<String, ByteSource> map = new HashMap<String, ByteSource>();
			for (AnonymousShareEntry anonymousShareEntry : anonymousUrl.getAnonymousShareEntries()) {
				map.put(anonymousShareEntry.getName(), anonymousShareEntryService.getAnonymousShareEntryByteSource(actor, anonymousShareEntry.getUuid()));
				// TODO : NEW FUNCTIONNALITY : just send one mail for all files, not one by by file.
			}
			
			//prepare an archive zip
			return new ArchiveZipStream(map);
		}
		String msg = "anonymousUrlUuid not valid : " + anonymousUrlUuid;
		logger.debug(msg);
		throw new BusinessException(msg);
	}

	@Override
	public List<String> findAllExpiredEntries(Account actor, Account owner) {
		if (!actor.hasAllRights()) {
			throw new BusinessException(BusinessErrorCode.ANONYMOUS_URL_FORBIDDEN, "You do not have the right to use this method.");
		}
		return anonymousUrlBusinessService.findAllExpiredEntries();
	}

	@Override
	public AnonymousUrl find(Account actor, Account owner, String uuid) {
		Validate.notEmpty(uuid);
		if (actor.hasAllRights() || actor.hasAnonymousShareSystemAccountRole()) {
			return anonymousUrlBusinessService.find(uuid);
		} else {
			throw new BusinessException(BusinessErrorCode.ANONYMOUS_URL_FORBIDDEN, "You do not have the right to use this method.");
		}
	}

	@Override
	public AnonymousUrl find(Account actor, Account owner, String uuid, String password) {
		Validate.notEmpty(uuid);
		if (actor.hasAllRights() || actor.hasAnonymousShareSystemAccountRole()) {
			AnonymousUrl anonymousUrl = anonymousUrlBusinessService.find(uuid);
			if (anonymousUrl == null) {
				throw new BusinessException(BusinessErrorCode.ANONYMOUS_URL_NOT_FOUND,
						"anonymousUrl not found : " + uuid);
			}
			accessBusinessCheck(anonymousUrl, password);
			return anonymousUrl;
		} else {
			throw new BusinessException(BusinessErrorCode.ANONYMOUS_URL_FORBIDDEN, "You do not have the right to use this method.");
		}
	}

	@Override
	public AnonymousUrl delete(Account actor, Account owner, String uuid) {
		if (actor.hasAllRights()) {
			AnonymousUrl anonymousUrl = anonymousUrlBusinessService.find(uuid);
			anonymousUrlBusinessService.delete(anonymousUrl);
			return anonymousUrl;
		} else {
			throw new BusinessException(BusinessErrorCode.ANONYMOUS_URL_FORBIDDEN, "You do not have the right to use this method.");
		}

	}

	private void accessBusinessCheck(AnonymousUrl url, String password)
			throws BusinessException {
		logger.debug("Checking access ");
		boolean isValid = isValid(url, password);
		if (!isValid) {
			throw new BusinessException(BusinessErrorCode.ANONYMOUS_URL_FORBIDDEN,
					"You do not have the right to get this anonymous url : "
							+ url.getUuid());
		}
	}

	@Override
	public ByteSource downloadThumbnail(Account actor,
			Account owner, String anonymousUrlUuid, String anonymousShareEntryUuid, String password, ThumbnailType kind)
			throws BusinessException {
		AnonymousUrl anonymousUrl = find(actor, owner, anonymousUrlUuid, password);
		for(AnonymousShareEntry ase : anonymousUrl.getAnonymousShareEntries()) {
			if(ase.getUuid().equals(anonymousShareEntryUuid)) {
				return anonymousShareEntryService.getAnonymousShareEntryThumbnailByteSource(
						actor, anonymousShareEntryUuid, kind);
			}
		}
		String msg = "AnonymousShareEntry with uuid : "
				+ anonymousShareEntryUuid + "not found within AnonymousUrl : "
				+ anonymousUrlUuid;
		logger.debug(msg);
		throw new BusinessException(BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_NOT_FOUND, msg);
	}

	@Override
	public SystemAccount getAnonymousURLAccount() {
		return anonymousUrlBusinessService.getAnonymousURLAccount();
	}

	@Override
	public AnonymousShareEntry getAnonymousShareEntry(Account actor,
			Account owner, String anonymousUrlUuid,
			String anonymousShareEntryUuid, String password) {
		AnonymousUrl anonymousUrl = find(actor, owner, anonymousUrlUuid, password);
		AnonymousShareEntry ase = anonymousShareEntryService.find(actor, actor, anonymousShareEntryUuid);
		if(ase.getAnonymousUrl().getUuid().equals(anonymousUrl.getUuid())) {
			return ase;
		}
		String msg = "There is no anonymousShareEntry with uuid : "
				+ anonymousShareEntryUuid + "matching the AnonymousUrl uuid: "
				+ anonymousUrlUuid;
		logger.debug(msg);
		throw new BusinessException(BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_FORBIDDEN, msg);
	}
}
