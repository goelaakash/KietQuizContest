package com.aakash.quizapp.Models;

public class Questions {

    String quest;
    String option1;
    String option2;
    String option3;
    String option4;
    String answer;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    String category;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public Questions(){

    }

    public Questions(String quest,String option1,String option2,String option3,String option4,String answer,String id){
        this.quest =quest;
        this.option1 = option1;
        this.option2 =option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer =answer;
        this.id=id;

    }


    public String getQuest() {
        return quest;
    }

    public void setQuest(String quest) {
        this.quest = quest;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
