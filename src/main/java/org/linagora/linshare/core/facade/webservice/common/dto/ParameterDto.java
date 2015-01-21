/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.common.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.Language;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "Parameter")
@ApiModel(value = "Parameter", description = "Parameters used by functionalities")
public class ParameterDto {

    @ApiModelProperty(value = "Integer")
	private int integer;

    @ApiModelProperty(value = "String")
	private String string;

    @ApiModelProperty(value = "Type")
	private String type;

    @ApiModelProperty(value = "Bool")
	private boolean bool;

    @ApiModelProperty(value = "Select")
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
}
