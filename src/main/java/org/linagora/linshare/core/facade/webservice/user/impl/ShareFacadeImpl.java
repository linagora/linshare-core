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
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.ShareFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.webservice.dto.ShareDto;

import com.google.common.collect.Lists;

public class ShareFacadeImpl extends UserGenericFacadeImp
		implements ShareFacade {

	private final ShareEntryService shareEntryService;

	private final ShareService shareService;

	public ShareFacadeImpl(
			final AccountService accountService, 
			final ShareEntryService shareEntryService,
			final ShareService shareService) {
		super(accountService);
		this.shareEntryService = shareEntryService;
		this.shareService = shareService;
	}

	@Override
	public List<ShareDto> getReceivedShares() throws BusinessException {
		User actor = getAuthentication();
		List<ShareEntry> shares = shareEntryService.findAllMyRecievedShareEntries(
				actor, actor);
		return Lists.transform(shares, ShareDto.toVo());
	}

	@Override
	public void sharedocument(String targetMail, String uuid, int securedShare)
			throws BusinessException {
		User actor = getAuthentication();
		if ((actor.isGuest() && !actor.getCanUpload()))
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		ShareContainer sc = new ShareContainer();
		sc.addDocumentUuid(uuid);
		sc.addMail(targetMail);
		sc.setSecured((securedShare == 1));
		shareService.create(actor, actor, sc);
	}

	@Override
	public void multiplesharedocuments(String targetMail, List<String> uuid,
			int securedShare, String messageOpt, String inReplyToOpt,
			String referencesOpt) throws BusinessException {
		List<String> listRecipient = new ArrayList<String>();
		listRecipient.add(targetMail);
		this.multiplesharedocuments(listRecipient, uuid, securedShare,
				messageOpt, inReplyToOpt, referencesOpt);
	}

	@Override
	public void multiplesharedocuments(List<String> mails, List<String> documentUuids,
			int securedShare, String messageOpt, String inReplyToOpt,
			String referencesOpt) throws BusinessException {
		User actor = getAuthentication();
		if ((actor.isGuest() && !actor.getCanUpload()))
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		ShareContainer sc = new ShareContainer();
		sc.addDocumentUuid(documentUuids);
		sc.addMail(mails);
		sc.setSecured((securedShare == 1));
		sc.setInReplyTo(inReplyToOpt);
		sc.setReferences(referencesOpt);
		sc.setMessage(messageOpt);
		shareService.create(actor, actor, sc);
	}

	@Override
	public void multiplesharedocuments(List<ShareDto> shares, boolean secured,
			String message) throws BusinessException {
		User actor = getAuthentication();
		if ((actor.isGuest() && !actor.getCanUpload()))
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		ShareContainer sc = new ShareContainer();
		for (ShareDto share : shares) {
			sc.addDocumentUuid(share.getDocumentDto().getUuid());
			sc.addUserDto(share.getRecipient());
		}
		sc.setSecured(secured);
		sc.setMessage(message);
		shareService.create(actor, actor, sc);
	}

	@Override
	public ShareDto getReceivedShare(String shareEntryUuid)
			throws BusinessException {
		User actor = getAuthentication();
		return ShareDto.getReceivedShare(shareEntryService.find(actor, actor, shareEntryUuid));
	}

	@Override
	public InputStream getDocumentStream(String shareEntryUuid)
			throws BusinessException {
		User actor = getAuthentication();
		return shareEntryService.getStream(actor, actor, shareEntryUuid);
	}

	@Override
	public InputStream getThumbnailStream(String shareEntryUuid) throws BusinessException {
		User actor = getAuthentication();
		return shareEntryService.getThumbnailStream(actor, actor, shareEntryUuid);
	}
}
