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

import java.util.Calendar;

/** Defines a unit of time.
 */
public enum TimeUnit {
    DAY(0), WEEK(1), MONTH(2);

    private int value;


    private TimeUnit(int value) {
        this.value = value;
    }
    

    public int toInt() {
        return value;
    }

    public int toCalendarValue() {
    	switch(this) {
    	case DAY : return Calendar.DATE;
    	case WEEK : return Calendar.WEEK_OF_MONTH;
    	case MONTH : return Calendar.MONTH;
    	default : throw new IllegalArgumentException("Doesn't match an existing TimeUnit");
    	}
    }
    public static TimeUnit fromInt(int value) {
        switch(value) {
            case 0 : return DAY;
            case 1 : return WEEK;
            case 2 : return MONTH;
            default : throw new IllegalArgumentException("Doesn't match an existing TimeUnit");
        }
    }
}
