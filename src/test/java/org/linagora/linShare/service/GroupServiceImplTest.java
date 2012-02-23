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

package org.linagora.linShare.service;

import java.security.acl.Owner;
import java.util.GregorianCalendar;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.common.service.LinShareMessageHandler;
import org.linagora.linShare.core.domain.constants.GroupMemberType;
import org.linagora.linShare.core.domain.constants.Language;
import org.linagora.linShare.core.domain.constants.LinShareTestConstants;
import org.linagora.linShare.core.domain.entities.Group;
import org.linagora.linShare.core.domain.entities.GroupMember;
import org.linagora.linShare.core.domain.entities.GroupUser;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.Role;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.GroupRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.GroupService;
import org.linagora.linShare.core.service.ShareService;
import org.linagora.linShare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.subethamail.smtp.server.SMTPServer;
import org.subethamail.wiser.Wiser;

/*
 * This all class was disable because of a huge spring context problem
 * */
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
public class GroupServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	private static Logger logger = LoggerFactory.getLogger(GroupServiceImplTest.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private ShareService shareService;
	
	@Autowired
	@Qualifier("groupRepository")
	private GroupRepository groupRepository;
	
	@Qualifier("userRepository")
	@Autowired
	private UserRepository<GroupUser> userRepository;
	
	private User owner; 
	
	private static String groupName = "testGroup";
	
	private Wiser wiser;
	
	
	public GroupServiceImplTest() {
		super();
        wiser = new Wiser(2525);
		
	}
	
	
	@Before
	public void setUp() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_SETUP);
		wiser.start();
//        MailTestUtils.reconfigureMailSenders(applicationContext, 2500);
		
		owner = userService.findOrCreateUser("user1@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
		
		
		// Create group
		Group group = new Group();
		
		GroupUser groupUser = new GroupUser(groupName.toLowerCase()+"@linshare.groups", "", groupName, groupName.toLowerCase()+"@linshare.groups");
		groupUser = userRepository.create(groupUser);
		
		//Set owner
		GroupMember groupOwner = new GroupMember();
		groupOwner.setType(GroupMemberType.OWNER);
		groupOwner.setUser(owner);
		groupOwner.setMembershipDate(GregorianCalendar.getInstance());
		
		group.addMember(groupOwner);
		group.setName(groupName);
		group.setGroupUser(groupUser);
		
		groupRepository.create(group);
		
		logger.debug(LinShareTestConstants.END_SETUP);
	}
	
	
	@After
	public void tearDown() throws Exception {
		logger.debug(LinShareTestConstants.BEGIN_TEARDOWN);
		
		Group groupPersistant = groupRepository.findByName("testGroup");
		
		// if this group is not already delete
		if(groupPersistant!=null){
			// clearing received shares
			Set<Share> receivedShare = groupPersistant.getGroupUser().getReceivedShares();
		
			// delete group and groupUser
			groupRepository.delete(groupPersistant);		
		
			// refresh share attribute
			for (Share share : receivedShare) {
				shareService.refreshShareAttributeOfDoc(share.getDocument());
			}
			receivedShare.clear();
		}
		wiser.stop();
		logger.debug(LinShareTestConstants.END_TEARDOWN);
	}
	
	@Test
	public void testCreate() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User owner2 = userService.findOrCreateUser("user2@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
		
		groupService.create(owner2, "testGroup2", "", "functionalEmail@email.com");
		
		Assert.assertNotNull(groupRepository.findByName("testGroup2"));
		
		Group group = new Group();
		group.setName("testGroup2");
		
		groupRepository.delete(group);
		
		Assert.assertNull(groupRepository.findByName("testGroup2"));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testDelete() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		groupService.delete(groupRepository.findByName(groupName), owner);
		
		Assert.assertNull(groupRepository.findByName(groupName));

		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testUpdate() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Group group = groupRepository.findByName(groupName);
		
		group.setName("fooBar");
		groupService.update(group, owner);
		
		Assert.assertNull(groupRepository.findByName(groupName));
		Assert.assertNotNull(groupRepository.findByName("fooBar"));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testRetreiveMember() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Group group = groupRepository.findByName(groupName);
		
		GroupMember member = new GroupMember();

		User user = userService.findOrCreateUser("user2@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
		
		member.setOwner(owner);
		member.setUser(user);
		
		group.addMember(member);
		
		groupService.update(group, owner);
		
		GroupMember groupMember = groupService.retreiveMember(group, user);
		
		Assert.assertTrue(groupMember.equals(member));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}	

	@Test
	public void testAddMember() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Group group = groupRepository.findByName(groupName);
		
		User newMember = userService.findOrCreateUser("user2@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
		
		groupService.addMember(group, owner, newMember, new MailContainer("", Language.DEFAULT));
		
		// test on user
		Assert.assertTrue(groupService.findByUser(newMember).contains(group));
		
		
		//test on group
		Set<GroupMember> members = group.getMembers();
		
		boolean haveNewMember = false;
		
		for (GroupMember groupMember : members) {
			if(groupMember.getUser().equals(newMember))
				haveNewMember = true;
		}
		Assert.assertTrue(haveNewMember);
		
		logger.debug(LinShareTestConstants.END_TEST);
	}	

	@Test
	public void testRemoveMember() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Group group = groupRepository.findByName(groupName);
		
		User newMember = userService.findOrCreateUser("user2@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
		
		groupService.addMember(group, owner, newMember, new MailContainer("", Language.DEFAULT));
		
		groupService.removeMember(group, owner, newMember);
		
		// test on user
		Assert.assertFalse(groupService.findByUser(newMember).contains(group));
		
		
		//test on group
		Set<GroupMember> members = group.getMembers();
		
		for (GroupMember groupMember : members) {
			Assert.assertFalse(groupMember.getUser().equals(newMember));
		}
		
		logger.debug(LinShareTestConstants.END_TEST);
	}	
	
	@Test
	public void testUpdateMember() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		Group group = groupRepository.findByName(groupName);
		
		User newMember = userService.findOrCreateUser("user2@linpki.org", LoadingServiceTestDatas.sqlSubDomain);
		
		groupService.addMember(group, owner, newMember, new MailContainer("", Language.DEFAULT));
		
		groupService.updateMember(group, owner, newMember, GroupMemberType.MEMBER);
		
		Assert.assertTrue(groupService.retreiveMember(group, newMember).getType().equals(GroupMemberType.MEMBER));
		
		groupService.updateMember(group, owner, newMember, GroupMemberType.MANAGER);
		
		Assert.assertTrue(groupService.retreiveMember(group, newMember).getType().equals(GroupMemberType.MANAGER));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	
	@Test
	public void testAcceptNewMember() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User newMember = userService.findOrCreateUser("user2@linpki.org", LoadingServiceTestDatas.sqlSubDomain);

		Group group = groupService.findByName(groupName);
		
		GroupMember groupMember = new GroupMember();
		
		groupMember.setUser(newMember);
		groupMember.setType(GroupMemberType.WAITING_APPROVAL);
		groupMember.setMembershipDate(GregorianCalendar.getInstance());
		groupMember.setOwner(owner);

		group.addMember(groupMember);
		
		groupService.acceptNewMember(group, owner, newMember,new MailContainer("", Language.DEFAULT));
		
		Assert.assertTrue(groupMember.getType().equals(GroupMemberType.MEMBER));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testRejectNewMember() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		User newMember = userService.findOrCreateUser("user2@linpki.org", LoadingServiceTestDatas.sqlSubDomain);

		Group group = groupService.findByName(groupName);
		
		GroupMember groupMember = new GroupMember();
		
		groupMember.setUser(newMember);
		groupMember.setType(GroupMemberType.WAITING_APPROVAL);
		groupMember.setMembershipDate(GregorianCalendar.getInstance());
		groupMember.setOwner(owner);

		group.addMember(groupMember);
		
		groupService.rejectNewMember(group, owner, newMember,new MailContainer("", Language.DEFAULT));
		
		Assert.assertTrue(groupMember.getType().equals(GroupMemberType.WAITING_APPROVAL));
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
	@Test
	public void testDeleteAllMembershipOfUser() throws BusinessException{
		logger.info(LinShareTestConstants.BEGIN_TEST);
		
		Group group = groupService.findByName(groupName);
		
		groupService.deleteAllMembershipOfUser(owner);
		
		Assert.assertTrue(group.getMembers().isEmpty());
		
		logger.debug(LinShareTestConstants.END_TEST);
	}
	
}
