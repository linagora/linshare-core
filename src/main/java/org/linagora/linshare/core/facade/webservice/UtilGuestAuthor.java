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
package org.linagora.linshare.core.facade.webservice;

import java.util.Objects;

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.DomainMto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class UtilGuestAuthor {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private final MongoTemplate mongoTemplate;

	public UtilGuestAuthor(MongoTemplate mongoTemplate) {
		super();
		this.mongoTemplate = mongoTemplate;
	}

	public AccountMto getAuthor(String guestUuid) {
		Query query = new Query();
		query.addCriteria(Criteria.where("resourceUuid").is(guestUuid));
		query.addCriteria(Criteria.where("action").is(LogAction.CREATE));
		AuditLogEntryUser guestCreationTrace = mongoTemplate.findOne(query, AuditLogEntryUser.class);
		AccountMto owner;
		if (Objects.isNull(guestCreationTrace)) {
			logger.warn(
					"Guest Audit trace not found a fake owner will be used. Using John DOE unknown-user@linshare.org instead.");
			owner = getFakeAuthor();
		} else {
			owner = guestCreationTrace.getActor();
		}
		return owner;
	}

	public AccountMto getFakeAuthor() {
		AccountMto owner;
		owner = new AccountMto();
		owner.setFirstName("John");
		owner.setLastName("DOE");
		owner.setMail("unknown-user@linshare.org");
		owner.setUuid("7bf2982c-6933-47db-9203-f3b9c543eced");
		owner.setAccountType(AccountType.INTERNAL);
		owner.setDomain(new DomainMto("bee08e5a-2fd9-43d1-a4f0-012a0078fec2", "FakeOwnerDomain"));
		return owner;
	}
}
