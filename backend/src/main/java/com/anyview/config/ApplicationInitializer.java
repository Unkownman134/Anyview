package com.anyview.config;

import com.anyview.service.QuestionInitService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationInitializer implements CommandLineRunner {
    private final QuestionInitService questionInitService;

    @Override
    public void run(String... args) throws Exception {
        // 初始化题库
        questionInitService.initQuestions();
    }
}
