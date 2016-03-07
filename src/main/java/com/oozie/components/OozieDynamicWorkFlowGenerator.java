package com.oozie.components;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

public class OozieDynamicWorkFlowGenerator {

	String jobTracker;
	String nameNode;
	OozieNodeFactory oozieNodeFactory;
	WorkFlowApp workFlowApp;
	Integer outputFileTypeId;
	Integer retailerId;
	Integer scheduleId;
	Integer retailerServiceID;
	String outputPath = new String();
	String outputFinaldir = new String();
	String inputfileConfiguration = new String();
	String mappingRuleAttributeValue = new String();
	String mappingConditionConfigValue = new String();
	String initialInputDir = new String();
	String initalLayoutPath = new String();
	String preprocessPath = new String();

	
	static final String nameService = "hdfs://nameservice/";

	public static void main(String[] args) throws JAXBException,
			FileNotFoundException {
		OozieDynamicWorkFlowGenerator dynWfGen = new OozieDynamicWorkFlowGenerator(
				"${jobTracker}", "${nameNode}");

		JAXBContext jc = JAXBContext.newInstance(WorkFlowApp.class,
				com.oozie.components.ActionNode.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		JAXBElement<WorkFlowApp> jaxbElem = new JAXBElement<WorkFlowApp>(
				new QName("uri:oozie:workflow:0.2", "workflow-app"),
				WorkFlowApp.class, dynWfGen.getWorkFlowApp());

	}
	/**
	 * constructor
	 * @param jobTracker
	 * @param nameNode
	 */
	public OozieDynamicWorkFlowGenerator(String jobTracker, String nameNode) {
		oozieNodeFactory = new OozieNodeFactory();
		workFlowApp = oozieNodeFactory.createWorkFlowApp();
		this.jobTracker = jobTracker;
		this.nameNode = nameNode;
	}
	/**
	 * constructor
	 * @return
	 */
	public WorkFlowApp getWorkFlowApp() {
		return workFlowApp;
	}

	public Boolean createWorkFlowApp() {

		// int numberOfJobs = 3;
		int numberOfJobs = 1;
		List<String> jobNames = new ArrayList<String>();
		for (int i = 0; i < numberOfJobs; i++) {
			jobNames.add("SparkJob_" + Integer.toString(i));
		}
		outputPath = "";
		String jobName = "sparkJob";
		workFlowApp.setName("spark-wf");
		// addStartNode("forking");
		addStartNode(jobName);
		// addFork(jobNames);
		// addJavaAction();		
		addSparkAction(jobName);
		// addJoin();
		addKill();
		addEndNode("end");
		return true;
	}
	/**
	 * Add fork actions
	 * @param jobNames
	 */
	private void addFork(List<String> jobNames) {
		Fork fork = oozieNodeFactory.createFork();
		fork.setName("forking");
		List<ForkTransition> forkTransitions = new ArrayList<ForkTransition>();

		for (String jobName : jobNames) {
			ForkTransition forkTransition = new ForkTransition();
			forkTransition.setStart(jobName);
			forkTransitions.add(forkTransition);
		}
		fork.setPath(forkTransitions);
		workFlowApp.getDecisionOrForkOrJoin().add(fork);
	}
	/**
	 * Add join node
	 */
	private void addJoin() {
		Join join = oozieNodeFactory.createJoin();
		join.setName("joining");
		join.setTo("end");
		workFlowApp.getDecisionOrForkOrJoin().add(join);
	}
	/**
	 * Kill node
	 */
	private void addKill() {
		Kill kill = oozieNodeFactory.createKill();
		kill.setName("fail");
		kill.setMessage("Java failed, error message[${wf:errorMessage(wf:lastErrorNode())}]");
		workFlowApp.getDecisionOrForkOrJoin().add(kill);
	}
	/**
	 * Method to add java action
	 */
	private void addJavaAction() {
		ActionNode action = oozieNodeFactory.createActionNode();
		action.setName("java");
		JavaAction javaAction = oozieNodeFactory.createJavaAction();
		javaAction.setNameSpace("uri:oozie:spark-action:0.1");
		javaAction.setJobTracker(jobTracker);
		javaAction.setNameNode(nameNode);
		setOkTransition(action, "new");
		setFailTransition(action, "Kill");
		action.setJava(javaAction);
		workFlowApp.getDecisionOrForkOrJoin().add(action);
	}
	/**
	 * Method to add spark actions
	 * @param jobNames
	 */
	private void addSparkAction(List<String> jobNames) {
		for (String jobName : jobNames) {
			ActionNode action = oozieNodeFactory.createActionNode();
			action.setName(jobName);
			SparkAction sparkAction = oozieNodeFactory.createSparkAction();
			sparkAction.setNameSpace("uri:oozie:spark-action:0.1");
			sparkAction.setJobTracker("<job tracker>");
			sparkAction	.setNameNode("<name node>");
			sparkAction.setMaster("yarn-cluster");
			
			sparkAction.setName("SparkDynamicOozieWorkFlow");
			sparkAction.setClassName("<spark main class name >");
			sparkAction.setJarName("<Spark main class jar name>");
			sparkAction.setSparkOpts("--jars <hdfs jar location> --num-executors 384");
			List<String> arguments = new ArrayList<String>();
			arguments.add("<argument one>");
			arguments.add("<argument two>");
			sparkAction.setArgument(arguments);
			setOkTransition(action, "joining");
			setFailTransition(action, "fail");
			action.setSpark(sparkAction);
			workFlowApp.getDecisionOrForkOrJoin().add(action);
		}		
		
	}

	/**
	 * Method to add spark action
	 * @param jobName
	 */
	private void addSparkAction(String jobName) {
		ActionNode action = oozieNodeFactory.createActionNode();
		action.setName(jobName);
		SparkAction sparkAction = oozieNodeFactory.createSparkAction();
		sparkAction.setNameSpace("uri:oozie:spark-action:0.1");
		sparkAction.setJobTracker("<Job tracker>");
		sparkAction.setNameNode("<Name node>");
		sparkAction.setMaster("yarn-client");
		
		sparkAction.setName("SparkDynamicOozieWorkFlow");
		sparkAction.setClassName("<spark main class name >");
		sparkAction.setJarName("<Spark main class jar name>");
		sparkAction.setSparkOpts("--num-executors 384");
		List<String> arguments = new ArrayList<String>();
		arguments.add("<Argument one>");
		arguments.add("<Argument two>");
		sparkAction.setArgument(arguments);
		setOkTransition(action, "end");
		setFailTransition(action, "fail");
		action.setSpark(sparkAction);
		workFlowApp.getDecisionOrForkOrJoin().add(action);
	}

	private void addEndNode(String name) {
		End endNode = oozieNodeFactory.createEnd();
		endNode.setName(name);
		workFlowApp.setEnd(endNode);
	}
	/**
	 * Method top add start node
	 * @param startNodeName
	 */
	void addStartNode(String startNodeName) {
		Start start = oozieNodeFactory.createStart();
		start.setTo(startNodeName);
		workFlowApp.setStart(start);
	}
	/**
	 * 
	 * @param act
	 * @param nodeName
	 */
	private void setOkTransition(ActionNode act, String nodeName) {
		ActionTransition okCallWfTrans = oozieNodeFactory
				.createActionTransition();
		okCallWfTrans.setTo(nodeName);
		act.setOk(okCallWfTrans);
	}
	/**
	 * 
	 * @param act
	 * @param nodeName
	 */
	private void setFailTransition(ActionNode act, String nodeName) {
		ActionTransition errorCallWfTrans = oozieNodeFactory
				.createActionTransition();
		errorCallWfTrans.setTo(nodeName);
		act.setError(errorCallWfTrans);
	}

}
