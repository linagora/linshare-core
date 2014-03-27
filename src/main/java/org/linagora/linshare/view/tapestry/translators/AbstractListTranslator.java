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
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.view.tapestry.translators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.Translator;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.services.FormSupport;


public abstract class AbstractListTranslator<A> implements Translator<List<A>> {

	private List<A> entities;
	private final String messageKey;
	private final String translatorName;
	
	public AbstractListTranslator(List<A> entities, final String translatorName, final String messageKey) {
		this.entities = entities;
		this.messageKey = messageKey;
		this.translatorName = translatorName;
	}

	public List<A> parseClient(Field field, String clientValue, String message) throws ValidationException {
		if(null!=this.entities && null!=clientValue && !"".equals(clientValue)){
			/*
			 * We retrieve elements separated by commas
			 */
			
			String[] clientValueSplit=clientValue.split(",");

			
			ArrayList<A> listVosSelected = new ArrayList<A>();

			
			/*
			 * We add all Zone which matches the zone entered by the user 
			 */
			for(int i=0;i<clientValueSplit.length;i++){
				/*
				 * The Zone equals method is based on the name of the zone
				 * The constructor of Zone takes the name in parameter  
				 */
				
				
				if("".equals(clientValueSplit[i].trim())){
					throw new ValidationException(message);

				}
				
				A example= getObjectFromName(clientValueSplit[i].trim());
				
				
				
				int index=this.entities.indexOf(example);
				/*
				 * when indexZone == -1 it means that the user entered a zone which is not available
				 * in this case we throw an exception with the appropriate message
				 * else we can add the Zone in the selected zone list
				 */
				if(index==-1){
					throw new ValidationException(message);
				}else{
					listVosSelected.add(this.entities.get(index));
				}
			}
			
			return listVosSelected;
		}else{
			return null;
		}

	}

	
	public String toClient(List<A> value) {
		if(null!=value){
			String listVos="";
			Iterator<A> i=value.iterator();
			while(i.hasNext()){
				listVos+=getNameFromObject(i.next());
				if(i.hasNext()){
					listVos+=",";
				}
			}
			return listVos;
		}else{
			return "";
		}
	}

	public  void render(Field field, String message, MarkupWriter writer, FormSupport formSupport) {
		//Do Nothing
	}

	public String getMessageKey() {
		return messageKey;
	}

	public String getName() {
		return translatorName;
	}
	
	public abstract A getObjectFromName(String name);

	public abstract String getNameFromObject(A a);
}
