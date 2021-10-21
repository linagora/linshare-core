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
package org.linagora.linshare.core.facade.webservice.adminv5.dto.parameters.nested;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
	name = "NestedParameter",
	description = "A NestedParameter",
	hidden = true,
	discriminatorProperty = "type",
	discriminatorMapping = {
			@DiscriminatorMapping(value = "BOOLEAN", schema = NestedBooleanParameterDto.class),
			@DiscriminatorMapping(value = "STRING", schema = NestedStringParameterDto.class),
			@DiscriminatorMapping(value = "LANGUAGE", schema = NestedLanguageParameterDto.class),
			@DiscriminatorMapping(value = "INTEGER", schema = NestedIntegerParameterDto.class),
			@DiscriminatorMapping(value = "INTEGER", schema = NestedFileSizeParameterDto.class),
			@DiscriminatorMapping(value = "INTEGER", schema = NestedTimeParameterDto.class),
	}
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = false)
@JsonSubTypes({
	@Type(value = NestedBooleanParameterDto.class, name="BOOLEAN"),
	@Type(value = NestedStringParameterDto.class, name="STRING"),
	@Type(value = NestedLanguageParameterDto.class, name="LANGUAGE"),
	@Type(value = NestedIntegerParameterDto.class, name="INTEGER"),
	@Type(value = NestedFileSizeParameterDto.class, name="UNIT_SIZE"),
	@Type(value = NestedTimeParameterDto.class, name="UNIT_TIME"),
})
public abstract class NestedParameterDto<T> {

	@Schema(description = "The current value")
	protected T value;

	@Schema(description = "The current value of my domain ancestor (parent domain)")
	protected T parentValue;

	public NestedParameterDto() {
		super();
	}

	public NestedParameterDto(T value, T parentValue) {
		super();
		this.value = value;
		this.parentValue = parentValue;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T getParentValue() {
		return parentValue;
	}

	@Schema(description = "Tell if parent value was overriden.", accessMode = AccessMode.READ_ONLY)
	@JsonProperty
	public boolean isOverriden() {
		return !parentValue.equals(value);
	}

	@JsonIgnore
	public void setOverriden(boolean noop) {
	}

	public void setParentValue(T parentValue) {
		this.parentValue = parentValue;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("value", value)
				.add("parentValue", parentValue)
				.add("isOverriden", isOverriden())
				.toString();
	}

}
