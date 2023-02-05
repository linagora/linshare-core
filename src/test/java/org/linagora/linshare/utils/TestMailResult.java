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
package org.linagora.linshare.utils;

import java.util.List;

import org.linagora.linshare.core.domain.constants.MailContentType;

public class TestMailResult {

	final MailContentType type;

	final String data;

	final String strPattern;

	final List<String> allMatches;

	public TestMailResult(MailContentType type, String data, String strPattern, List<String> allMatches) {
		super();
		this.type = type;
		this.data = data;
		this.strPattern = strPattern;
		this.allMatches = allMatches;
	}

	public MailContentType getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	public List<String> getAllMatches() {
		return allMatches;
	}

	public String getStrPattern() {
		return strPattern;
	}

	@Override
	public String toString() {
		return "Result [type=" + type + ", allMatches=" + allMatches + "]";
	}

}
