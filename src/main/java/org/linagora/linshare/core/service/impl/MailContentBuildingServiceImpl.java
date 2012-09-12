/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.domain.constants.AccountType;
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
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MailContentBuildingServiceImpl implements MailContentBuildingService {
	private final String pUrlBase;
	private final String pUrlInternal;
	private final String mailContentTxt;
	private final String mailContentHTML;
	private final String mailContentHTMLWithoutLogo;
	private final boolean displayLogo;
	
	private final static Logger logger = LoggerFactory.getLogger(MailContentBuildingServiceImpl.class);

	public MailContentBuildingServiceImpl(final String urlBase, 
			final String urlInternal, final String mailContentTxt,
			final String mailContentHTML, final String mailContentHTMLWithoutLogo,
			final boolean displayLogo) throws BusinessException {
		this.pUrlBase = urlBase;
		this.pUrlInternal = urlInternal;
        this.mailContentTxt = mailContentTxt;
        this.mailContentHTML = mailContentHTML;
        this.mailContentHTMLWithoutLogo = mailContentHTMLWithoutLogo;
        this.displayLogo = displayLogo;
	}

	
	/**
	 * Retrieve the mail subject from config.
	 * 
	 * @param language the language of the email
	 * @param mailSubject the enum key
	 * @return the MailSubject object
	 * @throws BusinessException when no mail subject was found for the given enum key
	 */
	private MailSubject getMailSubject(User actor, Language language, MailSubjectEnum mailSubject) throws BusinessException {
		AbstractDomain domain = actor.getDomain();
		Set<MailSubject> subjects = domain.getMessagesConfiguration().getMailSubjects();
		
		for (MailSubject mailSubject_ : subjects) {
			if (mailSubject_.getLanguage().equals(language) && mailSubject_.getMailSubject().equals(mailSubject)) {
				return new MailSubject(mailSubject_);
			}
		}

		logger.error("Bad mail subject "+ mailSubject.name() +" for language " + language.name());
		throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION,"Bad mail subject "+ mailSubject.name() +" for language " + language.name());
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
	 * Build the final mail content.
	 * @param mailContainer
	 * @param subject
	 * @param bodyTXT
	 * @param bodyHTML
	 * @param recipient
	 * @param personalMessage
	 * 
	 * @throws BusinessException
	 */
	private void buildMailContainerSetProperties(User actor, MailContainer mailContainer, String subject, String bodyTXT, String bodyHTML, User recipient, String personalMessage) throws BusinessException {
		MailTemplate greetings = buildTemplateGreetings(actor, mailContainer.getLanguage(), recipient);
		MailTemplate footer = buildTemplateFooter(actor, mailContainer.getLanguage());
		String contentTXT = this.mailContentTxt;
		String contentHTML = null;
		if (displayLogo) {
			contentHTML = this.mailContentHTML;
		} else {
			contentHTML = this.mailContentHTMLWithoutLogo;
		}
		
		if (personalMessage != null && personalMessage.trim().length() > 0) {
			MailTemplate personalMessageTemplate = buildTemplatePersonalMessage(actor, mailContainer.getLanguage(), personalMessage);

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
        contentTXT = StringUtils.replace(contentTXT, "${body}", bodyTXT);
        contentHTML = StringUtils.replace(contentHTML, "${greetings}", greetings.getContentHTML());
        contentHTML = StringUtils.replace(contentHTML, "${footer}", footer.getContentHTML());
        contentHTML = StringUtils.replace(contentHTML, "${body}", bodyHTML);
        contentHTML = StringUtils.replace(contentHTML, "${mailSubject}", subject);
        contentTXT = StringUtils.replace(contentTXT, "%{greetings}", greetings.getContentTXT());
        contentTXT = StringUtils.replace(contentTXT, "%{footer}", footer.getContentTXT());
        contentTXT = StringUtils.replace(contentTXT, "%{body}", bodyTXT);
        contentHTML = StringUtils.replace(contentHTML, "%{greetings}", greetings.getContentHTML());
        contentHTML = StringUtils.replace(contentHTML, "%{footer}", footer.getContentHTML());
        contentHTML = StringUtils.replace(contentHTML, "%{body}", bodyHTML);
        contentHTML = StringUtils.replace(contentHTML, "%{mailSubject}", subject);
        
        mailContainer.setContentTXT(contentTXT);
        mailContainer.setContentHTML(contentHTML);
        mailContainer.setSubject(subject);
        	
	}
	
	
	/**
	 * Build the final mail content.
	 * 
	 * @param mailContainer
	 * @param subject
	 * @param bodyTXT
	 * @param bodyHTML
	 * @param recipient
	 * @param owner
	 * @param personalMessage
	 * @throws BusinessException
	 */
	private MailContainer buildMailContainer(User actor, final MailContainer mailContainerInitial, String subject, String bodyTXT, String bodyHTML, User recipient, User owner, String personalMessage) throws BusinessException {
		MailContainer mailContainer = new MailContainer(mailContainerInitial);
		buildMailContainerSetProperties(actor, mailContainer, subject,  bodyTXT,  bodyHTML,  recipient,  personalMessage);
		return mailContainer;
	}
	
	/**
	 * Build the final mail content with recipient.
	 * @param actor
	 * @param replyTo
	 * @param subject
	 * @param bodyTXT
	 * @param bodyHTML
	 * @param recipient
	 * @param personalMessage
	 * @param mailContainer
	 * @throws BusinessException
	 */
	private MailContainerWithRecipient buildMailContainerWithRecipient(User actor, User replyTo, final MailContainer mailContainerInitial, String subject, String bodyTXT,
			String bodyHTML, User recipient, String personalMessage) throws BusinessException {
		
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(mailContainerInitial,recipient.getMail());
		if (replyTo != null) {
			mailContainer.setReplyTo(replyTo.getMail());
		}
        buildMailContainerSetProperties(actor, mailContainer, subject,  bodyTXT,  bodyHTML,  recipient,  personalMessage);
        return mailContainer;
	}	
	
	
	/**
	 * Template GREETINGS
	 */
	private MailTemplate buildTemplateGreetings(User actor, Language language, Contact contact) throws BusinessException {
		User tempUser = new Guest("", "", contact.getMail());
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
        
        return template;
	}
	
	/**
	 * Template CONFIRM_DOWNLOAD_ANONYMOUS
	 */
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
	
	
	// to be removed
	private MailTemplate buildTemplateDecryptUrl(User actor, Language language, boolean hasToBeFilled, String jwsEncryptUrl) throws BusinessException {
		return null;
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
	private MailTemplate buildTemplateGuestInvitation(User actor, Language language, User owner) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.GUEST_INVITATION);
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
	private MailTemplate buildTemplateShareNotification(User actor, Language language, List<DocumentVo> docs, User owner) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.SHARE_NOTIFICATION);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		StringBuffer names = new StringBuffer();
		StringBuffer namesTxt = new StringBuffer();
		if (docs != null && docs.size()>0) {
			for (DocumentVo doc : docs) {
				names.append("<li>"+doc.getFileName()+"</li>");
				namesTxt.append(doc.getFileName()+"\n");
			}	
		}
		
		String number = new Integer(docs.size()).toString();

		contentTXT = StringUtils.replace(contentTXT, "${firstName}", owner.getFirstName());
		contentTXT = StringUtils.replace(contentTXT, "${lastName}", owner.getLastName());
		contentTXT = StringUtils.replace(contentTXT, "${number}", number);
        contentTXT = StringUtils.replace(contentTXT, "${documentNamesTxt}", namesTxt.toString());
        contentHTML = StringUtils.replace(contentHTML, "${firstName}", owner.getFirstName());
		contentHTML = StringUtils.replace(contentHTML, "${lastName}", owner.getLastName());
		contentHTML = StringUtils.replace(contentHTML, "${number}", number);
        contentHTML = StringUtils.replace(contentHTML, "${documentNames}", names.toString());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	

	/**
	 * Template SHARE_NOTIFICATION
	 */
	private MailTemplate buildTemplateShareNotificationV2(User actor, Language language, List<String> docNames) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.SHARE_NOTIFICATION);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		StringBuffer names = new StringBuffer();
		StringBuffer namesTxt = new StringBuffer();
		for (String name : docNames) {
			names.append("<li>"+ name +"</li>");
			namesTxt.append(name +"\n");			
		}

		String number = new Integer(docNames.size()).toString();

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

	
	
	@Override
	public MailContainer buildMailAnonymousDownload(User actor, MailContainer mailContainer, List<String> docs, String email, User recipient) throws BusinessException {
		MailTemplate template = buildTemplateConfirmDownloadAnonymous(actor, mailContainer.getLanguage(), docs, email);
		MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.ANONYMOUS_DOWNLOAD);
		return buildMailContainer(actor, mailContainer, subject.getContent(), template.getContentTXT(), template.getContentHTML(), recipient, null, null);
	}
	
	
	@Override
	public List<MailContainerWithRecipient> buildMailAnonymousDownloadWithOneRecipient(User actor, MailContainer mailContainer, List<String> docs, String email, User recipient) throws BusinessException {
		MailTemplate template = buildTemplateConfirmDownloadAnonymous(actor, mailContainer.getLanguage(), docs, email);
		MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.ANONYMOUS_DOWNLOAD);
		
		
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
		mailContainerWithRecipient.add(buildMailContainerWithRecipient(actor, actor, mailContainer, subject.getContent(), template.getContentTXT(), template.getContentHTML(), recipient, null));
		return mailContainerWithRecipient;
	}	
	
	
	@Override
	public List<MailContainerWithRecipient> buildMailRegisteredDownloadWithOneRecipient(User actor, MailContainer mailContainer, List<String> docNames, User downloadingUser, User recipient) throws BusinessException {
		MailTemplate template = buildTemplateConfirmDownloadRegistered(actor, mailContainer.getLanguage(), docNames, downloadingUser);
		MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.REGISTERED_DOWNLOAD);
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
		mailContainerWithRecipient.add(buildMailContainerWithRecipient(actor, actor, mailContainer, subject.getContent(), template.getContentTXT(), template.getContentHTML(), recipient, null));
		return mailContainerWithRecipient;
	}	
	
	
	@Override
	public MailContainer buildMailNewGuest(User actor, MailContainer mailContainer, User owner, User recipient, String password) throws BusinessException {
		MailTemplate template1 = buildTemplateGuestInvitation(actor, mailContainer.getLanguage(), owner);
		MailTemplate template2 = buildTemplateLinshareURL(actor, mailContainer.getLanguage(), pUrlBase);
		MailTemplate template3 = buildTemplateAccountDescription(actor, mailContainer.getLanguage(), recipient, password);
		MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.NEW_GUEST);
		
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentTXT.append(template2.getContentTXT() + "\n");
		contentTXT.append(template3.getContentTXT() + "\n");
		contentHTML.append(template1.getContentHTML() + "<br />");
		contentHTML.append(template2.getContentHTML() + "<br />");
		contentHTML.append(template3.getContentHTML() + "<br />");
		
		return buildMailContainer(actor, mailContainer, subject.getContent(), contentTXT.toString(), contentHTML.toString(), recipient, owner, mailContainer.getPersonalMessage());
	}
	
	@Override
	public MailContainerWithRecipient buildMailNewGuestWithRecipient(User actor, MailContainer mailContainer, User owner, User recipient, String password) throws BusinessException {
		MailTemplate template1 = buildTemplateGuestInvitation(actor, mailContainer.getLanguage(), owner);
		MailTemplate template2 = buildTemplateLinshareURL(actor, mailContainer.getLanguage(), pUrlBase);
		MailTemplate template3 = buildTemplateAccountDescription(actor, mailContainer.getLanguage(), recipient, password);
		MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.NEW_GUEST);
		
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentTXT.append(template2.getContentTXT() + "\n");
		contentTXT.append(template3.getContentTXT() + "\n");
		contentHTML.append(template1.getContentHTML() + "<br />");
		contentHTML.append(template2.getContentHTML() + "<br />");
		contentHTML.append(template3.getContentHTML() + "<br />");
		
		return buildMailContainerWithRecipient(actor, actor, mailContainer, subject.getContent(), contentTXT.toString(), contentHTML.toString(), recipient, mailContainer.getPersonalMessage());
	}	
	
	
	@Override
	public List<MailContainerWithRecipient> buildMailNewGuestWithOneRecipient(User actor, MailContainer mailContainer, User owner, User recipient, String password) throws BusinessException {
		MailTemplate template1 = buildTemplateGuestInvitation(actor, mailContainer.getLanguage(), owner);
		MailTemplate template2 = buildTemplateLinshareURL(actor, mailContainer.getLanguage(), pUrlBase);
		MailTemplate template3 = buildTemplateAccountDescription(actor, mailContainer.getLanguage(), recipient, password);
		MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.NEW_GUEST);
		
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentTXT.append(template2.getContentTXT() + "\n");
		contentTXT.append(template3.getContentTXT() + "\n");
		contentHTML.append(template1.getContentHTML() + "<br />");
		contentHTML.append(template2.getContentHTML() + "<br />");
		contentHTML.append(template3.getContentHTML() + "<br />");
		
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
		
		mailContainerWithRecipient.add(buildMailContainerWithRecipient(actor, actor, mailContainer, subject.getContent(), contentTXT.toString(), contentHTML.toString(), recipient, mailContainer.getPersonalMessage()));
		
		return mailContainerWithRecipient;
	}
	
	
	@Override
	public MailContainer buildMailResetPassword(User actor, MailContainer mailContainer, User recipient, String password) throws BusinessException {
		MailTemplate template = buildTemplateAccountDescription(actor, mailContainer.getLanguage(), recipient, password);
		MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.RESET_PASSWORD);
		
		return buildMailContainer(actor, mailContainer, subject.getContent(), template.getContentTXT(), template.getContentHTML(), recipient, null, null);
	}
	
	
	@Override
	public MailContainerWithRecipient buildMailResetPasswordWithRecipient(User actor, MailContainer mailContainer, User recipient, String password) throws BusinessException {
		MailTemplate template = buildTemplateAccountDescription(actor, mailContainer.getLanguage(), recipient, password);
		MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.RESET_PASSWORD);
		
		return buildMailContainerWithRecipient(actor, null, mailContainer, subject.getContent(), template.getContentTXT(), template.getContentHTML(), recipient, null);
	}	
	
	
	@Override
	public List<MailContainerWithRecipient> buildMailResetPasswordWithOneRecipient(User actor, MailContainer mailContainer, User recipient, String password) throws BusinessException {
		MailTemplate template = buildTemplateAccountDescription(actor, mailContainer.getLanguage(), recipient, password);
		MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.RESET_PASSWORD);
		
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
		
		mailContainerWithRecipient.add(buildMailContainerWithRecipient(actor, null, mailContainer, subject.getContent(), template.getContentTXT(), template.getContentHTML(), recipient, null));
		
		return mailContainerWithRecipient;
	}	
	
	
	@Override
	public MailContainer buildMailNewSharing(User actor, MailContainer mailContainer, User owner, User recipient, List<DocumentVo> docs, String linShareUrl, String linShareUrlParam, String password, boolean hasToDecrypt, String jwsEncryptUrl) throws BusinessException {
		MailTemplate template1 = buildTemplateShareNotification(actor, mailContainer.getLanguage(), docs, owner);
		MailTemplate template2 = buildTemplateFileDownloadURL(actor, mailContainer.getLanguage(), linShareUrl);
		MailTemplate template3 = buildTemplateDecryptUrl(actor, mailContainer.getLanguage(), hasToDecrypt, jwsEncryptUrl);
		MailTemplate template4 = buildTemplatePasswordGiving(actor, mailContainer.getLanguage(), password);
		
		String subjectContent = mailContainer.getSubject();
		if (subjectContent == null || subjectContent.length() < 1) {
			MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.NEW_SHARING);
			subjectContent = subject.getContent();
		}
		
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentTXT.append(template2.getContentTXT() + "\n");
		contentTXT.append(template3.getContentTXT() + "\n");
		contentTXT.append(template4.getContentTXT() + "\n");
		contentHTML.append(template1.getContentHTML() + "<br />");
		contentHTML.append(template2.getContentHTML() + "<br />");
		contentHTML.append(template3.getContentHTML() + "<br />");
		contentHTML.append(template4.getContentHTML() + "<br />");
		
		return buildMailContainer(actor, mailContainer, subjectContent, contentTXT.toString(), contentHTML.toString(), recipient, owner, mailContainer.getPersonalMessage());
	}
	
	
//	@Override
//	public MailContainerWithRecipient buildMailNewSharingWithRecipient(User actor, MailContainer mailContainer, User recipient, List<String> docNames, String linShareUrl,
//			String linShareUrlParam, String password, boolean hasToDecrypt) throws BusinessException {
//		MailTemplate template1 = buildTemplateShareNotificationV2(actor, mailContainer.getLanguage(), docNames);
//		
//		MailTemplate template2 = buildTemplateFileDownloadURL(actor, mailContainer.getLanguage(), linShareUrl);
//		
//		String jwsEncryptUrl  = "";
//		if(hasToDecrypt) {
//			jwsEncryptUrl = getJwsEncryptUrlString();
//		}
//			
//			
//		MailTemplate template3 = buildTemplateDecryptUrl(actor, mailContainer.getLanguage(), hasToDecrypt, jwsEncryptUrl);
//		MailTemplate template4 = buildTemplatePasswordGiving(actor, mailContainer.getLanguage(), password);
//		
//		String subjectContent = mailContainer.getSubject();
//		if (subjectContent == null || subjectContent.length() < 1) {
//			MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.NEW_SHARING);
//			subjectContent = subject.getContent();
//		}
//		
//		StringBuffer contentTXT = new StringBuffer();
//		StringBuffer contentHTML = new StringBuffer();
//		contentTXT.append(template1.getContentTXT() + "\n");
//		contentTXT.append(template2.getContentTXT() + "\n");
//		contentTXT.append(template3.getContentTXT() + "\n");
//		contentTXT.append(template4.getContentTXT() + "\n");
//		contentHTML.append(template1.getContentHTML() + "<br />");
//		contentHTML.append(template2.getContentHTML() + "<br />");
//		contentHTML.append(template3.getContentHTML() + "<br />");
//		contentHTML.append(template4.getContentHTML() + "<br />");
//		
//		return buildMailContainerWithRecipient(actor, actor, mailContainer, subjectContent, contentTXT.toString(), contentHTML.toString(), recipient, mailContainer.getPersonalMessage());
//	}
	
	
	@Override
	public MailContainerWithRecipient buildMailNewSharingWithRecipient(User actor, MailContainer mailContainer, User recipient, List<String> docNames) throws BusinessException {
		String linShareUrlParam = "";
		
		String linshareUrl = pUrlInternal;
		if(recipient.getAccountType().equals(AccountType.GUEST)) {
			linshareUrl =  pUrlBase;
		}
		

		MailTemplate template1 = buildTemplateShareNotificationV2(actor, mailContainer.getLanguage(), docNames);
		MailTemplate template2 = buildTemplateFileDownloadURL(actor, mailContainer.getLanguage(), linshareUrl);
		
		String subjectContent = mailContainer.getSubject();
		if (subjectContent == null || subjectContent.length() < 1) {
			MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.NEW_SHARING);
			subjectContent = subject.getContent();
		}
		
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentTXT.append(template2.getContentTXT() + "\n");

		contentHTML.append(template1.getContentHTML() + "<br />");
		contentHTML.append(template2.getContentHTML() + "<br />");
		
		return buildMailContainerWithRecipient(actor, actor, mailContainer, subjectContent, contentTXT.toString(), contentHTML.toString(), recipient, mailContainer.getPersonalMessage());
	}

	
	@Override
	public MailContainerWithRecipient buildMailNewSharingWithRecipient(User actor, MailContainer mailContainer, Contact recipient, List<String> docNames, AnonymousUrl anonymousUrl, boolean hasToDecrypt) throws BusinessException {
		
		MailTemplate template1 = buildTemplateShareNotificationV2(actor, mailContainer.getLanguage(), docNames);
		MailTemplate template2 = buildTemplateFileDownloadURL(actor, mailContainer.getLanguage(), anonymousUrl.getFullUrl(pUrlBase));
		MailTemplate template3 = null;
		if(hasToDecrypt) {
			String jwsEncryptUrl = getJwsEncryptUrlString();
			template3 = buildTemplateDecryptUrl(actor, mailContainer.getLanguage(), hasToDecrypt, jwsEncryptUrl);
		}
		MailTemplate template4 = buildTemplatePasswordGiving(actor, mailContainer.getLanguage(), anonymousUrl.getTemporaryPlainTextPassword());
		
		String subjectContent = mailContainer.getSubject();
		if (subjectContent == null || subjectContent.length() < 1) {
			MailSubject subject = getMailSubject(actor, mailContainer.getLanguage(), MailSubjectEnum.NEW_SHARING);
			subjectContent = subject.getContent();
		}
		
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentTXT.append(template2.getContentTXT() + "\n");
		if(template3 != null) {
			contentTXT.append(template3.getContentTXT() + "\n");
		}
		contentTXT.append(template4.getContentTXT() + "\n");
		
		
		contentHTML.append(template1.getContentHTML() + "<br />");
		contentHTML.append(template2.getContentHTML() + "<br />");
		if(template3 != null) {
			contentHTML.append(template3.getContentHTML() + "<br />");
		}
		contentHTML.append(template4.getContentHTML() + "<br />");
		
        // nasty hook : very big s***
        User tempUser = new Guest("", "", recipient.getMail());
		return buildMailContainerWithRecipient(actor, actor, mailContainer, subjectContent, contentTXT.toString(), contentHTML.toString(), tempUser, mailContainer.getPersonalMessage());
	}
	
	
	@Override
	public MailContainer buildMailNewSharing(User actor, MailContainer mailContainer,
			User owner, String recipientMail, List<DocumentVo> docs,
			String linShareUrl, String linShareUrlParam, String password, 
			boolean hasToDecrypt, String jwsEncryptUrl)
			throws BusinessException {
		User tempUser = new Guest(recipientMail, "", recipientMail);
		return buildMailNewSharing(actor, mailContainer, owner, tempUser, docs, linShareUrl, linShareUrlParam, password, hasToDecrypt, jwsEncryptUrl);
	}

	
	
	

	
	

	
	
	
	
	
	
	
	
	/**
	 * 
	 * TOOLS
	 * 
	 */
	
	private String getLinShareRootUrl(Account recipient) {
		String linshareUrl = pUrlBase;
		if(recipient.getAccountType().equals(AccountType.INTERNAL)) {
			linshareUrl =  pUrlInternal;
		}
		return linshareUrl;
	}
	
	
	private String getJwsEncryptUrlString() {
		String jwsEncryptUrlString = "";
		StringBuffer jwsEncryptUrl = new StringBuffer();
		jwsEncryptUrl.append(pUrlBase);
		if(!pUrlBase.endsWith("/")) jwsEncryptUrl.append("/");
		jwsEncryptUrl.append("localDecrypt");
		jwsEncryptUrlString = jwsEncryptUrl.toString();
		return jwsEncryptUrlString;
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
		User sender = (User) shareEntry.getEntryOwner();
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		String linShareRootUrl = getLinShareRootUrl(sender);
		

		// expired share notification 
		mailContainer.appendTemplate(buildTemplateUpcomingOutdatedShare(sender, mailContainer.getLanguage(), shareEntry, days));
		
		// download URL
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(sender, mailContainer.getLanguage(), linShareRootUrl));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.SHARED_DOC_UPCOMING_OUTDATED));

		// recipient mail
		mailContainer.setRecipient(shareEntry.getRecipient().getMail());

		// reply mail
		mailContainer.setReplyTo(sender.getMail());
				
		return buildMailContainerSetProperties(sender, mailContainer, shareEntry.getRecipient());
	}

	
	/**
	 * notification for expired anonymous shares
	 */
	@Override
	public MailContainerWithRecipient buildMailUpcomingOutdatedShare(AnonymousShareEntry shareEntry, Integer days) throws BusinessException {
		
		// sharing is only possible between users.
		User sender = (User) shareEntry.getEntryOwner();
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		String linShareRootUrl = shareEntry.getAnonymousUrl().getFullUrl(pUrlBase);
		

		// expired share notification 
		mailContainer.appendTemplate(buildTemplateUpcomingOutdatedShare(sender, mailContainer.getLanguage(), shareEntry, days));
		
		// download URL
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(sender, mailContainer.getLanguage(), linShareRootUrl));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.SHARED_DOC_UPCOMING_OUTDATED));

		// recipient mail
		mailContainer.setRecipient(shareEntry.getContact().getMail());

		// reply mail
		mailContainer.setReplyTo(sender.getMail());
				
		return buildMailContainerSetProperties(sender, mailContainer, shareEntry.getContact());
	}
	
	
	/**
	 * notification for expired document entries.
	 */
	@Override
	public MailContainerWithRecipient buildMailUpcomingOutdatedDocument(DocumentEntry document, Integer days) throws BusinessException {
		
		User sender = (User) document.getEntryOwner();
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		String linShareRootUrl = getLinShareRootUrl(sender);
		

		// expired file notification 
		mailContainer.appendTemplate(buildTemplateUpcomingOutdatedFile(sender, mailContainer.getLanguage(), document, days));
		
		// download URL
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(sender, mailContainer.getLanguage(), linShareRootUrl));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.DOC_UPCOMING_OUTDATED));

		// recipient mail
		mailContainer.setRecipient(sender.getMail());

		// sender and recipient are the same person.
		return buildMailContainerSetProperties(sender, mailContainer, sender);
	}
	
	
	/**
	 * update notification for shared document 
	 */
	@Override
	public MailContainerWithRecipient buildMailSharedDocumentUpdated(AnonymousShareEntry shareEntry, String oldDocName, String fileSizeTxt)throws BusinessException {
		
		User sender = (User) shareEntry.getEntryOwner();
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		String linShareRootUrl = shareEntry.getAnonymousUrl().getFullUrl(pUrlBase);
		
		// file updated notification
		mailContainer.appendTemplate(buildTemplateFileUpdated(sender, mailContainer.getLanguage(), sender, shareEntry.getDocumentEntry(), oldDocName, fileSizeTxt));
		
		// download URL
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(sender, mailContainer.getLanguage(), linShareRootUrl));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.SHARED_DOC_UPDATED));
		
		// recipient mail
		mailContainer.setRecipient(shareEntry.getContact().getMail());
		
		// reply mail
		mailContainer.setReplyTo(sender.getMail());
		
		return buildMailContainerSetProperties(sender, mailContainer, shareEntry.getContact());
	}
	
	
	/**
	 * update notification for anonymous shared document 
	 */
	@Override
	public MailContainerWithRecipient buildMailSharedDocumentUpdated(ShareEntry shareEntry, String oldDocName, String fileSizeTxt) throws BusinessException {
		User sender = (User) shareEntry.getEntryOwner();
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		String linShareRootUrl = getLinShareRootUrl(sender);
		
		// file updated notification
		mailContainer.appendTemplate(buildTemplateFileUpdated(sender, mailContainer.getLanguage(), sender, shareEntry.getDocumentEntry(), oldDocName, fileSizeTxt));
		
		// download URL
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(sender, mailContainer.getLanguage(), linShareRootUrl));
		
		// subject
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.SHARED_DOC_UPDATED));
		
		// recipient mail
		mailContainer.setRecipient(shareEntry.getRecipient().getMail());
		
		// reply mail
		mailContainer.setReplyTo(sender.getMail());
		
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
		mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.SHARED_DOC_DELETED));
		
		// recipient mail
		mailContainer.setRecipient(shareEntry.getRecipient().getMail());
		
		// reply mail
		mailContainer.setReplyTo(sender.getMail());
		
		return buildMailContainerSetProperties(sender, mailContainer, shareEntry.getRecipient());
	}	
	
	/**
	 * notification for shared document 
	 */
	@Override
	public MailContainerWithRecipient buildMailNewSharingWithRecipient(User sender, MailContainer inputMailContainer, User recipient, 
			List<String> docNames, String linShareUrl, String linShareUrlParam, String password, boolean hasToDecrypt) throws BusinessException {
		
		MailContainerWithRecipient mailContainer = new MailContainerWithRecipient(sender.getExternalMailLocale());
		String linShareRootUrl = getLinShareRootUrl(sender);
		
		// share notification
		mailContainer.appendTemplate(buildTemplateShareNotificationV2(sender, mailContainer.getLanguage(), docNames));
		
		// LinShare URL
		mailContainer.appendTemplate(buildTemplateFileDownloadURL(sender, mailContainer.getLanguage(), linShareRootUrl));
		
		// Direct download Url
		if(hasToDecrypt) {
			mailContainer.appendTemplate(buildTemplateDecryptUrl(sender, mailContainer.getLanguage()));
		}
		
		// Password notification
		if(password != null && password.trim().length() > 0) {
			mailContainer.appendTemplate(buildTemplatePasswordGiving(sender, mailContainer.getLanguage(), password));
		}
		
		// subject
		String subjectContent = inputMailContainer.getSubject();
		if (subjectContent != null && subjectContent.length() >= 1) {
			// this means subject was filled by users 
			mailContainer.setSubject(subjectContent);
		} else {
			mailContainer.setMailSubject(getMailSubject(sender, mailContainer.getLanguage(), MailSubjectEnum.NEW_SHARING));
		}
		
		// reply mail
		mailContainer.setReplyTo(sender.getMail());
		
		// recipient mail
		mailContainer.setRecipient(recipient.getMail());
		
		return buildMailContainerSetProperties(sender, mailContainer, recipient, inputMailContainer.getPersonalMessage());
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
	 */
	private MailTemplate buildTemplateDecryptUrl(User actor, Language language) throws BusinessException {
		MailTemplate template = getMailTemplate(actor, language, MailTemplateEnum.DECRYPT_URL);
		
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();
		
		String jwsEncryptUrl = getJwsEncryptUrlString();
		contentTXT = StringUtils.replace(contentTXT, "${jwsEncryptUrl}", jwsEncryptUrl);
        contentHTML = StringUtils.replace(contentHTML, "${jwsEncryptUrl}", jwsEncryptUrl);
		
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
        
        return mailContainer;
	}
	
	
	
}
