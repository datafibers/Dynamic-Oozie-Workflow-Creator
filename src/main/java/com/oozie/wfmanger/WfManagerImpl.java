package com.oozie.wfmanger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.oozie.components.OozieDynamicWorkFlowGenerator;
import com.oozie.components.WorkFlowApp;
import com.oozie.wfsubmit.OzzieWfSubmit;

public class WfManagerImpl implements WfManager {

	private static final Logger LOGGER = Logger.getLogger(WfManagerImpl.class);
	private static final String CONTEXT = "context";

	public String generateWorkFlow() throws IOException {

		OozieDynamicWorkFlowGenerator dynWfGen = new OozieDynamicWorkFlowGenerator(
				"${jobTracker}", "${nameNode}");

		LOGGER.info("Create dynamic oozie workflow.XML and write to HDFS");
		Boolean workflowGenStatus = dynWfGen.createWorkFlowApp();

		if (workflowGenStatus) {
			JAXBContext jc;
			try {
				jc = JAXBContext.newInstance(WorkFlowApp.class,
						com.oozie.components.ActionNode.class);
				Marshaller marshaller;
				marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
						Boolean.TRUE);
				JAXBElement<WorkFlowApp> jaxbElem = new JAXBElement<WorkFlowApp>(
						new QName("uri:oozie:workflow:0.2", "workflow-app"),
						WorkFlowApp.class, dynWfGen.getWorkFlowApp());

				// Source workflow.xml file location
				File file = new File("<Source local File Path>"); // e.g :file:///app/data/abcd/spark/workflow.xml
				marshaller.marshal(jaxbElem, file);
				LOGGER.info("Workflow generation success");

				LOGGER.info("Delete exsisting workflow.xml from target HDFS location.");
				DeleteFromHdfs("<Target HDFS file path >"); // e.g hdfs://namenode/user/abdc/oozieSpark/workflow.xml");

				LOGGER.info("Write to HFDS");
				writeToHdfs("<Source local file location>",
						"<taget HDFS file location>");

				LOGGER.info("Triger oozie spark workflow ");
				String jobId = initateOozieWithGeneratedWorkflow();

				LOGGER.info("job id to track:" + jobId);

			} catch (JAXBException e) {
				LOGGER.info(CONTEXT, e);
			} catch (Exception e) {
				LOGGER.info(CONTEXT, e);
			}
		} else {
			return "Workflow Generation Failed.";
		}

		return "Workflow generated and passed for further processing.";
	}
	/**
	 * Method to initiate Oozie workflow
	 * @return jobID
	 */
	private String initateOozieWithGeneratedWorkflow() {
		OzzieWfSubmit submit = new OzzieWfSubmit();
		String jobID = submit.submitOozieWorkflow();
		return jobID;
	}

	/**
	 * Method to write file into HDFS
	 * @param inputPathLocation
	 * @param outputLoaction
	 * @throws IOException
	 */
	private void writeToHdfs(String inputPathLocation, String outputLoaction)
			throws IOException {

		BufferedReader inputReader = null;
		FileSystem fs = null;
		OutputStream outStream = null;
		BufferedWriter br = null;

		try {

			Configuration conf = new Configuration();
			conf.addResource(new org.apache.hadoop.fs.Path(
					"/etc/hadoop/conf/core-site.xml"));
			conf.addResource(new org.apache.hadoop.fs.Path(
					"/etc/hadoop/conf/hdfs-site.xml"));
			conf.setBoolean("fs.hdfs.impl.disable.cache", true);

			Path inputPath = new Path(inputPathLocation);
			Path outputPath = new Path(outputLoaction);

			FileSystem hdfs = FileSystem.get(conf);

			hdfs.copyFromLocalFile(inputPath, outputPath);

		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} finally {
			if (null != inputReader) {
				inputReader.close();
			}
			if (null != br) {
				br.close();
			}
			if (null != fs) {
				fs.close();
			}
			if (null != outStream) {
				outStream.close();
			}
		}
	}

	/**
	 * Method to delete file from HDFS
	 * @param fileName
	 * @throws IOException
	 */
	private void DeleteFromHdfs(String fileName) throws IOException {
		Configuration conf = new Configuration();
		conf.addResource(new org.apache.hadoop.fs.Path(
				"/etc/hadoop/conf/core-site.xml"));
		conf.addResource(new org.apache.hadoop.fs.Path(
				"/etc/hadoop/conf/hdfs-site.xml"));
		conf.setBoolean("fs.hdfs.impl.disable.cache", true);
		FileSystem hdfs = FileSystem.get(conf);
		hdfs.delete(new Path(fileName), true);
	}
}
