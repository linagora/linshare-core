/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.EntryBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.delegation.dto.ShareCreationDto;
import org.linagora.linshare.core.facade.webservice.user.ShareFacade;
import org.linagora.linshare.core.repository.MailingListContactRepository;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.ContactListService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.utils.Version;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;

public class ShareFacadeImpl extends UserGenericFacadeImp
		implements ShareFacade {

	private final ShareEntryService shareEntryService;

	private final ShareService shareService;

	private final EntryBusinessService entryBusinessService;

	private final ContactListService listService;

	private final AuditLogEntryService auditLogEntryService;
	
	private final MailingListContactRepository mailingListContactRepository;

	public ShareFacadeImpl(
			final AccountService accountService, 
			final ShareEntryService shareEntryService,
			final ShareService shareService,
			final EntryBusinessService entryBusinessService,
			final ContactListService listService,
			final AuditLogEntryService auditLogEntryService,
			final MailingListContactRepository mailingListContactRepository) {
		super(accountService);
		this.shareEntryService = shareEntryService;
		this.shareService = shareService;
		this.entryBusinessService = entryBusinessService;
		this.listService = listService;
		this.auditLogEntryService = auditLogEntryService;
		this.mailingListContactRepository = mailingListContactRepository;
	}

	@Override
	public List<ShareDto> getReceivedShares(Version version) throws BusinessException {
		User authUser = checkAuthentication();
		List<ShareEntry> shares = shareEntryService.findAllMyRecievedShareEntries(
				authUser, authUser);

		return ImmutableList.copyOf(Lists.transform(shares,
				ShareDto.toDto(version)));
	}

	@Override
	public List<ShareDto> getShares(Version version) throws BusinessException {
		User authUser = checkAuthentication();
		List<Entry> shares = entryBusinessService
				.findAllMyShareEntries(authUser);
		return ImmutableList.copyOf(Lists.transform(shares, ShareDto.EntrytoDto(version)));
	}

	@Override
	public void sharedocument(String targetMail, String uuid, int securedShare)
			throws BusinessException {
		User authUser = checkAuthentication();
		if ((authUser.isGuest() && !authUser.isCanUpload()))
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		ShareContainer sc = new ShareContainer();
		sc.addDocumentUuid(uuid);
		sc.addMail(targetMail);
		sc.setSecured((securedShare == 1));
		shareService.create(authUser, authUser, sc);
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
		User authUser = checkAuthentication();
		if ((authUser.isGuest() && !authUser.isCanUpload()))
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
		shareService.create(authUser, authUser, sc);
	}

	@Override
	public void multiplesharedocuments(List<ShareDto> shares, boolean secured,
			String message) throws BusinessException {
		User authUser = checkAuthentication();
		if ((authUser.isGuest() && !authUser.isCanUpload()))
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
		shareService.create(authUser, authUser, sc);
	}

	@Override
	public ShareDto getReceivedShare(Version version, String shareEntryUuid)
			throws BusinessException {
		User authUser = checkAuthentication();
		return ShareDto.getReceivedShare(version, shareEntryService.find(authUser, authUser, shareEntryUuid));
	}

	@Override
	public ByteSource getDocumentByteSource(String shareEntryUuid)
			throws BusinessException {
		User authUser = checkAuthentication();
		return shareEntryService.getByteSource(authUser, authUser, shareEntryUuid);
	}

	@Override
	public ByteSource getThumbnailByteSource(String shareEntryUuid, ThumbnailType kind) throws BusinessException {
		User authUser = checkAuthentication();
		if (kind == null) {
			kind = ThumbnailType.MEDIUM;
		}
		return shareEntryService.getThumbnailByteSource(authUser, authUser, shareEntryUuid, kind);
	}

	@Override
	public Set<ShareDto> create(ShareCreationDto createDto) {
		User authUser = checkAuthentication();
		if ((authUser.isGuest() && !authUser.isCanUpload()))
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		ShareContainer sc = new ShareContainer();
		if (createDto.getMailingListUuid() != null && !createDto.getMailingListUuid().isEmpty()) {
			for (String uuid : createDto.getMailingListUuid()) {
				ContactList list = listService.findByUuid(authUser.getLsUuid(), uuid);
				List<ContactListContact> contacts = mailingListContactRepository.findAllContacts(list);
				for (ContactListContact c : contacts) {
					sc.addContact(c);
				}
			}
		}
		sc.addDocumentUuid(createDto.getDocuments());
		sc.setSubject(createDto.getSubject());
		sc.setMessage(createDto.getMessage());
		sc.setSecured(createDto.getSecured());
		sc.setAcknowledgement(createDto.isCreationAcknowledgement());
		sc.setForceAnonymousSharing(createDto.getForceAnonymousSharing());
		sc.setExpiryDate(createDto.getExpirationDate());
		sc.addGenericUserDto(createDto.getRecipients());
		sc.setEnableUSDA(createDto.isEnableUSDA());
		sc.setNotificationDateForUSDA(createDto.getNotificationDateForUSDA());
		sc.setSharingNote(createDto.getSharingNote());
		sc.setInReplyTo(createDto.getInReplyTo());
		sc.setReferences(createDto.getReferences());
		sc.setExternalMailLocale(createDto.getExternalMailLocale());
		Set<Entry> shares = shareService.create(authUser, authUser, sc);
		Set<ShareDto> sharesDto = Sets.newHashSet();
		List<String> uuids = Lists.newArrayList();
		for (Entry entry : shares) {
			sharesDto.add(ShareDto.getSentShare(Version.V2, entry));
			uuids.add(entry.getUuid());
		}
		return sharesDto;
	}

	@Override
	public ShareDto delete(String shareUuid, Boolean received) throws BusinessException {
		Validate.notEmpty(shareUuid, "Missing required share uuid");
		Account authUser = checkAuthentication();
		Entry entry = shareService.delete(authUser, authUser, shareUuid);
		ShareDto dto;
		if (received) {
			dto = ShareDto.getReceivedShare(Version.V2, entry);
		} else {
			dto = ShareDto.getSentShare(Version.V2, entry);
		}
		return dto;
	}

	@Override
	public ShareDto getShare(Version version, String shareUuid) throws BusinessException {
		Validate.notEmpty(shareUuid, "Missing required share uuid");
		User authUser = checkAuthentication();
		return ShareDto.getSentShare(version, shareEntryService.find(authUser, authUser, shareUuid));
	}

	@Override
	public Set<AuditLogEntryUser> findAll(String actorUuid, String uuid, List<LogAction> actions, List<AuditLogEntryType> types,
			String beginDate, String endDate) {
		Account authUser = checkAuthentication();
		User actor = (User) getActor(authUser, actorUuid);
		ShareEntry entry = shareEntryService.find(authUser, actor, uuid);
		Set<AuditLogEntryUser> findAll = auditLogEntryService.findAll(authUser, actor, entry.getUuid(), actions, types, beginDate, endDate);
		return findAll;
	}

}
