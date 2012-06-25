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
package org.linagora.linshare.view.tapestry.components;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PersistentLocale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Import(library = {"jquery/jquery-1.3.1.min.js", "jquery/jquery.ui.core.js", "jquery/jquery.ui.datepicker.js", "jquery/jquery.ui.datepicker-fr.js", "DatePicker.js"},stylesheet = {"jquery/jquery-ui-1.7.2.custom.css", "DatePicker.css"})

public class DatePicker {
	private static final Logger logger = LoggerFactory.getLogger(DatePicker.class);
	
	@Parameter(required=false)
	@Property
	private Date minDate;
	
	@Parameter(required=false)
	@Property
	private Date maxDate;
	
	@Parameter(required=false)
	@Property
	private Date defaultDate;
	
	@Property
	private int dateMinD;
	@Property
	private int dateMinM;
	@Property
	private int dateMinY;
	@Property
	private int dateMaxD;
	@Property
	private int dateMaxM;
	@Property
	private int dateMaxY;
	@Property
	private int dateDefD;
	@Property
	private int dateDefM;
	@Property
	private int dateDefY;
	
	@Inject
	private PersistentLocale persistentLocale;
	
	@Inject
	private Messages messages;

	private Date datePicked;

	public Date getDatePicked() {
		return datePicked;
	}

	public void setDatePicked(Date datePicked) {
		this.datePicked = datePicked;
	}
	
	@SetupRender
	public void init() {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, new Locale(messages.get("components.datePicker.regional")));
		
		dateMinD = 0;
		dateMinM = 0;
		dateMinY = 0;
		dateMaxD = 0;
		dateMaxM = 0;
		dateMaxY = 0;
		dateDefD = 0;
		dateDefM = 0;
		dateDefY = 0;
		
		if (minDate != null) {
			Calendar calMin = dateFormat.getCalendar();
			calMin.setTime(minDate);
			dateMinD = calMin.get(Calendar.DAY_OF_MONTH);
			dateMinM = calMin.get(Calendar.MONTH);
			dateMinY = calMin.get(Calendar.YEAR);
		}
		if (maxDate != null) {
			Calendar calMax = dateFormat.getCalendar();
			calMax.setTime(maxDate);
			dateMaxD = calMax.get(Calendar.DAY_OF_MONTH);
			dateMaxM = calMax.get(Calendar.MONTH);
			dateMaxY = calMax.get(Calendar.YEAR);
		}
		if (defaultDate != null) {
			Calendar calDef = dateFormat.getCalendar();
			calDef.setTime(defaultDate);
			dateDefD = calDef.get(Calendar.DAY_OF_MONTH);
			dateDefM = calDef.get(Calendar.MONTH);
			dateDefY = calDef.get(Calendar.YEAR);
		}
	}
}
