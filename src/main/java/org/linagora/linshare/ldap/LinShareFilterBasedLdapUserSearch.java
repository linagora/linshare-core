/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.linagora.linshare.ldap;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.util.Assert;

/**
 * Dirty copy of FilterBasedLdapUserSearch.
 * @author FMartin
 *
 */
public class LinShareFilterBasedLdapUserSearch implements LdapUserSearch {

		private static final Log logger = LogFactory.getLog(LinShareFilterBasedLdapUserSearch.class);

		private final ContextSource contextSource;

		private final SearchControls searchControls = new SearchControls();

		private String searchBase = "";

		/**
		 * The filter expression used in the user search. This is an LDAP search filter (as
		 * defined in 'RFC 2254') with optional arguments. See the documentation for the
		 * <tt>search</tt> methods in {@link javax.naming.directory.DirContext DirContext} for
		 * more information.
		 *
		 * <p>
		 * In this case, the username is the only parameter.
		 * </p>
		 * Possible examples are:
		 * <ul>
		 * <li>(uid={0}) - this would search for a username match on the uid attribute.</li>
		 * </ul>
		 */
		private final String searchFilter;

		// ~ Constructors
		// ===================================================================================================

		@SuppressWarnings("deprecation")
		public LinShareFilterBasedLdapUserSearch(String searchBase, String searchFilter,
				BaseLdapPathContextSource contextSource) {
			Assert.notNull(contextSource, "contextSource must not be null");
			Assert.notNull(searchFilter, "searchFilter must not be null.");
			Assert.notNull(searchBase,
					"searchBase must not be null (an empty string is acceptable).");

			this.searchFilter = searchFilter;
			this.contextSource = contextSource;
			this.searchBase = searchBase;

			searchControls.setSearchScope(SearchControls.OBJECT_SCOPE);

			if (searchBase.length() == 0) {
				logger.info("SearchBase not set. Searches will be performed from the root: "
						+ contextSource.getBaseLdapPath());
			}
		}

		// ~ Methods
		// ========================================================================================================

		/**
		 * Return the LdapUserDetails containing the user's information
		 *
		 * @param username the username to search for.
		 *
		 * @return An LdapUserDetails object containing the details of the located user's
		 * directory entry
		 *
		 * @throws UsernameNotFoundException if no matching entry is found.
		 */
		public DirContextOperations searchForUser(String username) {
			if (logger.isDebugEnabled()) {
				logger.debug("Searching for user '" + username + "', with user search "
						+ this);
			}

			SpringSecurityLdapTemplate template = new SpringSecurityLdapTemplate(
					contextSource);

			template.setSearchControls(searchControls);

			try {

				return template.searchForSingleEntry(searchBase, searchFilter,
						new String[] { username });

			}
			catch (IncorrectResultSizeDataAccessException notFound) {
				if (notFound.getActualSize() == 0) {
					throw new UsernameNotFoundException("User " + username
							+ " not found in directory.");
				}
				// Search should never return multiple results if properly configured, so just
				// rethrow
				throw notFound;
			}
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append("[ searchFilter: '").append(searchFilter).append("', ");
			sb.append("searchBase: '").append(searchBase).append("'");
			sb.append(", scope: ")
					.append(SearchControls.OBJECT_SCOPE);
			sb.append(", searchTimeLimit: ").append(searchControls.getTimeLimit());
			sb.append(", derefLinkFlag: ").append(searchControls.getDerefLinkFlag())
					.append(" ]");
			return sb.toString();
		}
}
