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
