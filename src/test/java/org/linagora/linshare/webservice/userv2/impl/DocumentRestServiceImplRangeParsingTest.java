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
package org.linagora.linshare.webservice.userv2.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

class DocumentRestServiceImplRangeParsingTest {

	private static final long TOTAL_SIZE = 1000;

	@Test
	void noRangeHeaderReturnsNull() {
		assertNull(DocumentRestServiceImpl.parseRange(null, TOTAL_SIZE));
	}

	@Test
	void firstByte() {
		assertArrayEquals(new long[] { 0, 0 }, DocumentRestServiceImpl.parseRange("bytes=0-0", TOTAL_SIZE));
	}

	@Test
	void lastByte() {
		assertArrayEquals(new long[] { 999, 999 }, DocumentRestServiceImpl.parseRange("bytes=999-999", TOTAL_SIZE));
	}

	@Test
	void middleRange() {
		assertArrayEquals(new long[] { 100, 199 }, DocumentRestServiceImpl.parseRange("bytes=100-199", TOTAL_SIZE));
	}

	@Test
	void openEndedRangeGoesToEndOfResource() {
		assertArrayEquals(new long[] { 500, 999 }, DocumentRestServiceImpl.parseRange("bytes=500-", TOTAL_SIZE));
	}

	@Test
	void suffixRangeReturnsLastNBytes() {
		assertArrayEquals(new long[] { 900, 999 }, DocumentRestServiceImpl.parseRange("bytes=-100", TOTAL_SIZE));
	}

	@Test
	void suffixRangeLargerThanResourceClampsToWholeResource() {
		assertArrayEquals(new long[] { 0, 999 }, DocumentRestServiceImpl.parseRange("bytes=-5000", TOTAL_SIZE));
	}

	@Test
	void completeFileRangeMapsToWholeResource() {
		assertArrayEquals(new long[] { 0, 999 }, DocumentRestServiceImpl.parseRange("bytes=0-999", TOTAL_SIZE));
	}

	@Test
	void endBeyondResourceSizeClampsToLastByte() {
		assertArrayEquals(new long[] { 900, 999 }, DocumentRestServiceImpl.parseRange("bytes=900-999999", TOTAL_SIZE));
	}

	@Test
	void malformedRangeSyntaxIsIgnoredNotRejected() {
		assertNull(DocumentRestServiceImpl.parseRange("not-a-range", TOTAL_SIZE));
		assertNull(DocumentRestServiceImpl.parseRange("bytes=-", TOTAL_SIZE));
		assertNull(DocumentRestServiceImpl.parseRange("items=0-10", TOTAL_SIZE));
	}

	@Test
	void startBeyondResourceSizeIsUnsatisfiable() {
		WebApplicationException e = assertThrows(WebApplicationException.class,
				() -> DocumentRestServiceImpl.parseRange("bytes=1000-1001", TOTAL_SIZE));
		Response response = e.getResponse();
		org.junit.jupiter.api.Assertions.assertEquals(416, response.getStatus());
		org.junit.jupiter.api.Assertions.assertEquals("bytes */" + TOTAL_SIZE,
				response.getHeaderString("Content-Range"));
	}

	@Test
	void startAfterEndIsUnsatisfiable() {
		assertThrows(WebApplicationException.class, () -> DocumentRestServiceImpl.parseRange("bytes=500-100", TOTAL_SIZE));
	}

	@Test
	void zeroLengthResourceIsAlwaysUnsatisfiable() {
		assertThrows(WebApplicationException.class, () -> DocumentRestServiceImpl.parseRange("bytes=0-0", 0));
	}
}
