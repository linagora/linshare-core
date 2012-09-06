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
package org.linagora.linshare.view.tapestry.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.DocumentFacade;


@SupportsInformalParameters
@Import(library = {"ActionsBarDocument.js"})
public class ActionsBarShare {


	/***********************************
	 * Parameters
	 ***********************************/
	
	
	/**
	 * The user owner for the document list.
	 */
	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	private UserVo user;
	
	@Property(read=true)
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	private String formName;
	
	
	/***********************************
	 * Properties
	 ***********************************/

	/***********************************
	 * Service injection
	 ***********************************/

	@Inject
	private AbstractDomainFacade domainFacade;
	
	@Inject
	private DocumentFacade documentFacade;
	
	
    
	/***********************************
	 * Flags
	 ***********************************/
	@SuppressWarnings("unused")
	@Property
	private boolean activeSignature;
	
	@SuppressWarnings("unused")
	@Property
	private boolean activeEncipherment;

	
	/*********************************
	 * Phase render
	 *********************************/

	/**
	 * Initialization of the filesSelected list and set the userLogin from the user ASO.
	 * @throws BusinessException 
	 */
	@SetupRender
	public void initUserlogin() throws BusinessException {
		activeSignature = documentFacade.isSignatureActive(user);
		activeEncipherment = documentFacade.isEnciphermentActive(user);
	}
	
}
