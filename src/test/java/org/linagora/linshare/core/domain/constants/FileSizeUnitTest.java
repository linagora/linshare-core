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
package org.linagora.linshare.core.domain.constants;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class FileSizeUnitTest {

	public void testGetPlainSize() {
		Assertions.assertEquals(FileSizeUnit.KILO.getPlainSize(1), 1024L);
		Assertions.assertEquals(FileSizeUnit.MEGA.getPlainSize(1), 1048576L);
		Assertions.assertEquals(FileSizeUnit.GIGA.getPlainSize(1), 1073741824L);

	}

	public void testGetSiSize() {
		Assertions.assertEquals(FileSizeUnit.KILO.getSiSize(1), 1000L);
		Assertions.assertEquals(FileSizeUnit.MEGA.getSiSize(1), 1000000L);
		Assertions.assertEquals(FileSizeUnit.GIGA.getSiSize(1), 1000000000L);

	}

}
