package com.oozie.wfsubmit;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.WorkflowJob;

import com.oozie.wfmanger.WfManagerImpl;

public class OzzieWfSubmit {

	private static final Logger LOGGER = Logger.getLogger(WfManagerImpl.class);
	private static final String CONTEXT = "context";
	/**
	 * Method to submit Oozie Workflow
	 * @return
	 */
	public String submitOozieWorkflow() {
		
		LOGGER.info("Reached Oozie");
		OozieClient wc = new OozieClient("<oozie client>:11000/oozie/");
		
		Properties conf = wc.createConfiguration();
		// setting workflow parameters
		conf.setProperty(OozieClient.APP_PATH,"<HDFS path>/workflow.xml");
		conf.setProperty(OozieClient.LIBPATH,"/user/oozie/share/lib/lib_20151022151200/;/user/abcd/oozieSpark/lib/");
		conf.setProperty(OozieClient.USE_SYSTEM_LIBPATH, "true");
		String jobId = null;
		try {
			// submit and start the workflow job
			jobId = wc.run(conf);
			LOGGER.info("Workflow job submitted " + jobId);
			// wait until the workflow job finishes printing the status every 10
			// seconds
			while (wc.getJobInfo(jobId).getStatus() == WorkflowJob.Status.RUNNING) {
				LOGGER.info("Workflow job running ...");
				Thread.sleep(10 * 1000);
			}
			// print the final status o the workflow job
			LOGGER.info("Workflow job completed ...");
			LOGGER.info(wc.getJobInfo(jobId));
			// return jobId;

		} catch (Exception e) {
			LOGGER.info(CONTEXT, e);
		}
		return jobId;

	}
}
