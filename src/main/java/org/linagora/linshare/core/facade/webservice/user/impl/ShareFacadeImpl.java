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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.ShareFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.webservice.dto.ShareDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareFacadeImpl extends GenericFacadeImpl
		implements ShareFacade {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory
			.getLogger(ShareFacadeImpl.class);

	private final DocumentEntryService documentEntryService;

	private final org.linagora.linshare.core.facade.ShareFacade tapestryShareFacade;

	private final ShareEntryService shareEntryService;

	public ShareFacadeImpl(
			final DocumentEntryService documentEntryService,
			final AccountService accountService, 
			final org.linagora.linshare.core.facade.ShareFacade shareFacade,
			final ShareEntryService shareEntryService) {
		super(accountService);
		this.documentEntryService = documentEntryService;
		this.tapestryShareFacade = shareFacade;
		this.shareEntryService = shareEntryService;
	}

	@Override
	public List<ShareDto> getReceivedShares() throws BusinessException {
		User actor = getAuthentication();
		List<ShareEntry> shares = shareEntryService.findAllMyShareEntries(
				actor, actor);

		if (shares == null)
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_NOT_FOUND,
					"No such share");
		return convertReceivedShareEntryList(shares);
	}

	private static List<ShareDto> convertReceivedShareEntryList(List<ShareEntry> input) {
		if (input == null)
			return null;

		List<ShareDto> output = new ArrayList<ShareDto>();

		for (ShareEntry var : input) {
			output.add(ShareDto.getReceivedShare(var));
		}
		return output;
	}

	@Override
	public void sharedocument(String targetMail, String uuid, int securedShare)
			throws BusinessException {
		User actor = getAuthentication();
		DocumentEntry documentEntry;

		if ((actor instanceof Guest && !actor.getCanUpload()))
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		try {
			documentEntry = documentEntryService.findById(actor, uuid);
		} catch (BusinessException e) {
			throw e;
		}
		if (documentEntry == null) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_NOT_FOUND,
					"Document not found");
		}

		DocumentVo docVo = new DocumentVo(documentEntry.getUuid(),
				documentEntry.getName(), documentEntry.getComment(),
				documentEntry.getCreationDate(),
				documentEntry.getExpirationDate(), documentEntry.getType(),
				documentEntry.getEntryOwner().getLsUuid(),
				documentEntry.getCiphered(), documentEntry.getShareEntries()
						.size() > 0, documentEntry.getSize());

		List<DocumentVo> listDoc = new ArrayList<DocumentVo>();
		listDoc.add(docVo);

		List<String> listRecipient = new ArrayList<String>();
		listRecipient.add(targetMail);

		SuccessesAndFailsItems<ShareDocumentVo> successes;

		// give personal message and subject in WS in the future? null at this
		// time
		String message = null;
		String subject = null;
		MailContainer mailContainer = new MailContainer(
				actor.getExternalMailLocale(), message, subject);

		UserVo uo = new UserVo(actor);

		successes = tapestryShareFacade.createSharingWithMailUsingRecipientsEmail(
				uo, listDoc, listRecipient, (securedShare == 1), mailContainer);


		if ((successes.getSuccessesItem() == null) ||
			((successes.getFailsItem() != null) &&
			(successes.getFailsItem().size() > 0))) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FAULT,
					"Could not share the document");
		}
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
	public void multiplesharedocuments(List<String> mails, List<String> uuid,
			int securedShare, String messageOpt, String inReplyToOpt,
			String referencesOpt) throws BusinessException {
		User actor = getAuthentication();

		List<DocumentVo> listDoc = new ArrayList<DocumentVo>();

		// fetch the document
		DocumentEntry documentEntry;

		for (String onefileid : uuid) {
			documentEntry = documentEntryService.findById(actor, onefileid);
			DocumentVo documentVo = new DocumentVo(documentEntry);
			listDoc.add(documentVo);
		}

		// give personal message and subject in WS in the future? null at this
		// time
		String message = (messageOpt == null) ? "" : messageOpt;

		String inReplyTo = inReplyToOpt;
		if (inReplyToOpt != null) {
			if ("".equals(inReplyToOpt)) {
				inReplyTo = null;
			}
		}
		String references = referencesOpt;
		if (referencesOpt != null) {
			if ("".equals(referencesOpt)) {
				references = null;
			}
		}

		String subject = null;
		MailContainer mailContainer = new MailContainer(
				actor.getExternalMailLocale(), message, subject);
		// Useful for Thunderbird plugin.
		mailContainer.setReferences(references);
		mailContainer.setInReplyTo(inReplyTo);

		UserVo actorVo = new UserVo(actor);

		SuccessesAndFailsItems<ShareDocumentVo> successes;

		try {
			successes = tapestryShareFacade
					.createSharingWithMailUsingRecipientsEmail(actorVo,
							listDoc, mails, (securedShare == 1), mailContainer);
		} catch (BusinessException e) {
			throw e;
		}

		if ((successes.getSuccessesItem() == null)
				|| ((successes.getFailsItem() != null) && (successes
						.getFailsItem().size() > 0))) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FAULT,
					"Could not share the document");
		}

	}

	@Override
	public void multiplesharedocuments(List<ShareDto> shares, boolean secured,
			String message) throws BusinessException {
		User actor = getAuthentication();
		UserVo actorVo = new UserVo(actor);

		ArrayList<DocumentVo> listDoc = new ArrayList<DocumentVo>();
		ArrayList<String> mails = new ArrayList<String>();

		// FIXME XXX TODO HACK : Workaround to re-use tapestry facade. Refactor
		// and remove Vo when tapestry will be removed
		for (ShareDto share : shares) {
			// fetch the document
			DocumentEntry documentEntry = documentEntryService.findById(actor,
					share.getDocumentDto().getUuid());

			DocumentVo documentVo = new DocumentVo(documentEntry);
			listDoc.add(documentVo);

			// give personal message and subject in WS in the future? null at
			// this
			// time

			mails.add(share.getRecipient().getUuid());
		}
		String subject = null;
		MailContainer mailContainer = new MailContainer(
				actor.getExternalMailLocale(), message, subject);
		SuccessesAndFailsItems<ShareDocumentVo> successes;
		try {
			successes = tapestryShareFacade.createSharingWithMailUsingRecipientsEmail(
					actorVo, listDoc, mails, secured, mailContainer);
		} catch (BusinessException e) {
			throw e;
		}

		if ((successes.getSuccessesItem() == null)
				|| ((successes.getFailsItem() != null) && (successes
						.getFailsItem().size() > 0))) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_FAULT,
					"Could not share the document");
		}

	}

	@Override
	public ShareDto getReceivedShare(String shareEntryUuid)
			throws BusinessException {
		User actor = getAuthentication();
		return ShareDto.getReceivedShare(shareEntryService.findByUuid(actor, shareEntryUuid));
	}

	@Override
	public InputStream getDocumentStream(String shareEntryUuid)
			throws BusinessException {
		User actor = getAuthentication();
		return shareEntryService.getShareStream(actor, shareEntryUuid);
	}

	@Override
	public InputStream getThumbnailStream(String shareEntryUuid) throws BusinessException {
		User actor = getAuthentication();
		return shareEntryService.getShareThumbnailStream(actor, shareEntryUuid);
	}
	
}
