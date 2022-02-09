package Lab4;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

public class ClassifierJob {
    ArrayList<String> classVal;
    ArrayList<Attribute> attributes;

    Attribute salaryAttribute;
    Attribute classAttribute;

    Classifier classifier;

    public ClassifierJob() {
        classVal = new ArrayList<String>();
        classVal.add("Yes");
        classVal.add("No");

        attributes = new ArrayList<Attribute>();

        salaryAttribute = new Attribute("salary");
        attributes.add(salaryAttribute);

        classAttribute = new Attribute("class", classVal);
        attributes.add(classAttribute);

        classifier = null;
    }

    public void trainClassifier(ClassifierTrainData trainData) {
        Instances trainingDataset = new Instances("Train dataset", attributes, trainData.questionParameter.size());
        trainingDataset.setClassIndex(trainingDataset.numAttributes() - 1);

        for(int i = 0; i < trainData.questionParameter.size(); ++i) {
            Instance newInstance = new DenseInstance(2);
            newInstance.setValue(salaryAttribute, trainData.questionParameter.get(i));
            newInstance.setValue(classAttribute, trainData.questionAnswer.get(i) ? "Yes" : "No");

            trainingDataset.add(newInstance);
        }

        try {
            Classifier newClassifier  = new IBk();
            newClassifier.buildClassifier(trainingDataset);
            classifier = newClassifier;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String classify(Job job) {
        try {
            if (classifier == null) return "";

            Instances classifyDataset = new Instances("Clasify dataset", attributes, 0);
            classifyDataset.setClassIndex(classifyDataset.numAttributes() - 1);

            Instance newInstance = new DenseInstance(2);
            newInstance.setValue(salaryAttribute, job.salary);
            newInstance.setDataset(classifyDataset);

            int res = (int) classifier.classifyInstance(newInstance);
            return classVal.get(res);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public boolean isReadyToClassify() {
        return classifier != null;
    }
}
