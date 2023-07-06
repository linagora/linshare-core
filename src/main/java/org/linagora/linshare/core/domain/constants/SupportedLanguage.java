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

import java.util.Locale;

/**
 * Defines supported languages.
 */
public enum SupportedLanguage {
	ENGLISH(0, "en"), FRENCH(1, "fr"), VIETNAMESE(3, "vi"), RUSSIAN(4, "ru");

	private int value;
	private String tapestryLocale;

	private SupportedLanguage(int value, String tapestryLocale) {
		this.value = value;
		this.tapestryLocale = tapestryLocale;
	}

	public int toInt() {
		return value;
	}

	public static SupportedLanguage fromInt(int value) {
		for (SupportedLanguage lang : values()) {
			if (lang.value == value) {
				return lang;
			}
		}
		throw new IllegalArgumentException("Doesn't match an existing Language");
	}

	public static SupportedLanguage fromLocale(Locale locale) {
		if (Locale.FRENCH.equals(locale) || Locale.FRANCE.equals(locale)) {
			return FRENCH;
		}
		if (locale.toString().equals("vi")) {
			return VIETNAMESE;
		}
		if (locale.toString().equals("ru")) {
			return RUSSIAN;
		}
		return ENGLISH;
	}

	public static SupportedLanguage fromTapestryLocale(String locale) {
		if (locale == null)
			return null;
		if (locale.equals("fr")) {
			return FRENCH;
		}
		if (locale.equals("vi")) {
			return VIETNAMESE;
		}
		if (locale.equals("ru")) {
			return RUSSIAN;
		}
		return ENGLISH;
	}

	public String getTapestryLocale() {
		return tapestryLocale;
	}
	
	public static SupportedLanguage fromLanguage(Language language){
		switch (language) {
			case FRENCH: return FRENCH;
			case RUSSIAN: return RUSSIAN;
			case VIETNAMESE: return VIETNAMESE;
			case ENGLISH:
			default: return ENGLISH;
		}
	}

	public static Language toLanguage(SupportedLanguage language){
		switch (language) {
			case FRENCH: return Language.FRENCH;
			case RUSSIAN: return Language.RUSSIAN;
			case VIETNAMESE: return Language.VIETNAMESE;
			case ENGLISH:
			default: return Language.ENGLISH;
		}
	}
}
