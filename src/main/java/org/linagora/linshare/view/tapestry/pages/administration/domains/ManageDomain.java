package org.linagora.linshare.view.tapestry.pages.administration.domains;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.Facade.AbstractDomainFacade;
import org.linagora.linshare.core.domain.vo.AbstractDomainVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.pages.administration.ActivationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Import(library = {"../../../components/jquery/jquery-1.7.2.js","../../../components/jquery/jquery.ui.core.js","../../../components/jquery/jquery.ui.widget.min.js","../../../components/jquery/jquery.ui.mouse.min.js","../../../components/jquery/jquery.ui.sortable.min.js","ManageDomain.js"}, stylesheet={"../../../components/jquery/jquery-ui-1.8.21.custom.css","ManageDomain.css"})
public class ManageDomain {

	private static Logger logger = LoggerFactory.getLogger(ActivationPolicy.class);

    @SessionState
    private UserVo loginUser;
	
	@SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;
    
    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

	@Inject
	private Messages messages;
    
    @Inject
    private AbstractDomainFacade domainFacade;
    
	@InjectComponent
	private Form manageForm;
    
    @Property
    private String _domainName;
    
    @Property 
    private int indexDomain;
    
	@Property
	private String tabPos;
    
    public String[] getDomainNames(){
    	List<String> domainNames = domainFacade.findAllDomainIdentifiers();
    	
    	logger.debug("Domain name identifers retrieve:" + domainNames.toString());
    	
    	return (String[]) domainNames.toArray(new String[domainNames.size()]);
    }
    
    public Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
    
    
	public Object onSuccessFromManageForm() throws BusinessException {
		logger.debug("onSuccessFromManageForm");
		
		logger.debug("Retrieve string of the table domain:" + tabPos);
		
		if(tabPos == null){
			return Index.class;
		}
		
		String[] domainNames = tabPos.split(";");
		
		List<AbstractDomainVo> domainsVo = new ArrayList<AbstractDomainVo>();
		AbstractDomainVo abstractDomainVo; 
		
		int i = 0;
		
		for (String domainName : domainNames) {
			if(!domainName.isEmpty()){
				
				abstractDomainVo = new AbstractDomainVo();
				abstractDomainVo.setAuthShowOrder(new Long(i));
				abstractDomainVo.setIdentifier(domainName);
				domainsVo.add(abstractDomainVo);	
			}
			++i;
		}
		
		domainFacade.updateAllDomainForAuthShowOrder(loginUser, domainsVo);
		
		return Index.class;
	}
    
    public Object onActionFromCancel() {
		return Index.class;
	}
    
}
