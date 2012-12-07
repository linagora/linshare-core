/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
/* *********************************************************
 * 
 * This file is a part of eSignBox.
 * This software is a computer program whose purpose is 
 * to manage electronic signature.
 *
 * ==LICENSE NOTICE==
 * eSignBox is a free software subjected to the  
 * ** GNU Affero Public License ** as  published by the 
 * Free Software Foundation, ** version 3 ** of the license.
 * 
 * By application to section 7 in the GNU  Affero GPLv3, 
 * dynamic and static links do not extend license to other
 * softwares.
 * 
 * You can redistribute  and/or modify since  you respect 
 * the term of the license. 
 * 
 * NOTICE : THIS LICENSE IS FREE OF CHARGE AND THE SOFTWARE
 * IS DISTRIBUTED WITHOUT ANY WARRANTIES OF ANY KIND 
 * 
 * ==LICENSE NOTICE==
 * 
 * (c) 2008 MODIFY COPYRIGTH NAMES (ex: EASY-WAL)
 * name and contact for dev
 ********************************************************* */
package org.linagora.linshare.view.tapestry.services.impl;

import java.util.Properties;

import javax.naming.ConfigurationException;

import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.linagora.linshare.core.utils.PropertyPlaceholder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class able the use of properties files as symbol providers for tapestry
 * 5 services.
 */
public class PropertiesSymbolProvider implements SymbolProvider {

	final private Properties configuration;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Load symbol from properties file.
	 * 
	 * @throws ConfigurationException
	 */
	public PropertiesSymbolProvider(PropertyPlaceholder propertyPlaceholder) {
		configuration = propertyPlaceholder.getProperties();
	}

	public String valueForSymbol(String symbolName) {
		if (logger.isDebugEnabled()) {
			logger.debug("Access to Symbol {} from properties symbol provider",
					symbolName);
		}
		return configuration.getProperty(symbolName);
	}

	public Properties getConfiguration() {
		return configuration;
	}
}
