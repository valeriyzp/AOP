package Lab2;

import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;

import java.util.ArrayList;
import java.util.List;

@Agent
@Plans(@Plan(body=@Body(JobAgentCapability.openJobSite.class)))
public class JobAgentBDI {
    protected JobFinderForm window;

    @Belief
    protected List<Job> jobList = new ArrayList<Job>();

    @AgentFeature
    protected IBDIAgentFeature bdiFeature;

    @Capability(beliefmapping = @Mapping(value = "jobList"))
    protected JobAgentCapability capability = new JobAgentCapability();

    @AgentCreated
    public void created() {
        window = new JobFinderForm(this);
    }

    @AgentBody
    public void body() {
        window.show();
    }

    @Plan(trigger = @Trigger(factchangeds = "jobList"))
    public void showchanges()
    {
        window.setJobJList(jobList);

        // Print to console
        System.out.println("Updated job list size: " + jobList.size());
        System.out.println("Updated job list: ");
        int i = 1;
        for (Job job: jobList ) {
            System.out.print(i++ + ") ");
            System.out.println(job.toString());
        }
        System.out.println();
    }
}
