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
package org.linagora.linShare.view.tapestry.objects;

/**
 * Utilitary class used to export unquoted text
 * @author ncharles
 *
 */
public class JSONRaw extends Number{

	private static final long serialVersionUID = 4159212877665876722L;
	private final String rawText;

    public JSONRaw(String rawText) {
        this.rawText = rawText;
    }
    
    @Override public String toString() {
        return rawText;
    }
    
	public double doubleValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float floatValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int intValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long longValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
