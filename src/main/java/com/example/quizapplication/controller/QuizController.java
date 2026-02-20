package com.example.quizapplication.controller;

import com.example.quizapplication.model.Question;
import com.example.quizapplication.service.QuestionsService;
import com.example.quizapplication.service.QuizUserDetailsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class QuizController {
    private final QuizUserDetailsService userDetailsService;
    private final QuestionsService questionsService;
    private final AuthenticationManager authenticationManager;

    public QuizController(QuizUserDetailsService userDetailsService,
                          AuthenticationManager authenticationManager,
                          QuestionsService questionsService) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.questionsService = questionsService;
    }

    @GetMapping("/quizList")
    @PreAuthorize("hasRole('ADMIN')")
    public String quizList(Model model) {
        model.addAttribute("quizzes", questionsService.loadQuizzes());
        return "quizList";
    }

    @GetMapping("/quiz")
    public String quiz(Model model) {
        model.addAttribute("quizzes", questionsService.loadQuizzes());
        return "Quiz";
    }

    @GetMapping("/home")
    public String home(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin ? "redirect:/quizList" : "redirect:/quiz";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String role) {
        try {
            userDetailsService.registerUser(username, email, password, role);
        } catch (Exception e) {
            return "redirect:/register?error";
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Auto-login yaptıysan login'e değil home'a gitmek daha tutarlı
        return "redirect:/home";
    }

    @GetMapping("/addQuiz")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddQuizForm(Model model) {
        model.addAttribute("quiz", new Question());
        return "addQuiz";
    }

    @PostMapping("/addQuiz")
    @PreAuthorize("hasRole('ADMIN')")
    public String addQuiz(@ModelAttribute("quiz") Question quiz) {
        quiz.setId(questionsService.getNextId());
        questionsService.addQuiz(quiz);
        return "redirect:/home";
    }

    @GetMapping("/editQuiz/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditQuizForm(@PathVariable("id") int id, Model model) {
        Question quiz = questionsService.getQuizById(id);
        model.addAttribute("quiz", quiz);
        return "editQuiz";
    }

    @PutMapping("/editQuiz")
    @PreAuthorize("hasRole('ADMIN')")
    public String editQuiz(@ModelAttribute("quiz") Question quiz) {
        questionsService.editQuiz(quiz);
        return "redirect:/home";
    }

    @DeleteMapping("/deleteQuiz/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteQuiz(@PathVariable int id) {
        questionsService.deleteQuiz(id);
        return "redirect:/home";
    }

    @PostMapping("/submitQuiz")
    public String evaluateQuiz(@RequestParam Map<String, String> allParams, Model model) {
        int correctAnswers = 0;
        List<String> userAnswers = new ArrayList<>();
        ArrayList<Question> quizzes = questionsService.loadQuizzes();

        for (int i = 0; i < quizzes.size(); i++) {
            String userAnswer = allParams.get("answer" + i);
            userAnswers.add(userAnswer);
            if (userAnswer != null && quizzes.get(i).getCorrectAnswer().equals(userAnswer)) {
                correctAnswers++;
            }
        }

        model.addAttribute("quizzes", quizzes);
        model.addAttribute("userAnswers", userAnswers);
        model.addAttribute("correctAnswers", correctAnswers);
        model.addAttribute("totalQuestions", quizzes.size());

        return "result";
    }
}