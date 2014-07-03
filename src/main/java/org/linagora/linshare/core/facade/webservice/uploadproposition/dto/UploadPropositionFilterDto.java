package org.linagora.linshare.core.facade.webservice.uploadproposition.dto;

import java.util.List;

import org.linagora.linshare.core.domain.entities.UploadPropositionAction;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;
import org.linagora.linshare.core.domain.entities.UploadPropositionRule;

import com.google.common.collect.Lists;

public class UploadPropositionFilterDto {

	protected String uuid;

	protected String name;

	protected boolean matchAll;

	protected List<UploadPropositionRuleDto> uploadPropositionRules = Lists.newArrayList();

	protected List<UploadPropositionActionDto> uploadPropositionActions = Lists.newArrayList();

	//Tests only 
	public UploadPropositionFilterDto(String uuid, String name,
			boolean matchAll) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.matchAll = matchAll;
	}

	public UploadPropositionFilterDto(UploadPropositionFilter entity) {
		super();
		this.uuid = entity.getUuid();
		this.name = entity.getName();
		this.matchAll = entity.isMatchAll();
		for (UploadPropositionAction action : entity.getUploadPropositionActions()) {
			this.uploadPropositionActions.add(new UploadPropositionActionDto(action)); 
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

	public boolean isMatchAll() {
		return matchAll;
	}

	public void setMatchAll(boolean matchAll) {
		this.matchAll = matchAll;
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

}
