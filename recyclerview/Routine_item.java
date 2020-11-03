package com.example.trainer.recyclerview;

public class Routine_item {
    String exercise_name;
    String repetition;
    String set_number;

    public Routine_item(String exercise_name, String repetition, String set_number) {
        this.exercise_name = exercise_name;
        this.repetition = repetition;
        this.set_number = set_number;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }

    public String getRepetition() {
        return repetition;
    }

    public void setRepetition(String repetition) {
        this.repetition = repetition;
    }

    public String getSet_number() {
        return set_number;
    }

    public void setSet_number(String set_number) {
        this.set_number = set_number;
    }
}
