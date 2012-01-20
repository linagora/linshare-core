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
package org.linagora.linShare.core.domain.constants;

import java.math.BigInteger;

/** File size unit.
 */
public enum FileSizeUnit {
    KILO(0),
    MEGA(1),
    GIGA(2);

    private int value;

    private FileSizeUnit(final int value) {
        this.value = value;
    }

    public int toInt() {
        return value;
    }

    public static FileSizeUnit fromInt(final int value) {
    	FileSizeUnit ret = null;
    	switch(value) {
            case 0 : ret = KILO; break;
            case 1 : ret = MEGA; break;
            case 2 : ret = GIGA; break;
            default : throw new IllegalArgumentException("Doesn't match an existing Role");
        }
    	return ret;
    }

    public long getPlainSize(final long size) {       
        long ret = 0L;
        switch (value) {
            case 0 : ret = size * BigInteger.valueOf(2).pow(10).longValue(); break;
            case 1 : ret = size * BigInteger.valueOf(2).pow(20).longValue(); break;
            case 2 : ret = size * BigInteger.valueOf(2).pow(30).longValue(); break;
            default : ret = size; break;
        }
        
        return ret; 
    }
}
