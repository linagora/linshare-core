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

import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class AnonymizeWorkGroup extends MongoAnonymize {

	public AnonymizeWorkGroup(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
	}

	@Override
	public void process(String identifier) {
		Query query = Query.query(Criteria.where("lastAuthor.uuid").is(identifier));
		Update update = new Update();
		update.set("lastAuthor.mail", GDPRConstants.MAIL_ANONYMIZATION);
		update.set("lastAuthor.firstName", GDPRConstants.FIRST_NAME_ANONYMIZATION);
		update.set("lastAuthor.lastName", GDPRConstants.LAST_NAME_ANONYMIZATION);
		mongoTemplate.updateMulti(query, update, WorkGroupNode.class);
	}
}
