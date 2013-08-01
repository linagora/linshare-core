/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
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
package org.linagora.linshare.view.tapestry.components;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.Response;
import org.linagora.LinThumbnail.utils.Constants;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.models.SorterModel;
import org.linagora.linshare.view.tapestry.models.impl.ThreadEntrySorterModel;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;
import org.linagora.linshare.view.tapestry.pages.thread.ThreadContent;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Import(library= { "ListThreadDocument.js"})
@SupportsInformalParameters
public class ListThreadDocument {

    private static final Logger logger = LoggerFactory.getLogger(ListThreadDocument.class);

    /***********************************
     * Parameters
     ***********************************/

    /**
     * The user owner for the document list.
     */
    @Parameter(required=true,defaultPrefix=BindingConstants.PROP)
    private UserVo user;

    /**
     * The list of documents.
     */
    @Parameter(required=true,defaultPrefix=BindingConstants.PROP)
    @Property
    private List<ThreadEntryVo> listThreadEntries;
    
    @Parameter(required=true,defaultPrefix=BindingConstants.PROP)
    @Property
    private boolean admin;

    @Parameter(required=true,defaultPrefix=BindingConstants.PROP)
    @Property
    private String title;
    
    
    @Parameter(required=true,defaultPrefix=BindingConstants.PROP)
    private ThreadVo threadVo;


    /***********************************
     * Properties
     ***********************************/

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    @Property
    private ThreadEntryVo threadEntry;

    @Property
    private String login;

    @Property
    private boolean valueCheck;

    @Property
    private String checkBoxGroupUuid;


    /***********************************
     * Service injection
     ***********************************/

    @Inject
    private ThreadEntryFacade threadEntryFacade;

    @Inject
    private Messages messages;

    @Inject
    private Response response;

    @Inject
    private BeanModelSource beanModelSource;

    @Property
    @Persist
    private BeanModel<ThreadEntryVo> model;

    @Property
    @Persist
    private SorterModel<ThreadEntryVo> sorterModel;

    @Inject
    private ComponentResources componentResources;

    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;
    
    @InjectPage
    private ThreadContent threadContent;
    
	@Property
	@InjectComponent
	private ThreadEntryEditForm threadEntryEdit;


    /***********************************
     * Flags
     ***********************************/

    @Persist
    private boolean refreshFlag;

    @Persist
    private List<ThreadEntryVo> entries;

    @Persist
	private String selectedId;
    
    
    public ListThreadDocument() {
        super();
        checkBoxGroupUuid = "filesSelected_" + UUID.randomUUID().toString();
    }


    /*********************************
     * Phase render
     *********************************/

    /**
     * Initialization of the selected list and set the userLogin from the user ASO.
     * @throws BusinessException 
     */
    @SuppressWarnings("unchecked")
    @SetupRender
    public void init() throws BusinessException {
        if (listThreadEntries == null)
            return;
        Collections.sort(listThreadEntries);
        login = user.getLogin();
        initModel();
    }

    /***************************************************************************
     * ActionLink methods
     **************************************************************************/

    /**
     * The action triggered when the user click on the download link on the name
     * of the file.
     */

    public StreamResponse onActionFromDownload(String uuid)	throws BusinessException {
        // when user has been logged out
        if (listThreadEntries == null) {
            return null;
        }

        ThreadEntryVo current = null;
        for (ThreadEntryVo vo : listThreadEntries) {
            if (vo.getIdentifier().equals(uuid)) {
                current = vo;
                break;
            }
        }

        if (current == null) {
            logger.error("invalid uuid for this user");
            throw new BusinessException(BusinessErrorCode.INVALID_UUID,	"invalid uuid for this user");
        } else {
        	try {
                InputStream stream = threadEntryFacade.retrieveFileStream(current, user);
                return new FileStreamResponse(current, stream);
			} catch (Exception e) {
				logger.error("File don't exist anymore, please remove it");
				businessMessagesManagementService.notify(new BusinessException(BusinessErrorCode.FILE_UNREACHABLE,	"File unreachable in file system, please remove the entry"));
				return null;
			}
        }
    }
    
    public void onActionFromDelete(String uuid) {
    	threadContent.setSelectedThreadEntryId(uuid);
    }

    public Zone onActionFromFileEditProperties(String uuid) throws BusinessException {
    	
        // when user has been logged out
        if (listThreadEntries == null) {
            return null;
        }

        ThreadEntryVo current = null;
        for (ThreadEntryVo vo : listThreadEntries) {
            if (vo.getIdentifier().equals(uuid)) {
                current = vo;
                break;
            }
        }

        if (current == null) {
            logger.error("invalid uuid for this user");
            throw new BusinessException(BusinessErrorCode.INVALID_UUID,	"invalid uuid for this user");
        } else {
        	threadEntryEdit.setUuidThreadEntryToedit(uuid);
            return threadEntryEdit.getShowPopupWindow();
        }
    }

    /***************************************************************************
     * Events
     **************************************************************************/

    @SuppressWarnings("unchecked")
    @OnEvent(value="eventReorderList")
    public void reorderList(Object[] o1) {
        if (o1 != null && o1.length > 0) {
            this.entries = (List<ThreadEntryVo>)Arrays.copyOf(o1,1)[0];
            this.sorterModel = new ThreadEntrySorterModel(this.entries);
            refreshFlag = true;
        }
    }
    
    @OnEvent(value="eventDelete")
    public void deleteEntry() {
    	componentResources.getPage().getComponentResources().triggerEvent("eventDeleteThreadEntry", null, null);
    }


    /***************************************************************************
     * Other methods
     **************************************************************************/
    
    
    public boolean isDeletable() {
    	try {
    		return  null != threadEntryFacade.findById(user, threadEntry.getIdentifier());
    	} catch(BusinessException e) {
    		logger.debug(e.toString());
    		return false;
    	}
    }
    
    
    /**
     * model for the datagrid we need it to switch off the signature and the
     * encrypted column dynamically administration can desactivate the signature
     * and encryption function
     * 
     * @return
     * @throws BusinessException
     */
    public BeanModel<ThreadEntryVo> initModel() throws BusinessException {
        //Initialize the sorter model for sorter component.
        if (refreshFlag) {
            listThreadEntries = entries;
            refreshFlag = false;
        }
        sorterModel = new ThreadEntrySorterModel(listThreadEntries);
        model = beanModelSource.createDisplayModel(ThreadEntryVo.class, componentResources.getMessages());
        model.add("fileProperties", null);
        if (admin) {
        	model.add("fileDelete", null);
        }
        List<String> reorderlist = new ArrayList<String>();
        reorderlist.add("fileProperties");
        if (admin) {
        	reorderlist.add("fileDelete");
        }
        model.reorder(reorderlist.toArray(new String[reorderlist.size()]));

        return model;
    }

    /**
     * Generate the css class in order to display the icon corresponding to the file mime type
     * 
     * @return the css class
     */
    public String getTypeCSSClass() {
        String ret = threadEntry.getType();
        ret = ret.replace("/", "_");
        ret = ret.replace("+", "__");
        ret = ret.replace(".", "_-_");
        return ret;
    }

    /**
     * Format the file size for good readability
     * 
     * @return size in human readable format
     */
    public String getFriendlySize() {
        return FileUtils.getFriendlySize(threadEntry.getSize(), messages);
    }

    /**
     * Format the creation date for good displaying using DateFormatUtils of
     * apache commons lib.
     * 
     * @return creation date the date in localized format.
     */
    public String getCreationDate() {
        SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.timestamp"));
        return formatter.format(threadEntry.getCreationDate().getTime());
    }

    /**
     * Remove all carriage return for chenillekit tool tip
     * 
     * @return the formated comment
     */
    public String getFormattedComment() {
        String result = threadEntry.getFileComment().replaceAll("\r","");
        result = result.replaceAll("\n", " ");
        return result;
    }

    public boolean getCanUpload(){
    	try {
    		logger.debug(String.valueOf(threadEntryFacade.userCanUpload(user, threadVo)));
			return threadEntryFacade.userCanUpload(user, threadVo);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			return false;
		}
    }
    
    public boolean getIsAdmin(){
    	try {
			return threadEntryFacade.userIsAdmin(threadVo, user);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			return false;
		}
    }
    
    public Link getThumbnailPath() {
        return componentResources.createEventLink("thumbnail", threadEntry.getIdentifier());
    }

    public boolean getThumbnailExists() {
        return threadEntryFacade.documentHasThumbnail(login, threadEntry.getIdentifier());
    }

    public void onThumbnail(String docId) {
        InputStream stream = null;
        ThreadEntryVo current = null;
        for (ThreadEntryVo vo : listThreadEntries) {
            if (vo.getIdentifier().equals(docId)) {
                current = vo;
                break;
            }
        }
        try {
        	stream = threadEntryFacade.getDocumentThumbnail(user.getLsUuid(), current.getIdentifier());
        } catch (Exception e) {
			logger.error("Trying to get a thumbnail linked to a document which doesn't exist anymore");
		}
        if (stream == null)
            return;
        OutputStream os = null;
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        try {
            os = response.getOutputStream("image/png");
            BufferedImage bufferedImage=ImageIO.read(stream);
            if (bufferedImage != null)
                ImageIO.write(bufferedImage, Constants.THMB_DEFAULT_FORMAT, os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
