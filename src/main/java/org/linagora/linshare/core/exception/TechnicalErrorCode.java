/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.exception;

public enum TechnicalErrorCode implements ErrorCode{
	/** Exception error code.
	 */

	GENERIC(100),
	MAIL_EXCEPTION(101),
	NO_SUCH_LOG_ACTION(200),
	NO_SUCH_DOC_TYPE(201),
	NO_SUCH_TECHNICAL_PERMISSION_TYPE(202),
	NO_SUCH_UPLOAD_PROPOSITION_ACTION_TYPE(203),
	NO_SUCH_UPLOAD_PROPOSITION_RULE_FIELD_TYPE(204),
	NO_SUCH_UPLOAD_PROPOSITION_OPERATOR_TYPE(205),
	NO_SUCH_UPLOAD_REQUEST_STATUS(206),
	NO_SUCH_UPLOAD_REQUEST_EVENT_TYPE(207),
	NO_SUCH_UPLOAD_PROPOSITION_MATCH_TYPE(208),
	NO_SUCH_LANGUAGE(209),
	DATABASE_INCOHERENCE(1000),
	USER_INCOHERENCE(1100),
	USER_ERROR_CREATION(1101),
	COULD_NOT_INSERT_DOCUMENT(3000),
	COULD_NOT_DELETE_DOCUMENT(3001),
	COULD_NOT_UPDATE_DOCUMENT(3002),
	COULD_NOT_INSERT_SIGNATURE(3003),
	COULD_NOT_DELETE_SHARING(3100),
	BEAN_ERROR(4000),
	DATA_INCOHERENCE(5000),
	VIRUS_SCANNER_COMMUNICATION_FAILED(6000), 
	VIRUS_SCANNER_IS_DISABLED(6001),
	FUNCTIONALITY_NOT_FOUND(7000),
	FUNCTIONALITY_ENTITY_NOT_FOUND(7001),
	FUNCTIONALITY_ENTITY_MODIFICATION_NOT_ALLOW(7002),
	FUNCTIONALITY_ENTITY_UPDATE_FAILED(7003),
	METHOD_NOT_IMPLEMENTED(8000),
	MISSING_FILEDATASTORE_BUCKET(9000);

	private final int code;

	private String description;

	public String getDescription() {
		return description;
	}

	public int getCode() {
		return code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private TechnicalErrorCode(int code) {
		this.code = code;
	}

}
