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
package org.linagora.linshare.view.tapestry.services.impl;


import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.restmarshaller.StringClasse;



/**
 * Configuration entry point for the Marshaller.
 * Contribute to the ConfigureAliasMarshaller and the ConfigureOmitFieldMarshaller
 * Uses OrderedConfiguration because for some unknown reason, Configuration doesn't work
 * @author ncharles
 *
 */
public class ConfigureMarshallerModule {

	/**
	 * Contribute to the configuration of the aliases
	 * @param configuration
	 */
	public static void contributeConfigureAliasMarshaller(OrderedConfiguration<StringClasse> configuration) {

		configuration.add("1",new StringClasse(UserVo.class, "user"));
		configuration.add("2",new StringClasse(DocumentVo.class, "document"));


	}

	/**
	 * Contribute to the configuration of the omit field
	 * @param omitFields
	 */
	public static void contributeConfigureOmitFieldMarshaller(OrderedConfiguration<StringClasse> omitFields) {

		omitFields.add("1", new StringClasse(UserVo.class, "ownerLogin"));
		omitFields.add("2", new StringClasse(UserVo.class, "comment"));
		
		omitFields.add("3", new StringClasse(UserVo.class, "expirationDate"));


		omitFields.add("4", new StringClasse(DocumentVo.class, "ownerLogin"));
		//omitFields.add("5", new StringClasse(DocumentVo.class, "creationDate"));
		
		omitFields.add("6", new StringClasse(DocumentVo.class, "expirationDate"));
		omitFields.add("7", new StringClasse(DocumentVo.class, "signatures"));
		
		


	}

}
