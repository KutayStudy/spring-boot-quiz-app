package com.example.quizapplication.service;

import com.example.quizapplication.model.Question;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuestionsService {
    private final Map<Integer, Question> questions = new ConcurrentHashMap<>();
    private int nextId = 1;

    public ArrayList<Question> loadQuizzes(){
        return new ArrayList<>(questions.values());
    }

    public int getNextId() {
        return nextId++;
    }

    public Question getQuizById(int id) {
        return questions.get(Integer.valueOf(id));
    }

    public boolean addQuiz(Question q){
        int quizId = q.getId();
        if(questions.containsKey(quizId)) return false;
        else{
            questions.put(quizId,q);
            return true;
        }
    }
    public boolean editQuiz(Question q){
        int quizId = q.getId();

        if (questions.containsKey(quizId)) {
            questions.put(quizId, q);
            return true;
        } else return false;

    }

    public boolean deleteQuiz(int id){
        if(questions.containsKey(id)) {
            questions.remove(id);
            return true;
        } else return false;
    }

}
