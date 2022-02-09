package Lab2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class FilterParametersForm {
    private JPanel mainPanel;
    private JLabel windowDescription;
    private JCheckBox question1;
    private JCheckBox question2;
    private JCheckBox question3;
    private JCheckBox question4;
    private JCheckBox question5;
    private JButton filterButton;

    protected JobAgentBDI parentBDI;

    public FilterParametersForm(JobAgentBDI parentBDI) {
        this.parentBDI = parentBDI;

        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                List<Integer> questionParameter = new ArrayList<Integer>();
                List<Boolean> questionAnswer = new ArrayList<Boolean>();

                questionParameter.add(-1);
                questionAnswer.add(question1.isSelected());
                questionParameter.add(5000);
                questionAnswer.add(question2.isSelected());
                questionParameter.add(10000);
                questionAnswer.add(question3.isSelected());
                questionParameter.add(20000);
                questionAnswer.add(question4.isSelected());
                questionParameter.add(30000);
                questionAnswer.add(question5.isSelected());

                ClassifierTrainData dataToTrainClassifier = new ClassifierTrainData(questionParameter, questionAnswer);
                parentBDI.bdiFeature.dispatchTopLevelGoal(parentBDI.capability.new FilterJob(dataToTrainClassifier)).get();

                SwingUtilities.getWindowAncestor(mainPanel).dispose();
            }
        });
    }

    public void show() {
        JDialog frame = new JDialog(new JFrame(), "Filter", true);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setBounds((dimension.width - frame.getWidth()) / 2, (dimension.height - frame.getHeight()) / 2, frame.getWidth(), frame.getHeight());
        frame.setVisible(true);
    }
}
