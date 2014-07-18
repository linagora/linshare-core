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
package org.linagora.linshare.core.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailConfigRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailBuildingServiceImpl implements MailBuildingService, MailContentBuildingService {

	private final static Logger logger = LoggerFactory
			.getLogger(MailBuildingServiceImpl.class);

	private final boolean displayLogo;

	private final boolean insertLicenceTerm;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final MailConfigRepository mailConfigRepository;

	private static final String LINSHARE_LOGO = "<img src='cid:image.part.1@linshare.org' /><br/><br/>";

	private class ContactRepresentation {
		private String mail;
		private String firstName;
		private String lastName;

		public ContactRepresentation(String mail, String firstName,
				String lastName) {
			super();
			this.mail = StringUtils.trimToNull(mail);
			this.firstName = StringUtils.trimToNull(firstName);
			this.lastName = StringUtils.trimToNull(lastName);
		}

		public ContactRepresentation(String mail) {
			this.mail = StringUtils.trimToNull(mail);
			this.firstName = null;
			this.lastName = null;
		}

		public ContactRepresentation(User user) {
			this.mail = StringUtils.trimToNull(user.getMail());
			this.firstName = StringUtils.trimToNull(user.getFirstName());
			this.lastName = StringUtils.trimToNull(user.getLastName());
		}

		public ContactRepresentation(Account account) {
			this.mail = StringUtils
					.trimToNull(account.getAccountReprentation());
			if (account instanceof User) {
				User user = (User) account;
				this.firstName = StringUtils.trimToNull(user.getFirstName());
				this.lastName = StringUtils.trimToNull(user.getLastName());
				this.mail = StringUtils.trimToNull(user.getMail());
			}
		}

		public String getContactRepresentation() {
			if (this.firstName == null || this.lastName == null)
				return this.mail;
			return firstName + ' ' + lastName + " (" + mail + ')';
		}
	}

	/**
	 * XXX HACK
	 * 
	 * Helper using LinkedHashMap to chain the Key/Value substitution
	 * in mail templates.
	 * 
	 * @author nbertrand
	 */
	private class MailContainerBuilder {

		@SuppressWarnings("serial")
		private class KeyValueChain extends LinkedHashMap<String, String> {
			public KeyValueChain add(String key, String value) {
				logger.debug("Adding K/V pair: [" + key + ", " + value
						+ "]");
				super.put(key, value);
				return this;
			}

			public String build(String input) {
				logger.debug("Building mail template.");
				logger.debug("\tinput: " + input);
				String ret = input;

				for (Map.Entry<String, String> e : entrySet()) {
					ret = StringUtils.replace(ret, "${" + e.getKey() + "}",
							e.getValue());
				}
				logger.debug("\tret: " + ret);
				return ret;
			}
		}

		private KeyValueChain subjectChain;
		private KeyValueChain greetingsChain;
		private KeyValueChain bodyChain;
		private KeyValueChain footerChain;
		private KeyValueChain layoutChain;

		public MailContainerBuilder() {
			super();
			subjectChain = new KeyValueChain();
			greetingsChain = new KeyValueChain();
			bodyChain = new KeyValueChain();
			footerChain = new KeyValueChain();
			layoutChain = new KeyValueChain();
		}

		public KeyValueChain getSubjectChain() {
			return subjectChain;
		}

		public KeyValueChain getGreetingsChain() {
			return greetingsChain;
		}

		public KeyValueChain getBodyChain() {
			return bodyChain;
		}

		public KeyValueChain getFooterChain() {
			return footerChain;
		}

		public KeyValueChain getLayoutChain() {
			return layoutChain;
		}
	}

	public MailBuildingServiceImpl(boolean displayLogo,
			final AbstractDomainService abstractDomainService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final MailConfigRepository mailConfigRepository,
			boolean insertLicenceTerm) throws BusinessException {
		this.displayLogo = displayLogo;
		this.abstractDomainService = abstractDomainService;
		this.insertLicenceTerm = insertLicenceTerm;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mailConfigRepository = mailConfigRepository;
	}

	@Override
	public MailContainerWithRecipient buildAnonymousDownload(
			AnonymousShareEntry shareEntry) throws BusinessException {
		logger.info("Building mail content: "
				+ MailContentType.ANONYMOUS_DOWNLOAD.toString());

		User sender = (User) shareEntry.getEntryOwner();
		String documentName = shareEntry.getDocumentEntry().getName();
		String email = shareEntry.getAnonymousUrl().getContact().getMail();
		String actorRepresentation = new ContactRepresentation(email)
				.getContactRepresentation();

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				sender.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName());
		builder.getBodyChain()
				.add("email", email)
				.add("documentNames", documentName);
		container.setRecipient(sender.getMail());
		container.setFrom(abstractDomainService.getDomainMail(sender
				.getDomain()));

		return buildMailContainer(cfg, sender, container, null,
				MailContentType.ANONYMOUS_DOWNLOAD, builder);
	}

	@Override
	public MailContainerWithRecipient buildRegisteredDownload(
			ShareEntry shareEntry) throws BusinessException {
		logger.info("Building mail content: "
				+ MailContentType.REGISTERED_DOWNLOAD.toString());

		User sender = (User) shareEntry.getEntryOwner();
		String documentName = shareEntry.getDocumentEntry().getName();
		String actorRepresentation = new ContactRepresentation(shareEntry.getRecipient())
				.getContactRepresentation();

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				sender.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName());
		builder.getBodyChain()
				.add("recipientFirstName", shareEntry.getRecipient().getFirstName())
				.add("recipientLastName", shareEntry.getRecipient().getLastName())
				.add("documentNames", documentName);
		container.setRecipient(sender.getMail());
		container.setFrom(abstractDomainService.getDomainMail(sender
				.getDomain()));

		return buildMailContainer(cfg, sender, container, null,
				MailContentType.REGISTERED_DOWNLOAD, builder);
	}

	@Override
	public MailContainerWithRecipient buildNewGuest(User sender,
			User recipient, String password) throws BusinessException {
		logger.info("Building mail content: "
				+ MailContentType.NEW_GUEST.toString());

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				recipient.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getGreetingsChain()
				.add("firstName", recipient.getFirstName())
				.add("lastName", recipient.getLastName());
		builder.getBodyChain()
				.add("url", getLinShareUrlForAUserRecipient(recipient))
				.add("ownerFirstName", sender.getFirstName())
				.add("ownerLastName", sender.getLastName())
				.add("mail", recipient.getMail())
				.add("password", password);
		container.setRecipient(recipient.getMail());
		container.setReplyTo(sender.getMail());
		container.setFrom(abstractDomainService.getDomainMail(recipient
				.getDomain()));

		return buildMailContainer(cfg, sender, container, null,
				MailContentType.NEW_GUEST, builder);
	}

	@Override
	public MailContainerWithRecipient buildResetPassword(Guest recipient,
			String password) throws BusinessException {
		logger.info("Building mail content: "
				+ MailContentType.RESET_PASSWORD.toString());

		MailConfig cfg = recipient.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				recipient.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getGreetingsChain()
				.add("firstName", recipient.getFirstName())
				.add("lastName", recipient.getLastName());
		builder.getBodyChain()
				.add("url", getLinShareUrlForAUserRecipient(recipient))
				.add("mail", recipient.getMail())
				.add("password", password);
		container.setRecipient(recipient.getMail());
		container.setFrom(abstractDomainService.getDomainMail(recipient
				.getDomain()));

		return buildMailContainer(cfg, recipient, container, null,
				MailContentType.RESET_PASSWORD, builder);
	}

	@Override
	public MailContainerWithRecipient buildSharedDocUpdated(
			Entry shareEntry, String oldDocName,
			String fileSizeTxt) throws BusinessException {
		logger.info("Building mail content: "
				+ MailContentType.SHARED_DOC_UPDATED.toString());

		/*
		 * XXX ugly
		 */
		User sender = (User) shareEntry.getEntryOwner();
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url, firstName, lastName, mimeType, fileName, recipient, locale; // ugly
		if (shareEntry instanceof AnonymousShareEntry) {
			AnonymousShareEntry e = (AnonymousShareEntry) shareEntry;
			url = e.getAnonymousUrl()
					.getFullUrl(getLinShareUrlForAContactRecipient(sender));
			recipient = e.getAnonymousUrl().getContact().getMail();
			locale = sender.getExternalMailLocale();
			firstName = "";
			lastName = recipient;
			mimeType = e.getDocumentEntry().getType();
			fileName = e.getDocumentEntry().getName();
		} else {
			ShareEntry e = (ShareEntry) shareEntry;
			url = getLinShareUrlForAUserRecipient(
					e.getRecipient());
			recipient = e.getRecipient().getMail();
			locale = e.getRecipient().getExternalMailLocale();
			firstName = e.getRecipient().getFirstName();
			lastName = e.getRecipient().getLastName();
			mimeType = e.getDocumentEntry().getType();
			fileName = e.getDocumentEntry().getName();
		}

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				sender.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", firstName)
				.add("lastName", lastName);
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("fileName", fileName)
				.add("fileOldName", oldDocName)
				.add("mimeType", mimeType)
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(recipient);
		container.setFrom(abstractDomainService.getDomainMail(sender
				.getDomain()));

		return buildMailContainer(cfg, sender, container, null,
				MailContentType.SHARED_DOC_UPDATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildSharedDocDeleted(Account actor,
			Entry shareEntry) throws BusinessException {
		logger.info("Building mail content: "
				+ MailContentType.SHARED_DOC_DELETED.toString());

		/*
		 * XXX ugly
		 */
		User sender = (User) shareEntry.getEntryOwner();
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String firstName, lastName, fileName, recipient, locale; // ugly
		if (shareEntry instanceof AnonymousShareEntry) {
			AnonymousShareEntry e = (AnonymousShareEntry) shareEntry;
			recipient = e.getAnonymousUrl().getContact().getMail();
			locale = sender.getExternalMailLocale();
			firstName = "";
			lastName = recipient;
			fileName = e.getDocumentEntry().getName();
		} else {
			ShareEntry e = (ShareEntry) shareEntry;
			recipient = e.getRecipient().getMail();
			locale = e.getRecipient().getExternalMailLocale();
			firstName = e.getRecipient().getFirstName();
			lastName = e.getRecipient().getLastName();
			fileName = e.getDocumentEntry().getName();
		}

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				locale);
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", firstName)
				.add("lastName", lastName);
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("documentName", fileName);
		container.setRecipient(recipient);
		container.setFrom(abstractDomainService.getDomainMail(sender
				.getDomain()));

		return buildMailContainer(cfg, sender, container, null,
				MailContentType.SHARED_DOC_DELETED, builder);
	}

	@Override
	public MailContainerWithRecipient buildSharedDocUpcomingOutdated(
			Entry shareEntry, Integer days) throws BusinessException {
		logger.info("Building mail content: "
				+ MailContentType.SHARED_DOC_UPCOMING_OUTDATED.toString());

		/*
		 * XXX ugly
		 */
		User sender = (User) shareEntry.getEntryOwner();
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url, firstName, lastName, fileName, recipient, locale; // ugly
		if (shareEntry instanceof AnonymousShareEntry) {
			AnonymousShareEntry e = (AnonymousShareEntry) shareEntry;
			url = e.getAnonymousUrl()
					.getFullUrl(getLinShareUrlForAContactRecipient(sender));
			recipient = e.getAnonymousUrl().getContact().getMail();;
			locale = sender.getExternalMailLocale();
			firstName = "";
			lastName = recipient;
			fileName = e.getDocumentEntry().getName();
		} else {
			ShareEntry e = (ShareEntry) shareEntry;
			url = getLinShareUrlForAUserRecipient(
					e.getRecipient());
			recipient = e.getRecipient().getMail();
			locale = e.getRecipient().getExternalMailLocale();
			firstName = e.getRecipient().getFirstName();
			lastName = e.getRecipient().getLastName();
			fileName = e.getDocumentEntry().getName();
		}

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				sender.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", firstName)
				.add("lastName", lastName);
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("documentName", fileName)
				.add("nbDays", days.toString())
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(recipient);
		container.setFrom(abstractDomainService.getDomainMail(sender
				.getDomain()));

		return buildMailContainer(cfg, sender, container, null,
				MailContentType.SHARED_DOC_UPCOMING_OUTDATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildDocUpcomingOutdated(
			DocumentEntry document, Integer days) throws BusinessException {
		logger.info("Building mail content: "
				+ MailContentType.DOC_UPCOMING_OUTDATED.toString());

		User owner = (User) document.getEntryOwner();
		String actorRepresentation = new ContactRepresentation(owner)
				.getContactRepresentation();
		String url = getLinShareUrlForAUserRecipient(owner);

		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				owner.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("documentName", document.getName())
				.add("nbDays", days.toString())
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(owner.getMail());
		container.setFrom(abstractDomainService.getDomainMail(owner
				.getDomain()));

		return buildMailContainer(cfg, owner, container, null,
				MailContentType.DOC_UPCOMING_OUTDATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildNewSharing(User sender,
			MailContainer input, User recipient,
			List<ShareDocumentVo> shares) throws BusinessException {
		logger.info("Building mail content: "
				+ MailContentType.NEW_SHARING.toString());

		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url = getLinShareUrlForAUserRecipient(recipient);

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				recipient.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		StringBuffer names = new StringBuffer();
		long shareSize = 0;
		for (ShareDocumentVo share : shares) {
			if (recipient.getLsUuid().equals(share.getReceiver().getLsUuid())) {
				shareSize += 1;
				names.append("<li><a href='"
						+ getDirectDownloadLink(recipient, share) + "'>"
						+ share.getFileName() + "</a></li>");
			}
		}

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", recipient.getFirstName())
				.add("lastName", recipient.getLastName());
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("number", "" + shareSize)
				.add("documentNames", names.toString())
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(recipient.getMail());
		container.setFrom(abstractDomainService.getDomainMail(sender
				.getDomain()));

		return buildMailContainer(cfg, sender, container,
				input.getPersonalMessage(), MailContentType.NEW_SHARING,
				builder);
	}

	@Override
	public MailContainerWithRecipient buildNewSharingProtected(User sender,
			MailContainer input, AnonymousUrl anonUrl)
			throws BusinessException {
		logger.info("Building mail content: "
				+ MailContentType.NEW_SHARING_PROTECTED.toString());

		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url = anonUrl
				.getFullUrl(getLinShareUrlForAContactRecipient(sender));
		String email = anonUrl.getContact().getMail();

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				sender.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		StringBuffer names = new StringBuffer();
		for (String n : anonUrl.getDocumentNames()) {
			names.append("<li>" + n + "</li>");
		}

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", "")
				.add("lastName", email);
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("number", "" + anonUrl.getDocumentNames().size())
				.add("documentNames", names.toString())
				.add("password", anonUrl.getPassword())
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(email);
		container.setFrom(abstractDomainService.getDomainMail(sender
				.getDomain()));

		return buildMailContainer(cfg, sender, container,
				input.getPersonalMessage(),
				MailContentType.NEW_SHARING_PROTECTED, builder);
	}

	@Override
	public MailContainerWithRecipient buildNewSharingCyphered(User sender,
			MailContainer input, User recipient,
			List<ShareDocumentVo> shares) throws BusinessException {
		logger.info("Building mail content: "
				+ MailContentType.NEW_SHARING_CYPHERED.toString());

		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url = getLinShareUrlForAUserRecipient(recipient);

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				recipient.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		StringBuffer names = new StringBuffer();
		long shareSize = 0;
		for (ShareDocumentVo share : shares) {
			if (recipient.getLsUuid().equals(share.getReceiver().getLsUuid())) {
				shareSize += 1;
				names.append("<li><a href='"
						+ getDirectDownloadLink(recipient, share) + "'>"
						+ share.getFileName() + "</a></li>");
			}
		}

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", recipient.getFirstName())
				.add("lastName", recipient.getLastName());
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("number", "" + shareSize)
				.add("documentNames", names.toString())
				.add("jwsEncryptUrl", getJwsEncryptUrlString(url))
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(recipient.getMail());
		container.setFrom(abstractDomainService.getDomainMail(sender
				.getDomain()));

		return buildMailContainer(cfg, sender, container,
				input.getPersonalMessage(),
				MailContentType.NEW_SHARING_CYPHERED, builder);
	}

	@Override
	public MailContainerWithRecipient buildNewSharingCypheredProtected(
			User sender, MailContainer input, AnonymousUrl anonUrl)
			throws BusinessException {
		logger.info("Building mail content: "
				+ MailContentType.NEW_SHARING_CYPHERED_PROTECTED.toString());

		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url = anonUrl
				.getFullUrl(getLinShareUrlForAContactRecipient(sender));
		String email = anonUrl.getContact().getMail();

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				sender.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		StringBuffer names = new StringBuffer();
		for (String n : anonUrl.getDocumentNames()) {
			names.append("<li>" + n + "</li>");
		}

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", "")
				.add("lastName", email);
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("number", "" + anonUrl.getDocumentNames().size())
				.add("documentNames", names.toString())
				.add("password", anonUrl.getTemporaryPlainTextPassword())
				.add("jwsEncryptUrl", getJwsEncryptUrlString(url))
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(email);
		container.setFrom(abstractDomainService.getDomainMail(sender
				.getDomain()));

		return buildMailContainer(cfg, sender, container,
				input.getPersonalMessage(),
				MailContentType.NEW_SHARING_PROTECTED, builder);
	}

	@Override
	public MailContainerWithRecipient buildNewUploadRequest(User sender,
			MailContainer inputMailContainer, UploadRequestUrl requestUrl)
					throws BusinessException {

		logger.info("Building mail content: "
				+ MailContentType.UPLOAD_REQUEST_CREATED.toString());

		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url = requestUrl
				.getFullUrl(getLinShareUploadRequestUrl(sender));
		String email = requestUrl.getContact().getMail();

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
//		requestUrl.getUploadRequest().getLocale()
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				sender.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", actorRepresentation)
				.add("subject", requestUrl.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", "")
				.add("lastName", email);
		builder.getBodyChain()
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("password", requestUrl.getTemporaryPlainTextPassword())
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(email);
		container.setFrom(abstractDomainService.getDomainMail(sender
				.getDomain()));

		return buildMailContainer(cfg, sender, container,
				inputMailContainer.getPersonalMessage(),
				MailContentType.UPLOAD_REQUEST_CREATED, builder);
	}


	/*
	 * Adapters
	 */

	@Override
	public MailContainerWithRecipient buildMailUpcomingOutdatedShare(
			ShareEntry shareEntry, Integer days) throws BusinessException {
		return buildSharedDocUpcomingOutdated(shareEntry, days);
	}

	@Override
	public MailContainerWithRecipient buildMailUpcomingOutdatedShare(
			AnonymousShareEntry shareEntry, Integer days)
			throws BusinessException {
		return buildSharedDocUpcomingOutdated(shareEntry, days);
	}

	@Override
	public MailContainerWithRecipient buildMailUpcomingOutdatedDocument(
			DocumentEntry document, Integer days) throws BusinessException {
		return buildDocUpcomingOutdated(document, days);
	}

	@Override
	public MailContainerWithRecipient buildMailSharedDocumentUpdated(
			AnonymousShareEntry shareEntry, String oldDocName,
			String fileSizeTxt) throws BusinessException {
		return buildSharedDocUpdated(shareEntry, oldDocName, fileSizeTxt);
	}

	@Override
	public MailContainerWithRecipient buildMailSharedDocumentUpdated(
			ShareEntry shareEntry, String oldDocName, String fileSizeTxt)
			throws BusinessException {
		return buildSharedDocUpdated(shareEntry, oldDocName, fileSizeTxt);
	}

	@Override
	public MailContainerWithRecipient buildMailSharedFileDeletedWithRecipient(
			Account actor, ShareEntry shareEntry) throws BusinessException {
		return buildSharedDocDeleted(actor, shareEntry);
	}

	@Override
	public MailContainerWithRecipient buildMailAnonymousDownload(
			AnonymousShareEntry shareEntry) throws BusinessException {
		return buildAnonymousDownload(shareEntry);
	}

	@Override
	public MailContainerWithRecipient buildMailNewSharingWithRecipient(
			User sender, MailContainer input, User recipient,
			List<ShareDocumentVo> shares, boolean hasToDecrypt)
			throws BusinessException {
		if (hasToDecrypt)
			return buildNewSharingCyphered(sender, input, recipient, shares);
		return buildNewSharing(sender, input, recipient, shares);
	}

	@Override
	public MailContainerWithRecipient buildMailNewSharingWithRecipient(
			MailContainer input, AnonymousUrl anonUrl, User sender)
			throws BusinessException {
		if (anonUrl.oneDocumentIsEncrypted())
			return buildNewSharingCypheredProtected(sender, input, anonUrl);
		return buildNewSharingProtected(sender, input, anonUrl);
	}

	@Override
	public MailContainerWithRecipient buildMailResetPassword(Guest recipient,
			String password) throws BusinessException {
		return buildResetPassword(recipient, password);
	}

	@Override
	public MailContainerWithRecipient buildMailNewGuest(User sender,
			User recipient, String password) throws BusinessException {
		return buildNewGuest(sender, recipient, password);
	}

	@Override
	public MailContainerWithRecipient buildMailRegisteredDownloadWithOneRecipient(
			ShareEntry shareEntry) throws BusinessException {
		return buildRegisteredDownload(shareEntry);
	}

	/*
	 * Helpers
	 */

	private String getLinShareUploadRequestUrl(Account sender) {
		return functionalityReadOnlyService
				.getUploadRequestFunctionality(sender.getDomain())
				.getValue();
	}


	private String getLinShareUrlForAUserRecipient(Account recipient) {
		return functionalityReadOnlyService
				.getCustomNotificationUrlFunctionality(recipient.getDomain())
				.getValue();
	}

	private String getLinShareUrlForAContactRecipient(Account sender) {
		AbstractDomain senderDomain = abstractDomainService
				.getGuestDomain(sender.getDomainId());
		// guest domain could be inexistent into the database.
		if (senderDomain == null) {
			senderDomain = sender.getDomain();
		}
		return functionalityReadOnlyService
				.getCustomNotificationUrlFunctionality(senderDomain).getValue();
	}

	private String getDirectDownloadLink(User recipient, DocumentVo doc) {
		String path = getLinShareUrlForAUserRecipient(recipient);
		String sep = path.endsWith("/") ? "" : "/";
		String dl = path + sep + "index.listshareddocument.download/";
		return dl + doc.getIdentifier();
	}

	private String getJwsEncryptUrlString(String rootUrl) {
		String jwsEncryptUrlString = "";
		StringBuffer jwsEncryptUrl = new StringBuffer();

		jwsEncryptUrl.append(rootUrl);
		if (!rootUrl.endsWith("/")) {
			jwsEncryptUrl.append('/');
		}
		jwsEncryptUrl.append("localDecrypt");
		jwsEncryptUrlString = jwsEncryptUrl.toString();

		return jwsEncryptUrlString;
	}

	/*
	 * MAIL CONTAINER BUILDER SECTION
	 */
	private String formatSubject(String subject, Language lang) {
		if (StringUtils.isBlank(subject))
			return null;
		return lang.equals(Language.FRENCH) ? "${actorSubject} de la part de ${actorRepresentation}"
				: "${actorSubject} from ${actorRepresentation}";
	}

	private String formatPersonalMessage(String pm, Language lang) {
		return StringUtils.isBlank(pm) ? "" : pm + "<hr/><br/>";
	}

	private String formatFooter(String footer, Language lang) {
        if (insertLicenceTerm) {
    		if (lang.equals(Language.FRENCH)) {
				footer += "<br/>Vous utilisez la version libre et gratuite de <a href=\"http://www.linshare.org/\" title=\"LinShare\"><strong>LinShare</strong></a>™, développée par Linagora © 2009-2014. Contribuez à la R&D du produit en souscrivant à une offre entreprise.<br/>";
			} else {
				footer += "<br/>You are using the Open Source and free version of <a href=\"http://www.linshare.org/\" title=\"LinShare\"><strong>LinShare</strong></a>™, powered by Linagora © 2009-2014. Contribute to Linshare R&D by subscribing to an Enterprise offer.<br/>";
    		}
        }
        return footer;
	}

	private MailContainerWithRecipient buildMailContainer(MailConfig cfg,
			User sender, final MailContainerWithRecipient input, String pm,
			MailContentType type, MailContainerBuilder builder)
			throws BusinessException {
		Language lang = input.getLanguage();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				input);
		MailContent mailContent = cfg.findContent(lang, type);
		String subject = StringUtils.defaultIfEmpty(
				formatSubject(input.getSubject(), lang),
				mailContent.getSubject());
		String greetings = mailContent.getGreetings();
		String body = mailContent.getBody();
		MailFooter f = cfg.findFooter(lang);
		String footer = formatFooter(cfg.findFooter(lang).getFooter(), lang);
		String layout = cfg.getMailLayoutHtml().getLayout();

		pm = formatPersonalMessage(pm, lang);
		subject = builder.getSubjectChain().build(subject);
		greetings = builder.getGreetingsChain().build(greetings);
		body = builder.getBodyChain().build(body);
		footer = builder.getFooterChain().build(footer);
		layout = builder.getLayoutChain()
				.add("image", displayLogo ? LINSHARE_LOGO : "")
				.add("personalMessage", pm)
				.add("greetings", greetings)
				.add("body", body)
				.add("footer", footer)
				.build(layout);

		container.setSubject(subject);
		container.setContentHTML(layout);
		container.setContentTXT(container.getContentHTML());
		container.setInReplyTo(input.getInReplyTo());
		container.setReferences(input.getReferences());
		return container;
	}

}
