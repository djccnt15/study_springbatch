package com.batch_java.study_spring_batch.batch.business;


import java.util.ArrayList;
import java.util.List;

public class StepJobBuilder {

    private final List<Step> stepList;
    private JobExecutionListener jobExecutionListener;
    
    public StepJobBuilder() {
        this.stepList = new ArrayList<>();
    }
    
    public StepJobBuilder start(Step step) {
        if (stepList.isEmpty()) {
            stepList.add(step);
        } else {
            stepList.set(0, step);
        }
        return this;
    }
    
    public StepJobBuilder next(Step step) {
        stepList.add(step);
        return this;
    }
    
    public StepJobBuilder listener(JobExecutionListener jobExecutionListener) {
        this.jobExecutionListener = jobExecutionListener;
        return this;
    }
    
    public StepJob build() {
        return new StepJob(stepList, jobExecutionListener);
    }
}
