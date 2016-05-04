/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
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
package org.linagora.linshare.view.tapestry.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.internal.util.TapestryException;

/**
 * @author ngapaillard 
 * <p>This class is a layout very often use in the web which is constituted with:</p>
 * <p>
 * <UL>
 * <LI>Header (required)</LI>
 * <LI>Footer (required)</LI>
 * <LI>Left  (not required)</LI>
 * <LI>Right (not required) (exclusive with left)</LI>
 * <LI>Body (t:body)</LI>
 * <LI>Menu bar (not required)</LI> 
 * <LI>leftSize</LI>
 * <LI>rightSize</LI>
 * </UL>
 * </p>
 * <P>
 * 	This layout is based on the <b>Yahoo UI CSS library</b> which permits to place elements like a grid.
 * </P>
 */

/*IncludeStylesheet({"context:css/fonts-min.css","context:css/reset-min.css", "context:css/grids.css"})*/

@Import(stylesheet={"context:css/reset-fonts-grids.css"})
@SupportsInformalParameters
public class BorderLayout {

	/**
	 * All page size available.
	 */
	/**
	 * Corresponding to 100% width.
	 */
	private final static String ALLPAGE="allPage";
	/**
	 * Corresponding to 750px.
	 */	
	private final static String MINIMUM="minimum";
	
	/**
	 * Corresponding to 950px.
	 */
	private final static String MEDIUM="medium";
	
	/**
	 * Corresponding to 974px.
	 */
	private final static String MAXIMUM="maximum";
	
	/**
	 * All left panel size available.
	 * 
	 */
	
	
	/**
	 * Corresponding to 160px.
	 */	
	private final static String LEFT_MINIMUM="small";
	
	/**
	 * Corresponding to 180px.
	 */
	private final static String LEFT_MEDIUM="medium";
	
	/**
	 * Corresponding to 300px.
	 */
	private final static String LEFT_HIGH="high";
	
	/**
	 * Corresponding to 180px.
	 */	
	private final static String RIGHT_MINIMUM="small";
	
	/**
	 * Corresponding to 240px.
	 */
	private final static String RIGHT_MEDIUM="medium";
	
	/**
	 * Corresponding to 300px.
	 */
	private final static String RIGHT_HIGH="high";
	

	/**
	 * List of parameters
	 */
	
	/**
	 * <p>The page size.</p>
	 * Available sizes:
	 * <ul> 
	 * <li><b>allPage</b> corresponding to <b>100%</b>.</li>
	 * <li><b>minimum</b>  corresponding to <b>750px.</b></li>
	 * <li><b>medium</b>  corresponding to <b>950px.</b></li>
	 * <li><b>maximum</b>  corresponding to <b>974px.</b></li>
	 * </ul>
	 */
	@Parameter(required=false,value=ALLPAGE,defaultPrefix = BindingConstants.LITERAL)
	private String pageSize;
	
	/**
	 * <p>The left panel size.</p>
	 * Available sizes: 
	 * <ul>
	 * <li><b>small</b> corresponding to <b>160px.</b></li>
	 * <li><b>medium</b> corresponding to <b>180px.</b></li>
	 * <li><b>high</b> corresponding to <b>300px.</b></li>
	 * </ul>
	 */
	@Parameter(required=false,value=LEFT_MINIMUM,defaultPrefix = BindingConstants.LITERAL)
	private String leftSize;
	
	/**
	 * <p>The right panel size.</p>
	 * Available sizes: 
	 * <ul>
	 * <li><b>small</b> corresponding to <b>180px.</b></li>
	 * <li><b>medium</b> corresponding to <b>240px.</b></li>
	 * <li><b>high</b> corresponding to <b>300px.</b></li>
	 * </ul>
	 */
	@Parameter(required=false,value=RIGHT_MINIMUM,defaultPrefix = BindingConstants.LITERAL)
	private String rightSize;
	
	
	/**
	 * The title of the page.
	 */
	@Parameter(required=true,defaultPrefix = BindingConstants.LITERAL)
	@Property
	private String title;
	
	
	/**
	 * Identifier of the page.
	 */
	@Parameter(required=true,defaultPrefix = BindingConstants.LITERAL)
	@Property
	private String identifier;
	
	
	/**
	 * The header of the page (placed on top of the page).
	 */
	@Parameter(required=true,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block header;
	
	/**
	 * The left panel content.
	 */
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block left;
	
	/**
	 * The right panel content.
	 */
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block right;
	
	/**
	 * The footer of the page (placed on the bottom of the page).
	 */
	@Parameter(required=true,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block footer;

	
	/**
	 * For including head inside &lt;head&gt; tag.
	 */
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block head;

	
	/**
	 * <p>This block will be display under the header to the right of the left panel.</p>
	 */
	@Parameter(required=false,defaultPrefix = BindingConstants.BLOCK)
	@Property
	private Block menuBar;
	
	/**
	 * MenuBar properties is true if and only if a menuBar was specified.
	 * In other words when null!=menuBar.
	 */
	@Property
	private Boolean menuBarEnable=false;

	
	@Property
	private Boolean leftEnable=false;

	@Property
	private Boolean rightEnable=false;

	@Property
	private Boolean sideEnabled=false;
	
	
	@Property
	private Boolean headContent=false;

	
	@Property
	private String documentId;
	
	@Property
	private String documentLayoutClass;
	
	@SetupRender
	public void initClassValues(){
		
		if(null!=head){
			headContent=true;
		}
		
		if(ALLPAGE.equalsIgnoreCase(pageSize)){
			documentId="doc3";
		}else if(MINIMUM.equalsIgnoreCase(pageSize)){
			documentId="doc";
		}else if(MEDIUM.equalsIgnoreCase(pageSize)){
			documentId="doc2";
		}else if(MAXIMUM.equalsIgnoreCase(pageSize)){
			documentId="doc4";
		}else{
			throw new TapestryException("The parameter pageSize accepts only those values:"+ALLPAGE+";"+MINIMUM+";"+MEDIUM+";"+MAXIMUM+". Current value : "+pageSize,null);
		}
		
		// for the moment, we don't have left and right
		if(null!=left){
			leftEnable=true;

			if(LEFT_MINIMUM.equalsIgnoreCase(leftSize)){
				documentLayoutClass="yui-t1";
			}else if(LEFT_MEDIUM.equalsIgnoreCase(leftSize)){
				documentLayoutClass="yui-t2";
			}else if(LEFT_HIGH.equalsIgnoreCase(leftSize)){
				documentLayoutClass="yui-t3";
			}else{
				throw new TapestryException("The parameter left accepts only those values:"+LEFT_MINIMUM+";"+LEFT_MEDIUM+";"+LEFT_HIGH+". Current value : "+leftSize,null);
			}
		} else {
			// if we don't have left, we may have right
			if(null!=right){
				rightEnable=true;

				if(RIGHT_MINIMUM.equalsIgnoreCase(rightSize)){
					documentLayoutClass="yui-t4";
				}else if(RIGHT_MEDIUM.equalsIgnoreCase(rightSize)){
					documentLayoutClass="yui-t5";
				}else if(RIGHT_HIGH.equalsIgnoreCase(rightSize)){
					documentLayoutClass="yui-t6";
				}else{
					throw new TapestryException("The parameter right accepts only those values:"+RIGHT_MINIMUM+";"+RIGHT_MEDIUM+";"+RIGHT_HIGH+". Current value : "+leftSize,null);
				}
			} else {
				documentLayoutClass = "yui-t7";
			}
		}
		if(null!=menuBar){
			menuBarEnable=true;
		}
		
		sideEnabled = leftEnable || rightEnable;
		
	}	
}
