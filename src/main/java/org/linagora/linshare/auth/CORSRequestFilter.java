/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * PreflightedRequestFilter is used to cut the filter chain when current request
 * is a preflighted one. Search for CORS, same-origin policy and HTTP access
 * control for more information.
 * 
 * @author nbertrand
 */
public class CORSRequestFilter extends OncePerRequestFilter {

	public CORSRequestFilter() throws ServletException {
		super();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req,
			HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		if (req.getHeader("Origin") == null) {
			chain.doFilter(req, res);
			return;
		}

		res.addHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
		res.addHeader("Access-Control-Allow-Credentials", "true");

		/*
		 * Request is a preflighted one
		 */
		if (req.getHeader("Access-Control-Request-Method") != null
				&& req.getMethod().equals("OPTIONS")) {
			res.addHeader("Access-Control-Allow-Methods",
					"GET, POST, PUT, DELETE");
			res.addHeader("Access-Control-Allow-Headers",
					"Accept, Authorization, Cache-Control, Content-Type, Origin, X-Requested-With");
			res.addHeader("Access-Control-Max-Age", "1728000");

			logger.debug("Preflighted OPTIONS request, no filter applied.");
			return;
		}
		chain.doFilter(req, res);
	}
}
