package Lab4;

import javax.swing.*;
import java.awt.*;

public class ServerRequestHistoryForm {
    private JPanel mainPanel;
    private DefaultListModel requestHistoryListModel;
    private JList requestHistoryList;
    private JScrollPane JScrollPanel;

    public ServerRequestHistoryForm() {
        requestHistoryListModel = new DefaultListModel();
        requestHistoryList.setModel(requestHistoryListModel);
    }

    public void show() {
        JFrame mainFrame = new JFrame("JobAgent");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        mainFrame.setBounds((dimension.width - 850), 0, 850, 500);
        mainFrame.setContentPane(mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    public void addRequestToHistoryList(FindJobRequest request) {
        requestHistoryListModel.addElement(request);
    }
}
