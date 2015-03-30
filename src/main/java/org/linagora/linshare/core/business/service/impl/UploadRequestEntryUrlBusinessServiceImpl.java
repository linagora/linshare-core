package org.linagora.linshare.core.business.service.impl;

import java.util.Date;

import org.linagora.linshare.core.business.service.UploadRequestEntryUrlBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.UploadRequestEntryUrlRepository;
import org.linagora.linshare.core.service.PasswordService;
import org.linagora.linshare.core.utils.HashUtils;

public class UploadRequestEntryUrlBusinessServiceImpl implements
		UploadRequestEntryUrlBusinessService {

	private final UploadRequestEntryUrlRepository uploadRequestEntryUrlRepository;

	private final PasswordService passwordService;

	private final AccountRepository<Account> accountRepository;

	private final String path;

	public UploadRequestEntryUrlBusinessServiceImpl(final UploadRequestEntryUrlRepository uploadRequestEntryUrlRepository, final PasswordService passwordService, final String baseUrl, final AccountRepository<Account> accountRepository) {
		super();
		this.uploadRequestEntryUrlRepository = uploadRequestEntryUrlRepository;
		this.passwordService = passwordService;
		this.path = baseUrl;
		this.accountRepository = accountRepository;
	}

	@Override
	public UploadRequestEntryUrl findByUuid(String uuid)
			throws BusinessException {
		return uploadRequestEntryUrlRepository.findByUuid(uuid);
	}

	@Override
	public UploadRequestEntryUrl create(UploadRequestEntry requestEntry,
			Boolean passwordProtected, Date expiryDate) throws BusinessException {
		UploadRequestEntryUrl UREUrl =  new UploadRequestEntryUrl(requestEntry, path);
		UREUrl.setExpiryDate(expiryDate);
		if(passwordProtected) {
			String password = passwordService.generatePassword();
			// We store it temporary in this object for mail notification.
			UREUrl.setTemporaryPlainTextPassword(password);
			UREUrl.setPassword(HashUtils.hashSha1withBase64(password.getBytes()));
		}
		return uploadRequestEntryUrlRepository.create(UREUrl);
	}

	@Override
	public UploadRequestEntryUrl update(UploadRequestEntryUrl url)
			throws BusinessException {
		return uploadRequestEntryUrlRepository.update(url);
	}

	@Override
	public void delete(UploadRequestEntryUrl url) throws BusinessException {
		uploadRequestEntryUrlRepository.delete(url);
	}

	@Override
	public boolean isValidPassword(UploadRequestEntryUrl uploadRequestEntryUrl, String password) {
		if (uploadRequestEntryUrl == null) throw new IllegalArgumentException("uploadRequestEntry url cannot be null");

		// Check password validity
		if (password != null) {
			String hashedPassword = HashUtils.hashSha1withBase64(password.getBytes());
			return hashedPassword.equals(uploadRequestEntryUrl.getPassword());
		}
		return true;
	}

	@Override
	public boolean isExpired(UploadRequestEntryUrl uploadRequestEntryUrl) {
		if (uploadRequestEntryUrl == null)
			throw new IllegalArgumentException("UploadRequestEntryUrl url cannot be null");
		Date now = new Date();
		Date expiryDate = uploadRequestEntryUrl.getExpiryDate();
		return now.after(expiryDate);
	}

	@Override
	public SystemAccount getUploadRequestEntryURLAccount() {
		return accountRepository.getUploadRequestSystemAccount();
	}

	@Override
	public UploadRequestEntryUrl find(UploadRequestEntry entry)
			throws BusinessException {
		return uploadRequestEntryUrlRepository.findByUploadRequestEntry(entry);
	}
}
