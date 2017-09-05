/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.mongo.entities.mto;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.linagora.linshare.core.domain.constants.TargetKind;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.Thread;

import com.wordnik.swagger.annotations.ApiModelProperty;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class CopyMto {

	@ApiModelProperty(value = "Uuid")
	protected String uuid;

	@ApiModelProperty(value = "Name")
	protected String name;

	@ApiModelProperty(value = "Kind")
	protected TargetKind kind;

	public CopyMto() {
		super();
}

	public CopyMto(String uuid, String name, TargetKind kind) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.kind = kind;
	}

	public CopyMto(DocumentEntry de) {
		super();
		this.kind = TargetKind.PERSONAL_SPACE;
		this.uuid = de.getUuid();
		this.name = de.getName();
	}

	public CopyMto(ShareEntry de) {
		super();
		this.kind = TargetKind.RECEIVED_SHARE;
		this.uuid = de.getUuid();
		this.name = de.getName();
	}

	public CopyMto(Thread workGroup, boolean withName) {
		super();
		this.kind = TargetKind.SHARED_SPACE;
		this.uuid = workGroup.getLsUuid();
		if (withName) {
			this.name = workGroup.getName();
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TargetKind getKind() {
		return kind;
	}

	public void setKind(TargetKind kind) {
		this.kind = kind;
	}

}
