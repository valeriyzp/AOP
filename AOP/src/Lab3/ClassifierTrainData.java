package Lab3;

import java.util.List;

public class ClassifierTrainData {

    protected List<Integer> questionParameter;
    protected List<Boolean> questionAnswer;

    public ClassifierTrainData(List<Integer> questionParameter, List<Boolean> questionAnswer) {
        this.questionParameter = questionParameter;
        this.questionAnswer = questionAnswer;
    }
}
