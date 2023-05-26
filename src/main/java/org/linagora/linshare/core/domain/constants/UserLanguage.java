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

public enum UserLanguage {
	ENGLISH,
	FRENCH,
	RUSSIAN,
	VIETNAMESE;

	public static UserLanguage from(SupportedLanguage supportedLanguage) {
		switch (supportedLanguage) {
			case FRENCH: return FRENCH;
			case RUSSIAN: return RUSSIAN;
			case VIETNAMESE: return VIETNAMESE;
			case ENGLISH:
			default: return ENGLISH;
		}
	}

	public static UserLanguage from(Language language) {
		switch (language) {
			case FRENCH: return FRENCH;
			case RUSSIAN: return RUSSIAN;
			case VIETNAMESE: return VIETNAMESE;
			case ENGLISH:
			default: return ENGLISH;
		}
	}

	public SupportedLanguage convert() {
		switch (this) {
			case FRENCH: return SupportedLanguage.FRENCH;
			case RUSSIAN: return SupportedLanguage.RUSSIAN;
			case VIETNAMESE: return SupportedLanguage.VIETNAMESE;
			case ENGLISH:
			default: return SupportedLanguage.ENGLISH;
		}
	}

	public Language convertToLanguage() {
		switch (this) {
			case FRENCH: return Language.FRENCH;
			case RUSSIAN: return Language.RUSSIAN;
			case VIETNAMESE: return Language.VIETNAMESE;
			case ENGLISH:
			default: return Language.ENGLISH;
		}
	}
}
