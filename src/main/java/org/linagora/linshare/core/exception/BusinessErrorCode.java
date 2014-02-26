/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import javax.ws.rs.core.Response.Status;

/** Exception error code.
 */
public enum BusinessErrorCode implements ErrorCode {
    UNKNOWN(1000, Status.INTERNAL_SERVER_ERROR),
    AUTHENTICATION_ERROR(2000),
    DATABASE_INCOHERENCE_NO_ROOT_DOMAIN(2001, Status.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND(2200, Status.NOT_FOUND),
    DUPLICATE_USER_ENTRY(2201),
    CANNOT_DELETE_USER(2203, Status.FORBIDDEN),
    CANNOT_UPDATE_USER(2204, Status.FORBIDDEN),
    USER_CANNOT_CREATE_GUEST(2205, Status.FORBIDDEN),
    MIME_NOT_FOUND(3000),
    FILE_TOO_LARGE(3001),
    FILE_MIME_NOT_ALLOWED(3002),
    FILE_CONTAINS_VIRUS(3003),
    FILE_MIME_WARNING(3004),
    FILE_ENCRYPTION_UNDEFINED(3005),
    FILE_TIMESTAMP_NOT_COMPUTED(3006),
    FILE_SCAN_FAILED(3007),
    FILE_TIMESTAMP_WRONG_TSA_URL(3008),
    FILE_UNREACHABLE(3009),
    INVALID_FILENAME(3010),
    INVALID_UUID(4000),
    SHARED_DOCUMENT_NOT_FOUND(5000, Status.NOT_FOUND),
    CANNOT_SHARE_DOCUMENT(5001),
    CANNOT_DELETE_SHARED_DOCUMENT(5002),
    NO_SUCH_ELEMENT(6000, Status.NOT_FOUND),
    CANNOT_SIGN_DOCUMENT(9001),
    CANNOT_ENCRYPT_GENERATE_KEY(9002),
    CANNOT_ENCRYPT_DOCUMENT(9003),
    CANNOT_DECRYPT_DOCUMENT(9004),
    WRONG_URL(10000), 
    SECURED_URL_IS_EXPIRED(12000), 
    SECURED_URL_BAD_PASSWORD(12001), 
    SECURED_URL_WRONG_DOCUMENT_ID(12002),
    DOMAIN_ID_ALREADY_EXISTS(13000),
    DOMAIN_ID_NOT_FOUND(13001),
    DOMAIN_INVALID_TYPE(13002),
    DOMAIN_POLICY_NOT_FOUND(13003),
    LDAP_CONNECTION_NOT_FOUND(13004),
    DOMAIN_PATTERN_NOT_FOUND(13005),
    DOMAIN_BASEDN_NOT_FOUND(13006),
    DOMAIN_DO_NOT_ALREADY_EXISTS(13007),
    DOMAIN_DO_NOT_EXISTS(13008),
    DOMAIN_POLICY_INVALID(13009),
    FUNCTIONALITY_ENTITY_OUT_OF_DATE(14000),
    RELAY_HOST_NOT_ENABLE(15000),
    XSSFILTER_SCAN_FAILED(15666),
    DIRECTORY_UNAVAILABLE(16000),
    FORBIDDEN(17000, Status.FORBIDDEN),
    
    WEBSERVICE_FAULT(20000, Status.INTERNAL_SERVER_ERROR),
    WEBSERVICE_UNAUTHORIZED(20001, Status.FORBIDDEN),
    WEBSERVICE_NOT_FOUND(20002, Status.NOT_FOUND),
    
    LIST_DO_NOT_EXIST(25000, Status.NOT_FOUND),
    LIST_ALDREADY_EXISTS(25001),
    CONTACT_LIST_DO_NOT_EXIST(25002, Status.NOT_FOUND),
    
    THREAD_NOT_FOUND(26000, Status.NOT_FOUND),
    THREAD_MEMBER_NOT_FOUND(26001, Status.NOT_FOUND),
    THREAD_ENTRY_NOT_FOUND(26002, Status.NOT_FOUND),
    ;
    
    
	private final int code;
	
	private final Status status;
	
	private BusinessErrorCode(int code) {
		this.code = code;
		this.status = Status.BAD_REQUEST;
	}
	
	private BusinessErrorCode(int code, Status status) {
		this.code = code;
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public Status getStatus() {
		return status;
	}
}
