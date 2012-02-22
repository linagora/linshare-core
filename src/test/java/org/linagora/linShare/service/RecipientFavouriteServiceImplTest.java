package org.linagora.linShare.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.core.domain.constants.LinShareTestConstants;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.LinShareNotSuchElementException;
import org.linagora.linShare.core.repository.AbstractDomainRepository;
import org.linagora.linShare.core.repository.DomainPolicyRepository;
import org.linagora.linShare.core.repository.FunctionalityRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.repository.hibernate.RecipientFavouriteRepository;
import org.linagora.linShare.core.service.RecipientFavouriteService;
import org.linagora.linShare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:springContext-datasource.xml",
		"classpath:springContext-repository.xml",
		"classpath:springContext-service.xml",
		"classpath:springContext-dao.xml",
		"classpath:springContext-facade.xml",
		"classpath:springContext-startopends.xml",
		"classpath:springContext-jackRabbit.xml",
		"classpath:springContext-test.xml"
		})
public class RecipientFavouriteServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{
	private static Logger logger = LoggerFactory.getLogger(RecipientFavouriteServiceImplTest.class);

	@Autowired
	private RecipientFavouriteService recipientFavouriteService;
	
	@Qualifier("recipientFavouriteRepository")
	@Autowired
	private RecipientFavouriteRepository favouriteRepository;	
	
	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private AbstractDomainRepository abstractDomainRepository;
	
	@Autowired
	private DomainPolicyRepository domainPolicyRepository;
	
	@Qualifier("userRepository")
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	private LoadingServiceTestDatas datas;

	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		datas = new LoadingServiceTestDatas(functionalityRepository,abstractDomainRepository,domainPolicyRepository,userRepository,userService);
		datas.loadUsers();
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		datas.deleteUsers();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	
	@Test
	public void testIncrement() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = datas.getUser1();
		
		String user2 = datas.getUser2().getLogin();
		String user3 = datas.getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user2);
		recipients.add(user3);

		recipientFavouriteService.increment(owner, recipients);
		
		Assert.assertTrue(favouriteRepository.existFavourite(owner, user2));
		Assert.assertTrue(favouriteRepository.existFavourite(owner, user3));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testRecipientsOrderedByWeightDesc() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = datas.getUser1();
		
		String user2 = datas.getUser2().getLogin();
		String user3 = datas.getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user2);
		recipients.add(user3);
			
		favouriteRepository.incAndCreate(recipients,owner);
	
		favouriteRepository.inc(user3, owner);

		List<String> elementsOrderByWeightDesc = favouriteRepository.getElementsOrderByWeight(owner);
		Assert.assertTrue(elementsOrderByWeightDesc.get(1).equals(user3));
		Assert.assertTrue(elementsOrderByWeightDesc.get(0).equals(user2));
		
		List<String> recipientsOrderedByWeightDesc = recipientFavouriteService.recipientsOrderedByWeightDesc(owner);
		Assert.assertTrue(recipientsOrderedByWeightDesc.get(1).equals(user2));
		Assert.assertTrue(recipientsOrderedByWeightDesc.get(0).equals(user3));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testReorderRecipientsByWeightDesc() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = datas.getUser1();
		
		String user2 = datas.getUser2().getLogin();
		String user3 = datas.getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user2);
		recipients.add(user3);

		favouriteRepository.incAndCreate(recipients,owner);
		
		favouriteRepository.inc(user3, owner);
		
		List<String> recipientsOrderedByWeightDesc = recipientFavouriteService.reorderRecipientsByWeightDesc(recipients,owner);
		Assert.assertTrue(recipientsOrderedByWeightDesc.get(0).equals(user3));
		Assert.assertTrue(recipientsOrderedByWeightDesc.get(1).equals(user2));
		
		List<String> elementsOrderByWeightDesc = favouriteRepository.getElementsOrderByWeightDesc(owner);
		Assert.assertTrue(elementsOrderByWeightDesc.get(0).equals(user3));
		Assert.assertTrue(elementsOrderByWeightDesc.get(1).equals(user2));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testFindRecipientFavorite() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);

		User owner = datas.getUser1();
		
		String user2 = datas.getUser2().getLogin();
		String user3 = datas.getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user2);
		recipients.add(user3);

		favouriteRepository.incAndCreate(recipients,owner);
		
		favouriteRepository.inc(user3, owner);
		
		Assert.assertFalse(recipientFavouriteService.findRecipientFavorite( datas.getUser3().getMail(), owner).isEmpty());
		
		Assert.assertTrue(recipientFavouriteService.findRecipientFavorite( "failMail@mail.com", owner).isEmpty());
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testDeleteFavoritesOfUser() throws LinShareNotSuchElementException, BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User owner = datas.getUser1();
		
		String user2 = datas.getUser2().getLogin();
		String user3 = datas.getUser3().getLogin();
		
		List<String> recipients = new ArrayList<String>();
		recipients.add(user3);

		favouriteRepository.incAndCreate(recipients,owner);
		
		Assert.assertFalse(recipientFavouriteService.findRecipientFavorite(datas.getUser3().getMail(), owner).isEmpty());
		
		recipientFavouriteService.deleteFavoritesOfUser(owner);
		
		Assert.assertTrue(recipientFavouriteService.findRecipientFavorite( datas.getUser3().getMail(), owner).isEmpty());

		logger.debug(LinShareTestConstants.END_TEST);

	}
}
