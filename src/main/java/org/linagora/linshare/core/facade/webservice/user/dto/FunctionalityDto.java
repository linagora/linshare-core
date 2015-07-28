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
package org.linagora.linshare.core.facade.webservice.user.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = FunctionalityStringDto.class, name = "string"),
		@Type(value = FunctionalityIntegerDto.class, name = "integer"),
		@Type(value = FunctionalityBooleanDto.class, name = "boolean"),
		@Type(value = FunctionalityTimeDto.class, name = "time"),
		@Type(value = FunctionalitySizeDto.class, name = "size"),
		@Type(value = FunctionalityDto.class, name = "simple"),
		})
@XmlRootElement(name = "Functionality")
@XmlSeeAlso({ FunctionalityStringDto.class,
	FunctionalityIntegerDto.class,
	FunctionalityBooleanDto.class,
	FunctionalityTimeDto.class,
	FunctionalitySizeDto.class})
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
