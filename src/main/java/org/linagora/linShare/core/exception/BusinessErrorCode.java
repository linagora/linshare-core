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
package org.linagora.linShare.core.exception;

/** Exception error code.
 */
public enum BusinessErrorCode implements ErrorCode{
    UNKNOWN(1000),
    AUTHENTICATION_ERROR(2000),
    USER_CANNOT_UPLOAD(2100),
    USER_NOT_FOUND(2200),
    DUPLICATE_USER_ENTRY(2201),
    CANNOT_DELETE_USER(2201),
    MIME_NOT_FOUND(3000),
    FILE_TOO_LARGE(3001),
    FILE_MIME_NOT_ALLOWED(3002),
    FILE_CONTAINS_VIRUS(3003),
    FILE_MIME_WARNING(3004),
    FILE_ENCRYPTION_UNDEFINED(3005),
    FILE_TIMESTAMP_NOT_COMPUTED(3006),
    INVALID_UUID(4000),
    SHARED_DOCUMENT_NOT_FOUND(5000),
    CANNOT_SHARE_DOCUMENT(5001),
    NO_SUCH_ELEMENT(6000),
    CANNOT_SIGN_DOCUMENT(9001),
    CANNOT_ENCRYPT_GENERATE_KEY(9002),
    CANNOT_ENCRYPT_DOCUMENT(9003),
    CANNOT_DECRYPT_DOCUMENT(9004),
    WRONG_URL(10000), 
    SECURED_URL_IS_EXPIRED(12000), 
    SECURED_URL_BAD_PASSWORD(12001), 
    SECURED_URL_WRONG_DOCUMENT_ID(12002),;
    
    
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
