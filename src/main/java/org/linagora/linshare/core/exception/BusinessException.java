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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/** Catcheable exception raised in case of business error during process.
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 711201641073090937L;

	/** Associated error code. */
    BusinessErrorCode errorCode = BusinessErrorCode.UNKNOWN;

    /** Extra informations on the business exception. */
    List<String> extras = null;

    /** Constructor.
     * @param message error message.
     */
    public BusinessException(String message) {
        super(message);
    }

    /** Constructor.
     * @param errorCode error code.
     * @param message error message.
     */
    public BusinessException(BusinessErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /** Constructor.
     * @param errorCode error code.
     * @param message error message.
     * @param cause error cause.
     */
    public BusinessException(BusinessErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /** Constructor.
     * @param errorCode error code.
     * @param extras extra informations.
     */
    public BusinessException(BusinessErrorCode errorCode, String message, List<String> extras) {
        this(errorCode,message);
        this.extras = extras;
    }

    /** Constructor.
     * @param errorCode error code.
     * @param extras extra informations.
     */
    public BusinessException(BusinessErrorCode errorCode, String message, String[] extras) {
    	this(errorCode,message,Arrays.asList(extras));
    }

    /** Get error code.
     * @return error code.
     */
    public BusinessErrorCode getErrorCode() {
        return errorCode;
    }

	public boolean equalErrCode(BusinessErrorCode code) {
		return errorCode.equals(code);
	}

    /** Get extra informations.
     * @return extra informations.
     */
    public List<String> getExtras() {
        return extras;
    }

    /** Add an extra information.
     * @param extra : the extra information.
     */
    public void addExtra(String extra) {
        if (extras == null) {
            extras = new ArrayList<String>();
        }
        extras.add(extra);
    }
}
