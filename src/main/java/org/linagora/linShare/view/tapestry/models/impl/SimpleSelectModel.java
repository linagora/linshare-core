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
package org.linagora.linShare.view.tapestry.models.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.util.AbstractSelectModel;

public class SimpleSelectModel<T> extends AbstractSelectModel {

	private List<T> list;
	private Messages messages;
	private String keyHeader;


	public SimpleSelectModel(List<T> list, Messages messages, String keyHeader) {
		this.list = list;
		this.messages = messages;
		this.keyHeader = keyHeader;
	}

	public List<OptionGroupModel> getOptionGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	private List<OptionModel> generateOptionModelList() {
		List<OptionModel> optionModelList = new ArrayList<OptionModel>();
		if (list != null) {
			for (T obj : list) {
				optionModelList.add(new OptionModelImpl(messages.get(keyHeader+"." +obj.toString()),
						obj));
			}
		}
		return optionModelList;
	}


	public List<OptionModel> getOptions() {
		List<OptionModel> l = generateOptionModelList();

		return l;

	}

}
