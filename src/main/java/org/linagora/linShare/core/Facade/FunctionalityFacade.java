package org.linagora.linShare.core.Facade;

import java.util.List;

import org.linagora.linShare.core.domain.vo.FunctionalityVo;
import org.linagora.linShare.core.domain.vo.PolicyVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;

public interface FunctionalityFacade {

	public List<PolicyVo> getAllActivationPolicy(String domainIdentifier);
	public List<PolicyVo> getAllConfigurationPolicy(String domainIdentifier);
	public List<FunctionalityVo> getAllParameters(String domainIdentifier);
	
	public void updateActivationPolicies(UserVo actorVo, List<PolicyVo> policies) throws BusinessException;
	public void updateConfigurationPolicies(UserVo actorVo, List<PolicyVo> policies) throws BusinessException;
	public void updateParameters(UserVo actorVo, List<FunctionalityVo> functionalities) throws BusinessException;
	
	
	public Integer completionThreshold(String domainIdentifier);
	public boolean isEnableUserTab(String domainIdentifier);
	public boolean isEnableAuditTab(String domainIdentifier);
	public boolean isEnableHelpTab(String domainIdentifier);
	public boolean isEnableGroupTab(String domainIdentifier);
	
}
