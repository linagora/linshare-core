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
package org.linagora.linshare.auth;

/**
 *
 * @author xinit
 */
public interface AuthRole {
	static final String ROLE_AUTH = "ROLE_AUTH";
	static final String ROLE_AUTH_OIDC = "ROLE_AUTH_OIDC";
	static final String ROLE_USER = "ROLE_USER";
	static final String ROLE_DELEGATION = "ROLE_DELEGATION";
	static final String ROLE_ADMIN = "ROLE_ADMIN";
	static final String ROLE_UPLOAD = "ROLE_UPLOAD";
	static final String ROLE_INTERNAL = "ROLE_INTERNAL";
	static final String ROLE_SUPERADMIN = "ROLE_SUPERADMIN";
	static final String ROLE_SAFE = "ROLE_SAFE";
}
