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
package org.linagora.linshare.webservice.utils;

import java.util.Collections;
import java.util.List;

import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.reflections.Reflections;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
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
		List<String> excludedEnums = Lists.newArrayList();
		excludedEnums.add("LOG_ACTION_V1");
		excludedEnums.add("UPLOAD_PROPOSITION_ACTION_TYPE");
		excludedEnums.add("UPLOAD_PROPOSITION_EXCEPTION_RULE_TYPE");
		excludedEnums.add("UPLOAD_PROPOSITION_MATCH_TYPE");
		excludedEnums.add("UPLOAD_PROPOSITION_RULE_FIELD_TYPE");
		excludedEnums.add("UPLOAD_PROPOSITION_RULE_OPERATOR_TYPE");
		excludedEnums.add("UPLOAD_PROPOSITION_STATUS");
		excludedEnums.add("UPLOAD_REQUEST_HISTORY_EVENT_TYPE");
		// Iterate over all enums under ENUMS_PATH package
		for (final Class<? extends Enum> e : new Reflections(ENUMS_PATH)
				.getSubTypesOf(Enum.class)) {
			String enumIn = toUnderscore(e.getSimpleName());
			if (!excludedEnums.contains(enumIn) && !Strings.isNullOrEmpty(enumIn)) {
				res.add(enumIn);
			}
		}
		Collections.sort(res);
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
			Collections.sort(ret);
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
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, from).toUpperCase();
	}
}
