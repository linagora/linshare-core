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

import org.linagora.linshare.mongo.entities.logs.AuditLogEntry;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AnonymizeAuditLogEntry extends MongoAnonymize {

	public AnonymizeAuditLogEntry(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
	}

	@Override
	public void process(String identifier) {
		Query authUserQuery = Query.query(Criteria.where("authUser.uuid").is(identifier));
		Update authUserUpdate = new Update();
		authUserUpdate.set("authUser.mail", GDPRConstants.MAIL_ANONYMIZATION);
		authUserUpdate.set("authUser.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		authUserUpdate.set("authUser.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(authUserQuery, authUserUpdate, AuditLogEntry.class);

		Query actorQuery = Query.query(Criteria.where("actor.uuid").is(identifier));
		Update actorUpdate = new Update();
		actorUpdate.set("actor.mail", GDPRConstants.MAIL_ANONYMIZATION);
		actorUpdate.set("actor.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		actorUpdate.set("actor.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(actorQuery, actorUpdate, AuditLogEntry.class);
	}
}
