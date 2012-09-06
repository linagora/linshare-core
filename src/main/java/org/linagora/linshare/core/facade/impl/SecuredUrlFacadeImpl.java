/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.facade.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.MailContainer;
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
	public List<DocumentVo> getDocuments(String uuid, String password) throws BusinessException {
		List<DocumentVo> res = new ArrayList<DocumentVo>();
		List<AnonymousShareEntry> anonymousShareEntries = anonymousUrlService.getAnonymousShareEntry(uuid, password);
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
	public InputStream retrieveFileStream(String anonymousUrlUuid, String anonymousShareEntryUuid, String password, MailContainer mailContainer) throws BusinessException {
		return anonymousUrlService.retrieveFileStream(anonymousUrlUuid, anonymousShareEntryUuid, password, mailContainer);
	}


	@Override
	public FileStreamResponse retrieveArchiveZipStream(String anonymousUrlUuid, String password, MailContainer mailContainer) throws BusinessException {
		return anonymousUrlService.retrieveArchiveZipStream(anonymousUrlUuid, password, mailContainer);
	}
	
}
