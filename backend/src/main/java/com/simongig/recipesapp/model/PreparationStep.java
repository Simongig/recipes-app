package com.simongig.recipesapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreparationStep {
    private String title;
    private String content;

    public PreparationStep(@JsonProperty String title, @JsonProperty String content) {
        this.setTitle(title);
        this.setContent(content);
    }

    
    @Override
    public boolean equals(Object comp) {
        PreparationStep compPrepStep = (PreparationStep)comp;
        System.out.print(compPrepStep.title);
        System.out.println(this.title);
        if(this.title==compPrepStep.title) {
            return true;
        }
        else {
            return false;
        } 
    }  

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return String.format("Step: %s", title);
    }
}
