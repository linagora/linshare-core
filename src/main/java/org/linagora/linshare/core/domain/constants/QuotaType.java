package org.linagora.linshare.core.domain.constants;

public enum QuotaType {

	DOMAIN_QUOTA, ACCOUNT_QUOTA, ENSEMBLE_QUOTA, PLATFORM_QUOTA;

	public static QuotaType fromString(String s){
		try{
			return QuotaType.valueOf(s.toUpperCase());
		}catch (RuntimeException e) {
			throw new IllegalArgumentException("Doesn't match an existing EnsembleType");
		}
	}
}
