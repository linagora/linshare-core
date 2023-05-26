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
public enum Language {
	ENGLISH(0, "en"), FRENCH(1, "fr"), RUSSIAN(2, "ru"), VIETNAMESE(3, "vi");

	private int value;
	private String tapestryLocale;

	private Language(int value, String tapestryLocale) {
		this.value = value;
		this.tapestryLocale = tapestryLocale;
	}

	public int toInt() {
		return value;
	}

	public static Language fromInt(int value) {
		for (Language lang : values()) {
			if (lang.value == value) {
				return lang;
			}
		}
		throw new IllegalArgumentException("Doesn't match an existing Language");
	}

	public static Language fromLocale(Locale locale) {
		if (Locale.FRENCH.equals(locale) || Locale.FRANCE.equals(locale)) {
			return FRENCH;
		}
		if (locale.toString().equals("ru")) {
			return RUSSIAN;
		}
		if (locale.toString().equals("vi")) {
			return VIETNAMESE;
		}
		return ENGLISH;
	}

	public static Locale toLocale(Language language) {

		if (language != null) {
			if (language.equals(FRENCH)) {
				return Locale.FRENCH ;
			}
			if (language.equals(RUSSIAN)) {
				//TODO workAround russian locale not existent
				return new Locale.Builder().setLanguage("ru").setScript("Cyrl").build();
			}
			if (language.equals(VIETNAMESE)) {
				//TODO workAround vietnamese locale not existent
				return new Locale.Builder().setLanguage("vi").setScript("Latn").build();
			}
		}
		return Locale.ENGLISH;
	}

	public static Language fromTapestryLocale(String locale) {
		if (locale == null)
			return null;
		return Language.fromLocale(new Locale(locale));
	}

	public String getTapestryLocale() {
		return tapestryLocale;
	}

	public static Language toDefaultLanguage(Language defaultLanguage, String wantedLanguage) {
		if (wantedLanguage == null) {
			return defaultLanguage;
		}
		Language found = null;
		for (Language lang : Language.values()) {
			if (wantedLanguage.toUpperCase().equals(lang.toString())) {
				found = lang;
			}
		}
		if (found != null) {
			return found;
		}
		return defaultLanguage;
	}
}
