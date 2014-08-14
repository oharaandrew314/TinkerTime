package aohara.tinkertime.controllers;

import aohara.common.workflows.Workflow;

public interface WorkflowRunner {
	
	public void submitDownloadWorkflow(Workflow workflow);
	public void submitEnablerWorkflow(Workflow workflow);

}
