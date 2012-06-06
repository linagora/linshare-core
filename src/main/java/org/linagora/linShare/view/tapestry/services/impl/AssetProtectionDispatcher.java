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
package org.linagora.linShare.view.tapestry.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.tapestry5.internal.services.RequestConstants;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ClasspathAssetAliasManager;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.linagora.linShare.core.domain.constants.SecuredShareConstants;
import org.linagora.linShare.core.exception.BusinessErrorCode;


/**
 * Dispatcher that handles whether to allow or deny access to particular 
 * assets. 
 */
public class AssetProtectionDispatcher implements Dispatcher {

	private final ClasspathAssetAliasManager assetAliasManager;
	@Inject
	private final PageRenderLinkSource pageRenderLinkSource;
	private final List<Pattern> patterns;
	
	public AssetProtectionDispatcher(final PageRenderLinkSource pageRenderLinkSource, 
			final ClasspathAssetAliasManager manager, final List<String> regex) {
		this.assetAliasManager = manager;
		this.pageRenderLinkSource = pageRenderLinkSource;
		patterns = new ArrayList<Pattern>();
		for(String r : regex) {
			patterns.add(Pattern.compile(r));
		}
	}

	public boolean dispatch(Request request, Response response)	throws IOException {

		//we only protect assets, and don't examine any other url's.
		String path = request.getPath();

		if (!path.startsWith(RequestConstants.ASSET_PATH_PREFIX)) {
			return false;
		}
		
		String resourcePath = assetAliasManager.toResourcePath(path);
		
		for(Pattern p : patterns) {
			if(p.matcher(resourcePath).matches()) {
				//the resource is authorized
				return false;
			}
		}
		
		//if we get here, no regexp matches the path, so raise an error
		response.sendRedirect(this.pageRenderLinkSource.createPageRenderLinkWithContext(SecuredShareConstants.ERROR_PAGE, BusinessErrorCode.AUTHENTICATION_ERROR));
		return true;
	}

}
