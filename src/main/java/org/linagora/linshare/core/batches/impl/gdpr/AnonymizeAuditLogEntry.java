/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 *
 * Copyright (C) 2022 LINAGORA
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

package org.linagora.linshare.core.batches.impl.gdpr;

import java.util.List;

import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryAdmin;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.AuthenticationAuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.DocumentEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.DomainAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.DomainPatternAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.FunctionalityAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.GuestAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.JwtLongTimeAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.LdapConnectionAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.MailAttachmentAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.MailingListAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.MailingListContactAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ModeratorAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.PublicKeyAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.SafeDetailAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ShareEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ThreadAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ThreadMemberAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UploadRequestAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UploadRequestEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UploadRequestGroupAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UploadRequestUrlAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UserAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UserPreferenceAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.google.common.collect.ImmutableList;

public class AnonymizeAuditLogEntry extends MongoAnonymize {

	List<Class<? extends AuditLogEntry>> AUDIT_LOG_ENTRY_CLASSES = ImmutableList.of(
		AuditLogEntry.class,
		AuditLogEntryAdmin.class,
		AuditLogEntryUser.class,
		AuthenticationAuditLogEntryUser.class,
		MailAttachmentAuditLogEntry.class,
		SafeDetailAuditLogEntry.class,
		WorkGroupNodeAuditLogEntry.class,
		ShareEntryAuditLogEntry.class,
		DocumentEntryAuditLogEntry.class,
		ThreadAuditLogEntry.class,
		ThreadMemberAuditLogEntry.class,
		UserAuditLogEntry.class,
		GuestAuditLogEntry.class,
		MailingListContactAuditLogEntry.class,
		MailingListAuditLogEntry.class,
		ModeratorAuditLogEntry.class,
		UploadRequestAuditLogEntry.class,
		UploadRequestGroupAuditLogEntry.class,
		UploadRequestUrlAuditLogEntry.class,
		UploadRequestEntryAuditLogEntry.class,
		UserPreferenceAuditLogEntry.class,
		DomainAuditLogEntry.class,
		DomainPatternAuditLogEntry.class,
		LdapConnectionAuditLogEntry.class,
		FunctionalityAuditLogEntry.class,
		PublicKeyAuditLogEntry.class,
		JwtLongTimeAuditLogEntry.class,
		SharedSpaceNodeAuditLogEntry.class,
		SharedSpaceMemberAuditLogEntry.class);

	public AnonymizeAuditLogEntry(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
	}

	@Override
	public void process(String identifier) {
		AUDIT_LOG_ENTRY_CLASSES.forEach(clazz -> anonymizeAuditLogEntry(clazz, identifier));
	}

	private void anonymizeAuditLogEntry(Class<? extends AuditLogEntry> clazz, String identifier) {
		Query authUserQuery = Query.query(Criteria.where("authUser.uuid").is(identifier));
		Update authUserUpdate = new Update();
		authUserUpdate.set("authUser.mail", GDPRConstants.MAIL_ANONYMIZATION);
		authUserUpdate.set("authUser.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		authUserUpdate.set("authUser.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(authUserQuery, authUserUpdate, clazz);

		Query actorQuery = Query.query(Criteria.where("actor.uuid").is(identifier));
		Update actorUpdate = new Update();
		actorUpdate.set("actor.mail", GDPRConstants.MAIL_ANONYMIZATION);
		actorUpdate.set("actor.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		actorUpdate.set("actor.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(actorQuery, actorUpdate, clazz);
	}
}
