/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020 to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
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
		if (language == Language.FRENCH){
			return SupportedLanguage.FRENCH;
		}
		return SupportedLanguage.ENGLISH;
	}

	public static Language toLanguage(SupportedLanguage language){
		if (language == SupportedLanguage.FRENCH){
			return Language.FRENCH;
		}
		return Language.ENGLISH;
	}
}
