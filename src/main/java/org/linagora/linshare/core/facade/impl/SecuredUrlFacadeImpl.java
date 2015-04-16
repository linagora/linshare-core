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
package org.linagora.linshare.core.facade.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.facade.SecuredUrlFacade;
import org.linagora.linshare.core.service.AnonymousUrlService;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecuredUrlFacadeImpl implements SecuredUrlFacade {

	private static final Logger logger = LoggerFactory.getLogger(SecuredUrlFacadeImpl.class);
	
	private final AnonymousUrlService anonymousUrlService;
	
	public SecuredUrlFacadeImpl(final AnonymousUrlService anonymousUrlService) {
		this.anonymousUrlService = anonymousUrlService;
	}


	@Override
	public List<DocumentVo> getDocuments(String anonymousUrlUuid, String password) throws BusinessException {
		List<DocumentVo> res = new ArrayList<DocumentVo>();
		List<AnonymousShareEntry> anonymousShareEntries = anonymousUrlService.getAnonymousShareEntry(anonymousUrlUuid, password);
		logger.debug("anonymousShareEntries size : " + anonymousShareEntries.size());
		for (AnonymousShareEntry anonymousShareEntry : anonymousShareEntries) {
			res.add(new DocumentVo(anonymousShareEntry));
		}
		logger.debug("res size : " + res.size());
		return res;
	}

	
	@Override
	public boolean isPasswordProtected(String uuid) throws LinShareNotSuchElementException {
		return anonymousUrlService.isProtectedByPassword(uuid);
	}

	
	@Override
	public boolean isValid(String uuid, String password) {
		logger.debug("current uuid : " + uuid);
		logger.debug("current password: " + password);
		try {
			return anonymousUrlService.isValid(uuid, password);
		} catch (LinShareNotSuchElementException e) {
			logger.error("anonymous url is not valid : " + e.getMessage());
		}
		return false;
	}

	
	@Override
	public boolean exists(String uuid, String urlPath) {
		return anonymousUrlService.exists(uuid,urlPath);
	}


	@Override
	public InputStream retrieveFileStream(String anonymousUrlUuid, String anonymousShareEntryUuid, String password) throws BusinessException {
		return anonymousUrlService.retrieveFileStream(anonymousUrlUuid, anonymousShareEntryUuid, password);
	}


	@Override
	public FileStreamResponse retrieveArchiveZipStream(String anonymousUrlUuid, String password) throws BusinessException {
		return anonymousUrlService.retrieveArchiveZipStream(anonymousUrlUuid, password);
	}
	
}
