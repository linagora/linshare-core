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

public class FunctionalityPermissions {

	protected boolean parentAllowAPUpdate;

	protected boolean parentAllowCPUpdate;

	protected boolean parentAllowDPUpdate;

	protected boolean parentAllowParametersUpdate;

	public FunctionalityPermissions(boolean parentAllowAPUpdate,
			boolean parentAllowCPUpdate, boolean parentAllowDPUpdate,
			boolean parentAllowParametersUpdate) {
		super();
		this.parentAllowAPUpdate = parentAllowAPUpdate;
		this.parentAllowCPUpdate = parentAllowCPUpdate;
		this.parentAllowDPUpdate = parentAllowDPUpdate;
		this.parentAllowParametersUpdate = parentAllowParametersUpdate;
	}

	public boolean isParentAllowAPUpdate() {
		return parentAllowAPUpdate;
	}

	public void setParentAllowAPUpdate(boolean parentAllowAPUpdate) {
		this.parentAllowAPUpdate = parentAllowAPUpdate;
	}

	public boolean isParentAllowCPUpdate() {
		return parentAllowCPUpdate;
	}

	public void setParentAllowCPUpdate(boolean parentAllowCPUpdate) {
		this.parentAllowCPUpdate = parentAllowCPUpdate;
	}

	public boolean isParentAllowDPUpdate() {
		return parentAllowDPUpdate;
	}

	public void setParentAllowDPUpdate(boolean parentAllowDPUpdate) {
		this.parentAllowDPUpdate = parentAllowDPUpdate;
	}

	public boolean isParentAllowParametersUpdate() {
		return parentAllowParametersUpdate;
	}

	public void setParentAllowParametersUpdate(
			boolean parentAllowParametersUpdate) {
		this.parentAllowParametersUpdate = parentAllowParametersUpdate;
	}
}
