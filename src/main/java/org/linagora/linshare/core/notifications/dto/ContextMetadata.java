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
package org.linagora.linshare.core.notifications.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "ContextMetadata")
public class ContextMetadata {

	protected String mailType;

	protected List<Variable> variables;

	public ContextMetadata(String mailType) {
		super();
		this.mailType = mailType;
	}

	public String getMailType() {
		return mailType;
	}

	public void setMailType(String mailType) {
		this.mailType = mailType;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public void addVariable(Variable variable) {
		if (this.variables == null) {
			this.variables = Lists.newArrayList();
		}
		this.variables.add(variable);
	}

	@Override
	public String toString() {
		return "ContextMetadata [mailType=" + mailType + ", variables=" + variables + "]";
	}
}
