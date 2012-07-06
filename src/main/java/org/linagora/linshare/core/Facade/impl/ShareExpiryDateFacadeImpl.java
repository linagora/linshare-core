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
package org.linagora.linshare.core.Facade.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.Facade.ShareExpiryDateFacade;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.ShareExpiryDateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareExpiryDateFacadeImpl implements ShareExpiryDateFacade {

	private final DocumentEntryService documentEntryService;
	private final ShareExpiryDateService shareExpiryDateService;
	private final AccountService accountService;
	
	private static final Logger logger = LoggerFactory.getLogger(ShareExpiryDateFacadeImpl.class);
	
	public ShareExpiryDateFacadeImpl(final DocumentEntryService documentEntryService,
			final ShareExpiryDateService shareExpiryDateService,
			final AccountService accountService) {
		super();
		this.documentEntryService = documentEntryService;
		this.shareExpiryDateService = shareExpiryDateService;
		this.accountService = accountService;
	}

	public Calendar computeMinShareExpiryDateOfList(List<DocumentVo> docsVo, UserVo ownerVo) {
		if(ownerVo.getLsUid() == null) {
			logger.error("Can't find user with null parametter.");
			return null;
		}
		
		Account account = accountService.findByLsUid(ownerVo.getLsUid());
		if(account == null) {
			logger.error("Can't find logged user.");
			return null;
		}
		
		List<DocumentEntry> docList = new ArrayList<DocumentEntry>();
		for (DocumentVo documentVo : docsVo) {
			try {
				docList.add(documentEntryService.findById(account, documentVo.getIdentifier()));
			} catch (BusinessException e) {
				logger.error("document "  + documentVo.getIdentifier() + " not found : " + e.getMessage());
			}
		}
		return shareExpiryDateService.computeMinShareExpiryDateOfList(docList, account);
	}

	public Calendar computeShareExpiryDate(DocumentVo documentVo, UserVo ownerVo) {
		
		if(ownerVo.getLsUid() == null) {
			logger.error("Can't find user with null parametter.");
			return null;
		}
		
		Account account = accountService.findByLsUid(ownerVo.getLsUid());
		if(account == null) {
			logger.error("Can't find logged user.");
			return null;
		}
		
		DocumentEntry doc;
		try {
			doc = documentEntryService.findById(account, documentVo.getIdentifier());
			return shareExpiryDateService.computeShareExpiryDate(doc, account);
		} catch (BusinessException e) {
			logger.error("document "  + documentVo.getIdentifier() + " not found : " + e.getMessage());
		}
		return null;
	}

}
