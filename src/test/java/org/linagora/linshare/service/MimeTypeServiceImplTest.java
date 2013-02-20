package org.linagora.linshare.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linshare.core.domain.constants.LinShareTestConstants;
import org.linagora.linshare.core.domain.entities.AllowedMimeType;
import org.linagora.linshare.core.domain.entities.MimeTypeStatus;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AllowedMimeTypeRepository;
import org.linagora.linshare.core.service.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-business-service.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopends.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class MimeTypeServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{

	private static Logger logger = LoggerFactory.getLogger(MimeTypeServiceImplTest.class);
	
	@Autowired
	private MimeTypeService mimeTypeService;
	
	@Autowired
	private AllowedMimeTypeRepository allowedMimeTypeRepository;
	
	@Before
	public void setUp() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	
	@After
	public void tearDown() throws BusinessException {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}

	/**
	 * All @Ignore are caused by the hibernate file AllowedMimeType which is not load correctly
	 * 
	 */
	@Test
	public void testCreateAllowedMimeType() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AllowedMimeType mime1 = new AllowedMimeType((long)1,"zip","zip",MimeTypeStatus.AUTHORISED);
		AllowedMimeType mime2 = new AllowedMimeType((long)2,"rar","rar",MimeTypeStatus.AUTHORISED);
		
		List<AllowedMimeType> newlist = new ArrayList<AllowedMimeType>();
		
		newlist.add(mime1);
		newlist.add(mime2);
		
		mimeTypeService.createAllowedMimeType(newlist);
		
		Assert.assertTrue(mimeTypeService.getAllowedMimeType().size() == newlist.size());
		Assert.assertTrue(mimeTypeService.getAllowedMimeType().get(0) == newlist.get(0));
		Assert.assertTrue(mimeTypeService.getAllowedMimeType().get(1) == newlist.get(1));

		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testIsAllowed() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AllowedMimeType mime1 = new AllowedMimeType((long)1,"zip","zip",MimeTypeStatus.AUTHORISED);
		List<AllowedMimeType> newlist = new ArrayList<AllowedMimeType>();
		newlist.add(mime1);	
		
		mimeTypeService.createAllowedMimeType(newlist);
		
		Assert.assertTrue(mimeTypeService.isAllowed(mime1.getMimetype()));
		logger.debug(LinShareTestConstants.END_TEST);

	}
	
	@Test
	public void testGiveStatus() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AllowedMimeType mime1 = new AllowedMimeType((long)1,"zip","zip",MimeTypeStatus.AUTHORISED);
		List<AllowedMimeType> newlist = new ArrayList<AllowedMimeType>();
		newlist.add(mime1);	
		
		mimeTypeService.createAllowedMimeType(newlist);
		
		Assert.assertTrue(mimeTypeService.giveStatus(mime1.getMimetype()) == MimeTypeStatus.AUTHORISED);
		logger.debug(LinShareTestConstants.END_TEST);

	}
	
	@Test
	public void testSaveOrUpdateAllowedMimeType() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		AllowedMimeType mime1 = new AllowedMimeType((long)1,"zip","zip",MimeTypeStatus.AUTHORISED);
		List<AllowedMimeType> newlist = new ArrayList<AllowedMimeType>();
		newlist.add(mime1);	
		mimeTypeService.saveOrUpdateAllowedMimeType(newlist);
		
		Assert.assertTrue(mimeTypeService.getAllowedMimeType().get(0) == newlist.get(0));
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testgetAllSupportedMimeType() throws BusinessException {
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Assert.assertTrue(mimeTypeService.getAllSupportedMimeType().size()>0);
		logger.debug(LinShareTestConstants.END_TEST);
	}

}
