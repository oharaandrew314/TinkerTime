package aohara.tinkertime.controllers;

import aohara.common.workflows.Workflow;

/**
 * Public interface for a controller which will execute Asynchronous workflows
 * 
 * It is reccomended that the implementing Class use a different Executor for 
 * each of the methods implemented.  This ensures that each workflow is executed
 * in a manner appropriate to it.
 * 
 * @author Andrew O'Hara
 */
public interface WorkflowRunner {
	
	public void submitDownloadWorkflow(Workflow workflow);
	public void submitEnablerWorkflow(Workflow workflow);

}
