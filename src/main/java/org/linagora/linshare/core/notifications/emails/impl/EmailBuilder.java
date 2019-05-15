/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.notifications.emails.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.MailActivationBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.MailActivation;
import org.linagora.linshare.core.domain.entities.MailAttachment;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.FakeBuildEmailContext;
import org.linagora.linshare.core.notifications.dto.ContextMetadata;
import org.linagora.linshare.core.notifications.dto.Document;
import org.linagora.linshare.core.notifications.dto.Share;
import org.linagora.linshare.core.notifications.dto.Variable;
import org.linagora.linshare.core.notifications.emails.IEmailBuilder;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.mongo.entities.SharedSpaceAccount;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templatemode.TemplateMode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;

public abstract class EmailBuilder implements IEmailBuilder {

	protected final static String CST_MAIL_SUBJECT_VAR_NAME = "mailSubject";

	protected final static String MAIL_DTO_PATH = "org.linagora.linshare.core.notifications.dto.";

	protected static List<String> supportedClass = null;

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected TemplateEngine templateEngine;

	protected boolean insertLicenceTerm;

	protected MailActivationBusinessService mailActivationBusinessService;

	protected FunctionalityReadOnlyService functionalityReadOnlyService;

	protected DomainBusinessService domainBusinessService;

	protected FileDataStore fileDataStore;

	protected String urlTemplateForReceivedShares;

	protected String urlTemplateForDocuments;

	protected String urlTemplateForAnonymousUrl;

	protected String fakeLinshareURL = "http://127.0.0.1/";

	protected abstract MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException;

	protected abstract List<Context> getContextForFakeBuild(Language language);

	protected String urlFragmentQueryParamFileUuid;

	protected String urlTemplateForWorkgroup;

	protected String urlTemplateForUploadRequestEntries;

	public EmailBuilder() {
		initSupportedTypes();
	}

	public EmailBuilder(TemplateEngine templateEngine, boolean insertLicenceTerm,
			MailActivationBusinessService mailActivationBusinessService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			DomainBusinessService domainBusinessService,
			FileDataStore fileDataStore) {
		super();
		this.templateEngine = templateEngine;
		this.insertLicenceTerm = insertLicenceTerm;
		this.mailActivationBusinessService = mailActivationBusinessService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.domainBusinessService = domainBusinessService;
		this.fileDataStore = fileDataStore;
		initSupportedTypes();
	}

	/*
	 * FIXME : it is a little bit ugly, but it does the job :)
	 */
	private void initSupportedTypes() {
		Date date_before = new Date();
		if (supportedClass == null) {
			supportedClass = Lists.newArrayList();
			final ClassLoader loader = Thread.currentThread().getContextClassLoader();
			try {
				for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
					if (info.getName().startsWith(MAIL_DTO_PATH)) {
						final Class<?> clazz = info.load();
						supportedClass.add(clazz.getCanonicalName());
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (logger.isTraceEnabled()) {
			Date date_after = new Date();
			logger.trace("diff : " + String.valueOf(date_after.getTime() - date_before.getTime()));
		}
	}

	public void setTemplateEngine(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	public void setInsertLicenceTerm(boolean insertLicenceTerm) {
		this.insertLicenceTerm = insertLicenceTerm;
	}

	public void setMailActivationBusinessService(MailActivationBusinessService mailActivationBusinessService) {
		this.mailActivationBusinessService = mailActivationBusinessService;
	}

	public void setFunctionalityReadOnlyService(FunctionalityReadOnlyService functionalityReadOnlyService) {
		this.functionalityReadOnlyService = functionalityReadOnlyService;
	}

	public void setDomainBusinessService(DomainBusinessService domainBusinessService) {
		this.domainBusinessService = domainBusinessService;
	}

	public void setFileDataStore(FileDataStore fileDataStore) {
		this.fileDataStore = fileDataStore;
	}

	public void setUrlTemplateForReceivedShares(String urlTemplateForReceivedShares) {
		this.urlTemplateForReceivedShares = urlTemplateForReceivedShares;
	}

	public void setUrlTemplateForDocuments(String urlTemplateForDocuments) {
		this.urlTemplateForDocuments = urlTemplateForDocuments;
	}

	public void setUrlTemplateForAnonymousUrl(String urlTemplateForAnonymousUrl) {
		this.urlTemplateForAnonymousUrl = urlTemplateForAnonymousUrl;
	}

	public String getUrlFragmentQueryParamFileUuid() {
		return urlFragmentQueryParamFileUuid;
	}

	public void setUrlFragmentQueryParamFileUuid(String paramFilesUuid) {
		this.urlFragmentQueryParamFileUuid = paramFilesUuid;
	}

	public String getUrlTemplateForReceivedShares() {
		return urlTemplateForReceivedShares;
	}

	public String getUrlTemplateForDocuments() {
		return urlTemplateForDocuments;
	}

	public String getUrlTemplateForAnonymousUrl() {
		return urlTemplateForAnonymousUrl;
	}

	public String getUrlTemplateForWorkgroup() {
		return urlTemplateForWorkgroup;
	}

	public void setUrlTemplateForWorkgroup(String urlTemplateForWrokgroup) {
		this.urlTemplateForWorkgroup = urlTemplateForWrokgroup;
	}

	public String getUrlTemplateForUploadRequestEntries() {
		return urlTemplateForUploadRequestEntries;
	}

	public void setUrlTemplateForUploadRequestEntries(String urlTemplateForUploadRequestEntries) {
		this.urlTemplateForUploadRequestEntries = urlTemplateForUploadRequestEntries;
	}

	@Override
	public MailContainerWithRecipient build(EmailContext context) throws BusinessException {
		checkSupportedTemplateType(context);
		computeFromDomain(context);
		context.validateRequiredField();
		if (context.getLanguage() == null) {
			context.setLanguage(Language.ENGLISH);
		}
		if (isDisable(context)) {
			return null;
		}
		return buildMailContainer(context);
	}

	@Override
	public MailContainerWithRecipient fakeBuild(MailConfig cfg, Language language, Integer flavor)
			throws BusinessException {
		List<Context> contexts = getContextForFakeBuild(language);
		EmailContext emailContext = new FakeBuildEmailContext(language);
		if (contexts == null || contexts.isEmpty()) {
			throw new BusinessException(BusinessErrorCode.TEMPLATE_PARSING_ERROR,
					"Missing or empty context for fake build.");
		}
		Context ctx = contexts.get(0);
		if (flavor != null) {
			ctx = contexts.get(flavor);
		}
		MailContainerWithRecipient container = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx, emailContext);
		encodetMailAttachment(container);
		return container;
	}

	private void encodetMailAttachment(MailContainerWithRecipient container) {
		Set<String> keySet = container.getAttachments().keySet();
		for (String identifier : keySet) {
			DataSource dataSource = container.getAttachments().get(identifier);
			try (InputStream stream = dataSource.getInputStream()) {
				String base64String = Base64.encodeBase64String(IOUtils.toByteArray(stream));
				String content = container.getContent().replaceAll("cid:" + identifier,
						"data:image/png;base64, " + base64String);
				container.setContent(content);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new BusinessException(BusinessErrorCode.BASE64_INPUTSTREAM_ENCODE_ERROR, e.getMessage());
			}
		}
	}

	protected void checkSupportedTemplateType(EmailContext context) {
		if (!context.getType().equals(getSupportedType())) {
			logger.error("You can not use this builder {} with the current context {}.", getSupportedType(),
					context.getType());
			throw new BusinessException(BusinessErrorCode.TEMPLATE_PROCESSING_ERROR_INVALID_CONTEXT,
					"You can not use this builder with the current context.");
		}
	}

	protected void computeFromDomain(EmailContext context) {
		AbstractDomain recipientDomain = context.getFromDomain();
		if (context.isNeedToRetrieveGuestDomain()) {
			recipientDomain = domainBusinessService.findGuestDomain(recipientDomain);
			// guest domain could be inexistent into the database.
			if (recipientDomain == null) {
				recipientDomain = context.getFromDomain();
			}
			context.updateFromDomain(recipientDomain);
		}
	}

	protected boolean isDisable(EmailContext context) {
		MailActivation mailActivation = mailActivationBusinessService.findForInternalUsage(context.getFromDomain(),
				context.getActivation());
		return !mailActivation.isEnable();
	}

	protected String getLinShareUrl(Account recipient) {
		String value = functionalityReadOnlyService.getCustomNotificationUrlFunctionality(recipient.getDomain())
				.getValue();
		return value;
	}

	protected String getLinShareUrlForExternals(Account recipient) {
		String value = functionalityReadOnlyService.getCustomNotificationUrlForExternalsFunctionality(recipient.getDomain())
				.getValue();
		return value;
	}

	protected String getLinShareAnonymousURL(Account sender) {
		StringValueFunctionality notificationUrl = functionalityReadOnlyService
				.getAnonymousURLNotificationUrl(sender.getDomain());
		return notificationUrl.getValue();
	}

	protected String getFromMailAddress(AbstractDomain domain) {
		if (domain == null) {
			return null;
		}
		String fromMail = functionalityReadOnlyService.getDomainMailFunctionality(domain).getValue();
		return fromMail;
	}

	protected MailContainerWithRecipient buildMailContainerThymeleaf(MailConfig cfg, MailContentType type, Context ctx,
			EmailContext emailCtx) throws BusinessException {
		logger.debug("Building mail content: " + type);
		MailContainerWithRecipient container = new MailContainerWithRecipient(emailCtx.getLanguage());

		// default context
		Map<String, Object> templateResolutionAttributes = Maps.newHashMap();
		templateResolutionAttributes.put("mailConfig", cfg);
		templateResolutionAttributes.put("lang", emailCtx.getLanguage());
		try {
			TemplateSpec subjectSpec = new TemplateSpec(type.toString() + ":subject", null, TemplateMode.TEXT,
					templateResolutionAttributes);
			String subject = templateEngine.process(subjectSpec, ctx);
			// Remove all carriage return because email RFC does not supported
			// carriage return in the subject field.
			subject = subject.replace("\n", "").trim();
			ctx.setVariable(CST_MAIL_SUBJECT_VAR_NAME, subject);

			TemplateSpec templateSpec = new TemplateSpec(type.toString(), templateResolutionAttributes);
			// TemplateSpec templateSpec = new TemplateSpec(type.toString(),
			// null, TemplateMode.XML, templateResolutionAttributes);
			Date date_before = new Date();
			String body = templateEngine.process(templateSpec, ctx);
			if (logger.isTraceEnabled()) {
				Date date_after = new Date();
				logger.trace("diff : " + String.valueOf(date_after.getTime() - date_before.getTime()));
				logger.trace("subject : {}", subject);
				logger.trace("body : ");
				logger.trace(body);
			}
			container.setSubject(subject);
			container.setContent(body);

			container.setFrom(getFromMailAddress(emailCtx.getFromDomain()));
			container.setReplyTo(emailCtx.getMailReplyTo());
			container.setRecipient(emailCtx.getMailRcpt());

			// Message IDs from Web service API (ex Plugin Thunderbird)
			container.setInReplyTo(emailCtx.getInReplyTo());
			container.setReferences(emailCtx.getReferences());

			addDefaultMailAttachment(emailCtx, container);
			for (MailAttachment attachment : cfg.getMailAttachments()) {
				if (attachment.getEnable()) {
					if (attachment.getEnableForAll()) {
						addLogo(container, attachment.getCid(), attachment.getDocument(), attachment.getName());
					}
					if (emailCtx.getLanguage().equals(Language.fromInt(attachment.getLanguage()))) {
						addLogo(container, attachment.getCid(), attachment.getDocument(), attachment.getName());
					}
				}
			}
			addArrow(container);
			return container;
		} catch (org.thymeleaf.exceptions.TemplateInputException e) {
			String message = "[" + type.toString() + "]" + getCauseMsessage(e);
			logger.debug(message);
			BusinessException businessException = new BusinessException(getBusinessErrorCodeRecursif(e), message, e);
			throw businessException;
		}
	}

	protected void addLogo(MailContainerWithRecipient container, String identifier,
			org.linagora.linshare.core.domain.entities.Document document, String fileName) {
		Validate.notNull(document);
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, document);
		DataSource attachment = new DataSource() {

			@Override
			public InputStream getInputStream() throws IOException {
				return fileDataStore.get(metadata);
			}

			@Override
			public OutputStream getOutputStream() throws IOException {
				throw new UnsupportedOperationException();
			}

			@Override
			public String getContentType() {
				return metadata.getMimeType();
			}

			@Override
			public String getName() {
				return identifier;
			}
		};
		container.addAttachment(identifier, attachment);
	}

	private void addDefaultMailAttachment(EmailContext emailCtx, MailContainerWithRecipient container) {
		if (emailCtx.getLanguage().equals(Language.FRENCH)) {
			addAttachment(container, "logo.linshare@linshare.org",
					"/org/linagora/linshare/core/service/email-logo-fr.png");
		} else {
			addAttachment(container, "logo.linshare@linshare.org",
					"/org/linagora/linshare/core/service/email-logo-en.png");
		}
	}

	private void addArrow(MailContainerWithRecipient container) {
		addAttachment(container, "logo.libre.and.free@linshare.org",
				"/org/linagora/linshare/core/service/email-libre-and-free.png");
		addAttachment(container, "logo.arrow@linshare.org", "/org/linagora/linshare/core/service/email-arrow.png");
	}

	protected void addAttachment(MailContainerWithRecipient container, String identifier, String path) {
		URL resource = getClass().getResource(path);
		if (resource == null) {
			logger.error("Embedded logo was not found : " + identifier + " : " + path);
			throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION,
					"Error sending notification : embedded logo was not found.");
		}
		if (container.getContent().contains(identifier)) {
			container.addAttachment(identifier, new FileDataSource(resource.getFile()));
		}
	}

	protected String getCauseMsessage(TemplateInputException e) {
		return getCauseMsessage(e, null);
	}

	protected String getCauseMsessage(Throwable e, String message) {
		if (message == null) {
			message = formatTMLException(e);
		} else {
			message += formatTMLException(e);
		}
		if (e.getCause() != null) {
			message = getCauseMsessage(e.getCause(), message);
		}
		return message;
	}

	protected String formatTMLException(Throwable e) {
		String message = "";
		if (e.getClass().getSimpleName().equals("TemplateInputException")) {
			message = "[TemplateInputException]";
			if (e.getCause() == null) {
				message += e.getMessage();
			}
		} else if (e.getClass().getSimpleName().equals("TextParseException")) {
			message = "[TextParseException]" + e.getMessage();
		} else if (e.getClass().getSimpleName().equals("NoSuchPropertyException")) {
			message = "[NoSuchPropertyException]";
		} else if (e.getClass().getSimpleName().equals("TemplateProcessingException")) {
			message = "[TemplateProcessingException]";
		} else {
			message = "[" + e.getClass().getSimpleName() + "]" + e.getMessage();
		}
		return message;
	}

	protected BusinessErrorCode getBusinessErrorCodeRecursif(Throwable e) {
		BusinessErrorCode errorCode = getBusinessErrorCode(e);
		if (e.getCause() != null) {
			errorCode = getBusinessErrorCodeRecursif(e.getCause());
		}
		return errorCode;
	}

	protected BusinessErrorCode getBusinessErrorCode(Throwable e) {
		if (e.getClass().getSimpleName().equals("TemplateInputException")) {
			// Probably a missing template/fragment exception
			return BusinessErrorCode.TEMPLATE_PARSING_ERROR_TEMPLATE_INPUT_EXCEPTION;
		} else if (e.getClass().getSimpleName().equals("TextParseException")) {
			return BusinessErrorCode.TEMPLATE_PARSING_ERROR_TEXT_PARSE_EXCEPTION;
		} else if (e.getClass().getSimpleName().equals("NoSuchPropertyException")) {
			return BusinessErrorCode.TEMPLATE_PARSING_ERROR_NO_SUCH_PROPERTY_EXCEPTION;
		} else if (e.getClass().getSimpleName().equals("TemplateProcessingException")) {
			return BusinessErrorCode.TEMPLATE_PARSING_ERROR_TEMPLATE_PROCESSING_EXCEPTION;
		}
		return BusinessErrorCode.TEMPLATE_PARSING_ERROR;
	}

	@Override
	public List<ContextMetadata> getAvailableVariables() {
		List<ContextMetadata> res = Lists.newArrayList();
		List<Context> ctx = getContextForFakeBuild(Language.ENGLISH);
		for (Context context : ctx) {
			res.add(getAvailableVariables(context));
		}
		return res;
	}

	protected ContextMetadata getAvailableVariables(Context ctx) {
		ContextMetadata metadata = new ContextMetadata(getSupportedType().toString());
		if (ctx == null) {
			return metadata;
		}
		Set<String> variableNames = ctx.getVariableNames();
		for (String name : variableNames) {
			Object obj = ctx.getVariable(name);
			Variable variable = null;
			if (obj == null) {
				// variable could exists but not defined.
				variable = new Variable(name, "Undefined");
			} else {
				logger.trace(obj.toString());
				variable = getVariable(name, obj);
			}
			metadata.addVariable(variable);
		}
		return metadata;
	}

	private Variable getVariable(String name, Object obj) {
		Variable variable = new Variable(name, obj.getClass().getSimpleName());
		logger.trace(variable.toString());
		if (obj instanceof ArrayList) {
			List<?> array = (List<?>) obj;
			if (!array.isEmpty()) {
				Object next = array.iterator().next();
				String parametrizedClassName = next.getClass().getSimpleName();
				variable.setType(variable.getType() + "<" + parametrizedClassName + ">");
				variable.setVariables(getFields(next));
				if (variable.getVariables() == null) {
					variable.setStringValue(next.toString());
				}
			}
		} else {
			variable.setVariables(getFields(obj));
			if (variable.getVariables() == null) {
				variable.setStringValue(obj.toString());
			}
		}
		return variable;
	}

	private List<Variable> getFields(Object obj) {
		List<Variable> attributes = null;
		if (isSupportedFieldType(obj)) {
			attributes = Lists.newArrayList();
			for (Field field : obj.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				String typeName = field.getGenericType().getTypeName();
				try {
					Object subObject = field.get(obj);
					if (subObject instanceof ArrayList) {
						Variable attr = getVariable(field.getName(), subObject);
						logger.trace(attr.toString());
						attributes.add(attr);
					} else {
						Class<?> act = Class.forName(typeName);
						typeName = act.getSimpleName();
						Object value = field.get(obj);
						if (value != null) {
							Variable attr = new Variable(field.getName(), typeName);
							logger.trace(attr.toString());
							// FMA
							attr.setStringValue(value.toString());
							attributes.add(attr);
						}
					}
				} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
					logger.trace(e.getMessage(), e);
				}
			}
		}
		return attributes;
	}

	protected boolean isSupportedFieldType(Object obj) {
		return supportedClass.contains(obj.getClass().getCanonicalName());
	}

	protected String getOwnerDocumentLink(String linshareURL, String documentUuid) {
		StringBuilder sb = new StringBuilder();
		sb.append(linshareURL);
		Formatter formatter = new Formatter(sb);
		formatter.format(urlTemplateForDocuments, documentUuid);
		formatter.close();
		return sb.toString();
	}

	protected String getRecipientShareLink(String linshareURL, String shareUuid) {
		StringBuilder sb = new StringBuilder();
		sb.append(linshareURL);
		Formatter formatter = new Formatter(sb);
		formatter.format(urlTemplateForReceivedShares, shareUuid);
		formatter.close();
		return sb.toString();
	}

	protected String addNewFilesParam (String link, String filesUuid) {
		StringBuilder sb = new StringBuilder();
		sb.append(link);
		Formatter formatter = new Formatter(sb);
		formatter.format(urlFragmentQueryParamFileUuid, filesUuid);
		formatter.close();
		return sb.toString();
	}

	protected String getWorkGroupLink(String linshareURL, String workGroupUuid) {
		StringBuilder sb = new StringBuilder();
		sb.append(linshareURL);
		Formatter formatter = new Formatter(sb);
		formatter.format(urlTemplateForWorkgroup, workGroupUuid);
		formatter.close();
		return sb.toString();
	}

	protected List<Document> transformDocuments(Set<DocumentEntry> documentEntries) {
		return transform(documentEntries, false, null);
	}

	protected List<Document> transform(Set<DocumentEntry> documentEntries, boolean withLink, String linshareURL) {
		List<Document> documents = Lists.newArrayList();
		for (DocumentEntry documentEntry : documentEntries) {
			Document d = new Document(documentEntry);
			if (withLink) {
				d.setHref(getOwnerDocumentLink(linshareURL, documentEntry.getUuid()));
			}
			documents.add(d);

		}
		return documents;
	}

	protected Document getNewFakeDocument(String name) {
		return getNewFakeDocument(name, null);
	}

	protected Document getNewFakeDocument(String name, String linshareURL) {
		Document document = new Document(name);
		if (linshareURL != null) {
			document.setHref(getOwnerDocumentLink(linshareURL, document.getUuid()));
		}
		return document;
	}

	protected Share getNewFakeShare(String name) {
		return getNewFakeShare(name, null);
	}

	protected Share getNewFakeShare(String name, String linshareURL) {
		Share share = new Share(name);
		if (linshareURL != null) {
			share.setHref(getOwnerDocumentLink(linshareURL, share.getUuid()));
		}
		return share;
	}

	protected WorkgroupMember getNewFakeThreadMember(String name) {
		User user = new Guest("Peter", "Wilson", "peter.wilson@linshare.org");
		org.linagora.linshare.core.domain.entities.WorkGroup workGroup = new org.linagora.linshare.core.domain.entities.WorkGroup(user.getDomain(), user, name);
		WorkgroupMember workgroupdMember = new WorkgroupMember(true, false, user, workGroup);
		return workgroupdMember;
	}

	protected SharedSpaceMember getNewFakeSharedSpaceMember(String name) {
		User user = new Guest("Peter", "Wilson", "peter.wilson@linshare.org");
		SharedSpaceNode node = new SharedSpaceNode(name, null, NodeType.WORK_GROUP);
		SharedSpaceMember member = new SharedSpaceMember(new SharedSpaceNodeNested(node),
				new GenericLightEntity(UUID.randomUUID().toString(), "ADMIN"),
				new SharedSpaceAccount(user));
		return member;
	}

	protected Date getFakeExpirationDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONDAY, 3);
		return c.getTime();
	}

	protected Date getFakeCreationDate() {
		Calendar c = Calendar.getInstance();
		return c.getTime();
	}

	protected Context newFakeContext(Language language) {
		Context ctx = new Context(Language.toLocale(language));
		ctx.setVariable("linshareURL", fakeLinshareURL);
		ctx.setVariable(CST_MAIL_SUBJECT_VAR_NAME, "Some defaut and fake subject");
		return ctx;
	}
}
