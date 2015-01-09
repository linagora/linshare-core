/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.view.tapestry.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.util.LocaleUtils;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.linagora.linshare.core.domain.vo.UserVo;


/**
 * Set the locale for the user accordingly to its user defined locale, or the browser locale
 * if the persistent locale isn't set
 * @author ncharles
 *
 */
public class UserLocaleDispatcher implements Dispatcher {

	private final PersistentLocale persistentLocale;
	private final ApplicationStateManager stateManager;
	private final Locale defaultLocale; 
	private final List<Locale> supportedLocales;
	
	
	
	public UserLocaleDispatcher(PersistentLocale persistentLocale,
			ApplicationStateManager stateManager, 
			SymbolSource symbolSource, String defaultLocale) {
		super();
		this.persistentLocale = persistentLocale;
		this.stateManager = stateManager;
		this.defaultLocale = LocaleUtils.toLocale(defaultLocale);
		this.supportedLocales = new ArrayList<Locale>();

		String stringLocales=symbolSource.valueForSymbol(SymbolConstants.SUPPORTED_LOCALES);
		String[]listLocales=stringLocales.split(",");
		for (String currentLocale : listLocales) {
			this.supportedLocales.add(LocaleUtils.toLocale(currentLocale));
		}
	}



	public boolean dispatch(Request request, Response response)
			throws IOException {
		// Now we are sure we have in the ASO the UserVo
    	// we can set the persistentLocale 
    	
		if (!this.stateManager.exists(UserVo.class)) {
			//that's not for us
			return false;
		}
		
    	if (persistentLocale.get()==null) {
    		UserVo userVo = this.stateManager.get(UserVo.class);
    		// the user predefined locale overrides the browser locale 
    		if (userVo.getLocale()!=null && !userVo.getLocale().equals("")) {
    			persistentLocale.set(new Locale(userVo.getLocale().getTapestryLocale()));
    		} else {
				Locale requestLocale = request.getLocale();
				if (supportedLocales.contains(requestLocale)) {
					persistentLocale.set(request.getLocale());
				}
				else {
					persistentLocale.set(defaultLocale);
				}
    		}
		}
		
		return false;
	}

}
