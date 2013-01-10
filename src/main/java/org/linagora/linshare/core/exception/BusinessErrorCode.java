/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.exception;

/** Exception error code.
 */
public enum BusinessErrorCode implements ErrorCode{
    UNKNOWN(1000),
    AUTHENTICATION_ERROR(2000),
    DATABASE_INCOHERENCE_NO_ROOT_DOMAIN(2001),
    USER_CANNOT_UPLOAD(2100),
    USER_NOT_FOUND(2200),
    DUPLICATE_USER_ENTRY(2201),
    CANNOT_DELETE_USER(2203),
    CANNOT_UPDATE_USER(2204),
    USER_CANNOT_CREATE_GUEST(2205),
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
    INVALID_UUID(4000),
    SHARED_DOCUMENT_NOT_FOUND(5000),
    CANNOT_SHARE_DOCUMENT(5001),
    CANNOT_DELETE_SHARED_DOCUMENT(5002),
    NO_SUCH_ELEMENT(6000),
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
    FUNCTIONALITY_ENTITY_OUT_OF_DATE(14000),
    RELAY_HOST_NOT_ENABLE(15000),
    XSSFILTER_SCAN_FAILED(15666),
    DIRECTORY_UNAVAILABLE(16000),
    NOT_AUTHORIZED(17000),
    
    WEBSERVICE_FAULT(20000),
    WEBSERVICE_UNAUTHORIZED(20001),
    WEBSERVICE_NOT_FOUND(20002)
    ;
    
    
    
    
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

	private BusinessErrorCode(int code) {
		this.code = code;
	}
}
