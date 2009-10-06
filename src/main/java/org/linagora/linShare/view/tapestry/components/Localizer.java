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
package org.linagora.linShare.view.tapestry.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.internal.util.LocaleUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.services.PersistentLocale;

/**
 * The Localizer is a component which permits to switch the language of the application.
 * There is two ways to use it:
 * First way,a list of locales is supplied 
 * Second way no locales list and in this case the component will search the locales defined in the AppModule class.
 * 
 * The translation is done automatically by using java.util.Local .
 * This is a limitation because some languages are not really good supported.
 * 
 * The component can display the country or the language of your Locale.
 * There is a parameter for the translation:
 * respectiveLanguage if the value of this parameter is true, the translation of each language/country is done in the language of the link.
 * For example: Français,English,Deutsch
 * 
 * if false, this is the current Locale will use (persistentLocale.get()).
 * 
 * 
 *  
 * @author ngapaillard
 *
 */
public class Localizer {


	private final String COUNTRY="country";
	private final String LANGUAGE="language";

	/**
	 * The locales that you want display.
	 * If this parameter is not specify, the application will target the AppModule locale properties for retrieve locales.
	 */
	@Parameter(required=false,defaultPrefix=BindingConstants.PROP )
	@Property
	private List<Locale> locales;


	
	/**
	 * If this parameter is true so the translation will done with the language of the link.
	 * Else this is the current language which is used (persistentLocale.get()).
	 */
	@Parameter(required=false,value="false",defaultPrefix=BindingConstants.LITERAL)
	private boolean respectiveLanguage;
	
	/**
	 * If you want to exclude locales. (Usefull when the locales are retrieved from AppModule).
	 * 
	 */
	@Parameter(required=false,defaultPrefix=BindingConstants.PROP)
	@Property
	private List<Locale> exclude;

	/**
	 * The label presents before links of locales.
	 */
	@SuppressWarnings("unused")
	@Parameter(required=false,defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String title;
	
	/**
	 * The separator between each locale link.
	 */
	@SuppressWarnings("unused")
	@Parameter(required=false,value="",defaultPrefix=BindingConstants.LITERAL)
	@Property
	private String separator;

	/**
	 * The information that will be display.
	 * country or language.
	 */
	@Parameter(required=false,value="language",defaultPrefix=BindingConstants.LITERAL)
	private String type;

	
	@Inject
	private SymbolSource symbolSource;


	@Inject
	private PersistentLocale persistentLocale;


	
	private Locale currentLocale;


	private int count=0;
	
	@SetupRender
	public void initLanguage(){
		if(null==locales || locales.size()==0){
		
			if(null!=symbolSource.valueForSymbol(SymbolConstants.SUPPORTED_LOCALES)){
				String stringLocales=symbolSource.valueForSymbol(SymbolConstants.SUPPORTED_LOCALES);
				String[]listLocales=stringLocales.split(",");
				
				locales=this.getSupportedLocales(listLocales);
				
				
			}
		}else{
			locales.removeAll(exclude);
		}

	}

    void onActionFromLocaleLink(String locale)
    {
        persistentLocale.set(LocaleUtils.toLocale(locale));
        
    } 

    public String getLocaleValue(){

    	return currentLocale.toString();
    }
    
    public boolean isSelected(){
    	return currentLocale.equals(persistentLocale.get());
    }
    
	public String getLabel(){

		
		if(null==type || "".equals(type) || LANGUAGE.equalsIgnoreCase(type)){
			Locale locale=persistentLocale.get();
			if(respectiveLanguage){
				return currentLocale.getDisplayLanguage(currentLocale);
			}
			if(locale!=null){
				return currentLocale.getDisplayLanguage(locale);
			}else{
				return currentLocale.getDisplayLanguage();
			}
		}else if(COUNTRY.equalsIgnoreCase(type)){
			Locale locale=persistentLocale.get();
			if(respectiveLanguage){
				return currentLocale.getDisplayCountry(currentLocale);
			}
			if(locale!=null){
				return currentLocale.getDisplayCountry(locale);	
			}else{
				return currentLocale.getDisplayCountry();	
			}
					
		}
		return null;
	}

	public boolean isLast(){
		return count==locales.size();
	}
	
	private List<Locale> getSupportedLocales(String[]locales){
		ArrayList<Locale> newLocales=new ArrayList<Locale>();
		for(String currentLocale:locales){
			Locale local=LocaleUtils.toLocale(currentLocale);
			if((exclude!=null && !exclude.contains(local)) || exclude==null){
				newLocales.add(LocaleUtils.toLocale(currentLocale));
			}
		}

		return newLocales;
	}

	public Locale getCurrentLocale() {
		
		return currentLocale;
	}

	public void setCurrentLocale(Locale currentLocale) {
		count++;
		this.currentLocale = currentLocale;
	}

}	
