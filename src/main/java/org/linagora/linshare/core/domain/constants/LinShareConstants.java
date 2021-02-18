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
package org.linagora.linshare.core.domain.constants;

public class LinShareConstants {

	/**
	 * Default application root domain identifier
	 */
	public static final String rootDomainIdentifier = "LinShareRootDomain";

	/**
	 * Default application root email address
	 */
	public static final String defaultRootMailAddress = "root@localhost.localdomain";

	/**
	 * Default system email address
	 */
	public static final String defaultSystemMailAddress = "system";

	/**
	 * Default domain policy identifier
	 */
	public static final String defaultDomainPolicyIdentifier = "DefaultDomainPolicy";

	/**
	 * Default mime policy identifier
	 */
	public static final String defaultMimePolicyIdentifier = "3d6d8800-e0f7-11e3-8ec0-080027c0eef0";

	/**
	 * Default mail config identifier
	 */
	public static final String defaultMailConfigIdentifier = "946b190d-4c95-485f-bfe6-d288a2de1edd";

	/**
	 * Default mail attachment identifier (Cid)
	 */
	public static final String defaultMailAttachmentCid = "logo.linshare@linshare.org";

	/**
	 * Default application root domain identifier
	 */
	public static final Integer completionThresholdConstantForDeactivation = 999999999;

	/**
	 * Default max size for an upload
	 */
	public static final Long defaultMaxFileSize = Long.MAX_VALUE;

	/**
	 * Default max available size
	 */
	public static final Long defaultFreeSpace = Long.MAX_VALUE;

	public static final String defaultThreadView = "Default";

	/**
	 * Default linShare welcome message uuid.
	 */
	public static final String defaultWelcomeMessagesUuid = "4bc57114-c8c9-11e4-a859-37b5db95d856";
}
