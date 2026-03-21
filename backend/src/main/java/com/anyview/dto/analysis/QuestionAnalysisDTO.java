package com.anyview.dto.analysis;

import java.util.List;
import java.util.Map;

public class QuestionAnalysisDTO {
    private Long questionId;
    private String title;
    private String difficulty;
    private String type;
    private Integer totalScore;

    private Long totalSubmissions;
    private Long acceptedSubmissions;
    private Long failedSubmissions;
    private Long uniqueAttemptUsers;
    private Long uniqueSolvedUsers;
    private Double acceptanceRate;
    private Double averageScore;

    private Map<String, Long> submissionsByDifficulty;
    private List<SubmissionTrend> submissionTrend;
    private List<ErrorTypeStat> errorTypeStats;
    private List<CommonMistake> commonMistakes;
    private List<Map<String, Object>> scoreDistribution;

    public static class SubmissionTrend {
        private String date;
        private Long submissionCount;
        private Long acceptedCount;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Long getSubmissionCount() {
            return submissionCount;
        }

        public void setSubmissionCount(Long submissionCount) {
            this.submissionCount = submissionCount;
        }

        public Long getAcceptedCount() {
            return acceptedCount;
        }

        public void setAcceptedCount(Long acceptedCount) {
            this.acceptedCount = acceptedCount;
        }
    }

    public static class ErrorTypeStat {
        private String errorType;
        private Long count;

        public String getErrorType() {
            return errorType;
        }

        public void setErrorType(String errorType) {
            this.errorType = errorType;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    public static class CommonMistake {
        private String mistake;
        private Long count;
        private Double percentage;

        public String getMistake() {
            return mistake;
        }

        public void setMistake(String mistake) {
            this.mistake = mistake;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Long getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(Long totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public Long getAcceptedSubmissions() {
        return acceptedSubmissions;
    }

    public void setAcceptedSubmissions(Long acceptedSubmissions) {
        this.acceptedSubmissions = acceptedSubmissions;
    }

    public Long getFailedSubmissions() {
        return failedSubmissions;
    }

    public void setFailedSubmissions(Long failedSubmissions) {
        this.failedSubmissions = failedSubmissions;
    }

    public Long getUniqueAttemptUsers() {
        return uniqueAttemptUsers;
    }

    public void setUniqueAttemptUsers(Long uniqueAttemptUsers) {
        this.uniqueAttemptUsers = uniqueAttemptUsers;
    }

    public Long getUniqueSolvedUsers() {
        return uniqueSolvedUsers;
    }

    public void setUniqueSolvedUsers(Long uniqueSolvedUsers) {
        this.uniqueSolvedUsers = uniqueSolvedUsers;
    }

    public Double getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(Double acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Map<String, Long> getSubmissionsByDifficulty() {
        return submissionsByDifficulty;
    }

    public void setSubmissionsByDifficulty(Map<String, Long> submissionsByDifficulty) {
        this.submissionsByDifficulty = submissionsByDifficulty;
    }

    public List<SubmissionTrend> getSubmissionTrend() {
        return submissionTrend;
    }

    public void setSubmissionTrend(List<SubmissionTrend> submissionTrend) {
        this.submissionTrend = submissionTrend;
    }

    public List<ErrorTypeStat> getErrorTypeStats() {
        return errorTypeStats;
    }

    public void setErrorTypeStats(List<ErrorTypeStat> errorTypeStats) {
        this.errorTypeStats = errorTypeStats;
    }

    public List<CommonMistake> getCommonMistakes() {
        return commonMistakes;
    }

    public void setCommonMistakes(List<CommonMistake> commonMistakes) {
        this.commonMistakes = commonMistakes;
    }

    public List<Map<String, Object>> getScoreDistribution() {
        return scoreDistribution;
    }

    public void setScoreDistribution(List<Map<String, Object>> scoreDistribution) {
        this.scoreDistribution = scoreDistribution;
    }
}
