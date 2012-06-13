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
package org.linagora.linShare.auth.exceptions;

import org.springframework.security.authentication.BadCredentialsException;

public class BadDomainException extends BadCredentialsException {

	private static final long serialVersionUID = 5757544291808326295L;

	public BadDomainException(String msg, Throwable t) {
		super(msg, t);
	}

	public BadDomainException(String msg, Object extraInformation) {
		super(msg, extraInformation);
	}
	
	public BadDomainException(String msg) {
        super(msg);
    }

}
