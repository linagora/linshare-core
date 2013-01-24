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
package org.linagora.linshare.view.tapestry.pages;

import java.io.IOException;

import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.view.tapestry.pages.administration.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logout {

	private static Logger logger = LoggerFactory.getLogger(Logout.class);
	
	@SessionState
	private UserVo userDetailsVo;

	@Inject
	private Response response;
	
	@Inject
	private RequestGlobals requestGlobals;
	
	public void onActivate(){

		logger.info("logout user : " + userDetailsVo.getMail() + "(" + userDetailsVo.getLsUid() + ")");
		userDetailsVo=null;
//		requestGlobals.getHTTPServletRequest().getSession().invalidate(); //already done by j_spring_security_logout
		try {
			response.sendRedirect(requestGlobals.getHTTPServletRequest().getContextPath()+"/j_spring_security_logout");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
