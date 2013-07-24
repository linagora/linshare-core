package org.linagora.linshare.webservice.dto;

import java.util.AbstractMap.SimpleEntry;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;

@XmlRootElement(name = "Functionality")
public class FunctionalityDto {

	private String identifier;
	private String domain;
	private SimpleEntry<Boolean, Integer> activation;
	private SimpleEntry<Boolean, Integer> configuration;
	
	public FunctionalityDto() {
		super();
	}

	public FunctionalityDto(Functionality f) {
		super();

		Policy ap = f.getActivationPolicy();
		Policy cp = f.getConfigurationPolicy();

		this.identifier = f.getIdentifier();
		this.domain = f.getDomain().getIdentifier();
		this.activation = new SimpleEntry<Boolean, Integer>(ap.getStatus(),
				ap.getPolicy().toInt());
		this.configuration = new SimpleEntry<Boolean, Integer>(cp.getStatus(),
				cp.getPolicy().toInt());
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

	public SimpleEntry<Boolean, Integer> getActivation() {
		return activation;
	}

	public void setActivation(SimpleEntry<Boolean, Integer> activation) {
		this.activation = activation;
	}

	public SimpleEntry<Boolean, Integer> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SimpleEntry<Boolean, Integer> configuration) {
		this.configuration = configuration;
	}

}
