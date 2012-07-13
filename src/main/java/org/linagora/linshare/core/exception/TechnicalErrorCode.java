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

public enum TechnicalErrorCode implements ErrorCode{
	/** Exception error code.
	 */
	
	GENERIC(100),
    MAIL_EXCEPTION(101),
    NO_SUCH_LOG_ACTION(200),
    NO_SUCH_DOC_TYPE(201),
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
	METHOD_NOT_IMPLEMENTED(8000);
	
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
