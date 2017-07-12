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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.EntryBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.delegation.dto.ShareCreationDto;
import org.linagora.linshare.core.facade.webservice.user.ShareFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.MailingListService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ShareFacadeImpl extends UserGenericFacadeImp
		implements ShareFacade {

	private final ShareEntryService shareEntryService;

	private final ShareService shareService;

	private final EntryBusinessService entryBusinessService;

	private final MailingListService listService;

	private final AuditLogEntryService auditLogEntryService;

	public ShareFacadeImpl(
			final AccountService accountService, 
			final ShareEntryService shareEntryService,
			final ShareService shareService,
			final EntryBusinessService entryBusinessService,
			final MailingListService listService,
			final AuditLogEntryService auditLogEntryService) {
		super(accountService);
		this.shareEntryService = shareEntryService;
		this.shareService = shareService;
		this.entryBusinessService = entryBusinessService;
		this.listService = listService;
		this.auditLogEntryService = auditLogEntryService;
	}

	@Override
	public List<ShareDto> getReceivedShares() throws BusinessException {
		User actor = checkAuthentication();
		List<ShareEntry> shares = shareEntryService.findAllMyRecievedShareEntries(
				actor, actor);

		return ImmutableList.copyOf(Lists.transform(shares,
				ShareDto.toDto()));
	}

	@Override
	public List<ShareDto> getShares() throws BusinessException {
		User actor = checkAuthentication();
		List<Entry> shares = entryBusinessService
				.findAllMyShareEntries(actor);
		return ImmutableList.copyOf(Lists.transform(shares, ShareDto.EntrytoDto()));
	}

	@Override
	public void sharedocument(String targetMail, String uuid, int securedShare)
			throws BusinessException {
		User actor = checkAuthentication();
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
		User actor = checkAuthentication();
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
		User actor = checkAuthentication();
		if ((actor.isGuest() && !actor.getCanUpload()))
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		ShareContainer sc = new ShareContainer();
		for (ShareDto share : shares) {
			sc.addDocumentUuid(share.getDocument().getUuid());
			sc.addUserDto(share.getRecipient());
		}
		sc.setSecured(secured);
		sc.setMessage(message);
		shareService.create(actor, actor, sc);
	}

	@Override
	public ShareDto getReceivedShare(String shareEntryUuid)
			throws BusinessException {
		User actor = checkAuthentication();
		return ShareDto.getReceivedShare(shareEntryService.find(actor, actor, shareEntryUuid));
	}

	@Override
	public InputStream getDocumentStream(String shareEntryUuid)
			throws BusinessException {
		User actor = checkAuthentication();
		return shareEntryService.getStream(actor, actor, shareEntryUuid);
	}

	@Override
	public InputStream getThumbnailStream(String shareEntryUuid) throws BusinessException {
		User actor = checkAuthentication();
		return shareEntryService.getThumbnailStream(actor, actor, shareEntryUuid);
	}

	@Override
	public Set<ShareDto> create(ShareCreationDto createDto) {
		User actor = checkAuthentication();
		if ((actor.isGuest() && !actor.getCanUpload()))
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		ShareContainer sc = new ShareContainer();
		if (createDto.getMailingListUuid() != null && !createDto.getMailingListUuid().isEmpty()) {
			for (String uuid : createDto.getMailingListUuid()) {
				MailingList list = listService.findByUuid(actor.getLsUuid(), uuid);
				for (MailingListContact c : list.getMailingListContact()) {
					sc.addContact(c);
				}
			}
		}
		sc.addDocumentUuid(createDto.getDocuments());
		sc.setSubject(createDto.getSubject());
		sc.setMessage(createDto.getMessage());
		sc.setSecured(createDto.getSecured());
		sc.setAcknowledgement(createDto.isCreationAcknowledgement());
		sc.setExpiryDate(createDto.getExpirationDate());
		sc.addGenericUserDto(createDto.getRecipients());
		sc.setEnableUSDA(createDto.isEnableUSDA());
		sc.setNotificationDateForUSDA(createDto.getNotificationDateForUSDA());
		sc.setSharingNote(createDto.getSharingNote());
		Set<Entry> shares = shareService.create(actor, actor, sc);
		Set<ShareDto> sharesDto = Sets.newHashSet();
		List<String> uuids = Lists.newArrayList();
		for (Entry entry : shares) {
			sharesDto.add(ShareDto.getSentShare(entry));
			uuids.add(entry.getUuid());
		}
		return sharesDto;
	}

	@Override
	public ShareDto delete(String shareUuid, Boolean received) throws BusinessException {
		Validate.notEmpty(shareUuid, "Missing required share uuid");
		Account actor = checkAuthentication();
		Entry entry = shareService.delete(actor, actor, shareUuid);
		ShareDto dto;
		if (received) {
			dto = ShareDto.getReceivedShare(entry);
		} else {
			dto = ShareDto.getSentShare(entry);
		}
		return dto;
	}

	@Override
	public ShareDto getShare(String shareUuid) throws BusinessException {
		Validate.notEmpty(shareUuid, "Missing required share uuid");
		User actor = checkAuthentication();
		return ShareDto.getSentShare(shareEntryService.find(actor, actor, shareUuid));
	}

	@Override
	public DocumentDto copy(String shareEntryUuid)
			throws BusinessException {
		Validate.notEmpty(shareEntryUuid, "Missing required share uuid");
		Account actor = checkAuthentication();
		return new DocumentDto(shareEntryService.copy(actor, actor, shareEntryUuid));
	}

	@Override
	public Set<AuditLogEntryUser> findAll(String ownerUuid, String uuid, List<String> actions, List<String> types,
			String beginDate, String endDate) {
		Account actor = checkAuthentication();
		User owner = (User) getOwner(actor, ownerUuid);
		ShareEntry entry = shareEntryService.find(actor, owner, uuid);
		Set<AuditLogEntryUser> findAll = auditLogEntryService.findAll(actor, owner, entry.getUuid(), actions, types, beginDate, endDate);
		return findAll;
	}

}
