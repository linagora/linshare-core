/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009-2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.mongo.entities;

import org.linagora.linshare.core.domain.constants.UploadPropositionRuleFieldType;
import org.linagora.linshare.core.domain.constants.UploadPropositionRuleOperatorType;

public class UploadPropositionRule {

	protected UploadPropositionRuleOperatorType operator;

	protected UploadPropositionRuleFieldType field;

	protected String value;

	public UploadPropositionRule() {
		super();
	}

	public UploadPropositionRule(UploadPropositionRuleOperatorType operator, UploadPropositionRuleFieldType field,
			String value) {
		super();
		this.operator = operator;
		this.field = field;
		this.value = value;
	}

	public UploadPropositionRuleOperatorType getOperator() {
		return operator;
	}

	public void setOperator(UploadPropositionRuleOperatorType operator) {
		this.operator = operator;
	}

	public UploadPropositionRuleFieldType getField() {
		return field;
	}

	public void setField(UploadPropositionRuleFieldType field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
