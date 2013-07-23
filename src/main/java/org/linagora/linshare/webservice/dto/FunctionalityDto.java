package org.linagora.linshare.webservice.dto;

import java.util.AbstractMap.SimpleEntry;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Functionality;

@XmlRootElement(name = "FunctionalityDto")
public class FunctionalityDto {

	private String identifier;
	private String domain;
	private SimpleEntry<Boolean, Integer> activation;
	private SimpleEntry<Boolean, Integer> configuration;

	public FunctionalityDto(String identifier, String domain,
			SimpleEntry<Boolean, Integer> activation,
			SimpleEntry<Boolean, Integer> configuration) {
		super();
		this.identifier = identifier;
		this.domain = domain;
		this.activation = activation;
		this.configuration = configuration;
	}

	public FunctionalityDto(Functionality f) {
		super();
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

}
