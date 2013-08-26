package org.linagora.linshare.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Functionality;

@XmlRootElement(name = "Functionality")
public class FunctionalityDto {

	private String identifier;
	private String domain;

	protected PolicyDto activationPolicy;
	protected PolicyDto configurationPolicy;

	public FunctionalityDto() {
		super();
	}

	public FunctionalityDto(Functionality f) {
		super();
		this.activationPolicy = new PolicyDto(f.getActivationPolicy());
		this.configurationPolicy = new PolicyDto(f.getConfigurationPolicy());
		this.identifier = f.getIdentifier();
		this.domain = f.getDomain().getIdentifier();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public PolicyDto getActivationPolicy() {
		return activationPolicy;
	}

	public void setActivationPolicy(PolicyDto activationPolicy) {
		this.activationPolicy = activationPolicy;
	}

	public PolicyDto getConfigurationPolicy() {
		return configurationPolicy;
	}

	public void setConfigurationPolicy(PolicyDto configurationPolicy) {
		this.configurationPolicy = configurationPolicy;
	}
}
