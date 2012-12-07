
package org.linagora.linshare.core.domain.vo;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.linagora.linshare.core.domain.constants.FunctionalityType;


public class FunctionalityVo implements Comparable {

	protected String identifier;

	protected String domainIdentifier;
	
	public FunctionalityVo() {
		super();
	}
	
	public FunctionalityVo(String identifier, String domainIdentifier) {
		super();
		this.identifier = identifier;
		this.domainIdentifier = domainIdentifier;
	}
	
	public FunctionalityType getType() {
		return FunctionalityType.DEFAULT;
	}
	

	@NonVisual
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@NonVisual
	public String getDomainIdentifier() {
		return domainIdentifier;
	}

	public void setDomainIdentifier(String domainIdentifier) {
		this.domainIdentifier = domainIdentifier;
	}

	@Override
	public String toString() {
		return "Functionality identifier is : " + domainIdentifier + " : " + identifier + " :: ";
	}

	@Override
	public int compareTo(Object o) {
		FunctionalityVo obj = (FunctionalityVo)o;
		return this.identifier.compareToIgnoreCase(obj.getIdentifier());
	}
}
