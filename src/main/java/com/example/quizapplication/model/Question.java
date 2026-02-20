package com.example.quizapplication.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Question {

    @Min(1)
    private int id;

    @NotBlank
    private String questionText;

    private ArrayList<String> options;

    @NotBlank
    private String correctAnswer;

    // Form input için: "A,B,C,D"
    private String optionsText;

    public Question() {
        this.options = new ArrayList<>();
    }

    public Question(int id, String questionText) {
        this.id = id;
        this.questionText = questionText;
        this.options = new ArrayList<>();
    }

    public Question(int id, String questionText, ArrayList<String> options, String correctAnswer) {
        this.id = id;
        this.questionText = questionText;
        this.options = (options != null) ? options : new ArrayList<>();
        this.correctAnswer = correctAnswer;
        this.optionsText = String.join(",", this.options);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = (options != null) ? options : new ArrayList<>();
        this.optionsText = String.join(",", this.options);
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    // Thymeleaf input için
    public String getOptionsText() {
        if (options == null || options.isEmpty()) return "";
        return String.join(",", options);
    }

    public void setOptionsText(String optionsText) {
        this.optionsText = optionsText;

        if (optionsText == null || optionsText.trim().isEmpty()) {
            this.options = new ArrayList<>();
            return;
        }

        this.options = Arrays.stream(optionsText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Question id: ").append(id)
                .append("\nQuestion text: ").append(questionText);

        for (int i = 0; i < options.size(); i++) {
            sb.append("\n").append(i + 1).append(". option: ").append(options.get(i));
        }

        sb.append("\nCorrect answer: ").append(correctAnswer);
        return sb.toString();
    }
}