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
package org.linagora.linshare.core.service.impl;

import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.UploadProposition;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.objects.Recipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailConfigRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.utils.DocumentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

public class MailBuildingServiceImpl implements MailBuildingService {

	private final static Logger logger = LoggerFactory
			.getLogger(MailBuildingServiceImpl.class);

	private final boolean displayLogo;

	private final boolean insertLicenceTerm;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final MailConfigRepository mailConfigRepository;

	private static final String LINSHARE_LOGO = "<img src='cid:image.part.1@linshare.org' /><br/><br/>";

	private class FileRepresentation {

		private String name;

		public FileRepresentation(UploadRequestEntry entry) {
			super();
			this.name = entry.getName();
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private class ContactRepresentation {
		private String mail;
		private String firstName;
		private String lastName;

		@SuppressWarnings("unused")
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

		@SuppressWarnings("unused")
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
			return getContactRepresentation(false);
		}

		public String getContactRepresentation(boolean includeMail) {
			if (this.firstName == null || this.lastName == null)
				return this.mail;
			StringBuilder res = new StringBuilder();
			res.append(firstName);
			res.append(" ");
			res.append(lastName);
			if (includeMail) {
				res.append(" (");
				res.append(mail);
				res.append(")");
			}
			return res.toString();
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
				super.put(key, StringUtils.defaultString(value));
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

	private String formatCreationDate(Account account, Entry entry) {
		Locale locale = account.getJavaExternalMailLocale();
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, locale);
		return formatter.format(entry.getCreationDate().getTime());
	}

	private String formatDeletionDate(Account account) {
		Locale locale = account.getJavaExternalMailLocale();
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, locale);
		return formatter.format(new Date());
	}

	private String formatCreationDate(Account account, UploadRequest uploadRequest) {
		Locale locale = account.getJavaExternalMailLocale();
		DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL, locale);
		return formatter.format(uploadRequest.getCreationDate().getTime());
	}

	private String formatActivationDate(Account account, UploadRequest uploadRequest) {
		Locale locale = account.getJavaExternalMailLocale();
		DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL, locale);
		return formatter.format(uploadRequest.getActivationDate().getTime());
	}

	private String formatExpirationDate(Account account,
			UploadRequest uploadRequest) {
		if (uploadRequest.getExpiryDate() != null) {
			Locale locale = account.getJavaExternalMailLocale();
			DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL,
					locale);
			return formatter.format(uploadRequest.getExpiryDate().getTime());
		}
		return "";
	}

	@Override
	public MailContainerWithRecipient buildAnonymousDownload(
			AnonymousShareEntry shareEntry) throws BusinessException {
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
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(email);

		return buildMailContainer(cfg, container, null,
				MailContentType.ANONYMOUS_DOWNLOAD, builder);
	}

	@Override
	public MailContainerWithRecipient buildRegisteredDownload(
			ShareEntry shareEntry) throws BusinessException {
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
		container.setRecipient(sender);
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(shareEntry.getRecipient());
		return buildMailContainer(cfg, container, null,
				MailContentType.REGISTERED_DOWNLOAD, builder);
	}

	@Override
	public MailContainerWithRecipient buildNewGuest(Account s,
			User recipient, String password) throws BusinessException {
		User recipientUser = (User)recipient;
		User sender = (User) s;
		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				recipientUser.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getGreetingsChain()
				.add("firstName", recipientUser.getFirstName())
				.add("lastName", recipientUser.getLastName());
		builder.getBodyChain()
				.add("url", getLinShareUrlForAUserRecipient(recipientUser))
				.add("ownerFirstName", sender.getFirstName())
				.add("ownerLastName", sender.getLastName())
				.add("mail", recipientUser.getMail())
				.add("password", password);
		container.setRecipient(recipientUser);
		container.setReplyTo(sender);
		container.setFrom(getFromMailAddress(recipientUser));

		return buildMailContainer(cfg, container, null,
				MailContentType.NEW_GUEST, builder);
	}

	@Override
	public MailContainerWithRecipient buildResetPassword(Guest recipient,
			String password) throws BusinessException {
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
		container.setFrom(getFromMailAddress(recipient));

		return buildMailContainer(cfg, container, null,
				MailContentType.RESET_PASSWORD, builder);
	}

	@Override
	public MailContainerWithRecipient buildSharedDocUpdated(
			Entry shareEntry, String oldDocName, long size) throws BusinessException {
		/*
		 * XXX ugly
		 */
		User sender = (User) shareEntry.getEntryOwner();
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url, firstName, lastName, mimeType, fileName, recipient; // ugly
		Language locale;
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
				.add("fileName", fileName)
				.add("fileSize", DocumentUtils.humanReadableByteCount(size, true))
				.add("fileOldName", oldDocName)
				.add("mimeType", mimeType)
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(recipient);
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(sender.getMail());

		return buildMailContainer(cfg, container, null,
				MailContentType.SHARED_DOC_UPDATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildSharedDocDeleted(Account actor,
			Entry shareEntry) throws BusinessException {
		/*
		 * XXX ugly
		 */
		User sender = (User) shareEntry.getEntryOwner();
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String firstName, lastName, fileName, recipient; // ugly
		Language locale;
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
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(sender.getMail());
		return buildMailContainer(cfg, container, null,
				MailContentType.SHARED_DOC_DELETED, builder);
	}

	@Override
	public MailContainerWithRecipient buildSharedDocUpcomingOutdated(
			Entry shareEntry, Integer days) throws BusinessException {
		/*
		 * XXX ugly
		 */
		User sender = (User) shareEntry.getEntryOwner();
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url, firstName, lastName, fileName, recipient; // ugly
		Language locale;
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
				.add("documentName", fileName)
				.add("nbDays", days.toString())
				.add("url", url)
				.add("urlparam", "");
		container.setRecipient(recipient);
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(sender.getMail());

		return buildMailContainer(cfg, container, null,
				MailContentType.SHARED_DOC_UPCOMING_OUTDATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildDocUpcomingOutdated(
			DocumentEntry document, Integer days) throws BusinessException {
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
		container.setFrom(getFromMailAddress(owner));

		return buildMailContainer(cfg, container, null,
				MailContentType.DOC_UPCOMING_OUTDATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildNewSharing(User sender,
			MailContainer input, User recipient,
			Set<ShareEntry> shares) throws BusinessException {
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url = getLinShareUrlForAUserRecipient(recipient);

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				recipient.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		StringBuffer names = new StringBuffer();
		long shareSize = 0;
		for (ShareEntry share : shares) {
			if (recipient.getLsUuid().equals(share.getRecipient().getLsUuid())) {
				shareSize += 1;
				names.append("<li><a href='"
						+ getDirectDownloadLink(recipient, share) + "'>"
						+ share.getName() + "</a></li>");
			}
		}

		builder.getSubjectChain()
				.add("actorSubject", input.getSubject())
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
		container.setSubject(input.getSubject());
		container.setRecipient(recipient);
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(sender.getMail());

		return buildMailContainer(cfg, container,
				input.getPersonalMessage(), MailContentType.NEW_SHARING,
				builder);
	}

	private MailContainerWithRecipient buildNewAnonymousSharing(User sender, MailContainer input,
			AnonymousUrl anonUrl, MailContentType mailContentType) throws BusinessException {
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
				.add("actorSubject", input.getSubject())
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
				.add("jwsEncryptUrl", getJwsEncryptUrlString(getLinShareUrlForAContactRecipient(sender)))
				.add("url", url)
				.add("urlparam", "");
		container.setSubject(input.getSubject());
		container.setRecipient(email);
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(sender.getMail());

		return buildMailContainer(cfg, container, input.getPersonalMessage(),
				mailContentType, builder);
	}

	@Override
	public MailContainerWithRecipient buildNewSharing(User sender,
			MailContainer input, AnonymousUrl anonUrl) throws BusinessException {
		return buildNewAnonymousSharing(sender, input, anonUrl,
				MailContentType.NEW_SHARING);
	}

	@Override
	public MailContainerWithRecipient buildNewSharingProtected(User sender,
			MailContainer input, AnonymousUrl anonUrl) throws BusinessException {
		return buildNewAnonymousSharing(sender, input, anonUrl,
				MailContentType.NEW_SHARING_PROTECTED);
	}

	@Override
	public MailContainerWithRecipient buildNewSharingCyphered(
			User sender, MailContainer input, AnonymousUrl anonUrl)
			throws BusinessException {
		return buildNewAnonymousSharing(sender, input, anonUrl,
				MailContentType.NEW_SHARING_CYPHERED);
	}

	@Override
	public MailContainerWithRecipient buildNewSharingCypheredProtected(
			User sender, MailContainer input, AnonymousUrl anonUrl)
			throws BusinessException {
		return buildNewAnonymousSharing(sender, input, anonUrl,
				MailContentType.NEW_SHARING_CYPHERED_PROTECTED);
	}

	@Override
	public MailContainerWithRecipient buildNewUploadRequestEntryUrl(
			User owner, UploadRequestUrl request, UploadRequestEntryUrl uREUrl)
			throws BusinessException {

		String url = uREUrl
				.getFullUrl(getLinShareUrlForAContactRecipient(owner));
		String actorRepresentation = request.getContact().getMail();
		DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, Locale.forLanguageTag(owner.getExternalMailLocale().getTapestryLocale()));
		String expiryDate = df.format(uREUrl.getExpiryDate());
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				owner.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();
		builder.getSubjectChain()
				.add("actorSubject", container.getSubject())
				.add("actorRepresentation", actorRepresentation);
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("firstName", "")
				.add("lastName", actorRepresentation)
				.add("number", "" + 1)
				.add("documentNames", "<li>" + uREUrl.getUploadRequestEntry().getName() + "</li>")
				.add("expiryDate", expiryDate)
				.add("password", uREUrl.getTemporaryPlainTextPassword())
				.add("jwsEncryptUrl",
						getJwsEncryptUrlString(getLinShareUrlForAContactRecipient(owner)))
				.add("url", url).add("urlparam", "");
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(actorRepresentation);

		return buildMailContainer(cfg, container, null,
				MailContentType.UPLOAD_REQUEST_ENTRY_URL, builder);
	}

	@Override
	public MailContainerWithRecipient buildNewSharingCyphered(User sender,
			MailContainer input, User recipient,
			Set<ShareEntry> shares) throws BusinessException {
		String actorRepresentation = new ContactRepresentation(sender)
				.getContactRepresentation();
		String url = getLinShareUrlForAUserRecipient(recipient);

		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				recipient.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		StringBuffer names = new StringBuffer();
		long shareSize = 0;
		for (ShareEntry share : shares) {
			if (recipient.getLsUuid().equals(share.getRecipient().getLsUuid())) {
				shareSize += 1;
				names.append("<li><a href='"
						+ getDirectDownloadLink(recipient, share) + "'>"
						+ share.getName() + "</a></li>");
			}
		}

		builder.getSubjectChain()
				.add("actorSubject", input.getSubject())
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
		container.setSubject(input.getSubject());
		container.setRecipient(recipient);
		container.setFrom(getFromMailAddress(sender));
		container.setReplyTo(sender.getMail());

		return buildMailContainer(cfg, container,
				input.getPersonalMessage(),
				MailContentType.NEW_SHARING_CYPHERED, builder);
	}

	@Override
	public MailContainerWithRecipient buildCreateUploadProposition(User recipient, UploadProposition proposition)
			throws BusinessException {
		MailConfig cfg = recipient.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				recipient.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", proposition.getMail())
				.add("subject", proposition.getSubject());
		builder.getGreetingsChain()
				.add("firstName", recipient.getFirstName())
				.add("lastName", recipient.getLastName());
		builder.getBodyChain()
				.add("subject", proposition.getSubject())
				.add("body", proposition.getBody())
				.add("firstName", proposition.getFirstName())
				.add("lastName", proposition.getLastName())
				.add("mail", proposition.getMail())
				.add("uploadPropositionUrl", getUploadPropositionUrl(recipient));
		container.setRecipient(recipient.getMail());
		container.setFrom(getFromMailAddress(recipient));
		container.setFrom(abstractDomainService.getDomainMail(recipient.getDomain()));

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_PROPOSITION_CREATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildRejectUploadProposition(User sender, UploadProposition proposition)
			throws BusinessException {
		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				sender.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(sender).getContactRepresentation())
				.add("subject", proposition.getSubject());
		builder.getGreetingsChain()
				.add("firstName", proposition.getFirstName())
				.add("lastName", proposition.getLastName());
		builder.getBodyChain()
				.add("subject", proposition.getSubject())
				.add("body", proposition.getBody())
				.add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName())
				.add("mail", proposition.getMail());

		container.setRecipient(proposition.getMail());
		container.setFrom(getFromMailAddress(sender));

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_PROPOSITION_REJECTED, builder);
	}

	// Update
	@Override
	public MailContainerWithRecipient buildUpdateUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody());
		container.setRecipient(request.getContact().getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_UPDATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildActivateUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		String contact = request.getContact().getMail();
		builder.getGreetingsChain()
				.add("firstName", contact)
				.add("lastName", "");
		//  Why first name and last name ?
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("url", request.getFullUrl(getLinShareUploadRequestUrl(owner)))
				.add("expirationDate", formatExpirationDate(owner, request.getUploadRequest()))
				.add("ownerFirstName", owner.getFirstName())
				.add("ownerLastName", owner.getLastName())
				.add("ownerMail", owner.getMail())
				.add("maxFileCount", request.getUploadRequest().getMaxFileCount().toString())
				.add("password", request.getTemporaryPlainTextPassword());
		container.setRecipient(contact);
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);
		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_ACTIVATED, builder);
	}

	private String getFromMailAddress(User owner) {
		return abstractDomainService.getDomainMail(owner.getDomain());
	}

	@Override
	public MailContainerWithRecipient buildFilterUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody());
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_AUTO_FILTER, builder);
	}

	@Override
	public MailContainerWithRecipient buildCreateUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("expirationDate", formatExpirationDate(owner, request.getUploadRequest()))
				.add("ownerFirstName", owner.getFirstName())
				.add("ownerLastName", owner.getLastName())
				.add("ownerMail", owner.getMail())
				.add("maxFileCount", request.getUploadRequest().getMaxFileCount().toString())
				.add("activationDate", formatActivationDate(owner, request.getUploadRequest()));
		container.setRecipient(request.getContact().getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null,
				MailContentType.UPLOAD_REQUEST_CREATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildAckUploadRequest(User owner, UploadRequestUrl request, UploadRequestEntry entry)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		String contact = request.getContact().getMail();
		builder.getSubjectChain()
				.add("actorRepresentation", contact)
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("firstName", contact)
				.add("lastName", "")
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("fileSize", DocumentUtils.humanReadableByteCount(entry.getDocumentEntry().getSize(), true))
				.add("fileName", entry.getDocumentEntry().getName())
				.add("depositDate", formatCreationDate(owner, entry));
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(contact);
		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_ACKNOWLEDGMENT, builder);
	}

	@Override
	public MailContainerWithRecipient buildAckDeleteFileUploadRequest(User owner, UploadRequestUrl request, UploadRequestEntry entry)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		String contact = request.getContact().getMail();
		builder.getSubjectChain()
				.add("actorRepresentation", contact)
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("firstName", contact)
				.add("lastName", "")
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("fileSize", DocumentUtils.humanReadableByteCount(entry.getSize(), true))
				.add("fileName", entry.getName())
				.add("deleteDate", formatDeletionDate(owner));
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(contact);
		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_FILE_DELETED_BY_SENDER, builder);
	}

	@Override
	public MailContainerWithRecipient buildRemindUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("url", request.getFullUrl(getLinShareUploadRequestUrl(owner)))
				.add("password", request.getTemporaryPlainTextPassword());
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_REMINDER, builder);
	}

	@Override
	public MailContainerWithRecipient buildUploadRequestBeforeExpiryWarnOwner(User owner, UploadRequest request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("subject", request.getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("subject", request.getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequestGroup().getBody())
				.add("expirationDate", formatExpirationDate(owner, request))
				.add("creationDate", formatCreationDate(owner, request))
				.add("files", getFileNames(request));
		for (UploadRequestUrl uru : request.getUploadRequestURLs()) {
			builder.getBodyChain().add(
					"recipientMail",
					uru.getContact().getMail()
			);
		}
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);
		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_WARN_OWNER_BEFORE_EXPIRY, builder);
	}

	@Override
	public MailContainerWithRecipient buildUploadRequestBeforeExpiryWarnRecipient(User owner, UploadRequestUrl request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();
		builder.getSubjectChain()
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("files", getFileNames(request.getUploadRequest()))
				.add("ownerFirstName",owner.getFirstName())
				.add("ownerLastName",owner.getLastName())
				.add("ownerMail",owner.getMail())
				.add("ownerRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("expirationDate", formatExpirationDate(owner, request.getUploadRequest()))
				.add("creationDate", formatCreationDate(owner, request.getUploadRequest()))
				.add("url", request.getFullUrl(getLinShareUploadRequestUrl(owner)));
		container.setRecipient(request.getContact().getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);
		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_WARN_RECIPIENT_BEFORE_EXPIRY, builder);
	}

	@Override
	public MailContainerWithRecipient buildUploadRequestExpiryWarnOwner(User owner, UploadRequest request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("subject", request.getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("subject", request.getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequestGroup().getBody())
				.add("expirationDate", formatExpirationDate(owner, request))
				.add("creationDate", formatCreationDate(owner, request))
				.add("files", getFileNames(request));
		for (UploadRequestUrl uru : request.getUploadRequestURLs()) {
			builder.getBodyChain().add(
					"recipientMail",
					uru.getContact().getMail()
			);
		}
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_WARN_OWNER_EXPIRY, builder);
	}

	@Override
	public MailContainerWithRecipient buildUploadRequestExpiryWarnRecipient(User owner, UploadRequestUrl request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("files", getFileNames(request.getUploadRequest()))
				.add("ownerFirstName",owner.getFirstName())
				.add("ownerLastName",owner.getLastName())
				.add("ownerMail",owner.getMail())
				.add("ownerRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("expirationDate", formatExpirationDate(owner, request.getUploadRequest()))
				.add("creationDate", formatCreationDate(owner, request.getUploadRequest()))
				.add("url", request.getFullUrl(getLinShareUploadRequestUrl(owner)));
		container.setRecipient(request.getContact().getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_WARN_RECIPIENT_EXPIRY, builder);
	}

	@Override
	public MailContainerWithRecipient buildCloseUploadRequestByRecipient(User owner, UploadRequestUrl request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", request.getContact().getMail())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "")
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("files", getFileNames(request.getUploadRequest()));
		container.setRecipient(owner);
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(request.getContact());

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_CLOSED_BY_RECIPIENT, builder);
	}

	private String getFileNames(
			UploadRequest request) {
		ImmutableSet<FileRepresentation> files = FluentIterable
				.from(request.getUploadRequestEntries())
				.transform(new Function<UploadRequestEntry, FileRepresentation>() {
					@Override
					public FileRepresentation apply(UploadRequestEntry ure) {
						return new FileRepresentation(ure);
					}})
					.toSet();
		if (files.size() > 0) {
			return files.toString();
		}
		return " - ";
	}

	// TODO : to be use.
	@Override
	public MailContainerWithRecipient buildCloseUploadRequestByOwner(User owner, UploadRequestUrl request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody())
				.add("files", getFileNames(request.getUploadRequest()));
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_CLOSED_BY_OWNER, builder);
	}

	@Override
	public MailContainerWithRecipient buildDeleteUploadRequestByOwner(User owner, UploadRequestUrl request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", new ContactRepresentation(owner).getContactRepresentation())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "");
		builder.getBodyChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody());
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_DELETED_BY_OWNER, builder);
	}

	// TODO : to be use
	@Override
	public MailContainerWithRecipient buildErrorUploadRequestNoSpaceLeft(User owner, UploadRequestUrl request)
			throws BusinessException {
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				request.getLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		builder.getSubjectChain()
				.add("actorRepresentation", request.getContact().getMail())
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject());
		builder.getGreetingsChain()
				.add("firstName", owner.getFirstName())
				.add("lastName", owner.getLastName());
		builder.getBodyChain()
				.add("firstName", request.getContact().getMail())
				.add("lastName", "")
				.add("subject", request.getUploadRequest().getUploadRequestGroup().getSubject())
				.add("body", request.getUploadRequest().getUploadRequestGroup().getBody());
		container.setRecipient(owner.getMail());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(request.getContact());

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_NO_SPACE_LEFT, builder);
	}

	/*
	 * Helpers
	 */

	private String getLinShareUploadRequestUrl(Account sender) {
		return functionalityReadOnlyService
				.getUploadRequestFunctionality(sender.getDomain())
				.getValue();
	}

	private String getUploadPropositionUrl(Account recipient) {
		String baseUrl = getLinShareUrlForAUserRecipient(recipient);
		StringBuffer uploadPropositionUrl = new StringBuffer();
		uploadPropositionUrl.append(baseUrl);
		if (!baseUrl.endsWith("/")) {
			uploadPropositionUrl.append('/');
		}
		uploadPropositionUrl.append("uploadrequest/proposition");
		return uploadPropositionUrl.toString();
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

	private String getDirectDownloadLink(User recipient, ShareEntry share) {
		String path = getLinShareUrlForAUserRecipient(recipient);
		String sep = path.endsWith("/") ? "" : "/";
		String dl = path + sep + "index.listshareddocument.download/";
		return dl + share.getUuid();
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
	private String formatSubjectTemplate(String subject, MailContent mailContent) {
		if (StringUtils.isBlank(subject) || mailContent.isEnableAS() == false)
			return mailContent.getSubject();
		return mailContent.getAlternativeSubject();
	}

	private String formatPersonalMessage(String pm, Language lang) {
		if (StringUtils.isBlank(pm))
			return "";
		return "<p>" + pm.replace("\n", "<br/>") + "</p><hr/><br/>";
	}

	private String formatFooter(String footer, Language lang) {
        if (insertLicenceTerm) {
    		if (lang.equals(Language.FRENCH)) {
				footer += "<br/>Vous utilisez la version libre et gratuite de <a href=\"http://www.linshare.org/\" title=\"LinShare\"><strong>LinShare</strong></a>™, développée par Linagora © 2009–2015. Contribuez à la R&D du produit en souscrivant à une offre entreprise.<br/>";
			} else {
				footer += "<br/>You are using the Open Source and free version of <a href=\"http://www.linshare.org/\" title=\"LinShare\"><strong>LinShare</strong></a>™, powered by Linagora © 2009–2015. Contribute to Linshare R&D by subscribing to an Enterprise offer.<br/>";
    		}
        }
        return footer;
	}

	private MailContainerWithRecipient buildMailContainer(MailConfig cfg,
			final MailContainerWithRecipient input, String pm,
			MailContentType type, MailContainerBuilder builder)
			throws BusinessException {
		Language lang = input.getLanguage();
		MailContainerWithRecipient container = new MailContainerWithRecipient(
				input);
		MailContent mailContent = cfg.findContent(lang, type);
		String subjectTemplate = formatSubjectTemplate(input.getSubject(), mailContent);
		String greetings = mailContent.getGreetings();
		String body = mailContent.getBody();
		MailFooter f = cfg.findFooter(lang);
		String footer = formatFooter(f.getFooter(), lang);
		String layout = cfg.getMailLayoutHtml().getLayout();

		logger.info("Building mail content: " + type);
		pm = formatPersonalMessage(pm, lang);
		String subject = builder.getSubjectChain().build(subjectTemplate);
		greetings = builder.getGreetingsChain().build(greetings);
		body = builder.getBodyChain().build(body);
		footer = builder.getFooterChain().build(footer);
		layout = builder.getLayoutChain()
				.add("image", displayLogo ? LINSHARE_LOGO : "")
				.add("personalMessage", pm)
				.add("greetings", greetings)
				.add("body", body)
				.add("footer", footer)
				.add("mailSubject", subject)
				.build(layout);

		container.setSubject(subject);
		container.setContentHTML(layout);
		container.setContentTXT(container.getContentHTML());
		// Message IDs from Web service API (ex Plugin Thunderbird)
		container.setInReplyTo(input.getInReplyTo());
		container.setReferences(input.getReferences());
		return container;
	}

	@Override
	public MailContainerWithRecipient buildNewSharingPersonnalNotification(
			User sender, ShareContainer container, Set<Entry> entries) throws BusinessException {
		MailConfig cfg = sender.getDomain().getCurrentMailConfiguration();
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(
				sender.getExternalMailLocale());
		MailContainerBuilder builder = new MailContainerBuilder();

		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		StringBuffer creationDate = new StringBuffer();
		StringBuffer expirationDate = new StringBuffer();
		creationDate.append(formater.format(entries.iterator().next().getCreationDate().getTime()));
		expirationDate.append(formater.format(entries.iterator().next().getExpirationDate().getTime()));

		long count = 0;
		StringBuffer docNames = new StringBuffer();
		for (DocumentEntry entry : container.getDocuments()) {
			StringBuffer fileUrl = new StringBuffer();
			fileUrl.append(getLinShareUrlForAUserRecipient(sender)
					+ "files/index.listdocument.download/" + entry.getUuid());
			docNames.append("<li><a href='" + fileUrl + "'>" + entry.getName()
					+ "</a></li>");
			count++;
		}

		StringBuffer recipientNames = new StringBuffer();
		for (User rec : container.getShareRecipients()) {
			recipientNames.append("<li>" + rec.getFullName()
					+ "</li>");
		}

		for (Recipient recipient : container.getAnonymousShareRecipients()) {
			recipientNames.append("<li>" + recipient.getMail() + "</li>");
		}

		builder.getSubjectChain().add("subject", container.getSubject());
		builder.getSubjectChain().add("date", creationDate.toString());
		builder.getGreetingsChain().add("firstName", sender.getFirstName())
				.add("lastName", sender.getLastName());
		builder.getBodyChain()
				.add("message", container.getMessage())
				.add("documentNames", docNames.toString())
				.add("creationDate", creationDate.toString())
				.add("expirationDate", expirationDate.toString())
//				FIX: 1.9.0
				.add("expirationdate", expirationDate.toString())
				.add("fileNumber", "" + count)
				.add("recipientNames", recipientNames.toString());
		mailContainer.setSubject(container.getSubject());
		mailContainer.setRecipient(sender);
		mailContainer.setFrom(getFromMailAddress(sender));
		mailContainer.setReplyTo(sender.getMail());

		if (container.getMessage() == null) {
			return buildMailContainer(cfg, mailContainer,
					null, MailContentType.SHARE_CREATION_ACKNOWLEDGMENT_FOR_OWNER,
					builder);
		} else {
			return buildMailContainer(cfg, mailContainer,
					null, MailContentType.SHARE_CREATION_ACKNOWLEDGMENT_WITH_SPECIAL_MESSAGE_FOR_OWNER,
					builder);
		}
	}
}
