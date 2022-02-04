/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

import java.util.Locale;

/**
 * Defines supported languages.
 */
public enum Language {
	ENGLISH(0, "en"), FRENCH(1, "fr"), RUSSIAN(2, "ru");

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
		return ENGLISH;
	}

	public static Locale toLocale(Language language) {
		//TODO workAround russian locale not existent
		Locale russian = new Locale.Builder().setLanguage("ru").setScript("Cyrl").build();
		if (language != null) {
			if (language.equals(FRENCH)) {
				return Locale.FRENCH ;
			}
			if (language.equals(RUSSIAN)) {
				return russian;
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
