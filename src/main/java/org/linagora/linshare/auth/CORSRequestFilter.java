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
