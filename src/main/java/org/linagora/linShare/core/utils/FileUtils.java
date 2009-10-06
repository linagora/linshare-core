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
package org.linagora.linShare.core.utils;

import org.apache.tapestry5.ioc.Messages;

public class FileUtils {
	
	private static final float KiB = (float)Math.pow(2, 10);
	private static final float MiB = (float)Math.pow(2, 20);
	private static final float GiB = (float)Math.pow(2, 30);
	
	public enum Unit {Byte,KiB,MiB,GiB};
	
	
	private FileUtils() {
	}
	
    public static String getFriendlySize(long SizeInBytes, Messages messages){
        if(SizeInBytes<KiB){
            return String.format(messages.get("components.fileUtils.bytesSize.text"),SizeInBytes);
        }else if(SizeInBytes<MiB){
            return String.format(messages.get("components.fileUtils.kiloSize.text"), (float)(SizeInBytes/KiB));
        }else if(SizeInBytes<GiB){
            return String.format(messages.get("components.fileUtils.megaSize.text"), (float)(SizeInBytes/MiB));
        }else{
            return String.format(messages.get("components.fileUtils.gigaSize.text"), (float)(SizeInBytes/GiB));
        }
    }
    
    public static Unit getAppropriateUnitSize(long sizeInBytes){
        if(sizeInBytes<KiB){
            return Unit.Byte;
        }else if(sizeInBytes<MiB){
        	 return Unit.KiB;
        }else if(sizeInBytes<GiB){
        	 return Unit.MiB;
        }else{
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
				res = String.format(messages.get("components.fileUtils.bytesSize.text"),SizeInBytes);
				break;
			case KiB:
				res = String.format(messages.get("components.fileUtils.kiloSize.text"), (float)(SizeInBytes/KiB));
				break;
			case MiB:
				res = String.format(messages.get("components.fileUtils.megaSize.text"), (float)(SizeInBytes/MiB));
				break;
			case GiB:
				res = String.format(messages.get("components.fileUtils.gigaSize.text"), (float)(SizeInBytes/GiB));
				break;
			default:
				res = String.format(messages.get("components.fileUtils.gigaSize.text"), (float)(SizeInBytes/GiB));
				break;
			}
    	}
    	
    	return res;
    }
    
    
    
    
    
}
