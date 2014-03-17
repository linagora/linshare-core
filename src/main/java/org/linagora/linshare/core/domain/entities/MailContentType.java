package org.linagora.linshare.core.domain.entities;


public enum MailContentType {
	ACCOUNT_DESCRIPTION(0),
	ANONYMOUS_DOWNLOAD(1),
	CONFIRM_DOWNLOAD_ANONYMOUS(2),
	CONFIRM_DOWNLOAD_REGISTERED(3),
	DECRYPT_URL(4),
	DOC_UPCOMING_OUTDATED(5),
	FILE_DOWNLOAD_URL(6),
	FILE_UPDATED(7),
	GROUP_MEMBERSHIP_STATUS(8),
	GROUP_NEW_MEMBER(9),
	GROUP_SHARE_DELETED(10),
	GROUP_SHARE_NOTIFICATION(11),
	GUEST_INVITATION(12),
	LINSHARE_URL(13),
	NEW_GUEST(14),
	NEW_SHARING(15),
	NEW_SHARING_WITH_ACTOR(16),
	PASSWORD_GIVING(17),
	PERSONAL_MESSAGE(18),
	REGISTERED_DOWNLOAD(19),
	RESET_PASSWORD(20),
	SECURED_URL_UPCOMING_OUTDATED(21),
	SHARED_DOC_DELETED(22),
	SHARED_DOC_UPCOMING_OUTDATED(23),
	SHARED_DOC_UPDATED(24),
	SHARED_FILE_DELETED(25),
	SHARE_NOTIFICATION(26);

	private int value;

	private MailContentType(int value) {
		this.value = value;
	}

	public int toInt() {
		return this.value;
	}

	public static MailContentType fromInt(int value) {
		for (MailContentType template : values()) {
			if (template.value == value) {
				return template;
			}
		}
		throw new IllegalArgumentException(
				"Doesn't match an existing MailContentType");
	}
}
