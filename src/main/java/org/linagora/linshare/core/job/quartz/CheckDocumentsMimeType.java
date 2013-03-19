/**
 * 
 */
package org.linagora.linshare.core.job.quartz;

import org.linagora.linshare.core.batches.DocumentManagementBatch;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author nbertrand
 *
 */
public class CheckDocumentsMimeType extends QuartzJobBean {
	
    private DocumentManagementBatch batch;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        batch.checkDocumentsMimeType();
    }

    public void setBatch(DocumentManagementBatch documentManagementBatch) {
        this.batch = documentManagementBatch;
    }
}
