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
package org.linagora.linshare.view.tapestry.objects;

import java.util.List;

import org.apache.tapestry5.Asset;

public class Subsection {

	private final String name;

	private final List<String> descriptions;

	private final Integer index;

	private final String image;

	private final String imageTitle;



	public Subsection(final String name,final List<String> descriptions,final Asset image,final Integer index,final String imageTitle){
		this.name=name;
		this.imageTitle=imageTitle;
		this.descriptions=descriptions;
		if(null!=image){
			this.image=image.toClientURL();
		}else{
			this.image=null;
		}
		this.index=index;
	}

	public String getName() {
		return name;
	}


	public List<String> getDescriptions() {
		return descriptions;
	}

	public String getImage() {

		return image;
	}

	public Integer getIndex() {
		return index;
	}
	public String getImageTitle() {
		return imageTitle;
	}

}
