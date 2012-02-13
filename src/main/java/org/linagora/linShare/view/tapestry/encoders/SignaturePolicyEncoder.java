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
package org.linagora.linShare.view.tapestry.encoders;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import org.linagora.linShare.core.domain.vo.SignaturePolicyVo;

public class SignaturePolicyEncoder extends AbstractSelectModel implements ValueEncoder<SignaturePolicyVo> {

	private List<SignaturePolicyVo> allList;

	public SignaturePolicyEncoder(List<SignaturePolicyVo> allList) {
		this.allList = allList;
	}

	public String toClient(SignaturePolicyVo sp) {
		return sp.getOid();
	}

	public SignaturePolicyVo toValue(String id) {

		SignaturePolicyVo res = null;

		for (SignaturePolicyVo obj : allList) {
			if (obj.getOid().equals(id)){
				res = obj;
				break;
			}
		}
		return res;
	}

	public List<OptionGroupModel> getOptionGroups() {
		return null;
	}

	public List<OptionModel> getOptions() {
		List<OptionModel> optionModels = new ArrayList<OptionModel>();
		for (SignaturePolicyVo obj : allList) {
			optionModels.add(new OptionModelImpl(obj.getLabel(), obj.getOid()));
		}
		return optionModels;
	}

}
