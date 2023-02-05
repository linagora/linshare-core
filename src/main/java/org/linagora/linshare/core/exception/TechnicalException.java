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
package org.linagora.linshare.core.exception;

/**
 * TechnicalException in the application (RuntimeException)
 * @author ngapaillard
 *
 */
public class TechnicalException extends RuntimeException{

	private static final long serialVersionUID = 3026420385594366578L;

		/** Associated error code. */
	    TechnicalErrorCode errorCode = TechnicalErrorCode.GENERIC;

	    /** Constructor.
	     * @param message error message.
	     */
	    public TechnicalException(String message) {
	        super(message);
	    }

	    /** Constructor.
	     * @param errorCode error code.
	     * @param message error message.
	     */
	    public TechnicalException(TechnicalErrorCode errorCode, String message) {
	        super(message);
	        this.errorCode = errorCode;
	    }

	    /** Constructor.
	     * @param errorCode error code.
	     * @param message error message.
	     * @param cause error cause.
	     */
	    public TechnicalException(TechnicalErrorCode errorCode, String message, Throwable cause) {
	        super(message, cause);
	        this.errorCode = errorCode;
	    }

	    /** Get error code.
	     * @return error code.
	     */
	    public TechnicalErrorCode getErrorCode() {
	        return errorCode;
	    }
	
}
