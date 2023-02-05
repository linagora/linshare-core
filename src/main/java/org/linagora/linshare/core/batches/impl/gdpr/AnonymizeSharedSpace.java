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

import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceMemberAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.SharedSpaceNodeAuditLogEntry;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AnonymizeSharedSpace extends MongoAnonymize {

	public AnonymizeSharedSpace(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
	}

	@Override
	public void process(String identifier) {
		anonymizeSharedSpaceMember(identifier);
		anonymizeSharedSpaceNode(identifier);
		anonymizeSharedSpaceRole(identifier);

		anonymizeSharedSpaceMemberAuditLogEntry(identifier);
		anonymizeSharedSpaceNodeAuditLogEntry(identifier);
	}

	private void anonymizeSharedSpaceMember(String identifier) {
		Query query = Query.query(Criteria.where("account.uuid").is(identifier));
		Update update = new Update();
		update.set("account.mail", GDPRConstants.MAIL_ANONYMIZATION);
		update.set("account.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		update.set("account.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(query, update, SharedSpaceMember.class);
	}

	private void anonymizeSharedSpaceNode(String identifier) {
		Query query = Query.query(Criteria.where("author.uuid").is(identifier));
		Update update = new Update();
		update.set("author.mail", GDPRConstants.MAIL_ANONYMIZATION);
		update.set("author.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		update.set("author.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(query, update, SharedSpaceNode.class);
	}

	private void anonymizeSharedSpaceRole(String identifier) {
		Query query = Query.query(Criteria.where("author.uuid").is(identifier));
		Update update = new Update();
		update.set("author.mail", GDPRConstants.MAIL_ANONYMIZATION);
		update.set("author.name", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(query, update, SharedSpaceRole.class);
	}

	private void anonymizeSharedSpaceMemberAuditLogEntry(String identifier) {
		Query resourceAccountQuery = Query.query(Criteria.where("resource.account.uuid").is(identifier));
		Update resourceAccountUpdate = new Update();
		resourceAccountUpdate.set("resource.account.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceAccountUpdate.set("resource.account.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceAccountUpdate.set("resource.account.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceAccountQuery, resourceAccountUpdate, SharedSpaceMemberAuditLogEntry.class);

		Query resourceUserQuery = Query.query(Criteria.where("resource.user.uuid").is(identifier));
		Update resourceUserUpdate = new Update();
		resourceUserUpdate.set("resource.user.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUserUpdate.set("resource.user.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUserUpdate.set("resource.user.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceUserQuery, resourceUserUpdate, SharedSpaceMemberAuditLogEntry.class);

		Query resourceUpdatedAccountQuery = Query.query(Criteria.where("resourceUpdated.account.uuid").is(identifier));
		Update resourceUpdatedAccountUpdate = new Update();
		resourceUpdatedAccountUpdate.set("resourceUpdated.account.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdatedAccountUpdate.set("resourceUpdated.account.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdatedAccountUpdate.set("resourceUpdated.account.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceUpdatedAccountQuery, resourceUpdatedAccountUpdate, SharedSpaceMemberAuditLogEntry.class);

		Query resourceUpdatedUserQuery = Query.query(Criteria.where("resourceUpdated.user.uuid").is(identifier));
		Update resourceUpdatedUserUpdate = new Update();
		resourceUpdatedUserUpdate.set("resourceUpdated.user.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdatedUserUpdate.set("resourceUpdated.user.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdatedUserUpdate.set("resourceUpdated.user.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceUpdatedUserQuery, resourceUpdatedUserUpdate, SharedSpaceMemberAuditLogEntry.class);
	}

	private void anonymizeSharedSpaceNodeAuditLogEntry(String identifier) {
		Query resourceAuthorQuery = Query.query(Criteria.where("resource.author.uuid").is(identifier));
		Update resourceAuthorUpdate = new Update();
		resourceAuthorUpdate.set("resource.author.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceAuthorUpdate.set("resource.author.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceAuthorUpdate.set("resource.author.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceAuthorQuery, resourceAuthorUpdate, SharedSpaceNodeAuditLogEntry.class);

		Query resourceUpdatedAuthorQuery = Query.query(Criteria.where("resourceUpdated.author.uuid").is(identifier));
		Update resourceUpdatedAuthorUpdate = new Update();
		resourceUpdatedAuthorUpdate.set("resourceUpdated.author.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdatedAuthorUpdate.set("resourceUpdated.author.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdatedAuthorUpdate.set("resourceUpdated.author.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceUpdatedAuthorQuery, resourceUpdatedAuthorUpdate, SharedSpaceNodeAuditLogEntry.class);
	}
}
