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

import org.linagora.linshare.core.domain.constants.UploadPropositionActionType;
import org.linagora.linshare.core.domain.entities.UploadPropositionAction;

import com.google.common.base.Function;

public class UploadPropositionActionDto {

	protected String uuid;

	protected String action;

	protected String data;

	public UploadPropositionActionDto() {
		super();
	}


		public UploadPropositionActionDto(UploadPropositionAction entity) {
		super();
		this.uuid = entity.getUuid();
		this.action = entity.getActionType().name();
		this.data = entity.getData();
	}

	public UploadPropositionActionDto(String action) {
		super();
		this.action = action;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	/*
	 * Transformers
	 */
	public static Function<UploadPropositionAction, UploadPropositionActionDto> toVo() {
		return new Function<UploadPropositionAction, UploadPropositionActionDto>() {
			@Override
			public UploadPropositionActionDto apply(
					UploadPropositionAction entity) {
				return new UploadPropositionActionDto(entity);
			}
		};
	}

	public static Function<UploadPropositionActionDto, UploadPropositionAction> toEntity() {
		return new Function<UploadPropositionActionDto, UploadPropositionAction>() {
			@Override
			public UploadPropositionAction apply(UploadPropositionActionDto dto) {
				UploadPropositionAction entity = new UploadPropositionAction();
				entity.setUuid(dto.getUuid());
				entity.setData(dto.getData());
				entity.setActionType(UploadPropositionActionType.fromString(dto
						.getAction(), true));
				return entity;
			}
		};
	}
}
