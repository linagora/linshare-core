/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.batches.impl;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.SendFailedException;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareRecipientStatistic;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ParameterDto;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.job.quartz.SingleRunBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.hibernate.AnonymousShareEntryRepositoryImpl;
import org.linagora.linshare.core.repository.hibernate.ShareEntryRepositoryImpl;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.utils.Version;

import com.google.common.collect.Lists;

public class TopSharesMailNotificationBatchImpl extends GenericBatchImpl {

    public static final String DEFAULT_SENDER_MAIL = "no-reply@linshare.org";
    private final ShareEntryService shareEntryService;
    private final NotifierService notifierService;

    protected final AbstractDomainService abstractDomainService;
    private final ShareEntryRepositoryImpl shareRepository;
    private final AnonymousShareEntryRepositoryImpl anonymousShareRepository;
    private final String recipientsList;

    private final boolean jobActivated;

    public static final SimpleDateFormat DATE_FORMAT_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final SimpleDateFormat DATE_FORMAT_DAY = new SimpleDateFormat("yyyy-MM-dd");

    public TopSharesMailNotificationBatchImpl(
            final AccountRepository<Account> accountRepository,
            final ShareEntryService shareEntryService,
            final NotifierService notifierService,
            final AbstractDomainService abstractDomainService,
            final ShareEntryRepositoryImpl shareRepository,
            final AnonymousShareEntryRepositoryImpl anonymousShareRepository,
            final String recipientsList,
            final boolean topSharesNotificationActivated) {
        super(accountRepository);
        this.shareEntryService = shareEntryService;
        this.notifierService = notifierService;
        this.abstractDomainService = abstractDomainService;
        this.shareRepository = shareRepository;
        this.anonymousShareRepository = anonymousShareRepository;
        this.recipientsList = recipientsList;
        this.jobActivated = topSharesNotificationActivated;
    }

    @Override
    public List<String> getAll(BatchRunContext batchRunContext) {
        logger.info(getClass().toString() + " job starting ...");
        //It makes no sense to fetch each resource individually instead of doing it in a single request
        //So we are sending a single "fake" resource to trigger a single run
        return Lists.newArrayList("run_only_once_top_shared_mail_notification");
    }

    @Override
    public boolean needToRun() {
        return jobActivated;
    }

    @Override
    public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
            throws BatchBusinessException, BusinessException {
        ResultContext context = new SingleRunBatchResultContext(identifier);
        context.setProcessed(false);
        Map<String, File> csvFiles = null;
        try {
            console.logInfo(batchRunContext, total, position, identifier);

            if (StringUtils.isBlank(recipientsList)) {
                console.logError(batchRunContext, "No recipient mails set: please use parameter \"job.topSharesNotification.recipient.mails\" to add all comma separated recipient mails");
                throw new BusinessException(BusinessErrorCode.BATCH_FAILURE, "No recipient mails set.");
            }

            csvFiles = getCsvFiles(batchRunContext);
            sendNotification(csvFiles, batchRunContext);

            context.setProcessed(true);
        } catch (BusinessException businessException) {
            BatchBusinessException exception = new BatchBusinessException(context, "Error while creating top shares mail notification : " + businessException.getMessage());
            exception.setBusinessException(businessException);
            console.logError(batchRunContext, "Error while trying to create top shares mail notification", exception);
            throw exception;
        } finally {
            cleanTempFiles(batchRunContext, csvFiles);
        }
        return context;
    }

    @NotNull
    private Map<String, File> getCsvFiles(BatchRunContext batchRunContext) {
        Map<String, File> attachments = new HashMap<String, File>();
        try {
            File topSharesByFileSizeCsv = toCsv_TotalSize("Top_shares_by_file_size_" + getYesterdayDate() + ".csv",
                    shareEntryService.getTopSharesByFileSize(null, getYesterdayBegin(), getYesterdayEnd()));
            attachments.put(topSharesByFileSizeCsv.getName(), topSharesByFileSizeCsv);

            File topSharesByFileCountCsv = toCsv_Count("Top_shares_by_file_count_" + getYesterdayDate() + ".csv",
                    shareEntryService.getTopSharesByFileCount(null, getYesterdayBegin(), getYesterdayEnd()));
            attachments.put(topSharesByFileCountCsv.getName(), topSharesByFileCountCsv);

            File allSharesCsv = getAllSharesCsv("All_shares_" + getYesterdayDate() + ".csv",
                    getYesterdayBeginCalendar(), getYesterdayEndCalendar());
            attachments.put(allSharesCsv.getName(), allSharesCsv);
        } catch (Exception e) {
            cleanTempFiles(batchRunContext, attachments);
            throw e;
        }
        return attachments;
    }

    private File getAllSharesCsv(String filename, Calendar beginDate, Calendar endDate){
        File file = new File(filename);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("senderMail,senderName,senderUid,senderDomainName," +
                    "recipientMail,recipientType,recipientName,recipientUuid,recipientDomain," +
                    "fileName,fileUuid,fileSize (kB)," +
                    "timeStamp");

            anonymousShareRepository.findAllSharesInRange(beginDate, endDate).forEach(share -> {
                Account sender = share.getShareEntryGroup().getOwner();
                DocumentEntry document = share.getDocumentEntry();
                writer.println(String.join(",",sender.getMail(),sender.getFullName(),sender.getLsUuid(),sender.getDomain().getLabel(),
                share.getAnonymousUrl().getContact().getMail(),"external","","","",
                        document.getName(), document.getUuid(), Long.toString(document.getSize() / 1000L),
                        DATE_FORMAT_TIMESTAMP.format(share.getCreationDate().getTime())));
            });

            shareRepository.findAllSharesInRange(beginDate,endDate).forEach(share -> {
                Account sender = share.getShareEntryGroup().getOwner();
                User recipient = share.getRecipient();
                DocumentEntry document = share.getDocumentEntry();
                writer.println(String.join(",",sender.getMail(),sender.getFullName(),sender.getLsUuid(),sender.getDomain().getLabel(),
                        recipient.getMail(),"internal",recipient.getFullName(),recipient.getLsUuid(),recipient.getDomain().getLabel(),
                        document.getName(), document.getUuid(), Long.toString(document.getSize() / 1000L),
                        DATE_FORMAT_TIMESTAMP.format(share.getCreationDate().getTime())));
            });
            return file;
        } catch (Exception e) {
            throw new BusinessException(BusinessErrorCode.BATCH_FAILURE, "Error while writing the complete shares CSV", e);
        }
    }

    private void sendNotification(Map<String, File> csvFiles, BatchRunContext batchRunContext) {
        String sender = getSenderMail();
        Map<String, DataSource> attachments = csvFiles.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, entry -> new FileDataSource(entry.getValue())));

        getRecipients().forEach(recipient -> {
            try {
                notifierService.sendNotification(sender, null, recipient, getSubject(), getBody(),
                        null, null, attachments);
            } catch (SendFailedException e) {
                console.logError(batchRunContext, "Error while trying to send notification to " + recipient, e);
            }
        });
    }

    private String getSenderMail() {
        return abstractDomainService.getUniqueRootDomain().getFunctionalities()
                .stream()
                .filter(func -> func.equalsIdentifier(FunctionalityNames.DOMAIN__MAIL))
                .map(func -> func.getParameters(Version.V6).stream().findFirst().orElse(new ParameterDto("")).getString())
                .filter(mail -> !StringUtils.isBlank(mail))
                .findFirst().orElse(DEFAULT_SENDER_MAIL);
    }

    private File toCsv_Count(String filename, List<ShareRecipientStatistic> topShares) throws BusinessException {
        File csvOutputFile = new File(filename);
        try (PrintWriter writer = new PrintWriter(csvOutputFile)) {
            writer.println(ShareRecipientStatistic.getCsvHeader_Count());
            topShares.stream()
                    .map(ShareRecipientStatistic::toCsvLine_Count)
                    .forEach(writer::println);
            return csvOutputFile;
        } catch (Exception e) {
            throw new BusinessException(BusinessErrorCode.BATCH_FAILURE, "Error while writing the top shares CSV", e);
        }
    }

    private File toCsv_TotalSize(String filename, List<ShareRecipientStatistic> topShares) throws BusinessException {
        File csvOutputFile = new File(filename);
        try (PrintWriter writer = new PrintWriter(csvOutputFile)) {
            writer.println(ShareRecipientStatistic.getCsvHeader_TotalSize());
            topShares.stream()
                    .map(ShareRecipientStatistic::toCsvLine_TotalSize)
                    .forEach(writer::println);
            return csvOutputFile;
        } catch (Exception e) {
            throw new BusinessException(BusinessErrorCode.BATCH_FAILURE, "Error while writing the top shares CSV", e);
        }
    }

    private void cleanTempFiles(BatchRunContext batchRunContext, Map<String, File> csvFiles) {
        if (csvFiles != null) {
            csvFiles.forEach((fileName, file) -> {
                if (file != null && !file.delete()) {
                    console.logError(batchRunContext, "Could not delete temporary file : ", fileName);
                }
            });
        }
    }

    @Override
    public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
        console.logInfo(batchRunContext, total, position, "Top shares mail notification have been successfully sent");
    }

    @Override
    public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
        console.logError(batchRunContext, total, position, "creating top shares mail notification : " + exception.getContext().getIdentifier());
    }

    private String getYesterdayDate() {
        return DATE_FORMAT_DAY.format(getYesterdayBeginCalendar().getTime());
    }

    private String getYesterdayBegin() {
        return DATE_FORMAT_TIMESTAMP.format(getYesterdayBeginCalendar().getTime());
    }

    @NotNull
    private static Calendar getYesterdayBeginCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(GregorianCalendar.DATE, -1);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
        calendar.set(GregorianCalendar.MINUTE, 0);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar;
    }

    private String getYesterdayEnd() {
        return DATE_FORMAT_TIMESTAMP.format(getYesterdayEndCalendar().getTime());
    }

    @NotNull
    private static Calendar getYesterdayEndCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(GregorianCalendar.DATE, -1);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
        calendar.set(GregorianCalendar.MINUTE, 59);
        calendar.set(GregorianCalendar.SECOND, 59);
        calendar.set(GregorianCalendar.MILLISECOND, 999);
        return calendar;
    }

    public List<String> getRecipients() {
        return Arrays.stream(recipientsList.replaceAll("\\s", "")
                        .split(","))
                .collect(Collectors.toList());
    }

    private String getSubject() {
        return "Rapport de partages Linshare du " + getYesterdayDate() + "";
    }

    public String getBody() {
        return "<!DOCTYPE html>\n" +
                "<body>\n" +
                "<div>\n" +
                "<h2>Rapport de partages Linshare du " + getYesterdayDate() + "</h2>\n" +
                "<p>Bonjour,</p>\n" +
                "<p>Vous trouverez en pièce jointe les rapports du " + getYesterdayBegin() + " au " + getYesterdayEnd() + " sur les partages de fichier dans Linshare. Le rapport se compose des fichiers au format csv suivants :</p>\n" +
                "<ol>\n" +
                "<li>Utilisateurs aillant reçu le plus grand nombre de fichiers</li>\n" +
                "<li>Utilisateurs aillant reçu le plus grand volume de fichiers (somme de la taille des fichiers)</li>\n" +
                "<li>Liste exhaustive des échanges</li>\n" +
                "</ol>\n" +
                "\n" +
                "<p>Ce message est automatique, merci de ne pas répondre. En cas de problème ou question merci de contacter votre administrateur Linshare.</p>" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }
}