package Lab2;

import jadex.bdiv3.annotation.*;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Capability
public class JobAgentCapability {
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
            // Print to console
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
        protected  ClassifierTrainData trainData;

        public FilterJob(ClassifierTrainData trainData) {
            this.trainData = trainData;
        }
    }

    @Plan(trigger = @Trigger(goals = FilterJob.class))
    protected void filterJob(ClassifierTrainData trainData)
    {
        System.out.println("Start filtering");

        ArrayList<Job> filteredJobs = new ArrayList<Job> ();

        System.out.println("Start train classifier");

        ClassifierJob classifier = new ClassifierJob();
        classifier.trainClassifier(trainData);

        System.out.println("End train classifier");
        System.out.println("Start classifying");

        if(classifier.isReadyToClassify()) {
            for(Job job : getJobList()) {
                if(classifier.classify(job).equals("Yes"))
                {
                    filteredJobs.add(job);
                }
            }
        }

        System.out.println("End classifying");
        System.out.println("Start deleting same jobs");

        for(int i = 0; i < filteredJobs.size(); ++i)
        {
            for(int j = i + 1; j < filteredJobs.size(); ++j)
            {
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
    protected void findJob(FindJobRequest request)
    {
        // Print to console
        System.out.println("Request: \"" + request.jobName + "\"");
        System.out.print("Resources: ");
        System.out.println(request.sitesForSearch.toString());
        System.out.println();

        List<Job> jobList = new ArrayList<Job>();
        if(request.sitesForSearch.contains("Rabota.ua"))
            jobList.addAll(getJobsRabotaUA(request.jobName));
        if(request.sitesForSearch.contains("Work.ua"))
            jobList.addAll(getJobsWorkUA(request.jobName));

        this.setJobList(jobList);
    }

    public List<Job> getJobsRabotaUA(String jobName) {
        List<Job> jobList = new ArrayList<Job>();

        try {
            Document doc = Jsoup.connect("https://rabota.ua/zapros/" + jobName + "/Запорожье").get();

            for(Element card : doc.getElementsByClass("card"))
            {
                Job newJob = new Job();

                newJob.name = card.getElementsByClass("ga_listing").first().attr("title");
                newJob.company = card.getElementsByClass("company-profile-name").first().attr("title");
                String salary = card.getElementsByClass("salary").first().text();
                salary = salary.split("—")[0].replaceAll("[^0-9]", "");
                if(salary.equals("")) salary = "-1";
                newJob.salary = Integer.parseInt(salary);
                newJob.siteName = "Rabota.ua";
                newJob.reference = "https://rabota.ua" + card.getElementsByClass("ga_listing").first().attr("href");

                jobList.add(newJob);
            }
        }
        catch (Exception e) {
            if(e instanceof HttpStatusException) {
                System.out.print(e.toString());
            }
            else if(e instanceof IOException) {
                e.printStackTrace();
            }
        }

        return jobList;
    }

    public List<Job> getJobsWorkUA(String jobName) {
        List<Job> jobList = new ArrayList<Job>();

        try {
            Document doc = Jsoup.connect("https://www.work.ua/ru/jobs-zaporizhzhya-" + jobName).get();

            for(Element card : doc.getElementsByClass("card card-hover card-visited wordwrap job-link"))
            {
                Job newJob = new Job();

                newJob.name = card.getElementsByTag("a").first().text();
                newJob.company = card.getElementsByClass("add-top-xs").first().getElementsByTag("b").first().text();
                String salary = "-1";
                Elements divNoClass = card.select("div:not([class])");
                if(!divNoClass.isEmpty()) {
                    Elements bTag = divNoClass.first().getElementsByTag("b");
                    if(!bTag.isEmpty()) {
                        salary = bTag.first().text();
                        salary = salary.split("–")[0].replaceAll("[^0-9]", "");
                    }
                }
                newJob.salary = Integer.parseInt(salary);
                newJob.siteName = "Work.ua";
                newJob.reference = "https://work.ua" + card.getElementsByTag("a").first().attr("href");

                jobList.add(newJob);
            }
        }
        catch (Exception e) {
            if(e instanceof HttpStatusException ) {
                System.out.print(e.toString());
            }
            else if(e instanceof IOException) {
                e.printStackTrace();
            }
        }

        return jobList;
    }
}
