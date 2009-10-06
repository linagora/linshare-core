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
package org.linagora.linShare.view.tapestry.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentClassResolver;

/**
 * This is a small component that able to display any links (page link or external link).
 * This component will detect if the link is a page or else the link is displayed like it was entered.
 * @author ngapaillard
 *
 */
@SupportsInformalParameters
public class MultiLink {

	/**
	 * The url where the link is pointed target (equivalent to href).
	 */
	@Parameter(required=true,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String url;
	
	/**
	 * The target where the page targeted will be display (equivalent to target in <a> tag).
	 */
	@Parameter(required=false,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String target;
	

	
	/**
	 * The css class for the links.
	 */
	@SuppressWarnings("unused")
	@Parameter(name="class",required=false,value="multiLink",defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String myClass;
	
	/**
	 * The content of the link (image,label... with "A" tag it's the content between <a>and </a>).
	 */
	@SuppressWarnings("unused")
	@Parameter(required=true,defaultPrefix=BindingConstants.BLOCK)
	@Property
	private Block content;
	
	@Inject
	private ComponentClassResolver componentClassResolver;

	
	@SuppressWarnings("unused")
	@Property
	private boolean targetEnable=false;
	
	@SuppressWarnings("unused")
	@Property
	private boolean page=false;
	
	@SuppressWarnings("unused")
	@SetupRender
	private void initUrl(){
		if(null!=target && !"".equals(target)){
			targetEnable=true;
		}
		if(componentClassResolver.isPageName(url)){
			page=true;
		}
	}
	
	
}
