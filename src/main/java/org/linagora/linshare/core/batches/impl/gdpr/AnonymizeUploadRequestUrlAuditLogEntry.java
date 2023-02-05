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

import org.linagora.linshare.mongo.entities.logs.UploadRequestUrlAuditLogEntry;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AnonymizeUploadRequestUrlAuditLogEntry extends MongoAnonymize {

	public AnonymizeUploadRequestUrlAuditLogEntry(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
	}

	@Override
	public void process(String identifier) {
		Query resourceQuery = Query.query(Criteria.where("resource.uuid").is(identifier));
		Update resourceUpdate = new Update();
		resourceUpdate.set("resource.contactMail", GDPRConstants.MAIL_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceQuery, resourceUpdate, UploadRequestUrlAuditLogEntry.class);

		Query resourceUpdatedQuery = Query.query(Criteria.where("resourceUpdated.uuid").is(identifier));
		Update resourceUpdatedUpdate = new Update();
		resourceUpdatedUpdate.set("resourceUpdated.contactMail", GDPRConstants.MAIL_ANONYMIZATION);
		mongoTemplate.updateMulti(resourceUpdatedQuery, resourceUpdatedUpdate, UploadRequestUrlAuditLogEntry.class);
	}
}
