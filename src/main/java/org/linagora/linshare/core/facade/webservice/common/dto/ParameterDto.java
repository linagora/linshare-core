/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
}
