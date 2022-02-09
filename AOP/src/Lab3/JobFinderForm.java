package Lab3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class JobFinderForm {
    private JPanel mainPanel;
    private JTextField searchRequest;
    private JCheckBox isRabotaUA;
    private JCheckBox isWorkUA;
    private JButton searchButton;
    private DefaultListModel jobListModel;
    private JList JobJList;
    private JButton openJobSiteButton;
    private JPanel searchParametersPanel;
    private JScrollPane JListScrollPanel;
    private JButton filterJobListButton;

    protected JobAgentBDI parentBDI;

    public JobFinderForm(JobAgentBDI parentBDI)
    {
        this.parentBDI = parentBDI;

        jobListModel = new DefaultListModel();
        JobJList.setModel(jobListModel);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FindJobRequest request = new FindJobRequest();

                request.jobName = searchRequest.getText();
                if(isRabotaUA.isSelected())
                    request.sitesForSearch.add("Rabota.ua");
                if(isWorkUA.isSelected())
                    request.sitesForSearch.add("Work.ua");

                parentBDI.bdiFeature.dispatchTopLevelGoal(parentBDI.capability.new FindJob(request)).get();

                searchRequest.setText("");
                JListScrollPanel.getHorizontalScrollBar().setValue(0);
                JListScrollPanel.getVerticalScrollBar().setValue(0);
            }
        });

        openJobSiteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Job job : (List<Job>)JobJList.getSelectedValuesList()) {
                    parentBDI.bdiFeature.adoptPlan(parentBDI.capability.new openJobSite(job));
                }
            }
        });
        filterJobListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FilterParametersForm window = new FilterParametersForm(parentBDI);
                window.show();
            }
        });
    }

    public void show() {
        JFrame mainFrame = new JFrame("JobsAgent");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        mainFrame.setBounds((dimension.width - 850) / 2, (dimension.height - 500) / 2, 850, 500);
        mainFrame.setContentPane(mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    public void setJobJList(List<Job> jobList) {
        jobListModel.clear();

        for(Job job : jobList) {
            jobListModel.addElement(job);
        }

        // May need to use if JList don`t updates
        //JobJList.revalidate();
        //JobJList.repaint();

        // Instead of using DefaultListModel jobListModel
        // setListData constructs a read-only ListModel from an array of items, and calls setModel with this model
        //JobJList.setListData(jobList.toArray());
    }
}
