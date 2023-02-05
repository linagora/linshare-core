/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
