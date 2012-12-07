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
package org.linagora.linshare.view.tapestry.translators;

import java.util.ArrayList;
import java.util.List;

/**
 * Translator which permits to transform logins separated by commas into a login List and vice-versa
 */
public class StringListTranslator extends AbstractListTranslator<String> {

	public StringListTranslator(List<String> logins) {
		super(logins, "StringListTranslator", "pages.bam.Index.error.invalidLogin");
	}
	
	@SuppressWarnings("unchecked")
	public Class<List<String>> getType() {
		//this exact syntax and code is the only way I found. 
		//return (Class<List<T>>)new ArrayList<T>().getClass()
		//on one line does not work...
		List<String> l = new ArrayList<String>();
		return (Class<List<String>>)l.getClass();
	}

	@Override
	public String getObjectFromName(String name) {
		return name;
	}

	@Override
	public String getNameFromObject(String a) {
		return a;
	}


}
