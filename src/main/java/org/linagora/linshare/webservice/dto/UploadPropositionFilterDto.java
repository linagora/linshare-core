package org.linagora.linshare.webservice.dto;

import java.util.List;

import org.linagora.linshare.core.domain.constants.UploadPropositionMatchType;
import org.linagora.linshare.core.domain.entities.UploadPropositionAction;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;
import org.linagora.linshare.core.domain.entities.UploadPropositionRule;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class UploadPropositionFilterDto {

	protected String uuid;

	protected String name;

	protected String match;

	protected boolean enable;

	protected List<UploadPropositionRuleDto> uploadPropositionRules = Lists
			.newArrayList();

	protected List<UploadPropositionActionDto> uploadPropositionActions = Lists
			.newArrayList();

	public UploadPropositionFilterDto(UploadPropositionFilter entity) {
		super();
		this.uuid = entity.getUuid();
		this.name = entity.getName();
		this.enable = entity.isEnable();
		this.match= entity.getMatch().name();
		for (UploadPropositionAction action : entity
				.getUploadPropositionActions()) {
			this.uploadPropositionActions.add(new UploadPropositionActionDto(
					action));
		}
		for (UploadPropositionRule rule : entity.getUploadPropositionRules()) {
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

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
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

	public static Function<UploadPropositionFilterDto, UploadPropositionFilter> toEntity() {
		return new Function<UploadPropositionFilterDto, UploadPropositionFilter>() {
			@Override
			public UploadPropositionFilter apply(UploadPropositionFilterDto dto) {
				UploadPropositionFilter filter = new UploadPropositionFilter();
				filter.setUuid(dto.getUuid());
				filter.setMatch(UploadPropositionMatchType.fromString(dto.getMatch()));
				filter.setName(dto.getName());
				filter.setEnable(dto.isEnable());
				filter.setUploadPropositionActions(Lists.transform(
						dto.getUploadPropositionActions(),
						UploadPropositionActionDto.toEntity()));
				filter.setUploadPropositionRules(Lists.transform(
						dto.getUploadPropositionRules(),
						UploadPropositionRuleDto.toEntity()));
				return filter;
			}
		};
	}

}
