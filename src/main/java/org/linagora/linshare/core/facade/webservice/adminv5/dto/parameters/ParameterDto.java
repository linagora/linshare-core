/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

import org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested.NestedParameterDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.google.common.base.MoreObjects;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(
	name = "Parameter",
	description = "A Parameter",
	discriminatorProperty = "type",
	discriminatorMapping = {
			@DiscriminatorMapping(value = "BOOLEAN", schema = BooleanParameterDto.class),
			@DiscriminatorMapping(value = "STRING", schema = StringParameterDto.class),
			@DiscriminatorMapping(value = "INTEGER_DEFAULT", schema = IntegerDefaultParameterDto.class),
			@DiscriminatorMapping(value = "INTEGER_MAX", schema = IntegerMaximumParameterDto.class),
			@DiscriminatorMapping(value = "INTEGER_ALL", schema = IntegerDefaultAndMaximumParameterDto.class),
			@DiscriminatorMapping(value = "LANGUAGE", schema = LanguageParameterDto.class),
			@DiscriminatorMapping(value = "UNIT_TIME_DEFAULT", schema = TimeUnitDefaultParameterDto.class),
			@DiscriminatorMapping(value = "UNIT_TIME_MAX", schema = TimeUnitMaximumParameterDto.class),
			@DiscriminatorMapping(value = "UNIT_TIME_ALL", schema = TimeUnitDefaultAndMaximumParameterDto.class),
			@DiscriminatorMapping(value = "UNIT_SIZE_DEFAULT", schema = FileSizeUnitDefaultParameterDto.class),
			@DiscriminatorMapping(value = "UNIT_SIZE_MAX", schema = FileSizeUnitMaximumParameterDto.class),
			@DiscriminatorMapping(value = "UNIT_SIZE_ALL", schema = FileSizeUnitDefaultAndMaximumParameterDto.class)
	}
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
	@Type(value = BooleanParameterDto.class, name="BOOLEAN"),
	@Type(value = StringParameterDto.class, name="STRING"),
	@Type(value = IntegerDefaultParameterDto.class, name="INTEGER_DEFAULT"),
	@Type(value = IntegerMaximumParameterDto.class, name="INTEGER_MAX"),
	@Type(value = IntegerDefaultAndMaximumParameterDto.class, name="INTEGER_ALL"),
	@Type(value = LanguageParameterDto.class, name="LANGUAGE"),
	@Type(value = TimeUnitDefaultParameterDto.class, name="UNIT_TIME_DEFAULT"),
	@Type(value = TimeUnitMaximumParameterDto.class, name="UNIT_TIME_MAX"),
	@Type(value = TimeUnitDefaultAndMaximumParameterDto.class, name="UNIT_TIME_ALL"),
	@Type(value = FileSizeUnitDefaultParameterDto.class, name="UNIT_SIZE_DEFAULT"),
	@Type(value = FileSizeUnitMaximumParameterDto.class, name="UNIT_SIZE_MAX"),
	@Type(value = FileSizeUnitDefaultAndMaximumParameterDto.class, name="UNIT_SIZE_ALL"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ParameterDto <T> {

	@Schema(description = "Indicate if this Parameter should be displayed or hidden")
	protected boolean hidden;

	@Schema(description = "Indicate if this Parameter should be displayed in read only mode")
	protected boolean readonly;

	@Schema(name = "default", description = "Default value")
	protected NestedParameterDto<T> defaut;

	@Schema(name = "maximum", description = "Maximum value")
	protected NestedParameterDto<T> maximum;

	@Schema(name = "unlimited", description = "True to ignore maximum value.")
	protected UnlimitedParameterDto unlimited;

	@Schema(name = "type", description = "Parameter type.", accessMode = AccessMode.READ_ONLY)
	protected String type;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("hidden", hidden)
				.add("readonly", readonly)
				.add("defaut", defaut)
				.add("maximum", maximum)
				.add("unlimited", unlimited)
				.toString();
	}
}
