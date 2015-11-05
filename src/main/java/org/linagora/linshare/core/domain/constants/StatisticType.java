package org.linagora.linshare.core.domain.constants;

public enum StatisticType {

	USER_DAILY_STAT, USER_WEEKLY_STAT, USER_MONTHLY_STAT,
	THREAD_DAILY_STAT, THREAD_WEEKLY_STAT, THREAD_MONTHLY_STAT,
	DOMAIN_DAILY_STAT, DOMAIN_WEEKLY_STAT, DOMAIN_MONTHLY_STAT;

	public static StatisticType fromString(String s){
		try{
			return StatisticType.valueOf(s.toUpperCase());
		}catch(RuntimeException e){
			throw new IllegalArgumentException("Doesn't match an existing StatiticType");
		}
	}
}
