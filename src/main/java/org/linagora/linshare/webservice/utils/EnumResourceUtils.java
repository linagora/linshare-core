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

package org.linagora.linshare.webservice.utils;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.reflections.Reflections;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;

/**
 * Provides informations about the application enum constants using Java
 * reflection capabilities.
 * 
 * @author nbertrand
 */
public class EnumResourceUtils {

	private final static String ENUMS_PATH = "org.linagora.linshare.core.domain.constants.";

	public EnumResourceUtils() {
		super();
	}

	/**
	 * List all enums name under the {@link EnumResourceUtils#ENUMS_PATH}
	 * package.
	 * 
	 * @return enums
	 */
	@SuppressWarnings("rawtypes")
	public List<String> getAllEnumsName() {
		List<String> res = Lists.newArrayList();

		// Iterate over all enums under ENUMS_PATH package
		for (final Class<? extends Enum> e : new Reflections(ENUMS_PATH)
				.getSubTypesOf(Enum.class)) {
			res.add(toUnderscore(e.getSimpleName()));
		}
		return res;
	}

	/**
	 * List enum constants from the enum class name.
	 * 
	 * @param enumName
	 *            in lower_underscore_format.
	 * @return enums
	 */
	public List<String> findEnumConstants(String enumName)
			throws BusinessException {
		String className = ENUMS_PATH + toCamelCase(enumName);

		try {
			final Class<?> clazz = Class.forName(className);

			if (!clazz.isEnum()) {
				throw new BusinessException(
						BusinessErrorCode.WEBSERVICE_NOT_FOUND,
						"Unknown enum type : " + className);
			}
			List<String> ret = Lists.newArrayList();

			for (Object o : clazz.getEnumConstants()) {
				Enum<?> e = (Enum<?>) o;

				ret.add(e.toString());
			}
			return ret;
		} catch (ClassNotFoundException cnf) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_NOT_FOUND,
					"Unknown enum type : " + className);
		}
	}

	private String toCamelCase(String from) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, from);
	}

	private String toUnderscore(String from) {
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, from);
	}
}
