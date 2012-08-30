package org.linagora.linshare.view.tapestry.pages.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.Facade.ThreadEntryFacade;
import org.linagora.linshare.core.domain.vo.TagVo;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.components.ThreadFileUploadPopup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectThread {

	private static final Logger logger = LoggerFactory.getLogger(Index.class);


    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    @SessionState
    @Property
    private UserVo userVo;

    @InjectPage
    private Index index;

    @Property
    @Persist
    private ThreadVo selectedProject;
    
    @Property
    private String threadName;
    
    @InjectComponent
    private ThreadFileUploadPopup threadFileUploadPopup;
    
    @Property
    private ThreadEntryVo entry;

    @Property
    @Persist
    private Map<String, List<ThreadEntryVo>> inProjectEntries;

    @Property
    @Persist
    private Map<String, List<ThreadEntryVo>> outProjectEntries;



    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

	@Inject
    private Messages messages;
	
    @Inject
    private ThreadEntryFacade threadEntryFacade; 



    @SetupRender
    public void setupRender() {  	
    	logger.debug("Setup Render begins");

    	threadName = selectedProject.getName();
    	threadFileUploadPopup.setMyCurrentThread(selectedProject);

		try {
			List<ThreadEntryVo> allThreadEntries = threadEntryFacade.getAllThreadEntryVo(userVo, selectedProject);

	    	outProjectEntries = new HashMap<String, List<ThreadEntryVo>>();		
			inProjectEntries = new HashMap<String, List<ThreadEntryVo>>();
			
			logger.debug("Looping through all thread entries");
			for (ThreadEntryVo e : allThreadEntries) {
				logger.debug("entry : " + e.toString());
				List<TagVo> tags = e.getTags();
				logger.debug("Looping through tags");
				for (TagVo tag : tags) {
					logger.debug("tag : " + tag.toString());
					if (tag.getName().equals("Demande")) {
						this.addToOutProjectEntries(e, tags);
						break;
					}
					else if (tag.getName().equals("Réponse")) {
						this.addToInProjectEntries(e, tags);
						break;
					}
				}
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
    }

    @AfterRender
    public void afterRender() {
        ;
    }

    private void addToOutProjectEntries(ThreadEntryVo e, List<TagVo> tags) {
    	logger.debug("Entering addToOutProjectEntries");
    	logger.debug("Looping through tags : " + Arrays.toString(tags.toArray()));
    	for (TagVo tag : tags) {
    		logger.debug("current tag is " + tag.toString() + "; current tag name is " + tag.getName());
    		if (tag.getName().equals("Phases")) {
    			String currentTag = tag.toString();
    			List<ThreadEntryVo> entries = outProjectEntries.get(currentTag);
    			if (entries == null) {
    				outProjectEntries.put(currentTag, new ArrayList<ThreadEntryVo>());
    				entries = outProjectEntries.get(currentTag);
    			}
    			logger.debug("adding to outbox : " + tag.toString() + " -> " + e.toString());
    			entries.add(e);
    		}
		}
    }

    private void addToInProjectEntries(ThreadEntryVo e, List<TagVo> tags) {
    	logger.debug("Entering addToInProjectEntries");
    	logger.debug("Looping through tags : " + Arrays.toString(tags.toArray()));
    	for (TagVo tag : tags) {
    		logger.debug("current tag is " + tag.toString() + "; current tag name is " + tag.getName());
    		if (tag.getName().equals("Phases")) {
    			String currentTag = tag.toString();
    			List<ThreadEntryVo> entries = inProjectEntries.get(currentTag);
    			if (entries == null) {
    				inProjectEntries.put(currentTag, new ArrayList<ThreadEntryVo>());
    				entries = inProjectEntries.get(currentTag);
    			}
    			logger.debug("adding to outbox : " + tag.toString() + " -> " + e.toString());
    			entries.add(e);
    		}
		}
    }
    
    // FIXME : ugly
    public List<ThreadEntryVo> getInInstructions() {
    	return inProjectEntries.get("Phases:Instruction");
    }
    
    // FIXME : ugly
    public List<ThreadEntryVo> getOutInstructions() {
    	return outProjectEntries.get("Phases:Instruction");
    }
    
    // FIXME : ugly
    public List<ThreadEntryVo> getInContradictions() {
    	return inProjectEntries.get("Phases:Contradiction");
    }

    // FIXME : ugly
    public List<ThreadEntryVo> getOutContradictions() {
    	return outProjectEntries.get("Phases:Contradiction");
    }
    
    // FIXME : ugly
    public List<ThreadEntryVo> getInRecommandations() {
    	return inProjectEntries.get("Phases:Recommandation");
    }
    
    // FIXME : ugly
    public List<ThreadEntryVo> getOutRecommandations() {
    	return outProjectEntries.get("Phases:Recommandation");
    }

    public void setMySelectedProject(ThreadVo selectedProject) {
		this.selectedProject = selectedProject;
	}
    
    Object onException(Throwable cause) {
        shareSessionObjects.addError(messages.get("global.exception.message"));
        logger.error(cause.getMessage());
        cause.printStackTrace();
        return this;
    }
}
