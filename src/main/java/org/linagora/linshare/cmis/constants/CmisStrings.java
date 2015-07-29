/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

package org.linagora.linshare.cmis.constants;

import org.linagora.linshare.core.domain.constants.SupportedLanguage;

public class CmisStrings {

	private SupportedLanguage locale = SupportedLanguage.ENGLISH;

	public SupportedLanguage getLocale() {
		return locale;
	}

	public void setLocale(String lang) {
		this.locale = SupportedLanguage.fromTapestryLocale(lang);
	}

	public String getName(CmisDirectory e, SupportedLanguage locale) {
		if (locale.getTapestryLocale().equals("en"))
			return getNameEn(e);
		if (locale.getTapestryLocale().equals("fr"))
			return getNameFr(e);
		else
			return getNameEn(e);
	}

	public String getName(CmisDirectory e) {
		return getName(e, locale);
	}

	private String getNameEn(CmisDirectory e) {
		switch (e) {
		case MY_FILES:
			return "My Files";
		case MY_THREADS:
			return "My Threads";
		case THREAD:
			return "Thread ";
		}
		return null;
	}

	private String getNameFr(CmisDirectory e) {
		switch (e) {
		case MY_FILES:
			return "Mes Fichiers";
		case MY_THREADS:
			return "Mes Groupes";
		case THREAD:
			return "Groupe ";
		}
		return null;
	}
}
