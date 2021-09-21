/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Parameter")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParameterDto <T> {

	@Schema(description = "Indicate if this Parameter should be displayed or hidden")
	protected boolean hidden;

	@Schema(description = "Indicate if this Parameter should be displayed in read only mode")
	protected boolean readonly;

	@Schema(name = "Default", description = "Describe is this policy is enable or not")
	protected NestedParameterDto<T> defaut;

	@Schema(name = "Maximum", description = "Describe if this policy can be overriden")
	protected NestedParameterDto<T> maximum;

	@Schema(name = "Unlimited", description = "TODO")
	protected UnlimitedParameterDto unlimited;

	public ParameterDto() {
		super();
	}

	public ParameterDto(boolean hidden, boolean readonly, NestedParameterDto<T> defaut,
			NestedParameterDto<T> maximum) {
		super();
		this.hidden = hidden;
		this.readonly = readonly;
		this.defaut = defaut;
		this.maximum = maximum;
		this.unlimited = new UnlimitedParameterDto();
	}

	public ParameterDto(boolean hidden, boolean readonly, NestedParameterDto<T> defaut) {
		super();
		this.hidden = hidden;
		this.readonly = readonly;
		this.defaut = defaut;
		this.maximum = null;
		this.unlimited = null;
	}

	public ParameterDto(
			boolean hidden,
			boolean readonly,
			NestedParameterDto<T> defaut,
			NestedParameterDto<T> maximum,
			UnlimitedParameterDto unlimited) {
		super();
		this.hidden = hidden;
		this.readonly = readonly;
		this.defaut = defaut;
		this.maximum = maximum;
		this.unlimited = unlimited;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public NestedParameterDto<T> getDefaut() {
		return defaut;
	}

	public void setDefaut(NestedParameterDto<T> defaut) {
		this.defaut = defaut;
	}

	public NestedParameterDto<T> getMaximum() {
		return maximum;
	}

	public void setMaximum(NestedParameterDto<T> maximum) {
		this.maximum = maximum;
	}

	public UnlimitedParameterDto getUnlimited() {
		return unlimited;
	}

	public void setUnlimited(UnlimitedParameterDto unlimited) {
		this.unlimited = unlimited;
	}

	@Override
	public String toString() {
		return "ParameterDto [hidden=" + hidden + ", readonly=" + readonly + ", defaut=" + defaut + ", maximum="
				+ maximum + ", unlimited=" + unlimited + "]";
	}
}
