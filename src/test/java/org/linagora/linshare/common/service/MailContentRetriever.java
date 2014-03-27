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
package org.linagora.linshare.common.service;

import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailSubjectEnum;
import org.linagora.linshare.core.domain.constants.MailTemplateEnum;
import org.linagora.linshare.core.domain.entities.MailSubject;
import org.linagora.linshare.core.domain.entities.MailTemplate;

public class MailContentRetriever {
	
	private static final Set<MailTemplate> MAIL_TEMPLATES;
	private static final Set<MailSubject> MAIL_SUBJECTS;
	
	static {
		MAIL_TEMPLATES = new HashSet<MailTemplate>();
		
		MAIL_SUBJECTS = new HashSet<MailSubject>();
		
		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(0), "LinShare: An anonymous user downloaded the file you shared", Language.DEFAULT));

		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(1), "LinShare: An user downloaded the file you shared", Language.DEFAULT));

		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(2), "LinShare: Your account on LinShare has been created", Language.DEFAULT));

		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(3), "LinShare: Your password was reset", Language.DEFAULT));

		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(4), "LinShare: A user deposited files in sharing for you", Language.DEFAULT));

		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(5), "LinShare: An user has updated a shared file", Language.DEFAULT));

		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(6), "LinShare: A user deposited files in sharing for the group ${groupName}", Language.DEFAULT));

		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(7), "LinShare: Status of your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName}", Language.DEFAULT));

		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(8), "LinShare: You are now member of the group ${groupName}", Language.DEFAULT));

		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(9), "LinShare: A file shared with the group ${groupName} has been deleted.", Language.DEFAULT));

		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(10), "LinShare: An user has deleted a shared file", Language.DEFAULT));

		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(11), "LinShare: A sharing will be soon deleted", Language.DEFAULT));

		MAIL_SUBJECTS.add(new MailSubject(MailSubjectEnum.fromInt(12), "LinShare: A file will be soon deleted", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(0), "Welcome to LinShare, the Open Source secure files sharing system.", "Welcome to LinShare, the Open Source secure files sharing system.", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(1), "<a href=\"http://linshare.org/\" title=\"LinShare\"><strong>LinShare</strong></a> - Open Source secured file sharing application", "LinShare - http://linshare.org - Open Source secured file sharing application", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(2), "An anonymous user ${email} downloaded the following file(s) you shared via LinShare:<ul>${documentNames}</ul>", "An anonymous user ${email} downloaded the following file(s) you shared via LinShare:\n${documentNamesTxt}", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(3), "${recipientFirstName} ${recipientLastName} downloaded the following file you shared with him via LinShare:<ul>${documentNames}</ul>", "${recipientFirstName} ${recipientLastName} downloaded the following file you shared with him via LinShare:\n${documentNamesTxt}", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(4), "You can login to this address: <a href=\"${url}\">${url}</a><br/>", "You can now login to this address: ${url}", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(5), "In order to download the files, click on this link or paste it into your browser: <a href=\"${url}${urlparam}\">${url}${urlparam}</a>", "In order to download the files, click on this link or paste it into your browser:\n${url}${urlparam}", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(6), "<p>Some received files are <b>encrypted</b>. After downloading, take care of decrypting localy with the application:<br/><a href=\"${jwsEncryptUrl}\">${jwsEncryptUrl}</a><br/>You have to use <i>password</i> which has been communicated by the person who has done this sharing.</p><br/>", "Some received files are encrypted. After downloading, take care of decrypting localy with the application:\n${jwsEncryptUrl}\nYou have to use <i>password</i> which has been communicated by the person who has done this sharing.\n", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(7), "<strong>Personal message from ${ownerFirstName} ${ownerLastName}, via LinShare</strong><pre>${message}</pre><hr/>", "Personal message from ${ownerFirstName} ${ownerLastName}, via LinShare\n\n${message}\n\n--------------------------------------------------------------", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(8), "<strong>${ownerFirstName} ${ownerLastName}</strong> invites you to use LinShare.<br/>", "${ownerFirstName} ${ownerLastName} invites you to use LinShare.", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(9), "Your LinShare account:<ul><li>Login: <code>${mail}</code> &nbsp;(your e-mail address)</li><li>Password: <code>${password}</code></li></ul>", "Your LinShare account:\n- Login: ${mail}  (your e-mail address)\n- Password: ${password}", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(10), "<strong>${firstName} ${lastName}</strong> sent you ${number} file(s):<ul>${documentNames}</ul>", "${firstName} ${lastName} sent you ${number} file(s):\n\n${documentNamesTxt}", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(11), "The associated password to use is: <code>${password}</code><br/>", "The associated password to use is: ${password}", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(12), "<strong>${firstName} ${lastName}</strong> has updated the shared file <strong>${fileOldName}</strong>:<ul><li>New file name: ${fileName}</li><li>File size: ${fileSize}</li><li>MIME type: <code>${mimeType}</code></li></ul>", "${firstName} ${lastName} has updated the shared file ${fileOldName}:\n- New file name: ${fileName}\n- File size: ${fileSize}\n- MIME type: ${mimeType}\n", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(13), "<strong>${firstName} ${lastName}</strong> has shared ${number} file(s) with the group ${groupName}:<ul>${documentNames}</ul>", "${firstName} ${lastName} has shared ${number} file(s) with the group ${groupName}:\n\n${documentNamesTxt}", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(14), "You are now member of the group ${groupName}.", "You are now member of the group ${groupName}.", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(15), "Your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName} is ${status}.", "Your membership request for the group ${groupName} and the user ${newMemberFirstName} ${newMemberLastName} is ${status}.", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(16), "${firstName} ${lastName} has deleted the file <strong>${documentName}</strong> shared with the group <strong>${groupName}</strong>.", "${firstName} ${lastName} has deleted the file ${documentName} shared with the group ${groupName}.", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(17), "<strong>${firstName} ${lastName}</strong> has deleted the shared file <strong>${documentName}</strong>.", "${firstName} ${lastName} has deleted the shared file ${documentName}.", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(18), "A share from ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download the files before this date.", "A share from ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download the files before this date.", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(19), "The sharing of the file ${documentName} by ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download or copy this file.", "The sharing of the file ${documentName} by ${firstName} ${lastName} will expire in ${nbDays} days. Remember to download or copy this file.", Language.DEFAULT));

		MAIL_TEMPLATES.add(new MailTemplate(MailTemplateEnum.fromInt(20), "The file <strong>${documentName}</strong> will expire in ${nbDays} days.", "The file ${documentName} will expire in ${nbDays} days.", Language.DEFAULT));

	}
	
	
	public static final Set<MailTemplate> getMailTemplates() {
		return MAIL_TEMPLATES;
	}
	
	public static final Set<MailSubject> getMailSubjects() {
		return MAIL_SUBJECTS;
	}

}
