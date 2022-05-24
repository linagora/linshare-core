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
		update.set("author.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		update.set("author.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
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

		Query actorQuery = Query.query(Criteria.where("actor.uuid").is(identifier));
		Update actorUpdate = new Update();
		actorUpdate.set("actor.mail", GDPRConstants.MAIL_ANONYMIZATION);
		actorUpdate.set("actor.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		actorUpdate.set("actor.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(actorQuery, actorUpdate, SharedSpaceMemberAuditLogEntry.class);

		Query authUserQuery = Query.query(Criteria.where("authUser.uuid").is(identifier));
		Update authUserUpdate = new Update();
		authUserUpdate.set("authUser.mail", GDPRConstants.MAIL_ANONYMIZATION);
		authUserUpdate.set("authUser.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		authUserUpdate.set("authUser.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(authUserQuery, authUserUpdate, SharedSpaceMemberAuditLogEntry.class);
	}

	private void anonymizeSharedSpaceNodeAuditLogEntry(String identifier) {
		Query resourceAccountQuery = Query.query(Criteria.where("resource.account.uuid").is(identifier));
		Update resourceAccountUpdate = new Update();
		resourceAccountUpdate.set("resource.account.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceAccountUpdate.set("resource.account.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceAccountUpdate.set("resource.account.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceAccountQuery, resourceAccountUpdate, SharedSpaceNodeAuditLogEntry.class);

		Query resourceUserQuery = Query.query(Criteria.where("resource.user.uuid").is(identifier));
		Update resourceUserUpdate = new Update();
		resourceUserUpdate.set("resource.user.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUserUpdate.set("resource.user.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUserUpdate.set("resource.user.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceUserQuery, resourceUserUpdate, SharedSpaceNodeAuditLogEntry.class);

		Query resourceUpdatedAccountQuery = Query.query(Criteria.where("resourceUpdated.account.uuid").is(identifier));
		Update resourceUpdatedAccountUpdate = new Update();
		resourceUpdatedAccountUpdate.set("resourceUpdated.account.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdatedAccountUpdate.set("resourceUpdated.account.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdatedAccountUpdate.set("resourceUpdated.account.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceUpdatedAccountQuery, resourceUpdatedAccountUpdate, SharedSpaceNodeAuditLogEntry.class);

		Query resourceUpdatedUserQuery = Query.query(Criteria.where("resourceUpdated.user.uuid").is(identifier));
		Update resourceUpdatedUserUpdate = new Update();
		resourceUpdatedUserUpdate.set("resourceUpdated.user.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdatedUserUpdate.set("resourceUpdated.user.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdatedUserUpdate.set("resourceUpdated.user.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceUpdatedUserQuery, resourceUpdatedUserUpdate, SharedSpaceNodeAuditLogEntry.class);
	}
}
