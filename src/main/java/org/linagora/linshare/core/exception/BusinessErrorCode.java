/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.exception;

import javax.ws.rs.core.Response.Status;

/** Exception error code.
 */
public enum BusinessErrorCode implements ErrorCode {

	UNKNOWN(1000, Status.INTERNAL_SERVER_ERROR),
	BATCH_INCOMPLETE(1001, Status.INTERNAL_SERVER_ERROR),
	INVALID_CONFIGURATION(1002, Status.METHOD_NOT_ALLOWED),

	AUTHENTICATION_ERROR(2000),
	AUTHENTICATION_SECOND_FACTOR_ALREADY_EXISTS(2001, Status.FORBIDDEN),
	AUTHENTICATION_SECOND_FACTOR_NOT_ENABLED(2002, Status.FORBIDDEN),
	AUTHENTICATION_SECOND_FACTOR_FORBIDEN(2003, Status.FORBIDDEN),
	DATABASE_INCOHERENCE_NO_ROOT_DOMAIN(2004, Status.INTERNAL_SERVER_ERROR),
	USER_NOT_FOUND(2200, Status.NOT_FOUND),
	DUPLICATE_USER_ENTRY(2201),
	CANNOT_DELETE_USER(2203, Status.FORBIDDEN),
	CANNOT_UPDATE_USER(2204, Status.FORBIDDEN),
	USER_CANNOT_CREATE_GUEST(2205, Status.FORBIDDEN),
	USER_CANNOT_DELETE_GUEST(2206, Status.FORBIDDEN),
	USER_CANNOT_UPDATE_GUEST(2207, Status.FORBIDDEN),
	USER_FORBIDDEN(2208, Status.FORBIDDEN),
	USER_ALREADY_EXISTS_IN_DOMAIN_TARGET(2209),
	RESTRICTED_CONTACT_NOT_FOUND(2210, Status.NOT_FOUND),
	MIME_NOT_FOUND(3000, Status.NOT_FOUND),
	FILE_MIME_NOT_ALLOWED(3002, Status.FORBIDDEN),
	FILE_CONTAINS_VIRUS(3003, Status.FORBIDDEN),
	FILE_MIME_WARNING(3004),
	FILE_ENCRYPTION_UNDEFINED(3005),
	FILE_TIMESTAMP_NOT_COMPUTED(3006, Status.INTERNAL_SERVER_ERROR),
	FILE_SCAN_FAILED(3007, Status.INTERNAL_SERVER_ERROR),
	FILE_TIMESTAMP_WRONG_TSA_URL(3008, Status.INTERNAL_SERVER_ERROR),
	FILE_UNREACHABLE(3009),
	INVALID_FILENAME(3010),
	FILE_INVALID_INPUT_TEMP_FILE(3011, Status.BAD_REQUEST),
	INVALID_UUID(4000),
	SHARED_DOCUMENT_NOT_FOUND(5000, Status.NOT_FOUND),
	CANNOT_SHARE_DOCUMENT(5001),
	CANNOT_DELETE_SHARED_DOCUMENT(5002),
	SHARE_NOT_FOUND(5003, Status.NOT_FOUND),
	SHARE_MISSING_RECIPIENTS(5400, Status.BAD_REQUEST),
	SHARE_EXPIRY_DATE_INVALID(5401, Status.BAD_REQUEST),
	SHARE_WRONG_USDA_NOTIFICATION_DATE_AFTER(5403, Status.BAD_REQUEST),
	SHARE_WRONG_USDA_NOTIFICATION_DATE_BEFORE(5404, Status.BAD_REQUEST),
	NO_SUCH_ELEMENT(6000, Status.NOT_FOUND),
	METHOD_NOT_ALLOWED(6001, Status.METHOD_NOT_ALLOWED),
	CANNOT_SIGN_DOCUMENT(9001),
	CANNOT_ENCRYPT_GENERATE_KEY(9002),
	CANNOT_ENCRYPT_DOCUMENT(9003),
	CANNOT_DECRYPT_DOCUMENT(9004),
	CANNOT_DELETE_DOCUMENT_ENTRY(9005),
	CANNOT_DELETE_EXPIRED_DOCUMENT_ENTRY(9006),
	WRONG_URL(10000),
	SECURED_URL_IS_EXPIRED(12000),
	SECURED_URL_BAD_PASSWORD(12001),
	SECURED_URL_WRONG_DOCUMENT_ID(12002),
	DOMAIN_ID_ALREADY_EXISTS(13000),
	DOMAIN_ID_NOT_FOUND(13001, Status.NOT_FOUND),
	DOMAIN_INVALID_TYPE(13002),
	DOMAIN_POLICY_NOT_FOUND(13003, Status.NOT_FOUND),
	LDAP_CONNECTION_NOT_FOUND(13004, Status.NOT_FOUND),
	DOMAIN_PATTERN_NOT_FOUND(13005, Status.NOT_FOUND),
	DOMAIN_BASEDN_NOT_FOUND(13006),
	DOMAIN_INVALID_OPERATION(13007, Status.BAD_REQUEST),
	DOMAIN_DO_NOT_EXIST(13008, Status.NOT_FOUND),
	DOMAIN_POLICY_ALREADY_EXISTS(13009, Status.BAD_REQUEST),
	DOMAIN_ID_BAD_FORMAT(13010),
	LDAP_CONNECTION_ID_BAD_FORMAT(13011),
	DOMAIN_PATTERN_ID_BAD_FORMAT(13012),
	LDAP_CONNECTION_ID_ALREADY_EXISTS(13013),
	DOMAIN_PATTERN_ID_ALREADY_EXISTS(13014),
	LDAP_CONNECTION_CANNOT_BE_REMOVED(13015, Status.BAD_REQUEST),
	DOMAIN_PATTERN_CANNOT_BE_REMOVED(13016, Status.BAD_REQUEST),
	LDAP_CONNECTION_STILL_IN_USE(13017, Status.FORBIDDEN),
	DOMAIN_PATTERN_STILL_IN_USE(13018, Status.FORBIDDEN),
	DOMAIN_HAS_ACCESS_RULES(13019, Status.CONFLICT),
	DOMAIN_PATTERN_CANNOT_BE_UPDATED(13020, Status.FORBIDDEN),
	FUNCTIONALITY_ENTITY_OUT_OF_DATE(14000),
	UNAUTHORISED_FUNCTIONALITY_UPDATE_ATTEMPT(14001),
	FUNCTIONALITY_NOT_FOUND(14004, Status.NOT_FOUND),
	FUNCTIONALITY_DEFAULT_VALUE_NOT_AVAILABLE(14005, Status.FORBIDDEN),
	FUNCTIONALITY_MAX_VALUE_NOT_AVAILABLE(14006, Status.FORBIDDEN),
	RELAY_HOST_NOT_ENABLE(15000),
	DIRECTORY_UNAVAILABLE(16000),

	MAILCONFIG_IN_USE(16666),
	MAILCONFIG_NOT_FOUND(16667, Status.NOT_FOUND),
	MAILCONFIG_FORBIDDEN(166678, Status.FORBIDDEN),

	MAILCONTENT_IN_USE(17666),
	MAILCONTENT_NOT_FOUND(17667),
	MAILCONTENTLANG_NOT_FOUND(17668),
	MAILCONTENTLANG_DUPLICATE(17669),
	MAILCONTENT_FORBIDDEN(17670, Status.FORBIDDEN),
	MAILCONTENTLANG_FORBIDDEN(17671, Status.FORBIDDEN),

	MAILFOOTER_IN_USE(18666),
	MAILFOOTER_NOT_FOUND(18667),
	MAILFOOTERLANG_NOT_FOUND(18668),
	MAILFOOTERLANG_DUPLICATE(18669),
	MAILFOOTER_FORBIDDEN(18670, Status.FORBIDDEN),
	MAILFOOTERLANG_FORBIDDEN(18671, Status.FORBIDDEN),

	MAILLAYOUT_IN_USE(19666),
	MAILLAYOUT_NOT_FOUND(19667),
	MAILLAYOUT_FORBIDDEN(19668, Status.FORBIDDEN),
	MAILLAYOUT_DO_NOT_REMOVE_COPYRIGHT_FOOTER(19669, Status.BAD_REQUEST),

	FORBIDDEN(17000, Status.FORBIDDEN),
	UPDATE_FORBIDDEN(17001, Status.FORBIDDEN),
	BAD_REQUEST(17400, Status.BAD_REQUEST),

	WEBSERVICE_FAULT(20000, Status.INTERNAL_SERVER_ERROR),
	WEBSERVICE_FORBIDDEN(20001, Status.FORBIDDEN),
	WEBSERVICE_NOT_FOUND(20002, Status.NOT_FOUND),
	WEBSERVICE_BAD_REQUEST(20003, Status.BAD_REQUEST),
	WEBSERVICE_BAD_DATA_FORMAT(20004, Status.BAD_REQUEST),
	WEBSERVICE_BAD_REQUEST_NULL_POINTER_EXCEPTION(20005, Status.BAD_REQUEST),

	LIST_DO_NOT_EXIST(25000, Status.NOT_FOUND),
	LIST_ALDREADY_EXISTS(25001),
	CONTACT_LIST_DO_NOT_EXIST(25002, Status.NOT_FOUND),
	CONTACT_LIST_DUPLICATION_FORBIDDEN(25003, Status.FORBIDDEN),

	WORK_GROUP_DOCUMENT_NOT_FOUND(26002, Status.NOT_FOUND),
	WORK_GROUP_DOCUMENT_FORBIDDEN(26444, Status.FORBIDDEN),
	WORK_GROUP_DOCUMENT_ALREADY_EXISTS(26445, Status.BAD_REQUEST),
	WORK_GROUP_DOCUMENT_REVISION_ALREADY_EXISTS(26447, Status.BAD_REQUEST),
	WORK_GROUP_DOCUMENT_REVISION_NOT_FOUND(26448, Status.NOT_FOUND),
	WORK_GROUP_DOCUMENT_REVISION_DELETE_FORBIDDEN(26449, Status.FORBIDDEN),

	WORK_GROUP_FOLDER_NOT_FOUND(26003, Status.NOT_FOUND),
	WORK_GROUP_FOLDER_FORBIDDEN(26004, Status.FORBIDDEN),
	WORK_GROUP_FOLDER_FORBIDDEN_NOT_EMPTY(26006, Status.BAD_REQUEST),
	WORK_GROUP_FOLDER_ALREADY_EXISTS(28005, Status.BAD_REQUEST),
	WORK_GROUP_OPERATION_UNSUPPORTED(28006, Status.BAD_REQUEST),
	WORK_GROUP_NODE_NOT_FOUND(26007, Status.NOT_FOUND),
	INVALID_WORK_GROUP_NODE_TYPE(26008, Status.BAD_REQUEST),
	WORK_GROUP_NODE_DOWNLOAD_FORBIDDEN(26009, Status.FORBIDDEN),
	WORK_GROUP_NODE_DOWNLOAD_ARCHIVE_SIZE_TOO_LARGE(26010, Status.BAD_REQUEST),
	WORK_GROUP_NODE_DOWNLOAD_INTERNAL_ERROR(26011, Status.INTERNAL_SERVER_ERROR),

	DRIVE_ALREADY_EXISTS(50013,Status.BAD_REQUEST),
	DRIVE_NOT_FOUND(50014,Status.NOT_FOUND),
	DRIVE_FORBIDDEN(50015, Status.FORBIDDEN),

	WORK_GROUP_FORBIDDEN(55505,Status.FORBIDDEN),
	WORK_GROUP_NOT_FOUND(55506,Status.NOT_FOUND),
	WORK_GROUP_ALREADY_EXIST(55507,Status.BAD_REQUEST),

	SHARED_SPACE_ROLE_ALREADY_EXISTS(60003,Status.BAD_REQUEST),
	SHARED_SPACE_ROLE_NOT_FOUND(60004,Status.NOT_FOUND),
	SHARED_SPACE_ROLE_FORBIDDEN(60005, Status.FORBIDDEN),

	SHARED_SPACE_PERMISSION_ALREADY_EXISTS(61003,Status.BAD_REQUEST),
	SHARED_SPACE_PERMISSION_NOT_FOUND(61004,Status.NOT_FOUND),
	SHARED_SPACE_PERMISSION_FORBIDDEN(61005, Status.FORBIDDEN),

	SHARED_SPACE_MEMBER_ALREADY_EXISTS(62003,Status.BAD_REQUEST),
	SHARED_SPACE_MEMBER_NOT_FOUND(62004,Status.NOT_FOUND),
	SHARED_SPACE_MEMBER_FORBIDDEN(62005, Status.FORBIDDEN),

	API_REMOVED(62016, Status.METHOD_NOT_ALLOWED),

	SHARED_SPACE_NODE_NOT_FOUND(66000,Status.NOT_FOUND),
	SHARED_SPACE_NODE_FORBIDDEN(66001,Status.FORBIDDEN),
	INVALID_SHARED_SPACE_NODE_TYPE(66002, Status.FORBIDDEN),

	GUEST_NOT_FOUND(28000, Status.NOT_FOUND),
	GUEST_ALREADY_EXISTS(28001, Status.BAD_REQUEST),
	GUEST_FORBIDDEN(28403, Status.FORBIDDEN),
	GUEST_INVALID_INPUT(28405, Status.BAD_REQUEST),
	GUEST_INVALID_SEARCH_INPUT(28406, Status.BAD_REQUEST),
	GUEST_EXPIRY_DATE_INVALID(38400, Status.BAD_REQUEST),
	RESET_GUEST_PASSWORD_EXPIRED_TOKEN(28407, Status.BAD_REQUEST),
	RESET_GUEST_PASSWORD_ALREADY_USED_TOKEN(28408, Status.BAD_REQUEST),
	RESET_GUEST_PASSWORD_NOT_FOUND(28409, Status.NOT_FOUND),
	RESET_ACCOUNT_PASSWORD_INVALID_PASSWORD(28410, Status.BAD_REQUEST),
	RESET_ACCOUNT_PASSWORD_ALREADY_USED(28411, Status.BAD_REQUEST),
	RESET_ACCOUNT_PASSWORD_FORBIDDEN(28412, Status.FORBIDDEN),

	TECHNICAL_ACCOUNT_NOT_FOUND(29000, Status.NOT_FOUND),
	TECHNICAL_ACCOUNT_ALREADY_EXISTS(29001, Status.BAD_REQUEST),
	TECHNICAL_ACCOUNT_FORBIDEN(29002, Status.FORBIDDEN),

	UPLOAD_REQUEST_NOT_FOUND(30404, Status.NOT_FOUND),
	UPLOAD_REQUEST_TOO_MANY_FILES(30000, Status.BAD_REQUEST),
	UPLOAD_REQUEST_NOT_ENABLE_YET(30001, Status.BAD_REQUEST),
	UPLOAD_REQUEST_EXPIRED(30002, Status.BAD_REQUEST),
	UPLOAD_REQUEST_TOTAL_DEPOSIT_SIZE_TOO_LARGE(30003, Status.BAD_REQUEST),
	UPLOAD_REQUEST_FILE_TOO_LARGE(30004, Status.BAD_REQUEST),
	UPLOAD_REQUEST_READONLY_MODE(30005, Status.FORBIDDEN),
	UPLOAD_REQUEST_FORBIDDEN(30406, Status.FORBIDDEN),
	UPLOAD_REQUEST_GROUP_STATUS(30407, Status.FORBIDDEN),
	UPLOAD_REQUEST_STATUS_BAD_TRANSITON(30408, Status.BAD_REQUEST),
	UPLOAD_REQUEST_EXPIRY_DATE_INVALID(30409, Status.BAD_REQUEST),
	UPLOAD_REQUEST_NOTIFICATION_DATE_INVALID(30410, Status.BAD_REQUEST),
	UPLOAD_REQUEST_INTEGER_VALUE_INVALID(30411, Status.BAD_REQUEST),
	UPLOAD_REQUEST_SIZE_VALUE_INVALID(30412, Status.BAD_REQUEST),
	UPLOAD_REQUEST_GROUP_ENTRIES_ARCHIVE_DOWNLOAD_FORBIDDEN(30413, Status.FORBIDDEN),
	UPLOAD_REQUEST_ACTIVATION_DATE_INVALID(30414, Status.BAD_REQUEST),
	UPLOAD_REQUEST_CLOSURE_FORBIDDEN(30415, Status.FORBIDDEN),
	UPLOAD_REQUEST_ENTRY_NOT_FOUND(31404, Status.NOT_FOUND),
	UPLOAD_REQUEST_ENTRY_DOWNLOAD_INTERNAL_ERROR(31405, Status.INTERNAL_SERVER_ERROR),
	UPLOAD_REQUEST_DELETE_LAST_RECIPIENT(31500, Status.FORBIDDEN),
	UPLOAD_REQUEST_DELETE_RECIPIENT_FROM_INDIVIDUAL_REQUEST(31501, Status.FORBIDDEN),
	UPLOAD_REQUEST_NOT_UPDATABLE_GROUP_MODE(31406, Status.BAD_REQUEST),
	UPLOAD_REQUEST_ENTRY_FORBIDDEN(31407, Status.FORBIDDEN),
	UPLOAD_REQUEST_ENTRY_FILE_CANNOT_DELETED(31408, Status.FORBIDDEN),
	UPLOAD_REQUEST_ENTRY_FILE_CANNOT_BE_COPIED(31409, Status.FORBIDDEN),
	UPLOAD_REQUEST_GROUP_BAD_REQUEST(31410, Status.BAD_REQUEST),
	UPLOAD_REQUEST_STATUS_NOT_MODIFIED(31411, Status.NOT_MODIFIED),
	UPLOAD_REQUEST_GROUP_STATUS_NOT_MODIFIED(31412, Status.NOT_MODIFIED),
	UPLOAD_REQUEST_GROUP_FORBIDDEN(31413, Status.FORBIDDEN),
	UPLOAD_REQUEST_GROUP_UPDATE_FORBIDDEN(31414, Status.FORBIDDEN),
	UPLOAD_REQUEST_GROUP_NOT_FOUND(31415, Status.NOT_FOUND),

	UPLOAD_REQUEST_URL_FORBIDDEN(32401, Status.UNAUTHORIZED),
	UPLOAD_REQUEST_URL_FORBIDDEN_DEFAULT_PASSWORD_NOT_UPDATED(32402, Status.UNAUTHORIZED),
	UPLOAD_REQUEST_URL_PASSWORD_ALREADY_USED(32403, Status.UNAUTHORIZED),
	UPLOAD_REQUEST_URL_NOT_FOUND(32404, Status.NOT_FOUND),
	UPLOAD_REQUEST_ENTRY_URL_EXPIRED(32002, Status.FORBIDDEN),
	UPLOAD_REQUEST_ENTRY_URL_EXISTS(32300, Status.FORBIDDEN),

	DOCUMENT_ENTRY_FORBIDDEN(33403, Status.FORBIDDEN),
	DOCUMENT_ENTRY_NOT_FOUND(33404, Status.NOT_FOUND),

	ANONYMOUS_URL_FORBIDDEN(33413, Status.FORBIDDEN),
	ANONYMOUS_URL_NOT_FOUND(33414, Status.NOT_FOUND),
	ANONYMOUS_SHARE_ENTRY_FORBIDDEN(33423, Status.FORBIDDEN),
	ANONYMOUS_SHARE_ENTRY_NOT_FOUND(33424, Status.NOT_FOUND),

	SHARE_ENTRY_FORBIDDEN(34403, Status.FORBIDDEN),
	SHARE_ENTRY_NOT_FOUND(34404, Status.NOT_FOUND),

	UPLOAD_PROPOSITION_FILTER_NOT_FOUND(35004, Status.NOT_FOUND),
	UPLOAD_PROPOSITION_FILTER_CAN_NOT_CREATE(35005, Status.FORBIDDEN),
	UPLOAD_PROPOSITION_FILTER_CAN_NOT_READ(35006, Status.FORBIDDEN),
	UPLOAD_PROPOSITION_FILTER_CAN_NOT_LIST_ENABLED_FILTERS(35007, Status.FORBIDDEN),

	UPLOAD_PROPOSITION_NOT_FOUND(35054, Status.NOT_FOUND),
	UPLOAD_PROPOSITION_CAN_NOT_CREATE(35055, Status.FORBIDDEN),
	UPLOAD_PROPOSITION_CAN_NOT_READ(35056, Status.FORBIDDEN),
	UPLOAD_PROPOSITION_CAN_NOT_DELETE(35057, Status.FORBIDDEN),
	UPLOAD_PROPOSITION_CAN_NOT_UPDATE(35058, Status.FORBIDDEN),
	UPLOAD_PROPOSITION_CAN_NOT_LIST(35059, Status.FORBIDDEN),
	UPLOAD_PROPOSITION_CAN_NOT_UPDATE_STATUS(35060, Status.FORBIDDEN),

	UPLOAD_PROPOSITION_EXCEPTION_RULE_NOT_FOUND(35104, Status.NOT_FOUND),
	UPLOAD_PROPOSITION_EXCEPTION_RULE_CAN_NOT_CREATE(35105, Status.FORBIDDEN),
	UPLOAD_PROPOSITION_EXCEPTION_RULE_CAN_NOT_READ(35106, Status.FORBIDDEN),
	UPLOAD_PROPOSITION_EXCEPTION_RULE_CAN_NOT_LIST(35107, Status.FORBIDDEN),
	UPLOAD_PROPOSITION_EXCEPTION_RULE_ALREADY_EXISTS(35108, Status.FORBIDDEN),

	LDAP_ATTRIBUTE_CONTAINS_NULL(37001, Status.BAD_REQUEST),

	WELCOME_MESSAGES_ALREADY_EXISTS(36001, Status.BAD_REQUEST),
	WELCOME_MESSAGES_FORBIDDEN(36003, Status.FORBIDDEN),
	WELCOME_MESSAGES_NOT_FOUND(36004, Status.NOT_FOUND),
	WELCOME_MESSAGES_ILLEGAL_KEY(36005),

	NO_UPLOAD_RIGHTS_FOR_ACTOR(38001, Status.BAD_REQUEST),
	USER_PROVIDER_NOT_FOUND(37000,Status.NOT_FOUND),

	INVALID_INPUT_FOR_X509_CERTIFICATE(39000),

	MODE_MAINTENANCE_ENABLED(39001, Status.UNSUPPORTED_MEDIA_TYPE),
	// https://github.com/flowjs/flow.js
	// 200, 201, 202: The chunk was accepted and correct. No need to re-upload.
	// 404, 415. 500, 501: The file for which the chunk was uploaded is not supported, cancel the entire upload.
	// Anything else: Something went wrong, but try reuploading the file.

	ASYNC_TASK_NOT_FOUND(40404, Status.NOT_FOUND),
	ASYNC_TASK_FORBIDDEN(40403, Status.FORBIDDEN),

	SHARE_ENTRY_GROUP_NOT_FOUND(41404, Status.NOT_FOUND),
	SHARE_ENTRY_GROUP_FORBIDDEN(41403, Status.FORBIDDEN),

	BASE64_INPUTSTREAM_ENCODE_ERROR(42000),

	USER_PREFERENCE_FORBIDDEN(44403, Status.FORBIDDEN),
	USER_PREFERENCE_NOT_FOUND(44404, Status.NOT_FOUND),

	UPLOAD_REQUEST_TEMPLATE_FORBIDDEN(43401, Status.FORBIDDEN),

	MAILING_LIST_CONTACT_ALREADY_EXISTS(45400, Status.BAD_REQUEST),

	QUOTA_FORBIDDEN(46403, Status.FORBIDDEN),

	ACCOUNT_QUOTA_NOT_FOUND(46001, Status.NOT_FOUND),
	CONTAINER_QUOTA_NOT_FOUND(46002, Status.NOT_FOUND),
	DOMAIN_QUOTA_NOT_FOUND(46003, Status.NOT_FOUND),
	PLATFORM_QUOTA_NOT_FOUND(46004, Status.NOT_FOUND),

	QUOTA_FILE_FORBIDDEN_FILE_SIZE(46010, Status.FORBIDDEN),
	QUOTA_ACCOUNT_FORBIDDEN_NO_MORE_SPACE_AVALAIBLE(46011, Status.FORBIDDEN),
	QUOTA_CONTAINER_FORBIDDEN_NO_MORE_SPACE_AVALAIBLE(46012, Status.FORBIDDEN),
	QUOTA_DOMAIN_FORBIDDEN_NO_MORE_SPACE_AVALAIBLE(46013, Status.FORBIDDEN),
	QUOTA_GLOBAL_FORBIDDEN_NO_MORE_SPACE_AVALAIBLE(46014, Status.FORBIDDEN),
	QUOTA_PLATFORM_UNAUTHORIZED(46015, Status.FORBIDDEN),

	TEMPLATE_PARSING_ERROR(48000, Status.BAD_REQUEST),
	TEMPLATE_PARSING_ERROR_TEMPLATE_INPUT_EXCEPTION(48001, Status.BAD_REQUEST),
	TEMPLATE_PARSING_ERROR_TEXT_PARSE_EXCEPTION(48002, Status.BAD_REQUEST),
	TEMPLATE_PARSING_ERROR_NO_SUCH_PROPERTY_EXCEPTION(48003, Status.BAD_REQUEST),
	TEMPLATE_PARSING_ERROR_TEMPLATE_PROCESSING_EXCEPTION(48004, Status.BAD_REQUEST),
	TEMPLATE_PROCESSING_ERROR_INVALID_CONTEXT(48005, Status.INTERNAL_SERVER_ERROR),
	TEMPLATE_MISSING_TEMPLATE_BUILDER(48006, Status.BAD_REQUEST),

	UPGRADE_TASK_NOT_FOUND(49404, Status.NOT_FOUND),
	UPGRADE_TASK_FORBIDDEN(49403, Status.FORBIDDEN),

	SAFE_DETAIL_CAN_NOT_CREATE(50000,Status.FORBIDDEN),
	SAFE_DETAIL_CAN_NOT_DELETE(50001,Status.FORBIDDEN),
	SAFE_DETAIL_CAN_NOT_READ(50002,Status.FORBIDDEN),
	SAFE_DETAIL_CAN_NOT_LIST(50003,Status.FORBIDDEN),
	SAFE_DETAIL_ALREADY_EXIST(50004,Status.BAD_REQUEST),
	SAFE_DETAIL_NOT_FOUND(50005,Status.BAD_REQUEST),

	STATISTIC_DATE_PARSING_ERROR(51006, Status.BAD_REQUEST),
	STATISTIC_READ_DOMAIN_ERROR(51007, Status.BAD_REQUEST),
	STATISTIC_READ_ACTOR_ERROR(51008, Status.BAD_REQUEST),
	STATISTIC_FORBIDDEN(51009, Status.FORBIDDEN),

	PUBLIC_KEY_CAN_NOT_CREATE(52000,Status.FORBIDDEN),
	PUBLIC_KEY_CAN_NOT_DELETE(52001,Status.FORBIDDEN),
	PUBLIC_KEY_FORBIDDEN(52002,Status.FORBIDDEN),
	PUBLIC_KEY_ALREADY_EXIST(52003,Status.BAD_REQUEST),
	PUBLIC_KEY_NOT_FOUND(52004,Status.BAD_REQUEST),
	PUBLIC_KEY_UNKOWN_ISSUER(52005,Status.BAD_REQUEST),
	PUBLIC_KEY_INVALID_FORMAT(52006,Status.FORBIDDEN),

	JWT_PERMANENT_TOKEN_CAN_NOT_DELETE(53001, Status.FORBIDDEN),
	JWT_PERMANENT_TOKEN_NOT_FOUND(53002, Status.NOT_FOUND),
	JWT_PERMANENT_TOKEN_CAN_NOT_READ(53003,Status.FORBIDDEN),
	JWT_PERMANENT_TOKEN_CAN_NOT_CREATE(53004,Status.FORBIDDEN),
	JWT_PERMANENT_TOKEN_FORBIDDEN(53005,Status.FORBIDDEN),

	GROUP_LDAP_PATTERN_NOT_FOUND(55000,Status.BAD_REQUEST),
	GROUP_LDAP_PROVIDER_NOT_FOUND(55001,Status.NOT_FOUND),
	GROUP_LDAP_PATTERN_CANNOT_BE_UPDATED(55002, Status.FORBIDDEN),
	GROUP_LDAP_PATTERN_CANNOT_BE_REMOVED(55003, Status.FORBIDDEN),

	DRIVE_LDAP_PATTERN_NOT_FOUND(55004,Status.BAD_REQUEST),

	MAIL_ATTACHMENT_NOT_FOUND(56404, Status.NOT_FOUND),
	MAIL_ATTACHMENT_CAN_NOT_READ(56403,Status.FORBIDDEN),

	WRONG_PAGE_PARAMETERS(57000, Status.BAD_REQUEST);


	private final int code;

	private final Status status;

	private BusinessErrorCode(int code) {
		this.code = code;
		this.status = Status.BAD_REQUEST;
	}

	private BusinessErrorCode(int code, Status status) {
		this.code = code;
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public Status getStatus() {
		return status;
	}
}
