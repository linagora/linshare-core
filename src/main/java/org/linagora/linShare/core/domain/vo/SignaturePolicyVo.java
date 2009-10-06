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
package org.linagora.linShare.core.domain.vo;

public class SignaturePolicyVo {

	private String label;
	private String oid;
	
	
	public SignaturePolicyVo() {
		this.label = null;
		this.oid = null;
	}

	public SignaturePolicyVo(String label, String oid) {
		this.label = label;
		this.oid = oid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SignaturePolicyVo) {
			SignaturePolicyVo sp = (SignaturePolicyVo) obj;
			return this.oid.equals(sp.getOid());
		} else
			return false;
	}
	@Override
	public String toString() {
		return getLabel();
	}
	
	
}
