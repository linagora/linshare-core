package org.linagora.linShare.core.domain.vo;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;
import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.Role;

public class AbstractDomainVo {

	protected String identifier;

	protected String label;
	
	protected String defaultLocale;

	protected Role defaultRole;
	
	protected String domainDescription;

	@NonVisual 
	protected boolean template = false;
	
	@NonVisual
	protected boolean enable = true;

	protected String policyIdentifier;
	
	@NonVisual 
	protected Long usedSpace = new Long(0);
	
	protected String differentialKey;
	
	protected String patternIdentifier;
	
	protected String ldapIdentifier;

	public AbstractDomainVo() {
	}
	
	
	public AbstractDomainVo(AbstractDomain entity) {
		this.setDefaultLocale(entity.getDefaultLocale());
		this.setDefaultRole(entity.getDefaultRole());
		this.setDomainDescription(entity.getDescription());
		
		this.setPolicyIdentifier(entity.getPolicy().getIdentifier());
		
		if(entity.getUserProvider() != null) {
			this.setDifferentialKey(entity.getUserProvider().getBaseDn());
			this.setLdapIdentifier(entity.getUserProvider().getLdapconnexion().getIdentifier());
			this.setPatternIdentifier(entity.getUserProvider().getPattern().getIdentifier());
		}
		
		this.setEnable(entity.isEnable());
		this.setIdentifier(entity.getIdentifier());
		this.setLabel(entity.getLabel());
		this.setTemplate(entity.isTemplate());
		this.setUsedSpace(entity.getUsedSpace());
	}

	public AbstractDomainVo(String identifier, String differentialKey,
			String patternIdentifier, String ldapIdentifier) {
		this.identifier = identifier;
		this.differentialKey = differentialKey;
		this.patternIdentifier = patternIdentifier;
		this.ldapIdentifier = ldapIdentifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
		// Label temporary disable : no ihm use for now : to be done.   
		this.label = identifier;
	}

	@Validate("required")
	public String getIdentifier() {
		return identifier;
	}
	
//	@Validate("required")
	@NonVisual 
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public String getDifferentialKey() {
		return differentialKey;
	}

	public void setDifferentialKey(String differentialKey) {
		if(differentialKey != null)
		this.differentialKey = differentialKey.trim();
	}

	@Override
	public String toString() {
		return identifier;
	}

	@Validate("required")
	public String getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(String defaultLocale) {
		if(defaultLocale != null)
		this.defaultLocale = defaultLocale.trim();
	}

//	@Validate("required")
	public Role getDefaultRole() {
		return defaultRole;
	}

	public void setDefaultRole(Role defaultRole) {
		this.defaultRole = defaultRole;
	}

	@Validate("required")
	public String getDomainDescription() {
		return domainDescription;
	}

	public void setDomainDescription(String domainDescription) {
		if(domainDescription != null)
		this.domainDescription = domainDescription.trim();
	}

	public boolean isTemplate() {
		return template;
	}

	public void setTemplate(boolean template) {
		this.template = template;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getPolicyIdentifier() {
		return policyIdentifier;
	}

	public void setPolicyIdentifier(String policyIdentifier) {
		if(policyIdentifier != null)
		this.policyIdentifier = policyIdentifier.trim();
	}

	public Long getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(Long usedSpace) {
		this.usedSpace = usedSpace;
	}

	public String getPatternIdentifier() {
		return patternIdentifier;
	}

	public void setPatternIdentifier(String patternIdentifier) {
		if(patternIdentifier != null)
		this.patternIdentifier = patternIdentifier.trim();
	}

	public String getLdapIdentifier() {
		return ldapIdentifier;
	}

	public void setLdapIdentifier(String ldapIdentifier) {
		if(ldapIdentifier != null)
		this.ldapIdentifier = ldapIdentifier.trim();
	}

}
