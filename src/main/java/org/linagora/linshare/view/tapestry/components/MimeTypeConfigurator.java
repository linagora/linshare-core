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

import java.util.List;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.linagora.linshare.core.domain.entities.MimeTypeStatus;
import org.linagora.linshare.core.domain.vo.AllowedMimeTypeVO;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.MimeTypeFacade;

@SupportsInformalParameters
@Import(library = { "MimeTypeConfigurator.js" })

public class MimeTypeConfigurator {
	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
	

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
	@Inject
	private MimeTypeFacade  mimeTypeFacade;

    
    
    @Environmental
    private JavaScriptSupport renderSupport;
	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
	
	
	@Property
	private AllowedMimeTypeVO allowedMimeTypeVO;
    
    @Property
    @Persist
	private List<AllowedMimeTypeVO> supportedMimeType;
	
	private MimeTypeStatus mimeTypeStatus;
	
    @Property
    @Persist
	private int numberOfRow;
    
    @Property
    @Persist
	private int totalSize;
    
	

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
	@SetupRender
	void init() throws BusinessException {
        
		if (numberOfRow==0) numberOfRow = 10; 
		
		if(supportedMimeType==null) supportedMimeType = mimeTypeFacade.getAllowedMimeType(); //read from database
        
        if(supportedMimeType.size()==0){
        	supportedMimeType = mimeTypeFacade.getAllSupportedMimeType(); //read from provider if table is empty
        }
        
        totalSize = supportedMimeType.size();
        
	}
	
	@AfterRender
    public void afterRender() {
    }
	
    public void onSuccessFromMimeTypeConfiguratorForm() {
        try {
            
        	//mimeTypeFacade.createAllowedMimeType(selected);
        	mimeTypeFacade.saveOrUpdateAllowedMimeType(supportedMimeType);
        
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }
    
    
	
	public MimeTypeStatus getAuthorised() { return MimeTypeStatus.AUTHORISED; }
	public MimeTypeStatus getDenied() { return MimeTypeStatus.DENIED; }
	public MimeTypeStatus getWarn() { return MimeTypeStatus.WARN; }

	public MimeTypeStatus getMimeTypeStatus() { return  allowedMimeTypeVO.getStatus();}
	public void setMimeTypeStatus(MimeTypeStatus status) { allowedMimeTypeVO.setStatus(status);}
}
