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
package org.linagora.linshare.core.facade.webservice.user.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = FunctionalityStringDto.class, name = "string"),
		@Type(value = FunctionalityIntegerDto.class, name = "integer"),
		@Type(value = FunctionalityBooleanDto.class, name = "boolean"),
		@Type(value = FunctionalityTimeDto.class, name = "time"),
		@Type(value = FunctionalitySizeDto.class, name = "size"),
		@Type(value = FunctionalityEnumLangDto.class, name = "language"),
		@Type(value = FunctionalityDto.class, name = "simple"),
		})
@XmlRootElement(name = "Functionality")
@XmlSeeAlso({ FunctionalityStringDto.class,
	FunctionalityIntegerDto.class,
	FunctionalityBooleanDto.class,
	FunctionalityTimeDto.class,
	FunctionalitySizeDto.class,
	FunctionalityEnumLangDto.class})
public class FunctionalityDto {

	/**
	 * the functionality identifier.
	 */
	protected String identifier;
	/**
	 * if the functionality is enable/available.
	 */
	protected boolean enable;
	/**
	 * if the user can override the default parameters.
	 */
	protected Boolean canOverride;

	public FunctionalityDto() {
		super();
	}

	public FunctionalityDto(String identifier, boolean enable,
			Boolean  canOverride) {
		super();
		this.identifier = identifier;
		this.enable = enable;
		this.canOverride = canOverride;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public Boolean isCanOverride() {
		return canOverride;
	}

	public void setCanOverride(Boolean canOverride) {
		this.canOverride = canOverride;
	}
}
