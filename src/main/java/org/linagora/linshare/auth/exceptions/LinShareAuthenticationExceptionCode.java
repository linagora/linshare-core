package org.linagora.linshare.auth.exceptions;

public enum LinShareAuthenticationExceptionCode {

	DEFAULT_AUTHENTICATION_ERROR(1000),
	BAD_CREDENTIAL(1001),
	TOTP_BAD_FORMAT(1002),
	TOTP_BAD_VALUE(1003),
	ACCOUNT_LOCKED(1004),
	JWT_BAD_FORMAT(1005),
	DOMAIN_NOT_FOUND(1006);

	private int value;

	private LinShareAuthenticationExceptionCode(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

}
