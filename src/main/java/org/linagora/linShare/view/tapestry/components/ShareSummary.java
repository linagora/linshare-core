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

import java.util.List;
import java.util.Set;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.chenillekit.tapestry.core.components.Editor;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;

/**
 *
 */
public class ShareSummary {


    /* ***********************************************************
     *                         Parameters
     ************************************************************ */
	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	private Set<UserVo> users;

	@Parameter(required=true,defaultPrefix=BindingConstants.PROP)
	@Property
	private List<DocumentVo> documents;

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @Component(parameters = {"value=editorValue"})
    private Editor editor;

    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @Property
    private String editorValue;
}
