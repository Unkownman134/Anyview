package com.anyview.dto.analysis;

import java.util.List;
import java.util.Map;

public class UserAnalysisDTO {
    private Long userId;
    private String username;
    private String realName;

    private Long totalSubmissions;
    private Long acceptedSubmissions;
    private Long failedSubmissions;
    private Double acceptanceRate;
    private Double averageScore;

    private Long totalQuestionsAttempted;
    private Long totalQuestionsSolved;

    private Long totalTimeSpentMinutes;
    private Long loginCount;
    private String lastLoginTime;

    private List<ActivityTrend> activityTrend;
    private Map<String, Long> submissionsByDifficulty;
    private List<QuestionProgress> questionProgress;

    public static class ActivityTrend {
        private String date;
        private Long submissionCount;
        private Long loginCount;
        private Long timeSpentMinutes;

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

        public Long getLoginCount() {
            return loginCount;
        }

        public void setLoginCount(Long loginCount) {
            this.loginCount = loginCount;
        }

        public Long getTimeSpentMinutes() {
            return timeSpentMinutes;
        }

        public void setTimeSpentMinutes(Long timeSpentMinutes) {
            this.timeSpentMinutes = timeSpentMinutes;
        }
    }

    public static class QuestionProgress {
        private Long questionId;
        private String questionTitle;
        private String difficulty;
        private Integer attempts;
        private Boolean solved;
        private Integer bestScore;
        private String lastSubmitTime;

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public String getQuestionTitle() {
            return questionTitle;
        }

        public void setQuestionTitle(String questionTitle) {
            this.questionTitle = questionTitle;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public Integer getAttempts() {
            return attempts;
        }

        public void setAttempts(Integer attempts) {
            this.attempts = attempts;
        }

        public Boolean getSolved() {
            return solved;
        }

        public void setSolved(Boolean solved) {
            this.solved = solved;
        }

        public Integer getBestScore() {
            return bestScore;
        }

        public void setBestScore(Integer bestScore) {
            this.bestScore = bestScore;
        }

        public String getLastSubmitTime() {
            return lastSubmitTime;
        }

        public void setLastSubmitTime(String lastSubmitTime) {
            this.lastSubmitTime = lastSubmitTime;
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
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

    public Long getTotalQuestionsAttempted() {
        return totalQuestionsAttempted;
    }

    public void setTotalQuestionsAttempted(Long totalQuestionsAttempted) {
        this.totalQuestionsAttempted = totalQuestionsAttempted;
    }

    public Long getTotalQuestionsSolved() {
        return totalQuestionsSolved;
    }

    public void setTotalQuestionsSolved(Long totalQuestionsSolved) {
        this.totalQuestionsSolved = totalQuestionsSolved;
    }

    public Long getTotalTimeSpentMinutes() {
        return totalTimeSpentMinutes;
    }

    public void setTotalTimeSpentMinutes(Long totalTimeSpentMinutes) {
        this.totalTimeSpentMinutes = totalTimeSpentMinutes;
    }

    public Long getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Long loginCount) {
        this.loginCount = loginCount;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public List<ActivityTrend> getActivityTrend() {
        return activityTrend;
    }

    public void setActivityTrend(List<ActivityTrend> activityTrend) {
        this.activityTrend = activityTrend;
    }

    public Map<String, Long> getSubmissionsByDifficulty() {
        return submissionsByDifficulty;
    }

    public void setSubmissionsByDifficulty(Map<String, Long> submissionsByDifficulty) {
        this.submissionsByDifficulty = submissionsByDifficulty;
    }

    public List<QuestionProgress> getQuestionProgress() {
        return questionProgress;
    }

    public void setQuestionProgress(List<QuestionProgress> questionProgress) {
        this.questionProgress = questionProgress;
    }
}
