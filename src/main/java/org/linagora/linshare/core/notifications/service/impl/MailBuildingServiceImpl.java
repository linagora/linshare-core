/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.notifications.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.MailActivationBusinessService;
import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.config.LinShareStringTemplateResolver;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.dto.ContextMetadata;
import org.linagora.linshare.core.notifications.emails.impl.WorkSpaceDeletedtWarnEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.WorkSpaceWarnDeletedMemberEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.WorkSpaceWarnNewMemberEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.WorkSpaceWarnUpdatedMemberEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.EmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.FileWarnOwnerBeforeExpiryEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.GuestAccountNewCreationEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.GuestAccountResetPasswordEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.GuestAccountResetPasswordFor4_0_EmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.GuestWarnGuestAboutHisPasswordResetEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.JwtPermanentCreatedEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.JwtPermanentDeletedEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareAnonymousResetPasswordEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareFileDownloadEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareFileShareDeletedEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareNewShareAcknowledgementEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareNewShareEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareWarnRecipientAboutExpiredSahreEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareWarnRecipientBeforeExpiryEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareWarnSenderAboutShareExpirationEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.ShareWarnUndownloadedFilesharesEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestActivationForOwnerEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestActivationForRecipientEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestCloseByOwnerEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestClosedByRecipientEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestCreatedEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestDeleteFileEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestFileDeletedByOwnerEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestPasswordRenewalEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestRecipientRemovedEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestReminderEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestUnavailableSpaceEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestUpdatedSettingsEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestUploadedFileEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestWarnBeforeExpiryEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.UploadRequestWarnExpiryEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.WarnOwnerAboutGuestExpirationEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.WorkGroupDeletedtWarnEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.WorkGroupWarnDeletedMemberEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.WorkGroupWarnNewMemberEmailBuilder;
import org.linagora.linshare.core.notifications.emails.impl.WorkGroupWarnUpdatedMemberEmailBuilder;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

import com.google.common.collect.Maps;

public class MailBuildingServiceImpl implements MailBuildingService {

	private final static Logger logger = LoggerFactory
			.getLogger(MailBuildingServiceImpl.class);

	private final TemplateEngine templateEngine;

	private final Map<MailContentType, EmailBuilder> emailBuilders;

	private final DomainBusinessService domainBusinessService;

	/**
	 * Constructor
	 */
	public MailBuildingServiceImpl(
			final MailConfigBusinessService mailConfigBusinessService,
			final DomainBusinessService domainBusinessService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final MailActivationBusinessService mailActivationBusinessService,
			FileDataStore fileDataStore,
			boolean insertLicenceTerm,
			String urlTemplateForReceivedShares,
			String urlTemplateForDocuments,
			String urlTemplateForGuestReset,
			String urlTemplateForAnonymousUrl,
			boolean templatingStrictMode,
			boolean templatingSubjectPrefix,
			String urlFragmentQueryParamFileUuid,
			String urlTemplateForWorkgroup,
			String urlTemplateForWorkSpace,
			String urlTemplateForUploadRequestEntries,
			String urlTemplateForUploadRequestUploadedFile,
			String urlTemplateForJwtToken
			) throws Exception {
		this.domainBusinessService = domainBusinessService;
		this.templateEngine = new TemplateEngine();
		LinShareStringTemplateResolver templateResolver = new LinShareStringTemplateResolver(insertLicenceTerm, templatingSubjectPrefix);
		if (templatingStrictMode) {
			templateResolver.setTemplateMode(TemplateMode.XML);
		}
		templateEngine.setTemplateResolver(templateResolver);

		emailBuilders = Maps.newHashMap();
		emailBuilders.put(MailContentType.SHARE_NEW_SHARE_FOR_RECIPIENT, new ShareNewShareEmailBuilder());
		emailBuilders.put(MailContentType.SHARE_ANONYMOUS_RESET_PASSWORD, new ShareAnonymousResetPasswordEmailBuilder());

		GuestAccountNewCreationEmailBuilder newGuestBuilder = new GuestAccountNewCreationEmailBuilder();
		newGuestBuilder.setUrlTemplateForGuestReset(urlTemplateForGuestReset);
		emailBuilders.put(MailContentType.GUEST_ACCOUNT_NEW_CREATION, newGuestBuilder);

		emailBuilders.put(MailContentType.SHARE_FILE_DOWNLOAD, new ShareFileDownloadEmailBuilder());

		emailBuilders.put(MailContentType.GUEST_ACCOUNT_RESET_PASSWORD_LINK, new GuestAccountResetPasswordEmailBuilder(urlTemplateForGuestReset));
		emailBuilders.put(MailContentType.GUEST_WARN_GUEST_ABOUT_HIS_PASSWORD_RESET, new GuestWarnGuestAboutHisPasswordResetEmailBuilder());

		emailBuilders.put(MailContentType.SHARE_NEW_SHARE_ACKNOWLEDGEMENT_FOR_SENDER,
				new ShareNewShareAcknowledgementEmailBuilder());
		emailBuilders.put(MailContentType.SHARE_FILE_SHARE_DELETED, new ShareFileShareDeletedEmailBuilder());
		emailBuilders.put(MailContentType.SHARE_WARN_RECIPIENT_BEFORE_EXPIRY, new ShareWarnRecipientBeforeExpiryEmailBuilder());
		emailBuilders.put(MailContentType.SHARE_WARN_UNDOWNLOADED_FILESHARES, new ShareWarnUndownloadedFilesharesEmailBuilder());
		emailBuilders.put(MailContentType.SHARE_WARN_SENDER_ABOUT_SHARE_EXPIRATION_WITHOUT_DOWNLOAD, new ShareWarnSenderAboutShareExpirationEmailBuilder());
		emailBuilders.put(MailContentType.SHARE_WARN_RECIPIENT_ABOUT_EXPIRED_SHARE, new ShareWarnRecipientAboutExpiredSahreEmailBuilder());

		emailBuilders.put(MailContentType.FILE_WARN_OWNER_BEFORE_FILE_EXPIRY, new FileWarnOwnerBeforeExpiryEmailBuilder());

		emailBuilders.put(MailContentType.UPLOAD_REQUEST_UPLOADED_FILE, new UploadRequestUploadedFileEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_WARN_EXPIRY, new UploadRequestWarnExpiryEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_WARN_BEFORE_EXPIRY, new UploadRequestWarnBeforeExpiryEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_CLOSED_BY_RECIPIENT, new UploadRequestClosedByRecipientEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_FILE_DELETED_BY_RECIPIENT, new UploadRequestDeleteFileEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_UNAVAILABLE_SPACE, new UploadRequestUnavailableSpaceEmailBuilder());

		emailBuilders.put(MailContentType.UPLOAD_REQUEST_ACTIVATED_FOR_RECIPIENT, new UploadRequestActivationForRecipientEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_ACTIVATED_FOR_OWNER, new UploadRequestActivationForOwnerEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_REMINDER, new UploadRequestReminderEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_PASSWORD_RENEWAL, new UploadRequestPasswordRenewalEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_CREATED, new UploadRequestCreatedEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_CLOSED_BY_OWNER, new UploadRequestCloseByOwnerEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_RECIPIENT_REMOVED, new UploadRequestRecipientRemovedEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_UPDATED_SETTINGS, new UploadRequestUpdatedSettingsEmailBuilder());
		emailBuilders.put(MailContentType.UPLOAD_REQUEST_FILE_DELETED_BY_OWNER, new UploadRequestFileDeletedByOwnerEmailBuilder());

		emailBuilders.put(MailContentType.GUEST_WARN_OWNER_ABOUT_GUEST_EXPIRATION, new WarnOwnerAboutGuestExpirationEmailBuilder());

		emailBuilders.put(MailContentType.WORKGROUP_WARN_NEW_MEMBER, new WorkGroupWarnNewMemberEmailBuilder());
		emailBuilders.put(MailContentType.WORKGROUP_WARN_UPDATED_MEMBER, new WorkGroupWarnUpdatedMemberEmailBuilder());
		emailBuilders.put(MailContentType.WORKGROUP_WARN_DELETED_MEMBER, new WorkGroupWarnDeletedMemberEmailBuilder());
		emailBuilders.put(MailContentType.WORKGROUP_WARN_DELETED_WORKGROUP, new WorkGroupDeletedtWarnEmailBuilder());

		emailBuilders.put(MailContentType.ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_CREATED, new JwtPermanentCreatedEmailBuilder());
		emailBuilders.put(MailContentType.ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_DELETED, new JwtPermanentDeletedEmailBuilder());

		emailBuilders.put(MailContentType.WORK_SPACE_WARN_NEW_MEMBER, new WorkSpaceWarnNewMemberEmailBuilder());
		emailBuilders.put(MailContentType.WORK_SPACE_WARN_UPDATED_MEMBER, new WorkSpaceWarnUpdatedMemberEmailBuilder());
		emailBuilders.put(MailContentType.WORK_SPACE_WARN_DELETED_MEMBER, new WorkSpaceWarnDeletedMemberEmailBuilder());
		emailBuilders.put(MailContentType.WORK_SPACE_WARN_DELETED, new WorkSpaceDeletedtWarnEmailBuilder());

		emailBuilders.put(MailContentType.GUEST_ACCOUNT_RESET_PASSWORD_FOR_4_0, new GuestAccountResetPasswordFor4_0_EmailBuilder(urlTemplateForGuestReset));

		initMailBuilders(insertLicenceTerm, domainBusinessService, functionalityReadOnlyService,
				mailActivationBusinessService, fileDataStore, urlTemplateForReceivedShares, urlTemplateForDocuments,
				urlTemplateForAnonymousUrl, urlFragmentQueryParamFileUuid, urlTemplateForWorkgroup, urlTemplateForWorkSpace, urlTemplateForUploadRequestEntries, urlTemplateForUploadRequestUploadedFile, urlTemplateForJwtToken);
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
		FileDataStore fileDataStore,
		String urlTemplateForReceivedShares,
		String urlTemplateForDocuments,
		String urlTemplateForAnonymousUrl,
		String paramFilesUuid,
		String urlTemplateForWorkgroup,
		String urlTemplateForWorkSpace,
		String urlTemplateForUploadRequestEntries,
		String urlTemplateForUploadRequestUploadedFile,
		String urlTemplateForJwtToken
	) {
		Collection<EmailBuilder> values = emailBuilders.values();
		for (EmailBuilder emailBuilder : values) {
			emailBuilder.setTemplateEngine(templateEngine);
			emailBuilder.setInsertLicenceTerm(insertLicenceTerm);
			emailBuilder.setMailActivationBusinessService(mailActivationBusinessService);
			emailBuilder.setFunctionalityReadOnlyService(functionalityReadOnlyService);
			emailBuilder.setDomainBusinessService(domainBusinessService);
			emailBuilder.setFileDataStore(fileDataStore);
			emailBuilder.setUrlTemplateForDocuments(urlTemplateForDocuments);
			emailBuilder.setUrlTemplateForReceivedShares(urlTemplateForReceivedShares);
			emailBuilder.setUrlTemplateForAnonymousUrl(urlTemplateForAnonymousUrl);
			emailBuilder.setUrlFragmentQueryParamFileUuid(paramFilesUuid);
			emailBuilder.setUrlTemplateForWorkgroup(urlTemplateForWorkgroup);
			emailBuilder.setUrlTemplateForWorkSpace(urlTemplateForWorkSpace);
			emailBuilder.setUrlTemplateForUploadRequestEntries(urlTemplateForUploadRequestEntries);
			emailBuilder.setUrlTemplateForUploadRequestUploadedFile(urlTemplateForUploadRequestUploadedFile);
			emailBuilder.setUrlTemplateForJwtToken(urlTemplateForJwtToken);
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
		MailContent content = cfg.findContent(language, type);
		Validate.notNull(content);
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

}
