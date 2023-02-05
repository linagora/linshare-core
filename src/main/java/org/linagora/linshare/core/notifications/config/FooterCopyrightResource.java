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
package org.linagora.linshare.core.notifications.config;

import java.util.Calendar;

import org.linagora.linshare.core.domain.constants.Language;

public class FooterCopyrightResource extends LinShareTemplateResource {

	public FooterCopyrightResource(String baseName, Language lang) {
		super(String.join(""
						, "<!DOCTYPE html>"
						, "<html xmlns:th=\"http://www.thymeleaf.org\">"
						, "<body>"
						, "<div data-th-fragment=\"copyright\">"
						, "<p  style=\"line-height:15px;font-weight:300;margin-bottom:0;color:#9b9b9b;font-size:10px;margin-top:0\" data-th-utext=\"#{copyrightText}\"></p>"
						, "</div>"
						, "</body>"
						, "</html>"
				)
				, baseName);
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		if (lang.equals(Language.FRENCH)) {
			this.messages = String.join(""
					, "copyrightText =  Vous utilisez la version libre et gratuite de "
					, "<a href=\"http://www.linshare.org/\" style=\"text-decoration:none;color:#b2b2b2;\" target=\"_blank\">"
					, "<strong>LinShare</strong>™</a>"
					, ", développée par "
					, "<a href=\"http://www.linagora.com/\" style=\"text-decoration:none;color:#b2b2b2;\" target=\"_blank\">"
					, "<strong>Linagora</strong> ©</a>"
					, " 2009–"
					, String.valueOf(year)
					, "."
					, " Contribuez à la R&D du produit en souscrivant à une offre entreprise."
					, "\n"
					);
		} else {
			this.messages = String.join(""
					, "copyrightText =  You are using the Open Source and free version of "
					, "<a href=\"http://www.linshare.org/\" style=\"text-decoration:none;color:#b2b2b2;\" target=\"_blank\">"
					, "<strong>LinShare</strong>™</a>"
					, ", powered by "
					, "<a href=\"http://www.linagora.com/\" style=\"text-decoration:none;color:#b2b2b2;\" target=\"_blank\">"
					, "<strong>Linagora</strong> ©</a>"
					, " 2009–"
					, String.valueOf(year)
					, "."
					, " Contribute to LinShare R&amp;D by subscribing to an Enterprise offer."
					, "\n"
					);
		}
	}

}
