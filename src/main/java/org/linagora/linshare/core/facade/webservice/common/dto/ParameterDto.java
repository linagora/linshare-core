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
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.Language;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "Parameter")
@Schema(name = "Parameter", description = "Parameters used by functionalities")
public class ParameterDto {

    @Schema(description = "Integer")
	private int integer;
    
    @Schema(description = "Integer")
    @JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer maxInteger;

    @Schema(description = "String")
    private String string;

    @Schema(description = "max unit field, define the unit of the max value")
    @JsonInclude(JsonInclude.Include.NON_NULL)
	private String maxString;

    @Schema(description = "default value field, define if the default value is used")
    @JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean defaultValueUsed;

    @Schema(description = "max value used field, define if the maxValue field is used")
    @JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean maxValueUsed;

    @Schema(description = "Type")
	private String type;

    @Schema(description = "Bool")
	private boolean bool;

    @Schema(description = "Select")
	private List<String> select;


	public ParameterDto() {
		super();
	}

	public ParameterDto(String s) {
		this.string = s;
		this.type = "STRING";
	}
	
	public ParameterDto(int i) {
		this.integer = i;
		this.type = "INTEGER";
	}

	public ParameterDto(String type, List<String> listUnit, String unit, int integer) {
		this.string = unit;
		this.integer = integer;
		this.type = type;
		this.select = new ArrayList<String>(listUnit);
	}

	public ParameterDto(String type, List<String> listUnit) {
		this.type = type;
		this.select = new ArrayList<String>(listUnit);
	}

	public ParameterDto(Language value) {
		this.string = value.getTapestryLocale();
		this.type = "ENUM_LANG";
		this.select = new ArrayList<String>();
		this.select.add(Language.ENGLISH.getTapestryLocale());
		this.select.add(Language.FRENCH.getTapestryLocale());
	}

	public ParameterDto(boolean bool) {
		this.type = "BOOLEAN";
		this.bool = bool;
	}

	public int getInteger() {
		return integer;
	}

	public void setInteger(int integer) {
		this.integer = integer;
	}
	
	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	public List<String> getSelect() {
		return select;
	}

	public void setSelect(List<String> select) {
		this.select = select;
	}

	public Integer getMaxInteger() {
		return maxInteger;
	}

	public void setMaxInteger(Integer maxInteger) {
		this.maxInteger = maxInteger;
	}

	public String getMaxString() {
		return maxString;
	}

	public void setMaxString(String maxString) {
		this.maxString = maxString;
	}

	public Boolean getDefaultValueUsed() {
		return defaultValueUsed;
	}

	public void setDefaultValueUsed(Boolean defaultValueUsed) {
		this.defaultValueUsed = defaultValueUsed;
	}

	public Boolean getMaxValueUsed() {
		return maxValueUsed;
	}

	public void setMaxValueUsed(Boolean maxValueUsed) {
		this.maxValueUsed = maxValueUsed;
	}
}
