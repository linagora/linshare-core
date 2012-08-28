package org.linagora.linshare.view.tapestry.pages.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectComponent;
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
    
    @Property
    @Persist
    private ThreadVo selectedProject;
    
    @InjectComponent
    private ThreadFileUploadPopup threadFileUploadPopup;
    

//    @Property
//    private ThreadVo currentThread;
//    
//    @Property
//    private	List<String> inProjects;
//    
//    @Property
//    private	List<String> outProjects;
//    
//    @Property
//    private Map<String, List<ThreadEntryVo>> inProjectEntries;
//    
//    @Property
//    private Map<String, List<ThreadEntryVo>> outProjectEntries;
    


    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

	@Inject
    private Messages messages;
	
    @Inject
    private ThreadEntryFacade threadEntryFacade; 



    @SetupRender
    public void setupRender() {
    	threadFileUploadPopup.setMyCurrentThread(threadEntryFacade.getAllThread().get(0));
//		currentThread = threadEntryFacade.getAllThread().get(0);
//		try {
//			List<ThreadEntryVo> allThreadEntries = threadEntryFacade.getAllThreadEntryVo(userVo, currentThread);
//
//			outProjects = new ArrayList<String>();
//			outProjectEntries = new HashMap<String, List<ThreadEntryVo>>();
//			
//			inProjects = new ArrayList<String>();
//			inProjectEntries = new HashMap<String, List<ThreadEntryVo>>();		
//			
//			for (ThreadEntryVo entry : allThreadEntries) {
//				List<TagVo> tags = entry.getTags();
//				for (TagVo tag : tags) {
//					if (tag.getName() == "Demande") {
//						this.addToOutProjects(entry, tags);
//						break;
//					}
//					else if (tag.getName() == "Réponse") {
//						this.addToInProjects(entry, tags);
//						break;
//					}
//				}
//			}
//		} catch (BusinessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	logger.debug("Début du Setup Render");
    	logger.debug("Projet : " + selectedProject.getName());
    	logger.debug("Fin du Setup Render");
    }

    @AfterRender
    public void afterRender() {
        ;
    }

//    private void addToOutProjects(ThreadEntryVo entry, List<TagVo> tags) {
//    	for (TagVo tag : tags) {
//    		if (tag.getName() == "Projets") {
//    			String currentTag = tag.toString();
//    			if (!outProjects.contains(currentTag)) {
//    				outProjects.add(currentTag);
//    				outProjectEntries.put(currentTag, new ArrayList<ThreadEntryVo>());
//    			}
//    			outProjectEntries.get(currentTag).add(entry);
//    			sortProjectByPhase(outProjectEntries.get(currentTag));
//    		}
//		}
//    }
//
//    private void addToInProjects(ThreadEntryVo entry, List<TagVo> tags) {
//    	for (TagVo tag : tags) {
//    		if (tag.getName() == "Projets") {
//    			String currentTag = tag.toString();
//    			if (!inProjects.contains(currentTag)) {
//    				inProjects.add(currentTag);
//    				inProjectEntries.put(currentTag, new ArrayList<ThreadEntryVo>());
//    			}
//    			inProjectEntries.get(currentTag).add(entry);
//    			sortProjectByPhase(inProjectEntries.get(currentTag));
//    		}
//		}
//    }
//
//    // FIXME : please, hurry up and fix me, i'm a poor badly thought-out function
//    private void sortProjectByPhase(List<ThreadEntryVo> entries) {
//    	Collections.sort(entries, new Comparator<ThreadEntryVo>() {
//			@Override
//			public int compare(ThreadEntryVo o1, ThreadEntryVo o2) {
//				TagVo tag1 = null;
//				TagVo tag2 = null;
//				for (TagVo tag : o1.getTags()) {
//					if (tag.getName() == "Phases") {
//						tag1 = tag;
//						break;
//					}
//				}
//				for (TagVo tag : o2.getTags()) {
//					if (tag.getName() == "Phases") {
//						tag2 = tag;
//						break;
//					}
//				}
//				if (tag1 == null || tag2 == null)
//					return 0;
//				return tag1.toString().length() - tag2.toString().length();
//			}   		
//		});
//    }
    
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
