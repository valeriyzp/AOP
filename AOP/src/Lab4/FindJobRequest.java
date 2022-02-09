package Lab4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FindJobRequest implements Serializable {
    public String jobName;
    public List<String> sitesForSearch = new ArrayList<String>();
    public String clientInfo = "";
    public int numberOfRequestOnServer = 1;

    FindJobRequest() { ; }

    @Override
    public String toString() {

        return numberOfRequestOnServer + ") Client: " + clientInfo + " Request: \"" + jobName + "\" Resources: " + sitesForSearch.toString();
    }
}
