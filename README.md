# Dynamic-Oozie-Workflow-Creator
Dynamic oozie workflow utility will help you to define oozie workflow dynamicaly based on your requirement or input files. If you have several cases/flows in your workflow , you can add Nodes dynamicaly to control the flow in your workflow.If you have several cases/flows in your workflow , you can add Nodes dynamically to control the flow in your workflow.This is achieved by using the jaxax xml framework.

How to

1. Edit OozieDynamicWorkFlowGenerator.java file based on your requirment . This class is capable of creating nodes according to your input.

2. Call main  method of DynWfGenerator.java . This will perform following actions

        a. Create workflow.xml file dynamicaly

        b. Copy it to HDFSoozie lib path

        c. Trigger oozie workflow.

        d. Return job id 
 
 
 
