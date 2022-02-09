package Lab4;

import jadex.bdiv3.annotation.*;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Capability
public class GUIAgentCapability {
    @Belief
    public native List<Job> getJobList();

    @Belief
    public native void setJobList(List<Job> jobList);

    @Plan
    class openJobSite {
        private String url;

        openJobSite(Job job) {
            this.url = job.reference;
        }

        @PlanBody
        public void main() {
            System.out.println("Go to reference: " + url);
            System.out.println();

            try {
                Desktop.getDesktop().browse(new URL(url).toURI());
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    @Goal
    public class FindJob {
        @GoalParameter
        protected FindJobRequest request;

        public FindJob(FindJobRequest request) {
            this.request = request;
        }
    }

    @Goal
    public class FilterJob {
        @GoalParameter
        protected ClassifierTrainData trainData;

        public FilterJob(ClassifierTrainData trainData) {
            this.trainData = trainData;
        }
    }

    @Plan(trigger = @Trigger(goals = FilterJob.class))
    protected void filterJob(ClassifierTrainData trainData) {
        System.out.println("Start filtering");

        ArrayList<Job> filteredJobs = new ArrayList<Job> ();

        System.out.println("Start train classifier");

        ClassifierJob classifier = new ClassifierJob();
        classifier.trainClassifier(trainData);

        System.out.println("End train classifier");
        System.out.println("Start classifying");

        if(classifier.isReadyToClassify()) {
            for(Job job : getJobList()) {
                if(classifier.classify(job).equals("Yes")) {
                    filteredJobs.add(job);
                }
            }
        }

        System.out.println("End classifying");
        System.out.println("Start deleting same jobs");

        for(int i = 0; i < filteredJobs.size(); ++i) {
            for(int j = i + 1; j < filteredJobs.size(); ++j) {
                if(filteredJobs.get(i).name.equals(filteredJobs.get(j).name)) {
                    filteredJobs.remove(j);
                    --j;
                }
            }
        }

        System.out.println("End deleting same jobs");
        System.out.println();

        setJobList(filteredJobs);
    }

    @Plan(trigger = @Trigger(goals = FindJob.class))
    protected void findJob(FindJobRequest request) {
        try {
            Socket clientSocket = new Socket("localhost", Algorithms.portForSocket);

            System.out.println("Send request to server");

            ObjectOutputStream outStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outStream.writeObject(request);
            outStream.flush();

            System.out.println("Get answer from server");

            ObjectInputStream inStream = new ObjectInputStream(clientSocket.getInputStream());
            try {
                ArrayList<Job> jobList = (ArrayList<Job>)inStream.readObject();
                setJobList(jobList);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            clientSocket.close();
            inStream.close();
            outStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
