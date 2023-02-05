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

import org.linagora.linshare.mongo.entities.logs.MailingListAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.MailingListContactAuditLogEntry;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AnonymizeMailingListLogEntry extends MongoAnonymize {

	public AnonymizeMailingListLogEntry(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
	}

	@Override
	public void process(String identifier) {
		anonymizeMailingListAuditLogEntry(identifier);
		anonymizeMailingListContactAuditLogEntry(identifier);
	}

	private void anonymizeMailingListAuditLogEntry(String identifier) {
		Query resourceQuery = Query.query(Criteria.where("resource.owner.uuid").is(identifier));
		Update resourceUpdate = new Update();
		resourceUpdate.set("resource.owner.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdate.set("resource.owner.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdate.set("resource.owner.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceQuery, resourceUpdate, MailingListAuditLogEntry.class);

		Query resourceUpdatedQuery = Query.query(Criteria.where("resourceUpdated.owner.uuid").is(identifier));
		Update resourceUpdatedUpdate = new Update();
		resourceUpdatedUpdate.set("resourceUpdated.owner.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdatedUpdate.set("resourceUpdated.owner.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdatedUpdate.set("resourceUpdated.owner.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceUpdatedQuery, resourceUpdatedUpdate, MailingListAuditLogEntry.class);
	}

	private void anonymizeMailingListContactAuditLogEntry(String identifier) {
		Query resourceQuery = Query.query(Criteria.where("resource.uuid").is(identifier));
		Update resourceUpdate = new Update();
		resourceUpdate.set("resource.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdate.set("resource.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdate.set("resource.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceQuery, resourceUpdate, MailingListContactAuditLogEntry.class);

		Query resourceUpdatedQuery = Query.query(Criteria.where("resourceUpdated.uuid").is(identifier));
		Update resourceUpdatedUpdate = new Update();
		resourceUpdatedUpdate.set("resourceUpdated.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdatedUpdate.set("resourceUpdated.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdatedUpdate.set("resourceUpdated.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceUpdatedQuery, resourceUpdatedUpdate, MailingListContactAuditLogEntry.class);

		Query listOwnerQuery = Query.query(Criteria.where("list.owner.uuid").is(identifier));
		Update listOwnerUpdate = new Update();
		listOwnerUpdate.set("list.owner.mail", GDPRConstants.MAIL_ANONYMIZATION);
		listOwnerUpdate.set("list.owner.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		listOwnerUpdate.set("list.owner.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(listOwnerQuery, listOwnerUpdate, MailingListContactAuditLogEntry.class);

		Query listContactsQuery = Query.query(Criteria.where("list.contacts.uuid").is(identifier));
		Update listContactsUpdate = new Update();
		listContactsUpdate.set("list.contacts.mail", GDPRConstants.MAIL_ANONYMIZATION);
		listContactsUpdate.set("list.contacts.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		listContactsUpdate.set("list.contacts.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(listContactsQuery, listContactsUpdate, MailingListContactAuditLogEntry.class);
	}
}
