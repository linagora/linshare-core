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
package org.linagora.linshare.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailSubjectEnum;
import org.linagora.linshare.core.domain.constants.MailTemplateEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.entities.MailSubject;
import org.linagora.linshare.core.domain.entities.MailTemplate;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MailContentBuildingServiceImpl implements MailContentBuildingService {
	
	private final static Logger logger = LoggerFactory.getLogger(MailContentBuildingServiceImpl.class);
	
	private final String mailContentTxt;
	
	private final String mailContentHTML;
	
	private final String mailContentHTMLWithoutLogo;
	
	private final boolean displayLogo;
	
	private final boolean insertLicenceTerm;
	
	private final AbstractDomainService abstractDomainService;
	
	private final FunctionalityReadOnlyService functionalityReadOnlyService;
	
	class ContactRepresentation {
		private String mail;
		private String firstName;
		private String lastName;
		
		public ContactRepresentation(String mail, String firstName, String lastName) {
			super();
			this.mail = clean(mail);
			this.firstName = clean(firstName);
			this.lastName = clean(lastName);
		}
		
		public ContactRepresentation(String mail){
			this.mail = clean(mail);
			this.firstName = null;
			this.lastName = null;
		}
		
		public ContactRepresentation(User user){
			this.mail = clean(user.getMail());
			this.firstName = clean(user.getFirstName());
			this.lastName = clean(user.getLastName());
		}

		public ContactRepresentation(Account account){
			this.mail = clean(account.getAccountReprentation());
			if(account instanceof User) {
				User user = (User)account;
				this.firstName = clean(user.getFirstName());
				this.lastName = clean(user.getLastName());
				this.mail = clean(user.getMail());
			}
		}
		
		
		private String clean(String value) {
			if(value == null) return null;
			if(value.trim().equals("")) return null;
			return value;
		}
		
		public String getContactRepresntation() {
			if (this.firstName == null || this.lastName == null) {
				return this.mail;
			} else {
				StringBuilder str = new StringBuilder();
				str.append(this.firstName);
				str.append(' ');
				str.append(this.lastName);
				str.append(" (");
				str.append(this.mail);
				str.append(')');
				return str.toString(); 
			}
		}
	}

	public MailContentBuildingServiceImpl(final String mailContentTxt,
			final String mailContentHTML, final String mailContentHTMLWithoutLogo,
			final boolean displayLogo, AbstractDomainService abstractDomainService,FunctionalityReadOnlyService functionalityReadOnlyService, boolean insertLicenceTerm) throws BusinessException {
        this.mailContentTxt = mailContentTxt;
        this.mailContentHTML = mailContentHTML;
        this.mailContentHTMLWithoutLogo = mailContentHTMLWithoutLogo;
        this.displayLogo = displayLogo;
        this.abstractDomainService = abstractDomainService;
        this.insertLicenceTerm = insertLicenceTerm;
        this.functionalityReadOnlyService = functionalityReadOnlyService;
	}

	
	
	/**
	 * 
	 * TOOLS
	 * 
	 */
	private String getLinShareUrlForAUserRecipient(Account recipient) {
			return functionalityReadOnlyService.getCustomNotificationUrlFunctionality(recipient.getDomain()).getValue();
		}
	
	private String getLinShareUrlForAContactRecipient(Account sender) {
		AbstractDomain senderDomain = abstractDomainService.getGuestDomain(sender.getDomainId());
		// guest domain could be inexistent into the database.
		if(senderDomain == null) {
			senderDomain = sender.getDomain();
		}
		return functionalityReadOnlyService.getCustomNotificationUrlFunctionality(senderDomain).getValue();
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
	

	/**
	 * Retrieve the mail subject from config.
	 * @param language the language of the email
	 * @param mailSubjectEnum the enum key
	 * @param contactRepresentation TODO
	 * 
	 * @return the MailSubject object
	 * @throws BusinessException when no mail subject was found for the given enum key
	 */
	private MailSubject getMailSubject(User actor, Language language, MailSubjectEnum mailSubjectEnum, ContactRepresentation contactRepresentation) throws BusinessException {
		return getMailSubject(actor, language, mailSubjectEnum, contactRepresentation, null);
	}
	
	private MailSubject getMailSubject(User actor, Language language, MailSubjectEnum mailSubjectEnum, ContactRepresentation contactRepresentation, String subject) throws BusinessException {
		AbstractDomain domain = actor.getDomain();
		Set<MailSubject> subjects = domain.getMessagesConfiguration().getMailSubjects();
		MailSubject mailSubject = null;
		
		for (MailSubject mailSubject_ : subjects) {
			if (mailSubject_.getLanguage().equals(language) && mailSubject_.getMailSubject().equals(mailSubjectEnum)) {
				mailSubject = new MailSubject(mailSubject_);
				break;
			}
		}
		
		if(mailSubject == null) {
			logger.error("Bad mail subject "+ mailSubjectEnum.name() +" for language " + language.name());
			throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION,"Bad mail subject "+ mailSubjectEnum.name() +" for language " + language.name());
		}
		
		if(contactRepresentation != null) {
			mailSubject.setContent(StringUtils.replace(mailSubject.getContent(), "${actorRepresentation}", contactRepresentation.getContactRepresntation()));
		}
		
		if(subject != null) {
			mailSubject.setContent(StringUtils.replace(mailSubject.getContent(), "${actorSubject}", subject));
		} 
		
		return mailSubject;
	}
	
	private MailSubject getMailSubject(User actor, Language language, MailSubjectEnum mailSubjectEnum) throws BusinessException {
		return getMailSubject(actor, language, mailSubjectEnum, null);
	}
	

	
	/**
	 * Retrieve the mail template from config.
	 * 
	 * @param language the language of the email
	 * @param mailTemplate the enum key
	 * @return the MailTemplate object
	 * @throws BusinessException when no mail template was found for the given enum key
	 */
	private MailTemplate getMailTemplate(User actor, Language language, MailTemplateEnum mailTemplate) throws BusinessException {
		Set<MailTemplate> templates = actor.getDomain().getMessagesConfiguration().getMailTemplates();
		
		for (MailTemplate mailTemplate_ : templates) {
			if (mailTemplate_.getLanguage().equals(language) && mailTemplate_.getMailTemplate().equals(mailTemplate)) {
				return new MailTemplate(mailTemplate_);
			}
		}

		logger.error("Bad mail template "+ mailTemplate.name() +" for language " + language.name());
		throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION,"Bad mail template "+ mailTemplate.name() +" for language " + language.name());
	}

	
	/**
	 * MAIL BUILDER SECTION
	 */
	

	/**
	 * notification for expired shares
	 */
	@Override
	public MailContainerWithRecipient buildMailUpcomingOutdatedShare(ShareEntry shareEntry, Integer days) throws BusinessException {
		// sharing is only possible between users.
		User owner = (User) shareEntry.getEntryOwner();
		User recipient = shareEntry.getRecipient();
		
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(recipient.getExternalMailLocale());
		String shareEntryUrl = getLinShareUrlForAUserRecipient(recipient);

		// expired share notification 
		mailContainer.appendTemplate(buildTemplateUpcomingOutdatedShare(owner, mailContainer.getLanguage(), shareEntry, days));
		
		// download URL
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(owner, mailContainer.getLanguage(), shareEntryUrl));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(owner, mailContainer.getLanguage(), MailSubjectEnum.SHARED_DOC_UPCOMING_OUTDATED));

		// recipient mail
		mailContainer.setRecipient(recipient.getMail());

		// reply mail
		mailContainer.setReplyTo(owner.getMail());
		
		// domain mail
		mailContainer.setFrom(abstractDomainService.getDomainMail(owner.getDomain()));
				
		return buildMailContainerSetProperties(owner, mailContainer, shareEntry.getRecipient());
	}

	
	/**
	 * notification for expired anonymous shares
	 */
	@Override
	public MailContainerWithRecipient buildMailUpcomingOutdatedShare(AnonymousShareEntry shareEntry, Integer days) throws BusinessException {
		
		// sharing is only possible between users.
		User sender = (User) shareEntry.getEntryOwner();
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		String anonymousShareEntryUrl = shareEntry.getAnonymousUrl().getFullUrl(getLinShareUrlForAContactRecipient(sender));
		

		// expired share notification 
		mailContainer.appendTemplate(buildTemplateUpcomingOutdatedShare(sender, mailContainer.getLanguage(), shareEntry, days));
		
		// download URL
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(sender, mailContainer.getLanguage(), anonymousShareEntryUrl));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.SHARED_DOC_UPCOMING_OUTDATED));

		// recipient mail
		mailContainer.setRecipient(shareEntry.getAnonymousUrl().getContact().getMail());

		// reply mail
		mailContainer.setReplyTo(sender.getMail());
				
		// domain mail
		mailContainer.setFrom(abstractDomainService.getDomainMail(sender.getDomain()));
		
		return buildMailContainerSetProperties(sender, mailContainer, shareEntry.getAnonymousUrl().getContact());
	}
	
	
	/**
	 * notification for expired document entries.
	 */
	@Override
	public MailContainerWithRecipient buildMailUpcomingOutdatedDocument(DocumentEntry document, Integer days) throws BusinessException {
		
		User owner = (User) document.getEntryOwner();
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(owner.getExternalMailLocale());
		String linShareRootUrl = getLinShareUrlForAUserRecipient(owner);
		

		// expired file notification 
		mailContainer.appendTemplate(buildTemplateUpcomingOutdatedFile(owner, mailContainer.getLanguage(), document, days));
		
		// download URL
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(owner, mailContainer.getLanguage(), linShareRootUrl));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(owner, mailContainer.getLanguage(), MailSubjectEnum.DOC_UPCOMING_OUTDATED));

		// recipient mail
		mailContainer.setRecipient(owner.getMail());
		
		// domain mail
		mailContainer.setFrom(abstractDomainService.getDomainMail(owner.getDomain()));

		// sender and recipient are the same person.
		return buildMailContainerSetProperties(owner, mailContainer, owner);
	}
	
	
	/**
	 * update notification for shared document 
	 */
	@Override
	public MailContainerWithRecipient buildMailSharedDocumentUpdated(AnonymousShareEntry shareEntry, String oldDocName, String fileSizeTxt)throws BusinessException {
		
		User sender = (User) shareEntry.getEntryOwner();
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		String anonymousUrl = shareEntry.getAnonymousUrl().getFullUrl(getLinShareUrlForAContactRecipient(sender));
		String recipient = shareEntry.getAnonymousUrl().getContact().getMail();
		
		// file updated notification
		mailContainer.appendTemplate(buildTemplateFileUpdated(sender, mailContainer.getLanguage(), sender, shareEntry.getDocumentEntry(), oldDocName, fileSizeTxt));
		
		// download URL
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(sender, mailContainer.getLanguage(), anonymousUrl));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.SHARED_DOC_UPDATED, new ContactRepresentation(sender)));
		
		// recipient mail
		mailContainer.setRecipient(recipient);
		
		// reply mail
		mailContainer.setReplyTo(sender.getMail());
		
		// domain mail
		mailContainer.setFrom(abstractDomainService.getDomainMail(sender.getDomain()));

		return buildMailContainerSetProperties(sender, mailContainer, shareEntry.getAnonymousUrl().getContact());
	}
	
	
	/**
	 * update notification for anonymous shared document 
	 */
	@Override
	public MailContainerWithRecipient buildMailSharedDocumentUpdated(ShareEntry shareEntry, String oldDocName, String fileSizeTxt) throws BusinessException {
		User sender = (User) shareEntry.getEntryOwner();
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		String shareEntryUrl = getLinShareUrlForAUserRecipient(shareEntry.getRecipient());
		// file updated notification
		mailContainer.appendTemplate(buildTemplateFileUpdated(sender, mailContainer.getLanguage(), sender, shareEntry.getDocumentEntry(), oldDocName, fileSizeTxt));
		
		// download URL
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(sender, mailContainer.getLanguage(), shareEntryUrl));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.SHARED_DOC_UPDATED, new ContactRepresentation(sender)));
		
		// recipient mail
		mailContainer.setRecipient(shareEntry.getRecipient().getMail());
		
		// reply mail
		mailContainer.setReplyTo(sender.getMail());
		
		// domain mail
		mailContainer.setFrom(abstractDomainService.getDomainMail(shareEntry.getRecipient().getDomain()));

		return buildMailContainerSetProperties(sender, mailContainer, shareEntry.getRecipient());
	}
	
	/**
	 * notification for shared file deletion
	 */
	@Override
	public MailContainerWithRecipient buildMailSharedFileDeletedWithRecipient(Account actor, ShareEntry shareEntry) throws BusinessException {

		logger.debug("share uuid : " + shareEntry.getUuid());
		
		User sender = (User) shareEntry.getEntryOwner();
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		
		// share deleted notification
		mailContainer.appendTemplate(buildTemplateSharedFileDeleted(sender, mailContainer.getLanguage(), shareEntry, sender));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.SHARED_DOC_DELETED, new ContactRepresentation(actor)));
		
		// recipient mail
		mailContainer.setRecipient(shareEntry.getRecipient().getMail());
		
		// reply mail
		mailContainer.setReplyTo(sender.getMail());
		
		// domain mail
		mailContainer.setFrom(abstractDomainService.getDomainMail(shareEntry.getRecipient().getDomain()));

		return buildMailContainerSetProperties(sender, mailContainer, shareEntry.getRecipient());
	}
	
	
	/**
	 * download notification for anonymous share
	 */
	@Override
	public MailContainerWithRecipient buildMailAnonymousDownload(AnonymousShareEntry shareEntry) throws BusinessException {
		
		User sender = (User)shareEntry.getEntryOwner();
		String email = shareEntry.getAnonymousUrl().getContact().getMail();
		String documentName = shareEntry.getDocumentEntry().getName();
		
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		
		// share notification
		mailContainer.appendTemplate(buildTemplateConfirmDownloadAnonymous(sender, mailContainer.getLanguage(), documentName, email));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.ANONYMOUS_DOWNLOAD, new ContactRepresentation(email)));
		
		// recipient mail
		mailContainer.setRecipient(sender.getMail());
		
		// domain mail
		mailContainer.setFrom(abstractDomainService.getDomainMail(sender.getDomain()));
		
		return buildMailContainerSetProperties(sender, mailContainer, sender);
	}	
	
	
	/**
	 * notification for anonymous shared document 
	 */
	@Override
	public MailContainerWithRecipient buildMailNewSharingWithRecipient(User sender, MailContainer inputMailContainer, User recipient, 
			List<ShareDocumentVo> shares, boolean hasToDecrypt) throws BusinessException {
		
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		String linShareRootUrl = getLinShareUrlForAUserRecipient(recipient);

		// share notification
		mailContainer.appendTemplate(buildTemplateShareNotification(sender, recipient, mailContainer.getLanguage(), shares));
		
		// LinShare URL
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(sender, mailContainer.getLanguage(), linShareRootUrl));
		
		// Direct download Url
		if(hasToDecrypt) {
			mailContainer.appendTemplate(buildTemplateDecryptUrl(sender, mailContainer.getLanguage(), getLinShareUrlForAUserRecipient(recipient)));
		}
		
		// subject
		String subjectContent = inputMailContainer.getSubject();
		if (subjectContent != null && subjectContent.length() >= 1) {
			// this means subject was filled by users 
			mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.NEW_SHARING_WITH_ACTOR, new ContactRepresentation(sender), subjectContent));
			
		} else {
			mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.NEW_SHARING, new ContactRepresentation(sender)));
		}
		
		// reply mail
		mailContainer.setReplyTo(sender.getMail());
		
		// recipient mail
		mailContainer.setRecipient(recipient.getMail());
		
		// domain mail
		mailContainer.setFrom(abstractDomainService.getDomainMail(recipient.getDomain()));
		
		// Message IDs from Thunderbird Plugin
		mailContainer.setInReplyTo(inputMailContainer.getInReplyTo());
		mailContainer.setReferences(inputMailContainer.getReferences());

		return buildMailContainerSetProperties(sender, mailContainer, recipient, inputMailContainer.getPersonalMessage());
	}	
	
	
	/**
	 * notification for anonymous shared document 
	 */
	@Override
	public MailContainerWithRecipient buildMailNewSharingWithRecipient(MailContainer inputMailContainer, AnonymousUrl anonymousUrl, User sender) throws BusinessException {
		
		String linShareRootUrl = getLinShareUrlForAContactRecipient(sender);
		Contact contact = anonymousUrl.getContact();
		List<String> docNames = anonymousUrl.getDocumentNames();
		boolean hasToDecrypt = anonymousUrl.oneDocumentIsEncrypted();
		String password = anonymousUrl.getTemporaryPlainTextPassword();
		

		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		
		//share notification
		mailContainer.appendTemplate(buildTemplateShareNotification(sender, mailContainer.getLanguage(), docNames));
		
		// Direct download Url
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(sender, mailContainer.getLanguage(), anonymousUrl.getFullUrl(linShareRootUrl)));
		
		// Applet link to decipher
		if(hasToDecrypt) {
			mailContainer.appendTemplate(buildTemplateDecryptUrl(sender, mailContainer.getLanguage(), linShareRootUrl));
		}
		
		// Password notification
		if(password != null && password.trim().length() > 0) {
			mailContainer.appendTemplate(buildTemplatePasswordGiving(sender, mailContainer.getLanguage(), password));
		}
		
		// subject
		String subjectContent = inputMailContainer.getSubject();
		if (subjectContent != null && subjectContent.length() >= 1) {
			// this means subject was filled by users 
			mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.NEW_SHARING_WITH_ACTOR, new ContactRepresentation(sender), subjectContent));
		} else {
			mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.NEW_SHARING, new ContactRepresentation(sender)));
		}
			
		// recipient mail
		mailContainer.setRecipient(contact.getMail());
		
		// reply mail
		mailContainer.setReplyTo(sender.getMail());
		
		// domain mail
		mailContainer.setFrom(abstractDomainService.getDomainMail(sender.getDomain()));
		
		// Message IDs from Thunderbird Plugin
		mailContainer.setInReplyTo(inputMailContainer.getInReplyTo());
		mailContainer.setReferences(inputMailContainer.getReferences());

		return buildMailContainerSetProperties(sender, mailContainer, contact, inputMailContainer.getPersonalMessage());
	}
	

	/**
	 * reset password mail
	 */
	@Override
	public MailContainerWithRecipient buildMailResetPassword(Guest recipient, String password) throws BusinessException {
		
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(recipient.getExternalMailLocale());
		
		mailContainer.appendTemplate(buildTemplateAccountDescription(recipient, mailContainer.getLanguage(), recipient, password));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(recipient, mailContainer.getLanguage(), MailSubjectEnum.RESET_PASSWORD));
		
		// recipient mail
		mailContainer.setRecipient(recipient.getMail());
		
		// domain mail
		mailContainer.setFrom(abstractDomainService.getDomainMail(recipient.getDomain()));
		
		return buildMailContainerSetProperties(recipient, mailContainer, recipient);
	}



	/**
	 * Notify somebody that his linshare account has been created
	 */
	@Override
	public MailContainerWithRecipient buildMailNewGuest(User sender, User recipient, String password) throws BusinessException {
		
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		
		mailContainer.appendTemplate(buildTemplateGuestInvitation(sender, mailContainer.getLanguage()));
		
		// download URL
		String linshareUrl = getLinShareUrlForAUserRecipient(recipient);
		mailContainer.appendTemplate(buildTemplateLinshareURL(sender, mailContainer.getLanguage(), linshareUrl));
		
		// download URL
		mailContainer.appendTemplate(buildTemplateAccountDescription(sender, mailContainer.getLanguage(), recipient, password));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.NEW_GUEST));
		
		// recipient mail
		mailContainer.setRecipient(recipient.getMail());
		
		// reply mail
		mailContainer.setReplyTo(sender.getMail());
		
		// domain mail
		mailContainer.setFrom(abstractDomainService.getDomainMail(recipient.getDomain()));
		
		return buildMailContainerSetProperties(sender, mailContainer, recipient);
	}

	
	
	@Override
	public MailContainerWithRecipient buildMailRegisteredDownloadWithOneRecipient(ShareEntry shareEntry) throws BusinessException {
		
		User sender = (User)shareEntry.getEntryOwner();
		String documentName = shareEntry.getDocumentEntry().getName();
		
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		
		// download notification
		mailContainer.appendTemplate(buildTemplateConfirmDownloadRegistered(sender, mailContainer.getLanguage(), documentName, shareEntry.getRecipient()));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.REGISTERED_DOWNLOAD, new ContactRepresentation(shareEntry.getRecipient())));
		
		// recipient mail
		mailContainer.setRecipient(sender.getMail());
		
		// domain mail
		mailContainer.setFrom(abstractDomainService.getDomainMail(sender.getDomain()));
		
		return buildMailContainerSetProperties(sender, mailContainer, sender);
	}	
	
	
	
	
	/**
	 * 
	 * MAIL TEMPLATE BUILDER SECTION
	 * 
	 */
	
	
	
	
	
	/**
	 * TEMPLATE : DOC_UPCOMING_OUTDATED
	 */
	private MailTemplate buildTemplateUpcomingOutdatedFile(User actor, Language language, DocumentEntry document, Integer days) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.DOC_UPCOMING_OUTDATED);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();
		
        contentTXT = StringUtils.replace(contentTXT, "${nbDays}", days.toString());
        contentTXT = StringUtils.replace(contentTXT, "${documentName}", document.getName());
        contentHTML = StringUtils.replace(contentHTML, "${nbDays}", days.toString());
        contentHTML = StringUtils.replace(contentHTML, "${documentName}", document.getName());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	
	
	
	/**
	 * TEMPLATE : SHARED_DOC_UPCOMING_OUTDATED
	 */
	private MailTemplate buildTemplateUpcomingOutdatedShare(User sender, Language language, ShareEntry shareEntry, Integer days) throws BusinessException {
		// getting resource name
		String resourceName = shareEntry.getName();
		MailTemplate template = buildTemplateForUpcommingOutdatedShare(sender, language, days, resourceName);
        return template;
	}
	
	

	
	private MailTemplate buildTemplateUpcomingOutdatedShare(User sender, Language language, AnonymousShareEntry shareEntry, Integer days) throws BusinessException {
		// getting resource name
		String resourceName = shareEntry.getName();
		MailTemplate template = buildTemplateForUpcommingOutdatedShare(sender, language, days, resourceName);
        return template;
	}
	
	

	
	private MailTemplate buildTemplateForUpcommingOutdatedShare(User sender, Language language, Integer days, String ressourceName) throws BusinessException {
		MailTemplate template = getMailTemplate(sender, language, MailTemplateEnum.SHARED_DOC_UPCOMING_OUTDATED);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		
		contentTXT = StringUtils.replace(contentTXT, "${firstName}", sender.getFirstName());
		contentTXT = StringUtils.replace(contentTXT, "${lastName}", sender.getLastName());
        contentTXT = StringUtils.replace(contentTXT, "${nbDays}", days.toString());
        contentTXT = StringUtils.replace(contentTXT, "${documentName}", ressourceName);
        contentHTML = StringUtils.replace(contentHTML, "${firstName}", sender.getFirstName());
		contentHTML = StringUtils.replace(contentHTML, "${lastName}", sender.getLastName());
        contentHTML = StringUtils.replace(contentHTML, "${nbDays}", days.toString());
        contentHTML = StringUtils.replace(contentHTML, "${documentName}", ressourceName);
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
		return template;
	}
	
	
	
	
	/**
	 * TEMPLATE : DECRYPT_URL
	 * @param rootUrl TODO
	 */
	private MailTemplate buildTemplateDecryptUrl(User actor, Language language, String rootUrl) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.DECRYPT_URL);
		
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();
		
		String jwsEncryptUrl = getJwsEncryptUrlString(rootUrl);
		contentTXT = StringUtils.replace(contentTXT, "${jwsEncryptUrl}", jwsEncryptUrl);
        contentHTML = StringUtils.replace(contentHTML, "${jwsEncryptUrl}", jwsEncryptUrl);
		
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
	
		return template;
	}
	
	
	
	
	/**
	 * Template GREETINGS
	 */
	private MailTemplate buildTemplateGreetings(User actor, Language language, Contact contact) throws BusinessException {
		User tempUser = new Guest(contact.getMail(), "", contact.getMail());
		return buildTemplateGreetings(actor, language, tempUser);
	}
	
	
	private MailTemplate buildTemplateGreetings(User actor, Language language, User recipient) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.GREETINGS);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		contentTXT = StringUtils.replace(contentTXT, "${firstName}", recipient.getFirstName());
        contentTXT = StringUtils.replace(contentTXT, "${lastName}", recipient.getLastName());
        contentHTML = StringUtils.replace(contentHTML, "${firstName}", recipient.getFirstName());
        contentHTML = StringUtils.replace(contentHTML, "${lastName}", recipient.getLastName());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	
	
	
	/**
	 * Template FOOTER
	 */
	private MailTemplate buildTemplateFooter(User actor, Language language) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.FOOTER);
        if(insertLicenceTerm) {
        	StringBuilder contentTXT = new StringBuilder(template.getContentTXT());
        	StringBuilder contentHTML = new StringBuilder(template.getContentHTML());
    		if (language.equals(Language.FRENCH)) {
    			contentTXT.append("\r\nVous utilisez la version libre et gratuite de LinShare™ http://www.linshare.org,™, développée par Linagora © 2009-2013. Contribuez à la R&D du produit en souscrivant à une offre entreprise.\n\r");
    			contentHTML.append("<br/>Vous utilisez la version libre et gratuite de <a href=\"http://www.linshare.org/\" title=\"LinShare\"><strong>LinShare</strong></a>™, développée par Linagora © 2009-2013. Contribuez à la R&D du produit en souscrivant à une offre entreprise.<br/>");
    		} else {
    			contentTXT.append("\r\nYou are using the Open Source and free version of LinShare™, powered by Linagora © 2009-2013. Contribute to Linshare R&D by subscribing to an Enterprise offer.\n\r");
    			contentHTML.append("<br/>You are using the Open Source and free version of <a href=\"http://www.linshare.org/\" title=\"LinShare\"><strong>LinShare</strong></a>™, powered by Linagora © 2009-2013. Contribute to Linshare R&D by subscribing to an Enterprise offer.<br/>");
    		}
    		template.setContentTXT(contentTXT.toString());
    	    template.setContentHTML(contentHTML.toString());    		
        }
        return template;
	}
	
	
	
	
	/**
	 * Template CONFIRM_DOWNLOAD_ANONYMOUS
	 */
	private MailTemplate buildTemplateConfirmDownloadAnonymous(User actor, Language language, String docName, String email) throws BusinessException {
		List<String> list = new ArrayList<String>();
		list.add(docName);
		return buildTemplateConfirmDownloadAnonymous(actor, language, list, email);
	}
	
	
	private MailTemplate buildTemplateConfirmDownloadAnonymous(User actor, Language language, List<String> docNames, String email) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.CONFIRM_DOWNLOAD_ANONYMOUS);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		StringBuffer names = new StringBuffer();
		StringBuffer namesTxt = new StringBuffer();
		if (docNames != null && docNames.size()>0) {
			for (String name : docNames) {
				names.append("<li>"+name+"</li>");
				namesTxt.append(name+"\n");
			}	
		}

		contentTXT = StringUtils.replace(contentTXT, "${email}", email);
        contentTXT = StringUtils.replace(contentTXT, "${documentNamesTxt}", namesTxt.toString());
        contentHTML = StringUtils.replace(contentHTML, "${email}", email);
        contentHTML = StringUtils.replace(contentHTML, "${documentNames}", names.toString());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	
	/**
	 * Template CONFIRM_DOWNLOAD_REGISTERED
	 */
	private MailTemplate buildTemplateConfirmDownloadRegistered(User actor, Language language, String docName, User recipient) throws BusinessException {
		List<String> list = new ArrayList<String>();
		list.add(docName);
		return buildTemplateConfirmDownloadRegistered(actor, language, list, recipient);
	}
	private MailTemplate buildTemplateConfirmDownloadRegistered(User actor, Language language, List<String> docNames, User recipient) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.CONFIRM_DOWNLOAD_REGISTERED);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		StringBuffer names = new StringBuffer();
		StringBuffer namesTxt = new StringBuffer();
		if (docNames != null && docNames.size()>0) {
			for (String name: docNames) {
				names.append("<li>"+name+"</li>");
				namesTxt.append(name+"\n");
			}	
		}

		contentTXT = StringUtils.replace(contentTXT, "${recipientFirstName}", recipient.getFirstName());
		contentTXT = StringUtils.replace(contentTXT, "${recipientLastName}", recipient.getLastName());
        contentTXT = StringUtils.replace(contentTXT, "${documentNamesTxt}", namesTxt.toString());
        contentHTML = StringUtils.replace(contentHTML, "${recipientFirstName}", recipient.getFirstName());
		contentHTML = StringUtils.replace(contentHTML, "${recipientLastName}", recipient.getLastName());
        contentHTML = StringUtils.replace(contentHTML, "${documentNames}", names.toString());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	
	
	
	/**
	 * Template PERSONAL_MESSAGE
	 */
	private MailTemplate buildTemplatePersonalMessage(User actor, Language language, String message) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.PERSONAL_MESSAGE);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		contentTXT = StringUtils.replace(contentTXT, "${ownerFirstName}", actor.getFirstName());
        contentTXT = StringUtils.replace(contentTXT, "${ownerLastName}", actor.getLastName());
        contentTXT = StringUtils.replace(contentTXT, "${message}", message);
        contentHTML = StringUtils.replace(contentHTML, "${ownerFirstName}", actor.getFirstName());
        contentHTML = StringUtils.replace(contentHTML, "${ownerLastName}", actor.getLastName());
        contentHTML = StringUtils.replace(contentHTML, "${message}", message);
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	
	
	
	/**
	 * Template GUEST_INVITATION
	 */
	private MailTemplate buildTemplateGuestInvitation(User owner, Language language) throws BusinessException {
		MailTemplate template = getMailTemplate(owner, language, MailTemplateEnum.GUEST_INVITATION);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		contentTXT = StringUtils.replace(contentTXT, "${ownerFirstName}", owner.getFirstName());
        contentTXT = StringUtils.replace(contentTXT, "${ownerLastName}", owner.getLastName());
        contentHTML = StringUtils.replace(contentHTML, "${ownerFirstName}", owner.getFirstName());
        contentHTML = StringUtils.replace(contentHTML, "${ownerLastName}", owner.getLastName());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	
	
	
	/**
	 * Template LINSHARE_URL
	 */
	private MailTemplate buildTemplateLinshareURL(User actor, Language language, String linShareUrl) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.LINSHARE_URL);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		contentTXT = StringUtils.replace(contentTXT, "${url}", linShareUrl);
        contentHTML = StringUtils.replace(contentHTML, "${url}", linShareUrl);
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	
	
	
	/**
	 * Template ACCOUNT_DESCRIPTION
	 */
	private MailTemplate buildTemplateAccountDescription(User actor, Language language, User guest, String password) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.ACCOUNT_DESCRIPTION);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		contentTXT = StringUtils.replace(contentTXT, "${mail}", guest.getMail());
		contentTXT = StringUtils.replace(contentTXT, "${password}", password);
        contentHTML = StringUtils.replace(contentHTML, "${mail}", guest.getMail());
        contentHTML = StringUtils.replace(contentHTML, "${password}", password);
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	
	
	
	/**
	 * Template SHARE_NOTIFICATION
	 */
	private MailTemplate buildTemplateShareNotification(User actor, Language language, List<String> docNames) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.SHARE_NOTIFICATION);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		StringBuffer names = new StringBuffer();
		StringBuffer namesTxt = new StringBuffer();
		for (String name : docNames) {
			names.append("<li>"+ name +"</li>");
			namesTxt.append(name +"\n");			
		}

		String number = "" + docNames.size();

		contentTXT = StringUtils.replace(contentTXT, "${firstName}", actor.getFirstName());
		contentTXT = StringUtils.replace(contentTXT, "${lastName}", actor.getLastName());
		contentTXT = StringUtils.replace(contentTXT, "${number}", number);
        contentTXT = StringUtils.replace(contentTXT, "${documentNamesTxt}", namesTxt.toString());
        contentHTML = StringUtils.replace(contentHTML, "${firstName}", actor.getFirstName());
		contentHTML = StringUtils.replace(contentHTML, "${lastName}", actor.getLastName());
		contentHTML = StringUtils.replace(contentHTML, "${number}", number);
        contentHTML = StringUtils.replace(contentHTML, "${documentNames}", names.toString());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);

        return template;
	}
	
	private String getDirectDownloadLink(User recipient, DocumentVo doc) {
		final String path = getLinShareUrlForAUserRecipient(recipient);
		final String sep = path.endsWith("/") ? "" : "/";
		final String dl = path + sep + "index.listshareddocument.download/";
		return dl + doc.getIdentifier();
	}
	
	/**
	 * Template SHARE_NOTIFICATION
	 */
	private MailTemplate buildTemplateShareNotification(User actor, User recipient, Language language, List<ShareDocumentVo> shares)
			throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.SHARE_NOTIFICATION);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		StringBuffer names = new StringBuffer();
		StringBuffer namesTxt = new StringBuffer();
		long shareSize = 0;
		for (ShareDocumentVo share : shares) {
			if (recipient.getLsUuid().equals(share.getReceiver().getLsUuid())) {
				shareSize += 1;
				names.append("<li><a href='" + getDirectDownloadLink(recipient, share) + "'>" + share.getFileName() +"</a></li>");
				namesTxt.append(share.getFileName() + " <" + getDirectDownloadLink(recipient, share) + ">\n");
			}
		}

		String number = "" + shareSize;

		contentTXT = StringUtils.replace(contentTXT, "${firstName}", actor.getFirstName());
		contentTXT = StringUtils.replace(contentTXT, "${lastName}", actor.getLastName());
		contentTXT = StringUtils.replace(contentTXT, "${number}", number);
        contentTXT = StringUtils.replace(contentTXT, "${documentNamesTxt}", namesTxt.toString());
        contentHTML = StringUtils.replace(contentHTML, "${firstName}", actor.getFirstName());
		contentHTML = StringUtils.replace(contentHTML, "${lastName}", actor.getLastName());
		contentHTML = StringUtils.replace(contentHTML, "${number}", number);
        contentHTML = StringUtils.replace(contentHTML, "${documentNames}", names.toString());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);

        return template;
	}
	
	
	/**
	 * Template FILE_DOWNLOAD_URL
	 */
	private MailTemplate buildTemplateFileDownloadURL(User actor, Language language, String url) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.FILE_DOWNLOAD_URL);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		contentTXT = StringUtils.replace(contentTXT, "${url}", url);
		contentTXT = StringUtils.replace(contentTXT, "${urlparam}", "");
        contentHTML = StringUtils.replace(contentHTML, "${url}", url);
        contentHTML = StringUtils.replace(contentHTML, "${urlparam}", "");
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	
	/**
	 * Template PASSWORD_GIVING
	 */
	private MailTemplate buildTemplatePasswordGiving(User actor, Language language, String password) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.PASSWORD_GIVING);
		
		if (password != null && password.trim().length() > 0) {
			String contentTXT = template.getContentTXT();
			String contentHTML = template.getContentHTML();

			contentTXT = StringUtils.replace(contentTXT, "${password}", password);
	        contentHTML = StringUtils.replace(contentHTML, "${password}", password);

	        template.setContentTXT(contentTXT);
	        template.setContentHTML(contentHTML);
		}
		else {

	        template.setContentTXT("");
	        template.setContentHTML("");
		}
		
		return template;
	}
	
	
	
	
	/**
	 * Template FILE_UPDATED
	 */
	private MailTemplate buildTemplateFileUpdated(User actor, Language language, User owner, DocumentEntry document, String oldDocName, String fileSizeTxt) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.FILE_UPDATED);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		contentTXT = StringUtils.replace(contentTXT, "${firstName}", owner.getFirstName());
		contentTXT = StringUtils.replace(contentTXT, "${lastName}", owner.getLastName());
		contentTXT = StringUtils.replace(contentTXT, "${fileOldName}", oldDocName);
        contentTXT = StringUtils.replace(contentTXT, "${fileName}", document.getName());
		contentTXT = StringUtils.replace(contentTXT, "${fileSize}", fileSizeTxt);
        contentTXT = StringUtils.replace(contentTXT, "${mimeType}", document.getType());
        
        
        contentHTML = StringUtils.replace(contentHTML, "${firstName}", owner.getFirstName());
		contentHTML = StringUtils.replace(contentHTML, "${lastName}", owner.getLastName());
		contentHTML = StringUtils.replace(contentHTML, "${fileOldName}", oldDocName);
        contentHTML = StringUtils.replace(contentHTML, "${fileName}", document.getName());
        contentHTML = StringUtils.replace(contentHTML, "${fileSize}", fileSizeTxt);
        contentHTML = StringUtils.replace(contentHTML, "${mimeType}", document.getType());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	

	
	
	/**
	 * Template SHARED_FILE_DELETED
	 */
	private MailTemplate buildTemplateSharedFileDeleted(User actor, Language language, ShareEntry doc, User owner) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.SHARED_FILE_DELETED);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();
		
		contentTXT = StringUtils.replace(contentTXT, "${firstName}", owner.getFirstName());
		contentTXT = StringUtils.replace(contentTXT, "${lastName}", owner.getLastName());
        contentTXT = StringUtils.replace(contentTXT, "${documentName}", doc.getName());
        contentHTML = StringUtils.replace(contentHTML, "${firstName}", owner.getFirstName());
		contentHTML = StringUtils.replace(contentHTML, "${lastName}", owner.getLastName());
        contentHTML = StringUtils.replace(contentHTML, "${documentName}", doc.getName());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * MAIL CONTAINER BUILDER SECTION
	 * 
	 */
	
	
	
	/**
	 * fill in mail container with contact recipient
	 * @param sender
	 * @param mailContainer
	 * @param personalMessage
	 * @param contact
	 * @throws BusinessException
	 */
	private MailContainerWithRecipient buildMailContainerSetProperties(User sender, MailContainerWithRecipient mailContainer, Contact contact, String personalMessage) throws BusinessException {
		MailTemplate greetings = buildTemplateGreetings(sender, mailContainer.getLanguage(), contact);
		return buildMailContainerSetProperties(sender, mailContainer, personalMessage, greetings); 
	}
	
	private MailContainerWithRecipient buildMailContainerSetProperties(User sender, MailContainerWithRecipient mailContainer, Contact contact) throws BusinessException {
		MailTemplate greetings = buildTemplateGreetings(sender, mailContainer.getLanguage(), contact);
		// no personal message.
		return buildMailContainerSetProperties(sender, mailContainer, "", greetings); 
	}
	
	
	
	
	/**
	 * fill in mail container with user recipient
	 * @param sender
	 * @param mailContainer
	 * @param personalMessage
	 * @param recipient
	 * @throws BusinessException
	 */
	private MailContainerWithRecipient buildMailContainerSetProperties(User sender, MailContainerWithRecipient mailContainer, User recipient,  String personalMessage) throws BusinessException {
		MailTemplate greetings = buildTemplateGreetings(sender, mailContainer.getLanguage(), recipient);
		return buildMailContainerSetProperties(sender, mailContainer, personalMessage, greetings); 
	}
	
	private MailContainerWithRecipient buildMailContainerSetProperties(User sender, MailContainerWithRecipient mailContainer, User recipient) throws BusinessException {
		MailTemplate greetings = buildTemplateGreetings(sender, mailContainer.getLanguage(), recipient);
		// no personal message.
		return buildMailContainerSetProperties(sender, mailContainer, "", greetings); 
	}
	
	
	/**
	 * fill in mail container
	 * @param sender
	 * @param mailContainer
	 * @param personalMessage
	 * @param greetings
	 * @throws BusinessException
	 */
	private MailContainerWithRecipient buildMailContainerSetProperties(User sender, final MailContainerWithRecipient inputMailContainer, String personalMessage, MailTemplate greetings) throws BusinessException {
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(inputMailContainer);
		
		MailTemplate footer = buildTemplateFooter(sender, inputMailContainer.getLanguage());
		String contentTXT = this.mailContentTxt;
		String contentHTML = null;
		if (displayLogo) {
			contentHTML = this.mailContentHTML;
		} else {
			contentHTML = this.mailContentHTMLWithoutLogo;
		}
		
		if (personalMessage != null && personalMessage.trim().length() > 0) {
			MailTemplate personalMessageTemplate = buildTemplatePersonalMessage(sender, inputMailContainer.getLanguage(), personalMessage);

			contentTXT = StringUtils.replace(contentTXT, "${personalMessage}", personalMessageTemplate.getContentTXT());
	        contentHTML = StringUtils.replace(contentHTML, "${personalMessage}", personalMessageTemplate.getContentHTML());
			contentTXT = StringUtils.replace(contentTXT, "%{personalMessage}", personalMessageTemplate.getContentTXT());
	        contentHTML = StringUtils.replace(contentHTML, "%{personalMessage}", personalMessageTemplate.getContentHTML());
		}
		else {
			contentTXT = StringUtils.replace(contentTXT, "${personalMessage}", "");
	        contentHTML = StringUtils.replace(contentHTML, "${personalMessage}", "");
			contentTXT = StringUtils.replace(contentTXT, "%{personalMessage}", "");
	        contentHTML = StringUtils.replace(contentHTML, "%{personalMessage}", "");
		}
		
		
        contentTXT = StringUtils.replace(contentTXT, "${greetings}", greetings.getContentTXT());
        contentTXT = StringUtils.replace(contentTXT, "${footer}", footer.getContentTXT());
        contentTXT = StringUtils.replace(contentTXT, "${body}", inputMailContainer.getContentTXT());
        
        contentTXT = StringUtils.replace(contentTXT, "%{greetings}", greetings.getContentTXT());
        contentTXT = StringUtils.replace(contentTXT, "%{footer}", footer.getContentTXT());
        contentTXT = StringUtils.replace(contentTXT, "%{body}", inputMailContainer.getContentTXT());
        
        
        
        contentHTML = StringUtils.replace(contentHTML, "${greetings}", greetings.getContentHTML());
        contentHTML = StringUtils.replace(contentHTML, "${footer}", footer.getContentHTML());
        contentHTML = StringUtils.replace(contentHTML, "${body}", inputMailContainer.getContentHTML());
        contentHTML = StringUtils.replace(contentHTML, "${mailSubject}", inputMailContainer.getSubject());
        
        contentHTML = StringUtils.replace(contentHTML, "%{greetings}", greetings.getContentHTML());
        contentHTML = StringUtils.replace(contentHTML, "%{footer}", footer.getContentHTML());
        contentHTML = StringUtils.replace(contentHTML, "%{body}", inputMailContainer.getContentHTML());
        contentHTML = StringUtils.replace(contentHTML, "%{mailSubject}", inputMailContainer.getSubject());
        
        
        
        mailContainer.setContentTXT(contentTXT);
        mailContainer.setContentHTML(contentHTML);
        	
        if(logger.isDebugEnabled()) {
        	logger.debug("Subject : " + mailContainer.getSubject());
        	logger.debug("ContentTXT : " + mailContainer.getContentTXT());
        }
        
        // Message IDs from Thunderbird Plugin
        mailContainer.setInReplyTo(inputMailContainer.getInReplyTo());
        mailContainer.setReferences(inputMailContainer.getReferences());
        
        return mailContainer;
	}
}
