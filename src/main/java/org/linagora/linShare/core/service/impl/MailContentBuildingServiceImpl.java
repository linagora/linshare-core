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
package org.linagora.linShare.core.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linShare.core.domain.constants.Language;
import org.linagora.linShare.core.domain.constants.MailSubjectEnum;
import org.linagora.linShare.core.domain.constants.MailTemplateEnum;
import org.linagora.linShare.core.domain.entities.Contact;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.Group;
import org.linagora.linShare.core.domain.entities.GroupMember;
import org.linagora.linShare.core.domain.entities.GroupMembershipStatus;
import org.linagora.linShare.core.domain.entities.Guest;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.MailSubject;
import org.linagora.linShare.core.domain.entities.MailTemplate;
import org.linagora.linShare.core.domain.entities.Parameter;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.service.MailContentBuildingService;
import org.linagora.linShare.core.service.ParameterService;


public class MailContentBuildingServiceImpl implements MailContentBuildingService {
	private final ParameterService parameterService;
    private final static Log logger = LogFactory.getLog(MailContentBuildingServiceImpl.class);

	
	public MailContentBuildingServiceImpl(final ParameterService parameterService) throws BusinessException {
		this.parameterService = parameterService;
	}

	/**
	 * Retrieve the mail subject from config.
	 * 
	 * @param language the language of the email
	 * @param mailSubject the enum key
	 * @return the MailSubject object
	 * @throws BusinessException when no mail subject was found for the given enum key
	 */
	private MailSubject getMailSubject(Language language, MailSubjectEnum mailSubject) throws BusinessException {
		Parameter config = parameterService.loadConfig();
		Set<MailSubject> subjects = config.getMailSubjects();
		
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
	private MailTemplate getMailTemplate(Language language, MailTemplateEnum mailTemplate) throws BusinessException {
		Parameter config = parameterService.loadConfig();
		Set<MailTemplate> templates = config.getMailTemplates();
		
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
	private MailContainer buildMailContainer(MailContainer mailContainerInitial, String subject, String bodyTXT, String bodyHTML, User recipient, User owner, String personalMessage) throws BusinessException {
		MailContainer mailContainer = new MailContainer(mailContainerInitial);
		MailTemplate greetings = buildTemplateGreetings(mailContainer.getLanguage(), recipient);
		MailTemplate footer = buildTemplateFooter(mailContainer.getLanguage());
		String contentTXT = mailContainer.getContentTXT();
		String contentHTML = mailContainer.getContentHTML();
		
		if (personalMessage != null && personalMessage.trim().length() > 0) {
			MailTemplate personalMessageTemplate = buildTemplatePersonalMessage(mailContainer.getLanguage(), owner, personalMessage);

			contentTXT = StringUtils.replace(contentTXT, "${personalMessage}", personalMessageTemplate.getContentTXT());
	        contentHTML = StringUtils.replace(contentHTML, "${personalMessage}", personalMessageTemplate.getContentHTML());
		}
		else {
			contentTXT = StringUtils.replace(contentTXT, "${personalMessage}", "");
	        contentHTML = StringUtils.replace(contentHTML, "${personalMessage}", "");
		}
		
        contentTXT = StringUtils.replace(contentTXT, "${greetings}", greetings.getContentTXT());
        contentTXT = StringUtils.replace(contentTXT, "${footer}", footer.getContentTXT());
        contentTXT = StringUtils.replace(contentTXT, "${body}", bodyTXT);
        contentHTML = StringUtils.replace(contentHTML, "${greetings}", greetings.getContentHTML());
        contentHTML = StringUtils.replace(contentHTML, "${footer}", footer.getContentHTML());
        contentHTML = StringUtils.replace(contentHTML, "${body}", bodyHTML);
        contentHTML = StringUtils.replace(contentHTML, "${mailSubject}", subject);
        
        mailContainer.setContentTXT(contentTXT);
        mailContainer.setContentHTML(contentHTML);
        mailContainer.setSubject(subject);
        
        return mailContainer;
	}
	
	/**
	 * Template GREETINGS
	 */
	private MailTemplate buildTemplateGreetings(Language language, User user) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.GREETINGS);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		contentTXT = StringUtils.replace(contentTXT, "${firstName}", user.getFirstName());
        contentTXT = StringUtils.replace(contentTXT, "${lastName}", user.getLastName());
        contentHTML = StringUtils.replace(contentHTML, "${firstName}", user.getFirstName());
        contentHTML = StringUtils.replace(contentHTML, "${lastName}", user.getLastName());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	/**
	 * Template FOOTER
	 */
	private MailTemplate buildTemplateFooter(Language language) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.FOOTER);
        
        return template;
	}
	
	/**
	 * Template CONFIRM_DOWNLOAD_ANONYMOUS
	 */
	private MailTemplate buildTemplateConfirmDownloadAnonymous(Language language, List<Document> docs, String email) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.CONFIRM_DOWNLOAD_ANONYMOUS);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		StringBuffer names = new StringBuffer();
		StringBuffer namesTxt = new StringBuffer();
		if (docs != null && docs.size()>0) {
			for (Document doc : docs) {
				names.append("<li>"+doc.getName()+"</li>");
				namesTxt.append(doc.getName()+"\n");
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
	private MailTemplate buildTemplateConfirmDownloadRegistered(Language language, List<Document> docs, User recipient) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.CONFIRM_DOWNLOAD_REGISTERED);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		StringBuffer names = new StringBuffer();
		StringBuffer namesTxt = new StringBuffer();
		if (docs != null && docs.size()>0) {
			for (Document doc : docs) {
				names.append("<li>"+doc.getName()+"</li>");
				namesTxt.append(doc.getName()+"\n");
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
	 * Template DECRYPT_URL
	 */
	private MailTemplate buildTemplateDecryptUrl(Language language, boolean hasToBeFilled, String linShareUrlAnonymous, String jwsEncryptUrl) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.DECRYPT_URL);
		
		if(hasToBeFilled){
			String contentTXT = template.getContentTXT();
			String contentHTML = template.getContentHTML();
			
			contentTXT = StringUtils.replace(contentTXT, "${jwsEncryptUrl}", jwsEncryptUrl);
	        contentHTML = StringUtils.replace(contentHTML, "${jwsEncryptUrl}", jwsEncryptUrl);
			
	        template.setContentTXT(contentTXT);
	        template.setContentHTML(contentHTML);
		} else {
	        template.setContentTXT("");
	        template.setContentHTML("");
		}
		
		return template;
	}
	
	/**
	 * Template PERSONAL_MESSAGE
	 */
	private MailTemplate buildTemplatePersonalMessage(Language language, User owner, String message) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.PERSONAL_MESSAGE);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		contentTXT = StringUtils.replace(contentTXT, "${ownerFirstName}", owner.getFirstName());
        contentTXT = StringUtils.replace(contentTXT, "${ownerLastName}", owner.getLastName());
        contentTXT = StringUtils.replace(contentTXT, "${message}", message);
        contentHTML = StringUtils.replace(contentHTML, "${ownerFirstName}", owner.getFirstName());
        contentHTML = StringUtils.replace(contentHTML, "${ownerLastName}", owner.getLastName());
        contentHTML = StringUtils.replace(contentHTML, "${message}", message);
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	/**
	 * Template GUEST_INVITATION
	 */
	private MailTemplate buildTemplateGuestInvitation(Language language, User owner) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.GUEST_INVITATION);
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
	private MailTemplate buildTemplateLinshareURL(Language language, String linShareUrl) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.LINSHARE_URL);
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
	private MailTemplate buildTemplateAccountDescription(Language language, User guest, String password) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.ACCOUNT_DESCRIPTION);
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
	private MailTemplate buildTemplateShareNotification(Language language, List<DocumentVo> docs, User owner) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.SHARE_NOTIFICATION);
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
	 * Template FILE_DOWNLOAD_URL
	 */
	private MailTemplate buildTemplateFileDownloadURL(Language language, String url, String urlParam) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.FILE_DOWNLOAD_URL);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();

		contentTXT = StringUtils.replace(contentTXT, "${url}", url);
		contentTXT = StringUtils.replace(contentTXT, "${urlparam}", urlParam);
        contentHTML = StringUtils.replace(contentHTML, "${url}", url);
        contentHTML = StringUtils.replace(contentHTML, "${urlparam}", urlParam);
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	/**
	 * Template PASSWORD_GIVING
	 */
	private MailTemplate buildTemplatePasswordGiving(Language language, String password) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.PASSWORD_GIVING);
		
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
	private MailTemplate buildTemplateFileUpdated(Language language, User owner, Document document, String oldDocName, String fileSizeTxt) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.FILE_UPDATED);
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
	 * Template GROUP_SHARE_NOTIFICATION
	 */
	private MailTemplate buildTemplateGroupShareNotification(Language language, List<DocumentVo> docs, User owner, Group group) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.GROUP_SHARE_NOTIFICATION);
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
        contentTXT = StringUtils.replace(contentTXT, "${groupName}", group.getName());
        contentHTML = StringUtils.replace(contentHTML, "${firstName}", owner.getFirstName());
		contentHTML = StringUtils.replace(contentHTML, "${lastName}", owner.getLastName());
		contentHTML = StringUtils.replace(contentHTML, "${number}", number);
        contentHTML = StringUtils.replace(contentHTML, "${documentNames}", names.toString());
        contentHTML = StringUtils.replace(contentHTML, "${groupName}", group.getName());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	/**
	 * Template GROUP_MEMBERSHIP_STATUS
	 */
	private MailTemplate buildTemplateGroupMembershipStatus(MailContainer mailContainer, GroupMember newMember, Group group, GroupMembershipStatus status) throws BusinessException {
		MailTemplate template = getMailTemplate(mailContainer.getLanguage(), MailTemplateEnum.GROUP_MEMBERSHIP_STATUS);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();
		
		String statusString = mailContainer.getData("GroupMembershipStatus."+status.toString());
		logger.debug(status.toString());
		
		contentTXT = StringUtils.replace(contentTXT, "${newMemberFirstName}", newMember.getUser().getFirstName());
		contentTXT = StringUtils.replace(contentTXT, "${newMemberLastName}", newMember.getUser().getLastName());
        contentTXT = StringUtils.replace(contentTXT, "${status}", statusString);
        contentTXT = StringUtils.replace(contentTXT, "${groupName}", group.getName());
        contentHTML = StringUtils.replace(contentHTML, "${newMemberFirstName}", newMember.getUser().getFirstName());
		contentHTML = StringUtils.replace(contentHTML, "${newMemberLastName}", newMember.getUser().getLastName());
		contentHTML = StringUtils.replace(contentHTML, "${status}", statusString);
        contentHTML = StringUtils.replace(contentHTML, "${groupName}", group.getName());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	/**
	 * Template GROUP_NEW_MEMBER
	 */
	private MailTemplate buildTemplateGroupNewMember(
			MailContainer mailContainer, GroupMember newMember, Group group) throws BusinessException {
		MailTemplate template = getMailTemplate(mailContainer.getLanguage(), MailTemplateEnum.GROUP_NEW_MEMBER);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();
		
		
        contentTXT = StringUtils.replace(contentTXT, "${groupName}", group.getName());
        contentHTML = StringUtils.replace(contentHTML, "${groupName}", group.getName());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}
	
	/**
	 * Template GROUP_SHARE_DELETED
	 */
	private MailTemplate buildTemplateGroupSharingDeleted(Language language, Document doc, User manager, Group group) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.GROUP_SHARE_DELETED);
		String contentTXT = template.getContentTXT();
		String contentHTML = template.getContentHTML();
		
		contentTXT = StringUtils.replace(contentTXT, "${firstName}", manager.getFirstName());
		contentTXT = StringUtils.replace(contentTXT, "${lastName}", manager.getLastName());
        contentTXT = StringUtils.replace(contentTXT, "${groupName}", group.getName());
        contentTXT = StringUtils.replace(contentTXT, "${documentName}", doc.getName());
        contentHTML = StringUtils.replace(contentHTML, "${firstName}", manager.getFirstName());
		contentHTML = StringUtils.replace(contentHTML, "${lastName}", manager.getLastName());
        contentHTML = StringUtils.replace(contentHTML, "${groupName}", group.getName());
        contentHTML = StringUtils.replace(contentHTML, "${documentName}", doc.getName());
        
        template.setContentTXT(contentTXT);
        template.setContentHTML(contentHTML);
        
        return template;
	}

	/**
	 * Template SHARED_FILE_DELETED
	 */
	private MailTemplate buildTemplateSharedFileDeleted(Language language, Document doc, User owner) throws BusinessException {
		MailTemplate template = getMailTemplate(language, MailTemplateEnum.SHARED_FILE_DELETED);
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
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.impl.MailContentBuildingService#buildMailAnonymousDownload(org.linagora.linShare.core.domain.entities.MailContainer, java.util.List, java.lang.String, org.linagora.linShare.core.domain.entities.User)
	 */
	public MailContainer buildMailAnonymousDownload(MailContainer mailContainer, List<Document> docs, String email, User recipient) throws BusinessException {
		MailTemplate template = buildTemplateConfirmDownloadAnonymous(mailContainer.getLanguage(), docs, email);
		MailSubject subject = getMailSubject(mailContainer.getLanguage(), MailSubjectEnum.ANONYMOUS_DOWNLOAD);
		return buildMailContainer(mailContainer, subject.getContent(), template.getContentTXT(), template.getContentHTML(), recipient, null, null);
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.impl.MailContentBuildingService#buildMailRegisteredDownload(org.linagora.linShare.core.domain.entities.MailContainer, java.util.List, org.linagora.linShare.core.domain.entities.User, org.linagora.linShare.core.domain.entities.User)
	 */
	public MailContainer buildMailRegisteredDownload(MailContainer mailContainer, List<Document> docs, User downloadingUser, User recipient) throws BusinessException {
		MailTemplate template = buildTemplateConfirmDownloadRegistered(mailContainer.getLanguage(), docs, downloadingUser);
		MailSubject subject = getMailSubject(mailContainer.getLanguage(), MailSubjectEnum.REGISTERED_DOWNLOAD);
		return buildMailContainer(mailContainer, subject.getContent(), template.getContentTXT(), template.getContentHTML(), recipient, null, null);
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.impl.MailContentBuildingService#buildMailNewGuest(org.linagora.linShare.core.domain.entities.MailContainer, org.linagora.linShare.core.domain.entities.User, org.linagora.linShare.core.domain.entities.User, java.lang.String)
	 */
	public MailContainer buildMailNewGuest(MailContainer mailContainer, User owner, User recipient, String password) throws BusinessException {
		MailTemplate template1 = buildTemplateGuestInvitation(mailContainer.getLanguage(), owner);
		MailTemplate template2 = buildTemplateLinshareURL(mailContainer.getLanguage(), mailContainer.getData("urlBase"));
		MailTemplate template3 = buildTemplateAccountDescription(mailContainer.getLanguage(), recipient, password);
		MailSubject subject = getMailSubject(mailContainer.getLanguage(), MailSubjectEnum.NEW_GUEST);
		
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentTXT.append(template2.getContentTXT() + "\n");
		contentTXT.append(template3.getContentTXT() + "\n");
		contentHTML.append(template1.getContentHTML() + "<br />");
		contentHTML.append(template2.getContentHTML() + "<br />");
		contentHTML.append(template3.getContentHTML() + "<br />");
		
		return buildMailContainer(mailContainer, subject.getContent(), contentTXT.toString(), contentHTML.toString(), recipient, owner, mailContainer.getPersonalMessage());
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.impl.MailContentBuildingService#buildMailResetPassword(org.linagora.linShare.core.domain.entities.MailContainer, org.linagora.linShare.core.domain.entities.User, java.lang.String)
	 */
	public MailContainer buildMailResetPassword(MailContainer mailContainer, User recipient, String password) throws BusinessException {
		MailTemplate template = buildTemplateAccountDescription(mailContainer.getLanguage(), recipient, password);
		MailSubject subject = getMailSubject(mailContainer.getLanguage(), MailSubjectEnum.RESET_PASSWORD);
		
		return buildMailContainer(mailContainer, subject.getContent(), template.getContentTXT(), template.getContentHTML(), recipient, null, null);
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.impl.MailContentBuildingService#buildMailNewSharing(org.linagora.linShare.core.domain.entities.MailContainer, org.linagora.linShare.core.domain.entities.User, org.linagora.linShare.core.domain.entities.User, java.util.List, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public MailContainer buildMailNewSharing(MailContainer mailContainer, User owner, User recipient, List<DocumentVo> docs, String linShareUrl, String linShareUrlParam, String password, boolean hasToDecrypt, String jwsEncryptUrl) throws BusinessException {
		MailTemplate template1 = buildTemplateShareNotification(mailContainer.getLanguage(), docs, owner);
		MailTemplate template2 = buildTemplateFileDownloadURL(mailContainer.getLanguage(), linShareUrl, linShareUrlParam);
		MailTemplate template3 = buildTemplateDecryptUrl(mailContainer.getLanguage(), hasToDecrypt, linShareUrl, jwsEncryptUrl);
		MailTemplate template4 = buildTemplatePasswordGiving(mailContainer.getLanguage(), password);
		
		String subjectContent = mailContainer.getSubject();
		if (subjectContent == null || subjectContent.length() < 1) {
			MailSubject subject = getMailSubject(mailContainer.getLanguage(), MailSubjectEnum.NEW_SHARING);
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
		
		return buildMailContainer(mailContainer, subjectContent, contentTXT.toString(), contentHTML.toString(), recipient, owner, mailContainer.getPersonalMessage());
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.MailContentBuildingService#buildMailNewSharing(org.linagora.linShare.core.domain.entities.MailContainer, org.linagora.linShare.core.domain.entities.User, java.lang.String, java.util.List, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public MailContainer buildMailNewSharing(MailContainer mailContainer,
			User owner, String recipientMail, List<DocumentVo> docs,
			String linShareUrl, String linShareUrlParam, String password, 
			boolean hasToDecrypt, String jwsEncryptUrl)
			throws BusinessException {
		User tempUser = new Guest(recipientMail, recipientMail, "", recipientMail);
		return buildMailNewSharing(mailContainer, owner, tempUser, docs, linShareUrl, linShareUrlParam, password, hasToDecrypt, jwsEncryptUrl);
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.MailContentBuildingService#buildMailSharedDocUpdated(org.linagora.linShare.core.domain.entities.MailContainer, org.linagora.linShare.core.domain.entities.User, java.lang.String, org.linagora.linShare.core.domain.entities.Document, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public MailContainer buildMailSharedDocUpdated(MailContainer mailContainer,
			User owner, String recipientMail, Document document,
			String oldDocName, String fileSizeTxt, String linShareUrl,
			String linShareUrlParam)
			throws BusinessException {
		User tempUser = new Guest(recipientMail, recipientMail, "", recipientMail);
		return buildMailSharedDocUpdated(mailContainer, owner, tempUser, document, oldDocName, fileSizeTxt, linShareUrl, linShareUrlParam);
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.MailContentBuildingService#buildMailSharedDocUpdated(org.linagora.linShare.core.domain.entities.MailContainer, org.linagora.linShare.core.domain.entities.User, org.linagora.linShare.core.domain.entities.User, org.linagora.linShare.core.domain.entities.Document, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public MailContainer buildMailSharedDocUpdated(MailContainer mailContainer, 
			User owner, User recipient, Document document, 
			String oldDocName, String fileSizeTxt, 
			String linShareUrl, String linShareUrlParam) 
			throws BusinessException {
		MailTemplate template1 = buildTemplateFileUpdated(mailContainer.getLanguage(), owner, document, oldDocName, fileSizeTxt);
		MailTemplate template2 = buildTemplateFileDownloadURL(mailContainer.getLanguage(), linShareUrl, linShareUrlParam);
		MailSubject subject = getMailSubject(mailContainer.getLanguage(), MailSubjectEnum.SHARED_DOC_UPDATED);
		
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentTXT.append(template2.getContentTXT() + "\n");
		contentHTML.append(template1.getContentHTML() + "<br />");
		contentHTML.append(template2.getContentHTML() + "<br />");
		
		return buildMailContainer(mailContainer, subject.getContent(), contentTXT.toString(), contentHTML.toString(), recipient, null, null);
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.MailContentBuildingService#buildMailNewGroupSharing(org.linagora.linShare.core.domain.entities.MailContainer, org.linagora.linShare.core.domain.entities.User, org.linagora.linShare.core.domain.entities.Group, java.util.List, java.lang.String, java.lang.String)
	 */
	public MailContainer buildMailNewGroupSharing(MailContainer mailContainer,
			User owner, Group group, List<DocumentVo> docs, String linShareUrl,
			String linShareUrlParam) throws BusinessException {
		User tempUser = new Guest(group.getFunctionalEmail(), group.getFunctionalEmail(), "", group.getFunctionalEmail());
		return buildMailNewGroupSharing(mailContainer, owner, tempUser, group, docs, linShareUrl, linShareUrlParam);
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.MailContentBuildingService#buildMailNewGroupSharing(org.linagora.linShare.core.domain.entities.MailContainer, org.linagora.linShare.core.domain.entities.User, org.linagora.linShare.core.domain.entities.User, org.linagora.linShare.core.domain.entities.Group, java.util.List, java.lang.String, java.lang.String)
	 */
	public MailContainer buildMailNewGroupSharing(MailContainer mailContainer,
			User owner, User recipient, Group group, List<DocumentVo> docs,
			String linShareUrl, String linShareUrlParam)
			throws BusinessException {
		MailTemplate template1 = buildTemplateGroupShareNotification(mailContainer.getLanguage(), docs, owner, group);
		MailTemplate template2 = buildTemplateFileDownloadURL(mailContainer.getLanguage(), linShareUrl, linShareUrlParam);
		
		String subjectContent = mailContainer.getSubject();
		if (subjectContent == null || subjectContent.length() < 1) {
			MailSubject subject = getMailSubject(mailContainer.getLanguage(), MailSubjectEnum.NEW_GROUP_SHARING);
			subjectContent = subject.getContent();
		}
		subjectContent = StringUtils.replace(subjectContent, "${groupName}", group.getName());
		
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentTXT.append(template2.getContentTXT() + "\n");
		contentHTML.append(template1.getContentHTML() + "<br />");
		contentHTML.append(template2.getContentHTML() + "<br />");
		
		return buildMailContainer(mailContainer, subjectContent, contentTXT.toString(), contentHTML.toString(), recipient, owner, mailContainer.getPersonalMessage());
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.MailContentBuildingService#buildMailGroupSharingDeleted(org.linagora.linShare.core.domain.entities.MailContainer, org.linagora.linShare.core.domain.entities.User, org.linagora.linShare.core.domain.entities.Group, org.linagora.linShare.core.domain.entities.Document)
	 */
	public MailContainer buildMailGroupSharingDeleted(
			MailContainer mailContainer, User manager, Group group, Document doc)
			throws BusinessException {
		User tempUser = new Guest(group.getFunctionalEmail(), group.getFunctionalEmail(), "", group.getFunctionalEmail());
		return buildMailGroupSharingDeleted(mailContainer, manager, tempUser, group, doc);
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.MailContentBuildingService#buildMailGroupSharingDeleted(org.linagora.linShare.core.domain.entities.MailContainer, org.linagora.linShare.core.domain.entities.User, org.linagora.linShare.core.domain.entities.User, org.linagora.linShare.core.domain.entities.Group, org.linagora.linShare.core.domain.entities.Document)
	 */
	public MailContainer buildMailGroupSharingDeleted(
			MailContainer mailContainer, User manager, User user, Group group, Document doc)
			throws BusinessException {
		MailTemplate template1 = buildTemplateGroupSharingDeleted(mailContainer.getLanguage(), doc, manager, group);
		
		MailSubject subject = getMailSubject(mailContainer.getLanguage(), MailSubjectEnum.GROUP_SHARING_DELETED);
		String subjectContent = StringUtils.replace(subject.getContent(), "${groupName}", group.getName());
		
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentHTML.append(template1.getContentHTML() + "<br />");
		
		return buildMailContainer(mailContainer, subjectContent, contentTXT.toString(), contentHTML.toString(), user, null, null);
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.MailContentBuildingService#buildMailGroupMembershipStatus(org.linagora.linShare.core.domain.entities.MailContainer, org.linagora.linShare.core.domain.entities.GroupMember, org.linagora.linShare.core.domain.entities.Group, org.linagora.linShare.core.domain.entities.GroupMembershipStatus)
	 */
	public MailContainer buildMailGroupMembershipStatus(
			MailContainer mailContainer, GroupMember newMember, Group group, GroupMembershipStatus status)
			throws BusinessException {
		MailTemplate template1 = buildTemplateGroupMembershipStatus(mailContainer, newMember, group, status);
		MailSubject subject = getMailSubject(mailContainer.getLanguage(), MailSubjectEnum.MEMBERSHIP_REQUEST_STATUS);

		String subjectString = StringUtils.replace(subject.getContent(), "${newMemberFirstName}", newMember.getUser().getFirstName());
		subjectString = StringUtils.replace(subjectString, "${newMemberLastName}", newMember.getUser().getLastName());
		subjectString = StringUtils.replace(subjectString, "${groupName}", group.getName());
        
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentHTML.append(template1.getContentHTML() + "<br />");
		
		return buildMailContainer(mailContainer, subjectString, contentTXT.toString(), contentHTML.toString(), newMember.getOwner(), null, null);
	}
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.core.service.MailContentBuildingService#buildMailNewGroupMember(org.linagora.linShare.core.domain.entities.MailContainer, org.linagora.linShare.core.domain.entities.GroupMember, org.linagora.linShare.core.domain.entities.Group)
	 */
	public MailContainer buildMailNewGroupMember(
			MailContainer mailContainer, GroupMember newMember, Group group) 
			throws BusinessException {
		MailTemplate template1 = buildTemplateGroupNewMember(mailContainer, newMember, group);
		MailSubject subject = getMailSubject(mailContainer.getLanguage(), MailSubjectEnum.NEW_GROUP_MEMBER);
		
		String subjectString = StringUtils.replace(subject.getContent(), "${groupName}", group.getName());
        
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentHTML.append(template1.getContentHTML() + "<br />");
		
		return buildMailContainer(mailContainer, subjectString, contentTXT.toString(), contentHTML.toString(), newMember.getUser(), null, null);
	}
	
	public MailContainer buildMailSharedFileDeleted(
			MailContainer mailContainer, Document doc, User owner,
			Contact receiver)
			throws BusinessException {
		User tempUser = new Guest(receiver.getMail(), receiver.getMail(), "", receiver.getMail());
		return buildMailSharedFileDeleted(mailContainer, doc, owner, tempUser);
	}
	
	public MailContainer buildMailSharedFileDeleted(
			MailContainer mailContainer, Document doc, User owner, User receiver) 
			throws BusinessException {

		MailTemplate template1 = buildTemplateSharedFileDeleted(mailContainer.getLanguage(), doc, owner);
		MailSubject subject = getMailSubject(mailContainer.getLanguage(), MailSubjectEnum.SHARED_DOC_DELETED);
        
		StringBuffer contentTXT = new StringBuffer();
		StringBuffer contentHTML = new StringBuffer();
		contentTXT.append(template1.getContentTXT() + "\n");
		contentHTML.append(template1.getContentHTML() + "<br />");
		
		return buildMailContainer(mailContainer, subject.getContent(), contentTXT.toString(), contentHTML.toString(), receiver, null, null);
	}
}
