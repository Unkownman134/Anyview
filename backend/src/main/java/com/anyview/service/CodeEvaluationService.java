package com.anyview.service;

import com.anyview.entity.Question;
import com.anyview.entity.Submission;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeEvaluationService {

    public EvaluationResult evaluateSubmission(Submission submission, Question question) throws Exception {
        // 解析测试用例
        List<TestCase> testCases = parseTestCases(question.getTestCases());
        List<TestResult> testResults = new ArrayList<>();

        int passed = 0;
        int total = testCases.size();

        for (TestCase testCase : testCases) {
            TestResult result = executeTestCase(submission.getCode(), question.getType(), testCase);
            testResults.add(result);
            if (result.isPassed()) {
                passed++;
            }
        }

        // 计算得分
        int score = (total > 0) ? (passed * 100 / total) : 0;
        boolean allPassed = passed == total;

        return new EvaluationResult(
                allPassed,
                score,
                passed,
                total,
                testResults
        );
    }

    private List<TestCase> parseTestCases(String testCasesStr) {
        List<TestCase> testCases = new ArrayList<>();
        // 简单解析测试用例格式：[[input, expectedOutput], [input, expectedOutput], ...]
        // 实际项目中可能需要更复杂的解析
        // 这里使用简单的字符串处理
        String[] cases = testCasesStr.split("\\n");
        for (String testCase : cases) {
            if (testCase.trim().isEmpty()) continue;
            // 简单解析，实际项目中应该使用JSON解析
            String[] parts = testCase.split(",\\[" , 2);
            if (parts.length == 2) {
                String input = parts[0].replaceAll("^\\[\\[" , "").replaceAll("\\]" , "");
                String expectedOutput = parts[1].replaceAll("\\]\\]$", "");
                testCases.add(new TestCase(input, expectedOutput));
            }
        }
        return testCases;
    }

    private TestResult executeTestCase(String code, String language, TestCase testCase) {
        try {
            // 这里应该调用实际的代码执行器
            // 为了演示，我们简单模拟一下
            // 实际项目中，应该使用安全的代码执行环境，比如Docker容器
            System.out.println("执行代码：" + code);
            System.out.println("输入：" + testCase.getInput());
            System.out.println("期望输出：" + testCase.getExpectedOutput());

            // 模拟执行结果
            // 实际项目中，应该执行代码并获取输出
            String actualOutput = "模拟输出";
            boolean passed = actualOutput.equals(testCase.getExpectedOutput());

            return new TestResult(passed, actualOutput, testCase.getExpectedOutput(), "执行成功");
        } catch (Exception e) {
            return new TestResult(false, "", testCase.getExpectedOutput(), "执行失败：" + e.getMessage());
        }
    }

    // 内部类
    public static class EvaluationResult {
        private boolean allPassed;
        private int score;
        private int passed;
        private int total;
        private List<TestResult> testResults;

        public EvaluationResult(boolean allPassed, int score, int passed, int total, List<TestResult> testResults) {
            this.allPassed = allPassed;
            this.score = score;
            this.passed = passed;
            this.total = total;
            this.testResults = testResults;
        }

        // Getters
        public boolean isAllPassed() { return allPassed; }
        public int getScore() { return score; }
        public int getPassed() { return passed; }
        public int getTotal() { return total; }
        public List<TestResult> getTestResults() { return testResults; }
    }

    public static class TestCase {
        private String input;
        private String expectedOutput;

        public TestCase(String input, String expectedOutput) {
            this.input = input;
            this.expectedOutput = expectedOutput;
        }

        // Getters
        public String getInput() { return input; }
        public String getExpectedOutput() { return expectedOutput; }
    }

    public static class TestResult {
        private boolean passed;
        private String actualOutput;
        private String expectedOutput;
        private String message;

        public TestResult(boolean passed, String actualOutput, String expectedOutput, String message) {
            this.passed = passed;
            this.actualOutput = actualOutput;
            this.expectedOutput = expectedOutput;
            this.message = message;
        }

        // Getters
        public boolean isPassed() { return passed; }
        public String getActualOutput() { return actualOutput; }
        public String getExpectedOutput() { return expectedOutput; }
        public String getMessage() { return message; }
    }
}
