package org.linagora.linshare.core.domain.constants;

public enum EnsembleType {

	USER, THREAD;

	public static EnsembleType fromString(String s){
		try{
			return EnsembleType.valueOf(s.toUpperCase());
		}catch(RuntimeException e){
			throw new IllegalArgumentException("Doesn't match an existing EnsembleType");
		}
	}
}
