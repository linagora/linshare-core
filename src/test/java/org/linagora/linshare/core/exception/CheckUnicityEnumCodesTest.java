/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


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
