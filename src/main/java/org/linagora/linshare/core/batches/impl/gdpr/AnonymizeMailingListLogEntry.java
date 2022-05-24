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
