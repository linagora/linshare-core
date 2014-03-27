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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.linagora.linkit.timeline.data.TimelineEvent;
import org.linagora.linshare.core.domain.vo.FileLogEntryVo;
import org.linagora.linshare.core.domain.vo.LogEntryVo;
import org.linagora.linshare.core.domain.vo.ShareLogEntryVo;
import org.linagora.linshare.core.domain.vo.UserLogEntryVo;
import org.linagora.linshare.view.tapestry.services.TimelineConverter;

public class TimelineConverterImpl implements TimelineConverter {

	private final String FileEntryDaoClassName = "fileEntry";
	private final String ShareEntryDaoClassName = "shareEntry";
	private final String UserEntryDaoClassName = "userEntry";
	/*private final Messages messages;
	*/	
	DateFormat df;
	public TimelineConverterImpl() {
		df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, Locale.US);
	}

	public TimelineEvent convert(LogEntryVo logEntry) {
		TimelineEvent theEvent = new TimelineEvent(df.format(logEntry.getActionDate().getTime()), logEntry.getLogAction().toString());
		theEvent.setCaption(logEntry.getDescription());
		// complete it
		
		if (logEntry instanceof UserLogEntryVo) {
			theEvent.setClassname(UserEntryDaoClassName);
			
			theEvent.setDescription("Target : <br/>" + 
					"Name : " + ((UserLogEntryVo)logEntry).getTargetFirstname() +" " +  ((UserLogEntryVo)logEntry).getTargetLastname() +"<br/>"+
					"Mail : " + ((UserLogEntryVo)logEntry).getTargetMail());
			
		} else {
			if (logEntry instanceof ShareLogEntryVo) {
				theEvent.setClassname(ShareEntryDaoClassName);
				theEvent.setDescription("Target : " + ((ShareLogEntryVo)logEntry).getTargetMail());
			} else {
				if (logEntry instanceof FileLogEntryVo) {
					theEvent.setClassname(FileEntryDaoClassName);
					theEvent.setDescription("file name : " + ((FileLogEntryVo)logEntry).getFileName());
				} 
			}
		}
		
		
		return theEvent;
	}

	public List<TimelineEvent> convert(List<LogEntryVo> listLogEntry) {
		List<TimelineEvent> list = new ArrayList<TimelineEvent>();
		if (listLogEntry==null) 
			return list;
		
		for (LogEntryVo logEntryVo : listLogEntry) {
			list.add(convert(logEntryVo));
		}
		return list;
	}

}
