package Lab4;

import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.rules.eca.ChangeInfo;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Agent
public class JobAgentBDI {
    protected ServerRequestHistoryForm window;
    private static ServerSocket server;

    @Belief
    protected List<FindJobRequest> requestHistoryList = new ArrayList<FindJobRequest>();

    @AgentFeature
    protected IBDIAgentFeature bdiFeature;

    @AgentCreated
    public void created() {
        window = new ServerRequestHistoryForm();

        Runnable run = new Runnable() {
            public void run() {
                if(server!=null) {
                    try {
                        server.close();
                    }
                    catch(Exception e) {
                        ;
                    }
                }
                try {
                    server = new ServerSocket(Algorithms.portForSocket);

                    while(true) {
                        Socket clientSocket = server.accept();

                        bdiFeature.dispatchTopLevelGoal(new FindJob(clientSocket)).get();
                    }
                }
                catch(IOException e)
                {
                    throw new RuntimeException(e.getMessage());
                }
            }
        };

        Thread newThread = new Thread(run);
        newThread.start();
    }

    @AgentBody
    public void body() {
        window.show();
    }

    @Plan(trigger=@Trigger(factaddeds="requestHistoryList"))
    public void showchanges(ChangeEvent event)
    {
        FindJobRequest addedRequest = ((ChangeInfo<FindJobRequest>)event.getValue()).getValue();
        window.addRequestToHistoryList(addedRequest);
    }

    @Goal
    public class FindJob {
        @GoalParameter
        protected Socket clientSocket;

        public FindJob(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
    }

    @Plan(trigger = @Trigger(goals = FindJob.class))
    protected void findJob(Socket clientSocket) {
        try {
            try {
                ObjectInputStream inStream = new ObjectInputStream(clientSocket.getInputStream());
                FindJobRequest request = (FindJobRequest) inStream.readObject();

                System.out.println("Request: \"" + request.jobName + "\"");
                System.out.print("Resources: ");
                System.out.println(request.sitesForSearch.toString());
                System.out.println();

                request.clientInfo = clientSocket.toString();
                request.numberOfRequestOnServer = requestHistoryList.size() + 1;
                requestHistoryList.add(request);

                List<Job> jobList = new ArrayList<Job>();
                if(request.sitesForSearch.contains("Rabota.ua"))
                    jobList.addAll(getJobsRabotaUA(request.jobName));
                if(request.sitesForSearch.contains("Work.ua"))
                    jobList.addAll(getJobsWorkUA(request.jobName));

                ObjectOutputStream outStream = new ObjectOutputStream(clientSocket.getOutputStream());
                outStream.writeObject(jobList);
                outStream.flush();

                clientSocket.close();
                inStream.close();
                outStream.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Job> getJobsRabotaUA(String jobName) {
        List<Job> jobList = new ArrayList<Job>();

        try {
            Document doc = Jsoup.connect("https://rabota.ua/zapros/" + jobName + "/Запорожье").get();

            for(Element card : doc.getElementsByClass("card")) {
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

            for(Element card : doc.getElementsByClass("card card-hover card-visited wordwrap job-link")) {
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
