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

import org.linagora.linshare.mongo.entities.logs.GuestAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ModeratorAuditLogEntry;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AnonymizeModeratorLogEntry extends MongoAnonymize {

	public AnonymizeModeratorLogEntry(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
	}

	@Override
	public void process(String identifier) {
		anonymizeGuestAuditLogEntry(identifier);
		anonymizeModeratorAuditLogEntry(identifier);
	}

	private void anonymizeGuestAuditLogEntry(String identifier) {
		Query resourceQuery = Query.query(Criteria.where("resource.uuid").is(identifier));
		Update resourceUpdate = new Update();
		resourceUpdate.set("resource.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdate.set("resource.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdate.set("resource.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceQuery, resourceUpdate, GuestAuditLogEntry.class);

		Query query = Query.query(Criteria.where("resourceUpdated.uuid").is(identifier));
		Update update = new Update();
		update.set("resourceUpdated.mail", GDPRConstants.MAIL_ANONYMIZATION);
		update.set("resourceUpdated.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		update.set("resourceUpdated.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(query, update, GuestAuditLogEntry.class);
	}

	private void anonymizeModeratorAuditLogEntry(String identifier) {
		Query resourceQuery = Query.query(Criteria.where("resource.account.uuid").is(identifier));
		Update resourceUpdate = new Update();
		resourceUpdate.set("resource.account.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceUpdate.set("resource.account.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceUpdate.set("resource.account.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceQuery, resourceUpdate, ModeratorAuditLogEntry.class);

		Query resourceGuestQuery = Query.query(Criteria.where("resource.guest.uuid").is(identifier));
		Update resourceGuestUpdate = new Update();
		resourceGuestUpdate.set("resource.guest.mail", GDPRConstants.MAIL_ANONYMIZATION);
		resourceGuestUpdate.set("resource.guest.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		resourceGuestUpdate.set("resource.guest.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceGuestQuery, resourceGuestUpdate, ModeratorAuditLogEntry.class);

		Query query = Query.query(Criteria.where("resourceUpdated.account.uuid").is(identifier));
		Update update = new Update();
		update.set("resourceUpdated.account.mail", GDPRConstants.MAIL_ANONYMIZATION);
		update.set("resourceUpdated.account.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		update.set("resourceUpdated.account.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(query, update, ModeratorAuditLogEntry.class);

		Query queryGuest = Query.query(Criteria.where("resourceUpdated.guest.uuid").is(identifier));
		Update updateGuest = new Update();
		updateGuest.set("resourceUpdated.guest.mail", GDPRConstants.MAIL_ANONYMIZATION);
		updateGuest.set("resourceUpdated.guest.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		updateGuest.set("resourceUpdated.guest.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(queryGuest, updateGuest, ModeratorAuditLogEntry.class);
	}
}
