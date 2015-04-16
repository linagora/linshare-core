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

package org.linagora.linshare.core.facade.webservice.uploadproposition.dto;

import java.util.List;

import org.linagora.linshare.core.domain.entities.UploadPropositionAction;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;
import org.linagora.linshare.core.domain.entities.UploadPropositionRule;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class UploadPropositionFilterDto {

	protected String uuid;

	protected String name;

	protected String match;

	protected List<UploadPropositionRuleDto> uploadPropositionRules = Lists
			.newArrayList();

	protected List<UploadPropositionActionDto> uploadPropositionActions = Lists
			.newArrayList();

	public UploadPropositionFilterDto(UploadPropositionFilter entity) {
		super();
		this.uuid = entity.getUuid();
		this.name = entity.getName();
		this.match = entity.getMatch().name();
		for (UploadPropositionAction action : entity.getActions()) {
			this.uploadPropositionActions.add(new UploadPropositionActionDto(
					action));
		}
		for (UploadPropositionRule rule : entity.getRules()) {
			this.uploadPropositionRules.add(new UploadPropositionRuleDto(rule));
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

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public List<UploadPropositionRuleDto> getUploadPropositionRules() {
		return uploadPropositionRules;
	}

	public void setUploadPropositionRules(
			List<UploadPropositionRuleDto> uploadPropositionRules) {
		this.uploadPropositionRules = uploadPropositionRules;
	}

	public List<UploadPropositionActionDto> getUploadPropositionActions() {
		return uploadPropositionActions;
	}

	public void setUploadPropositionActions(
			List<UploadPropositionActionDto> uploadPropositionActions) {
		this.uploadPropositionActions = uploadPropositionActions;
	}

	/*
	 * Transformers
	 */
	public static Function<UploadPropositionFilter, UploadPropositionFilterDto> toVo() {
		return new Function<UploadPropositionFilter, UploadPropositionFilterDto>() {
			@Override
			public UploadPropositionFilterDto apply(UploadPropositionFilter arg0) {
				return new UploadPropositionFilterDto(arg0);
			}
		};
	}
}
