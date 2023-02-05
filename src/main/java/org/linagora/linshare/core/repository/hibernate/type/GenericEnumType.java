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
package org.linagora.linshare.core.repository.hibernate.type;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.AbstractStandardBasicType;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/**
 * Implements a generic enum user type identified / represented by a single identifier / column.
 * <p><ul>
 *    <li>The enum type being represented by the certain user type must be set
 *        by using the 'enumClass' property.</li>
 *    <li>The identifier representing a enum value is retrieved by the identifierMethod.
 *        The name of the identifier method can be specified by the
 *        'identifierMethod' property and by default the name() method is used.</li>
 *    <li>The identifier type is automatically determined by
 *        the return-type of the identifierMethod.</li>
 *    <li>The valueOfMethod is the name of the static factory method returning
 *        the enumeration object being represented by the given indentifier.
 *        The valueOfMethod's name can be specified by setting the 'valueOfMethod'
 *        property. The default valueOfMethod's name is 'valueOf'.</li>
 * </p>
 * <p>
 * Example of an enum type represented by an int value:
 * <code>
 * public enum SimpleNumber {
 *   Unknown(-1), Zero(0), One(1), Two(2), Three(3);
 *   int value;
 *   protected SimpleNumber(int value) {
 *       this.value = value;
 *       }
 *
 *   public int toInt() { return value; }
 *   public static SimpleNumber fromInt(int value) {
 *         switch(value) {
 *           case 0: return Zero;
 *           case 1: return One;
 *           case 2: return Two;
 *           case 3: return Three;
 *           default:
 *                 return Unknown;
 *     }
 *   }
 * }
 * </code>
 * <p>
 * The Mapping would look like this:
 * <code>
 *    <typedef name="SimpleNumber" class="GenericEnumUserType">
 *        <param name="enumClass">SimpleNumber</param>
 *        <param name="identifierMethod">toInt</param>
 *        <param name="valueOfMethod">fromInt</param>
 *    </typedef>
 *    <class>
 *      ...
 *     <property name="number" column="number" type="SimpleNumber"/>
 *    </class>
 * </code>
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class GenericEnumType implements UserType, ParameterizedType {

    private static final String DEFAULT_IDENTIFIER_METHOD_NAME = "name";
    private static final String DEFAULT_VALUE_OF_METHOD_NAME = "valueOf";
    private Class<? extends Enum> enumClass;
    private Class<?> identifierType;
    private Method identifierMethod;
    private Method valueOfMethod;
    private AbstractStandardBasicType<? extends Object> type;
    private int[] sqlTypes;

	public void setParameterValues(Properties parameters) {
		String enumClassName = parameters.getProperty("enumClass");
		try {
			enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
		} catch (ClassNotFoundException cfne) {
			throw new HibernateException("Enum class not found", cfne);
		}

		String identifierMethodName = parameters.getProperty("identifierMethod", DEFAULT_IDENTIFIER_METHOD_NAME);

		try {
			identifierMethod = enumClass.getMethod(identifierMethodName, new Class[0]);
			identifierType = identifierMethod.getReturnType();
		} catch (Exception e) {
			throw new HibernateException("Failed to obtain identifier method", e);
		}

		TypeConfiguration typeConfiguration = new TypeConfiguration();
		type = (AbstractSingleColumnStandardBasicType<? extends Object>) typeConfiguration.getTypeResolver()
				.heuristicType(identifierType.getName(), parameters);

		if (type == null) {
			throw new HibernateException("Unsupported identifier type " + identifierType.getName());
		}

		sqlTypes = new int[] { ((AbstractSingleColumnStandardBasicType<?>) type).sqlType() };

		String valueOfMethodName = parameters.getProperty("valueOfMethod", DEFAULT_VALUE_OF_METHOD_NAME);

		try {
			valueOfMethod = enumClass.getMethod(valueOfMethodName, new Class[] { identifierType });
		} catch (Exception e) {
			throw new HibernateException("Failed to obtain valueOf method", e);
		}
	}

    public Class<? extends Enum> returnedClass() {
        return enumClass;
    }
    
    public int[] sqlTypes() {
        return sqlTypes;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean isMutable() {
        return false;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor sessionImplementor, Object arg3)
			throws HibernateException, SQLException {
		Object identifier = type.get(rs, names[0], sessionImplementor);
		if (rs.wasNull()) {
			return null;
		}

		try {
			return valueOfMethod.invoke(enumClass, new Object[] { identifier });
		} catch (Exception e) {
			throw new HibernateException("Exception while invoking valueOf method '" + valueOfMethod.getName() + "' of "
					+ "enumeration class '" + enumClass + "'", e);
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor sessionImplementor)
			throws HibernateException, SQLException {
		try {
			if (value == null) {
				st.setNull(index, ((AbstractSingleColumnStandardBasicType<?>) type).sqlType());
			} else {
				Object identifier = identifierMethod.invoke(value, new Object[0]);
				type.nullSafeSet(st, identifier, index, sessionImplementor);
			}
		} catch (Exception e) {
			throw new HibernateException("Exception while invoking identifierMethod '" + identifierMethod.getName()
					+ "' of " + "enumeration class '" + enumClass + "'", e);
		}
	}
}
