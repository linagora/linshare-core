/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.business.service.AnonymousUrlBusinessService;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.AnonymousUrlService;
import org.linagora.linshare.core.utils.ArchiveZipStream;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public boolean exists(String uuid, String urlPath) {
		try {
			AnonymousUrl anonymousUrl = anonymousUrlBusinessService.getAnonymousUrl(uuid);
			if(anonymousUrl.getUrlPath().endsWith(urlPath)) {
				return true;
			}
			logger.error("the source path is different than the anonymous url path : " + urlPath + " : " + anonymousUrl.getUrlPath());
		} catch (LinShareNotSuchElementException e) {
			logger.warn("the anonymousUrl '" + uuid + "' does not exist.");
			logger.debug(e.toString());
		}
		return false;
	}


	@Override
	public boolean isProtectedByPassword(String uuid) throws LinShareNotSuchElementException {
		AnonymousUrl anonymousUrl = anonymousUrlBusinessService.getAnonymousUrl(uuid);
		return !StringUtils.isEmpty(anonymousUrl.getPassword());
	}


	@Override
	public boolean isValid(String uuid, String password) throws LinShareNotSuchElementException {
		AnonymousUrl anonymousUrl = anonymousUrlBusinessService.getAnonymousUrl(uuid);
		return isValid(anonymousUrl, password);
	}


	private boolean isValid(AnonymousUrl anonymousUrl, String password) {
		if(!anonymousUrlBusinessService.isExpired(anonymousUrl)) {
			if(password != null) {
				return anonymousUrlBusinessService.isValidPassword(anonymousUrl, password);
			} else {
				return true;
			}
		}
		return false;
	}


	@Override
	public List<AnonymousShareEntry> getAnonymousShareEntry(String anonymousUrlUuid, String password)  throws LinShareNotSuchElementException{
		List<AnonymousShareEntry> res = new ArrayList<AnonymousShareEntry>();
		AnonymousUrl anonymousUrl = anonymousUrlBusinessService.getAnonymousUrl(anonymousUrlUuid);
		if(isValid(anonymousUrl, password)) {
			res.addAll(anonymousUrl.getAnonymousShareEntries());				
		}
		return res;
	}


	@Override
	public InputStream retrieveFileStream(String anonymousUrlUuid, String anonymousShareEntryUuid, String password) throws BusinessException {
		AnonymousUrl anonymousUrl = anonymousUrlBusinessService.getAnonymousUrl(anonymousUrlUuid);
		if(isValid(anonymousUrl, password)) {
			// anonymous share are not made with a thousand contacts, performance will not be poor most of the time.
			for (AnonymousShareEntry anonymousShareEntry : anonymousUrl.getAnonymousShareEntries()) {
				if(anonymousShareEntry.getUuid().equals(anonymousShareEntryUuid)) {
					return anonymousShareEntryService.getAnonymousShareEntryStream(anonymousShareEntry.getUuid());
				}
			}
			String msg = "anonymousShareEntryUuid not found : " + anonymousShareEntryUuid;
			logger.debug(msg);
			throw new LinShareNotSuchElementException(msg);
		}
		String msg = "anonymousUrlUuid not valid : " + anonymousUrlUuid;
		logger.debug(msg);
		throw new LinShareNotSuchElementException(msg);
	}


	@Override
	public FileStreamResponse retrieveArchiveZipStream(String anonymousUrlUuid, String password) throws BusinessException {
		AnonymousUrl anonymousUrl = anonymousUrlBusinessService.getAnonymousUrl(anonymousUrlUuid);
		if(isValid(anonymousUrl, password)) {
			
			Map<String,InputStream> map = new HashMap<String, InputStream>();
			for (AnonymousShareEntry anonymousShareEntry : anonymousUrl.getAnonymousShareEntries()) {
				map.put(anonymousShareEntry.getName(), anonymousShareEntryService.getAnonymousShareEntryStream(anonymousShareEntry.getUuid()));
				// TODO : NEW FUNCTIONNALITY : just send one mail for all files, not one by by file.
			}
			
			//prepare an archive zip
			ArchiveZipStream ai = new ArchiveZipStream(map);
			
			return (new FileStreamResponse(ai,null));
		}
		String msg = "anonymousUrlUuid not valid : " + anonymousUrlUuid;
		logger.debug(msg);
		throw new LinShareNotSuchElementException(msg);
	}
	
}
