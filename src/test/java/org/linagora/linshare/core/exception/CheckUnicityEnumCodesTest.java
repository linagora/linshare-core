/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = { "classpath:springContext-test.xml", })
public class CheckUnicityEnumCodesTest {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void testUniqueBusinessErrorCode() {
		List<BusinessErrorCode> businessErrorCodesWithDuplicatedCodes = Lists.newArrayList();
		Set<Integer> checkedCodes = Sets.newHashSet();
		List<BusinessErrorCode> errorCodes = Arrays.asList(BusinessErrorCode.values());
		for (BusinessErrorCode businessErrorCode : errorCodes) {
			if (checkedCodes.contains(businessErrorCode.getCode())) {
				businessErrorCodesWithDuplicatedCodes.add(businessErrorCode);
				continue;
			}
			checkedCodes.add(businessErrorCode.getCode());
		}
		Assertions.assertTrue(businessErrorCodesWithDuplicatedCodes.isEmpty(),
				"Found BusinessErrorCode with duplicated codes : " + businessErrorCodesWithDuplicatedCodes.toString());
	}

	@Test
	public void testUniqueMailContentTypeId() {
		List<Integer> contentTypesIds = Lists.newArrayList();
		Lists.newArrayList(MailContentType.values()).forEach(type -> contentTypesIds.add(type.toInt()));
		Set<Integer> checkedContentTypeIds = Sets.newHashSet();
		List<Integer> duplicatesContentTypesIds = Lists.newArrayList();
		for (Integer id : contentTypesIds) {
			if (!checkedContentTypeIds.add(id)) {
				duplicatesContentTypesIds.add(id);
			}
		}
		Assertions.assertTrue(duplicatesContentTypesIds.isEmpty(),
				duplicatesContentTypesIds.size() + " duplicated MailContentTypeId(s): " + duplicatesContentTypesIds.toString());
	}
}
