/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2017 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2017. Contribute to
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
package org.linagora.linshare.core.notifications.config;

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
		if (lang.equals(Language.FRENCH)) {
			this.messages = String.join(""
					, "copyrightText =  Vous utilisez la version libre et gratuite de "
					, "<a href=\"http://www.linshare.org/\" style=\"text-decoration:none;color:#b2b2b2;\" target=\"_blank\">"
					, "<strong>LinShare</strong>™</a>"
					, ", développée par "
					, "<a href=\"http://www.linagora.com/\" style=\"text-decoration:none;color:#b2b2b2;\" target=\"_blank\">"
					, "<strong>Linagora</strong> ©</a>"
					, " 2009–2017."
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
					, " 2009–2017."
					, " Contribute to LinShare R&amp;D by subscribing to an Enterprise offer."
					, "\n"
					);
		}
	}

}
