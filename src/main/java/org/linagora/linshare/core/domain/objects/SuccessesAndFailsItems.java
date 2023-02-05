/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.domain.objects;

import java.util.ArrayList;
import java.util.List;

public class SuccessesAndFailsItems<T> {

	
	private List<T> successesItem;
	
	private List<T> failsItem;
	
	public SuccessesAndFailsItems() {
		this.successesItem = new ArrayList<T>();
		this.failsItem = new ArrayList<T>();
	}

	public List<T> getSuccessesItem() {
		return successesItem;
	}

	public List<T> getFailsItem() {
		return failsItem;
	}

	public void addSuccessItem(T e) {
		this.successesItem.add(e);
	}
	
	public void addFailItem(T e) {
		this.failsItem.add(e);
	}
	
	public void setSuccessesItem(List<T> successesItem) {
		this.successesItem = successesItem;
	}

	public void setFailsItem(List<T> failsItem) {
		this.failsItem = failsItem;
	}

	public void addAll(SuccessesAndFailsItems<T> createSharingWithMail) {
		this.successesItem.addAll(createSharingWithMail.getSuccessesItem());
		this.failsItem.addAll(createSharingWithMail.getFailsItem());
	}
	
	
	
}
