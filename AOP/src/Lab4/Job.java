package Lab4;

import java.io.Serializable;

public class Job implements Serializable {
    public String name;
    public String company;
    public int salary = -1;
    public String siteName;
    public String reference;

    Job() {
        ;
    }

    @Override
    public String toString() {
        String res = "";
        res += Algorithms.rightPadding(siteName, 10) + " - ";
        res += Algorithms.rightPadding(company, 16) + " - ";
        if(salary == -1)
            res += Algorithms.rightPadding(" ", 10) + " - ";
        else
            res += Algorithms.rightPadding(String.valueOf(salary) + " грн.", 10) + " - ";
        res += name.length() > 75 ? name.substring(0, 75) : name;

        return res;
    }
}
