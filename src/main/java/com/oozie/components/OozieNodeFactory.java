package com.oozie.components;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;

import javax.xml.namespace.QName;

public class OozieNodeFactory {

	private final static QName _WorkflowApp_QNAME = new QName(
			"uri:oozie:workflow:0.2", "workflow-app");

	public OozieNodeFactory() {
	}

	/**
	 * Create an instance of {@link WorkFlowApp }
	 * 
	 */
	public WorkFlowApp createWorkFlowApp() {
		return new WorkFlowApp();
	}

	/**
	 * Create an instance of {@link FileSystem }
	 * 
	 * 
	 * /** Create an instance of {@link KILL }
	 * 
	 */
	public Kill createKill() {
		return new Kill();
	}

	/**
	 * Create an instance of {@link JAVA }
	 * 
	 */
	public JavaAction createJavaAction() {
		return new JavaAction();
	}

	/**
	 * Create an instance of {@link JAVA }
	 * 
	 */
	public SparkAction createSparkAction() {
		return new SparkAction();
	}

	/**
	 * Create an instance of {@link JOIN }
	 * 
	 */
	public Join createJoin() {
		return new Join();
	}

	/**
	 * Create an instance of {@link ACTION }
	 * 
	 */
	public ActionNode createActionNode() {
		return new ActionNode();
	}	

	/**
	 * Create an instance of {@link FORKTRANSITION }
	 * 
	 */
	public ForkTransition createForkTransition() {
		return new ForkTransition();
	}

	/**
	 * Create an instance of {@link ACTIONTRANSITION }
	 * 
	 */
	public ActionTransition createActionTransition() {
		return new ActionTransition();
	}	

	/**
	 * Create an instance of {@link FORK }
	 * 
	 */
	public Fork createFork() {
		return new Fork();
	}

	/**
	 * Create an instance of {@link CREDENTIALS }
	 * 
	 */

	/**
	 * Create an instance of {@link START }
	 * 
	 */
	public Start createStart() {
		return new Start();
	}

	
	/**
	 * Create an instance of {@link END }
	 * 
	 */
	public End createEnd() {
		return new End();
	}

	@XmlElementDecl(namespace = "uri:oozie:workflow:0.2", name = "workflow-app")
	public JAXBElement<WorkFlowApp> createWorkflowApp(WorkFlowApp value) {
		return new JAXBElement<WorkFlowApp>(_WorkflowApp_QNAME,
				WorkFlowApp.class, value);
	}

}
