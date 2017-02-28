/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.core.notifications.service.impl;

import java.text.DateFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.MailActivationBusinessService;
import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailActivationType;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.MailActivation;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.UploadProposition;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.config.LinShareStringTemplateResolver;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.dto.ContextMetadata;
import org.linagora.linshare.core.notifications.emails.impl.EmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.FileWarnOwnerBeforeExpiryEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.GuestAccountNewCreationEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.GuestAccountResetPasswordEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareFileDownloadEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareFileShareDeletedEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareNewShareAcknowledgementEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareNewShareEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareWarnRecipientBeforeExpiryEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareWarnUndownloadedFilesharesEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestClosedByRecipientEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestDeleteFileEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestUnavailableSpaceEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestUploadedFileEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestWarnBeforeExpiryEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestWarnExpiryEmailBuilder;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class MailBuildingServiceImpl implements MailBuildingService {

	private final static Logger logger = LoggerFactory
			.getLogger(MailBuildingServiceImpl.class);

	private final TemplateEngine templateEngine;

	private final Map<MailContentType, EmailBuilder> emailBuilders;

	private final DomainBusinessService domainBusinessService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final MailActivationBusinessService mailActivationBusinessService;

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

		public ContactRepresentation(User user) {
			this.mail = StringUtils.trimToNull(user.getMail());
			this.firstName = StringUtils.trimToNull(user.getFirstName());
			this.lastName = StringUtils.trimToNull(user.getLastName());
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
		}

		private KeyValueChain subjectChain;
		private KeyValueChain greetingsChain;
		private KeyValueChain bodyChain;

		public MailContainerBuilder() {
			super();
			subjectChain = new KeyValueChain();
			greetingsChain = new KeyValueChain();
			bodyChain = new KeyValueChain();
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
	}

	/**
	 * Constructor
	 */
	public MailBuildingServiceImpl(
			final MailConfigBusinessService mailConfigBusinessService,
			final DomainBusinessService domainBusinessService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final MailActivationBusinessService mailActivationBusinessService,
			boolean insertLicenceTerm,
			String urlTemplateForReceivedShares,
			String urlTemplateForDocuments,
			String urlTemplateForGuestReset,
			String urlTemplateForAnonymousUrl,
			boolean templatingStrictMode,
			boolean templatingSubjectPrefix
			) throws Exception {
		this.domainBusinessService = domainBusinessService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mailActivationBusinessService = mailActivationBusinessService;
		this.templateEngine = new TemplateEngine();
		LinShareStringTemplateResolver templateResolver = new LinShareStringTemplateResolver(insertLicenceTerm, templatingSubjectPrefix);
		if (templatingStrictMode) {
			templateResolver.setTemplateMode(TemplateMode.XML);
		}
		templateEngine.setTemplateResolver(templateResolver);

		emailBuilders = Maps.newHashMap();
		emailBuilders.put(MailContentType.SHARE_NEW_SHARE_FOR_RECIPIENT, new ShareNewShareEmailBuilder());

		GuestAccountNewCreationEmailBuilder newGuestBuilder = new GuestAccountNewCreationEmailBuilder();
		newGuestBuilder.setUrlTemplateForGuestReset(urlTemplateForGuestReset);
		emailBuilders.put(MailContentType.GUEST_ACCOUNT_NEW_CREATION, newGuestBuilder);

		emailBuilders.put(MailContentType.SHARE_FILE_DOWNLOAD, new ShareFileDownloadEmailBuilder());

		GuestAccountResetPasswordEmailBuilder resetGuestBuilder = new GuestAccountResetPasswordEmailBuilder();
		resetGuestBuilder.setUrlTemplateForGuestReset(urlTemplateForGuestReset);
		emailBuilders.put(MailContentType.GUEST_ACCOUNT_RESET_PASSWORD_LINK, resetGuestBuilder);

		emailBuilders.put(MailContentType.SHARE_NEW_SHARE_ACKNOWLEDGEMENT_FOR_SENDER,
				new ShareNewShareAcknowledgementEmailBuilder());
		emailBuilders.put(MailContentType.SHARE_FILE_SHARE_DELETED, new ShareFileShareDeletedEmailBuilder());
		emailBuilders.put(MailContentType.SHARE_WARN_RECIPIENT_BEFORE_EXPIRY, new ShareWarnRecipientBeforeExpiryEmailBuilder());
		emailBuilders.put(MailContentType.SHARE_WARN_UNDOWNLOADED_FILESHARES, new ShareWarnUndownloadedFilesharesEmailBuilder());
		emailBuilders.put(MailContentType.FILE_WARN_OWNER_BEFORE_FILE_EXPIRY, new FileWarnOwnerBeforeExpiryEmailBuilder());

		emailBuilders.put(MailContentType.UPLOAD_REQUEST_UPLOADED_FILE, new UploadRequestUploadedFileEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_WARN_EXPIRY, new UploadRequestWarnExpiryEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_WARN_BEFORE_EXPIRY, new UploadRequestWarnBeforeExpiryEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_CLOSED_BY_RECIPIENT, new UploadRequestClosedByRecipientEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_FILE_DELETED_BY_RECIPIENT, new UploadRequestDeleteFileEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_UNAVAILABLE_SPACE, new UploadRequestUnavailableSpaceEmailBuilder());


		initMailBuilders(insertLicenceTerm, domainBusinessService, functionalityReadOnlyService, mailActivationBusinessService, urlTemplateForReceivedShares, urlTemplateForDocuments, urlTemplateForAnonymousUrl);
		Set<MailContentType> keySet = emailBuilders.keySet();
		logger.debug("mail content loaded : size : {}", keySet.size());
		for (MailContentType mailContentType : keySet) {
			logger.debug(" mailContentType : {}", mailContentType );
		}
		logger.debug("end");
	}

	private void initMailBuilders(boolean insertLicenceTerm,
		DomainBusinessService domainBusinessService,
		FunctionalityReadOnlyService functionalityReadOnlyService,
		MailActivationBusinessService mailActivationBusinessService,
		String urlTemplateForReceivedShares,
		String urlTemplateForDocuments,
		String urlTemplateForAnonymousUrl
	) {
		Collection<EmailBuilder> values = emailBuilders.values();
		for (EmailBuilder emailBuilder : values) {
			emailBuilder.setTemplateEngine(templateEngine);
			emailBuilder.setInsertLicenceTerm(insertLicenceTerm);
			emailBuilder.setMailActivationBusinessService(mailActivationBusinessService);
			emailBuilder.setFunctionalityReadOnlyService(functionalityReadOnlyService);
			emailBuilder.setDomainBusinessService(domainBusinessService);
			emailBuilder.setUrlTemplateForDocuments(urlTemplateForDocuments);
			emailBuilder.setUrlTemplateForReceivedShares(urlTemplateForReceivedShares);
			emailBuilder.setUrlTemplateForAnonymousUrl(urlTemplateForAnonymousUrl);
		}
	}

	@Override
	public MailContainerWithRecipient build(EmailContext context) throws BusinessException {
		Validate.notNull(context, "Email context can't be null");
		MailContentType type = context.getType();
		EmailBuilder builder = emailBuilders.get(type);
		Validate.notNull(builder, "Missing email builder!");
		return builder.build(context);
	}

	@Override
	public boolean fakeBuildIsSupported(MailContentType type) throws BusinessException {
		Validate.notNull(type, "MailContentType can't be null");
		EmailBuilder builder = emailBuilders.get(type);
		if (builder == null) {
			return false;
		}
		return true;
	}

	@Override
	public MailContainerWithRecipient fakeBuild(MailContentType type, MailConfig cfg, Language language, Integer flavor) throws BusinessException {
		Validate.notNull(type, "MailContentType can't be null");
		if (cfg == null) {
			cfg = this.domainBusinessService.getUniqueRootDomain().getCurrentMailConfiguration();
		}
		if (language == null) {
			language = Language.ENGLISH;
		}
		EmailBuilder builder = emailBuilders.get(type);
		cfg.findContent(language, type);
		return builder.fakeBuild(cfg, language, flavor);
	}

	@Override
	public List<ContextMetadata> getAvailableVariables(MailContentType type) {
		Validate.notNull(type, "MailContentType can't be null");
		EmailBuilder builder = emailBuilders.get(type);
		if (builder == null) {
			throw new BusinessException(BusinessErrorCode.TEMPLATE_MISSING_TEMPLATE_BUILDER, "Missing template builder");
		}
		return builder.getAvailableVariables();
	}

	/**
	 * Old and ugly code, to be removed.
	 */

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
	public MailContainerWithRecipient buildCreateUploadProposition(User recipient, UploadProposition proposition)
			throws BusinessException {
//		if (isDisable(recipient, MailActivationType.UPLOAD_PROPOSITION_CREATED)) {
		if (isDisable(recipient, null)) {
			return null;
		}
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

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_PROPOSITION_CREATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildRejectUploadProposition(User sender, UploadProposition proposition)
			throws BusinessException {
		// MailActivationType.UPLOAD_PROPOSITION_REJECTED
		if (isDisable(sender, null)) {
			return null;
		}
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
		// MailActivationType.UPLOAD_REQUEST_UPDATED
		if (isDisable(request.getContact(), owner, null)) {
			return null;
		}
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

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_UPDATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildActivateUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		// MailActivationType.UPLOAD_REQUEST_ACTIVATED
		if (isDisable(request.getContact(), owner, null)) {
			return null;
		}
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
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);
		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_ACTIVATED, builder);
	}

	private String getFromMailAddress(User owner) {
		String fromMail = functionalityReadOnlyService
				.getDomainMailFunctionality(owner.getDomain()).getValue();
		return fromMail;
	}

	// TODO : to be used ?
	@Override
	public MailContainerWithRecipient buildFilterUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		// MailActivationType.UPLOAD_REQUEST_AUTO_FILTER
		if (isDisable(request.getContact(), owner, null)) {
			return null;
		}
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
		// MailActivationType.UPLOAD_REQUEST_CREATED
		if (isDisable(request.getContact(), owner, null)) {
			return null;
		}
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
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null,
				MailContentType.UPLOAD_REQUEST_CREATED, builder);
	}

	@Override
	public MailContainerWithRecipient buildRemindUploadRequest(User owner, UploadRequestUrl request)
			throws BusinessException {
		// MailActivationType.UPLOAD_REQUEST_REMINDER
		if (isDisable(request.getContact(), owner, null)) {
			return null;
		}
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

	private String getFileNames(
			UploadRequestUrl requestUrl) {
		ImmutableSet<FileRepresentation> files = FluentIterable
				.from(requestUrl.getUploadRequestEntries())
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
		// MailActivationType.UPLOAD_REQUEST_CLOSED_BY_OWNER
		if (isDisable(request.getContact(), owner, null)) {
			return null;
		}
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
				.add("files", getFileNames(request));
		container.setRecipient(request.getContact());
		container.setFrom(getFromMailAddress(owner));
		container.setReplyTo(owner);

		return buildMailContainer(cfg, container, null, MailContentType.UPLOAD_REQUEST_CLOSED_BY_OWNER, builder);
	}

	@Override
	public MailContainerWithRecipient buildDeleteUploadRequestByOwner(User owner, UploadRequestUrl request)
			throws BusinessException {
		// MailActivationType.UPLOAD_REQUEST_DELETED_BY_OWNER
		if (isDisable(request.getContact(), owner, null)) {
			return null;
		}
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
		String value = functionalityReadOnlyService
				.getCustomNotificationUrlFunctionality(recipient.getDomain())
				.getValue();
		if (!value.endsWith("/")) {
			return value + "/";
		}
		return value;
	}

	/*
	 * MAIL CONTAINER BUILDER SECTION
	 */



	

	private MailContainerWithRecipient buildMailContainer(MailConfig cfg,
			final MailContainerWithRecipient input, String pm,
			MailContentType type, MailContainerBuilder builder)
			throws BusinessException {
		MailContainerWithRecipient container = new MailContainerWithRecipient(input);
		String layout = cfg.getMailLayoutHtml().getLayout();
		container.setContent(layout);
		// Message IDs from Web service API (ex Plugin Thunderbird)
		container.setInReplyTo(input.getInReplyTo());
		container.setReferences(input.getReferences());
		return container;
	}

	@SuppressWarnings("unused")
	private boolean isDisable(Contact contact, Account sender, MailActivationType type) {
		// Disable old deprecated notifications !!
		if (true) {
			return true;
		}
		AbstractDomain recipientDomain = domainBusinessService.findGuestDomain(sender.getDomain());
		// guest domain could be inexistent into the database.
		if (recipientDomain == null) {
			recipientDomain = sender.getDomain();
		}
		MailActivation mailActivation = mailActivationBusinessService
				.findForInternalUsage(recipientDomain, type);
		boolean enable = mailActivation.isEnable();
		return !enable;
	}

	@SuppressWarnings("unused")
	private boolean isDisable(Account recipient, MailActivationType type) {
		// Disable old deprecated notifications !!
		if (true) {
			return true;
		}
		MailActivation mailActivation = mailActivationBusinessService
				.findForInternalUsage(recipient.getDomain(), type);
		boolean enable = mailActivation.isEnable();
		return !enable;
	}

}
