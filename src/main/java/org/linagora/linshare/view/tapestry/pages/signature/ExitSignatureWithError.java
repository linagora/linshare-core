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
package org.linagora.linshare.view.tapestry.pages.signature;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.linagora.linshare.core.domain.vo.DocToSignContext;
import org.linagora.linshare.core.domain.vo.UserSignature;

public class ExitSignatureWithError {
	

	@Property
	@SessionState
	private UserSignature userSignature;
	
	@Inject
	private PageRenderLinkSource linkFactory;	
	
	
	public Object onActionFromQuitWizard(){
		
			if(userSignature.getDocContext().equals(DocToSignContext.DOCUMENT)){
				userSignature = null;
				return  linkFactory.createPageRenderLink("files/Index");
			} else if (userSignature.getDocContext().equals(DocToSignContext.SHARED)) {
				userSignature = null;
				return  linkFactory.createPageRenderLink("Index");
			} 

			userSignature = null;
			return null;
	}
	
}
