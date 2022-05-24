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

import org.linagora.linshare.mongo.entities.logs.ThreadAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ThreadMemberAuditLogEntry;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AnonymizeThreadAuditLogEntry extends MongoAnonymize {

	public AnonymizeThreadAuditLogEntry(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
	}

	@Override
	public void process(String identifier) {
		anonymizeThreadAuditLogEntry(identifier);
		anonymizeThreadMemberAuditLogEntry(identifier);
	}

	private void anonymizeThreadAuditLogEntry(String identifier) {
		Query resourceQuery = Query.query(Criteria.where("resource.members.user.uuid").is(identifier));
		Update resourceUpdate = new Update();
		resourceUpdate.set("resource.members.user.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdate.set("resource.members.user.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdate.set("resource.members.user.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceQuery, resourceUpdate, ThreadAuditLogEntry.class);

		Query resourceUpdatedQuery = Query.query(Criteria.where("resourceUpdated.members.user.uuid").is(identifier));
		Update resourceUpdatedUpdate = new Update();
		resourceUpdatedUpdate.set("resourceUpdated.members.user.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdatedUpdate.set("resourceUpdated.members.user.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdatedUpdate.set("resourceUpdated.members.user.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceUpdatedQuery, resourceUpdatedUpdate, ThreadAuditLogEntry.class);
	}

	private void anonymizeThreadMemberAuditLogEntry(String identifier) {
		Query resourceQuery = Query.query(Criteria.where("resource.user.uuid").is(identifier));
		Update resourceUpdate = new Update();
		resourceUpdate.set("resource.user.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdate.set("resource.user.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdate.set("resource.user.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceQuery, resourceUpdate, ThreadMemberAuditLogEntry.class);

		Query resourceUpdatedQuery = Query.query(Criteria.where("resourceUpdated.user.uuid").is(identifier));
		Update resourceUpdatedUpdate = new Update();
		resourceUpdatedUpdate.set("resourceUpdated.user.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdatedUpdate.set("resourceUpdated.user.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdatedUpdate.set("resourceUpdated.user.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceUpdatedQuery, resourceUpdatedUpdate, ThreadMemberAuditLogEntry.class);
	}
}
