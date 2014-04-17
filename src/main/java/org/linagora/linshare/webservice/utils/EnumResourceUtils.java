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
	 * @return
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
	 * @return
	 * @throws BusinessException
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
