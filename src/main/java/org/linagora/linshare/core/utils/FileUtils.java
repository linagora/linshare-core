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
package org.linagora.linshare.core.utils;

import org.apache.tapestry5.ioc.Messages;

public class FileUtils {
	
	private static final float KiB = (float)Math.pow(2, 10);
	private static final float MiB = (float)Math.pow(2, 20);
	private static final float GiB = (float)Math.pow(2, 30);
	
	public enum Unit {Byte,KiB,MiB,GiB};
	
	
	private FileUtils() {
	}
	
    public static String getFriendlySize(long SizeInBytes, Messages messages) {
        if (SizeInBytes < KiB){
            return String.format(messages.get("components.fileUtils.bytesSize.text"), SizeInBytes);
        } else if(SizeInBytes < MiB){
            return String.format(messages.get("components.fileUtils.kiloSize.text"), SizeInBytes / KiB);
        } else if(SizeInBytes < GiB){
            return String.format(messages.get("components.fileUtils.megaSize.text"), SizeInBytes / MiB);
        } else{
            return String.format(messages.get("components.fileUtils.gigaSize.text"), SizeInBytes / GiB);
        }
    }
    
    public static Unit getAppropriateUnitSize(long sizeInBytes){
        if (sizeInBytes < KiB){
            return Unit.Byte;
        } else if (sizeInBytes < MiB){
        	 return Unit.KiB;
        } else if (sizeInBytes < GiB){
        	 return Unit.MiB;
        } else{
        	 return Unit.GiB;
        }
    }
    
    public static String getFriendlySize(long SizeInBytes, Messages messages, Unit wantedUnit){
    	
    	String res = null;
    	
    	if (wantedUnit==null) {
    		res = getFriendlySize(SizeInBytes, messages);
    	} else {
    	
	    	switch (wantedUnit) {
			case Byte:
				res = String.format(messages.get("components.fileUtils.bytesSize.text"), SizeInBytes);
				break;
			case KiB:
				res = String.format(messages.get("components.fileUtils.kiloSize.text"), SizeInBytes / KiB);
				break;
			case MiB:
				res = String.format(messages.get("components.fileUtils.megaSize.text"), SizeInBytes / MiB);
				break;
			case GiB:
				res = String.format(messages.get("components.fileUtils.gigaSize.text"), SizeInBytes / GiB);
				break;
			default:
				res = String.format(messages.get("components.fileUtils.gigaSize.text"), SizeInBytes / GiB);
				break;
			}
    	}
    	
    	return res;
    }
    
    
    
    
    
}
