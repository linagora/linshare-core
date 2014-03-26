package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailContentType;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.MailLayout;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

/**
 * @author nbertrand
 */
public interface MailConfigService {

	/**
	 * Find all mail configurations visible by this domain.
	 * 
	 * @param domainId
	 * @return
	 */
	List<MailConfig> findAllConfigs(User actor, String domainId)
			throws BusinessException;

	/**
	 * Find a mail configuration.
	 * 
	 * @param uuid
	 * @return
	 */
	MailConfig findConfigByUuid(User actor, String uuid)
			throws BusinessException;

	/**
	 * Create a new mail configuration.
	 * 
	 * @param config
	 * @throws BusinessException
	 */
	void createConfig(User actor, MailConfig config) throws BusinessException;

	/**
	 * Update an existing mail configuration.
	 * 
	 * @param config
	 * @throws BusinessException
	 */
	void updateConfig(User actor, MailConfig config) throws BusinessException;

	/**
	 * Delete an existing mail configuration if it's not currently in use.
	 * 
	 * @param uuid
	 * @throws BusinessException
	 */
	void deleteConfig(User actor, String uuid) throws BusinessException;

	/**
	 * Find all mail contents visible by this domain.
	 * 
	 * @param domainId
	 * @return
	 */
	List<MailContent> findAllContents(User actor, String domainId)
			throws BusinessException;

	/**
	 * Find a mail content.
	 * 
	 * @param uuid
	 * @return
	 */
	MailContent findContentByUuid(User actor, String uuid)
			throws BusinessException;

	/**
	 * Find a mail content used by a mail configuration by its language and
	 * type.
	 * 
	 * @param config
	 * @param lang
	 * @param type
	 * @return
	 */
	MailContent findContentFromDomain(User actor, String domainId, Language lang,
			MailContentType type) throws BusinessException;

	/**
	 * Create a new mail content.
	 * 
	 * @param content
	 * @throws BusinessException
	 */
	void createContent(User actor, MailContent content)
			throws BusinessException;

	/**
	 * Update an existing mail content.
	 * 
	 * @param content
	 * @throws BusinessException
	 */
	void updateContent(User actor, MailContent content)
			throws BusinessException;

	/**
	 * Delete an existing mail content.
	 * 
	 * @param uuid
	 * @throws BusinessException
	 */
	void deleteContent(User actor, String uuid) throws BusinessException;

	/**
	 * Find all mail footers visible by this domain.
	 * 
	 * @param domainId
	 * @return
	 */
	List<MailFooter> findAllFooters(User actor, String domainId)
			throws BusinessException;

	/**
	 * Find a mail footer.
	 * 
	 * @param uuid
	 * @return
	 */
	MailFooter findFooterByUuid(User actor, String uuid)
			throws BusinessException;

	/**
	 * Create a new mail footer.
	 * 
	 * @param footer
	 * @throws BusinessException
	 */
	void createFooter(User actor, MailFooter footer) throws BusinessException;

	/**
	 * Update an existing mail footer.
	 * 
	 * @param footer
	 * @throws BusinessException
	 */
	void updateFooter(User actor, MailFooter footer) throws BusinessException;

	/**
	 * Delete an existing mail footer.
	 * 
	 * @param uuid
	 * @throws BusinessException
	 */
	void deleteFooter(User actor, String uuid) throws BusinessException;

	/**
	 * Find all mail layouts visible by this domain.
	 * 
	 * @param domainId
	 * @return
	 */
	List<MailLayout> findAllLayouts(User actor, String domainId)
			throws BusinessException;

	/**
	 * Find a mail layout.
	 * 
	 * @param uuid
	 * @return
	 */
	MailLayout findLayoutByUuid(User actor, String uuid)
			throws BusinessException;

	/**
	 * Create a new mail layout.
	 * 
	 * @param layout
	 * @throws BusinessException
	 */
	void createLayout(User actor, MailLayout layout) throws BusinessException;

	/**
	 * Update an existing mail layout.
	 * 
	 * @param layout
	 * @throws BusinessException
	 */
	void updateLayout(User actor, MailLayout layout) throws BusinessException;

	/**
	 * Delete an existing mail layout.
	 * 
	 * @param uuid
	 * @throws BusinessException
	 */
	void deleteLayout(User actor, String uuid) throws BusinessException;

	/**
	 * Check permissions for this user on a mail configuration.
	 * 
	 * @param actor
	 * @param config
	 * @return
	 */
	boolean hasRights(User actor, MailConfig config);

	/**
	 * Check permissions for this user on a mail content.
	 * 
	 * @param actor
	 * @param content
	 * @return
	 */
	boolean hasRights(User actor, MailContent content);

	/**
	 * Check permissions for this user on a mail footer.
	 * 
	 * @param actor
	 * @param footer
	 * @return
	 */
	boolean hasRights(User actor, MailFooter footer);

	/**
	 * Check permissions for this user on a mail layout.
	 * 
	 * @param actor
	 * @param layout
	 * @return
	 */
	boolean hasRights(User actor, MailLayout layout);
}
