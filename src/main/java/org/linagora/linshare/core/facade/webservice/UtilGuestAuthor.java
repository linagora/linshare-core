/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2022 LINAGORA
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
