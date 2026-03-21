package com.anyview.dto.analysis;

import java.util.List;
import java.util.Map;

public class SystemAnalysisDTO {
    private OverviewStats overview;
    private List<TrendData> userTrend;
    private List<TrendData> submissionTrend;
    private Map<String, Long> submissionsByHour;
    private Map<String, Long> submissionsByDayOfWeek;
    private List<ErrorTypeStat> errorTypeStats;
    private List<HotQuestion> hotQuestions;
    private List<ActiveUser> mostActiveUsers;

    public static class OverviewStats {
        private Long totalUsers;
        private Long totalSubmissions;
        private Double averageScore;
        private Double acceptanceRate;

        public Long getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(Long totalUsers) {
            this.totalUsers = totalUsers;
        }

        public Long getTotalSubmissions() {
            return totalSubmissions;
        }

        public void setTotalSubmissions(Long totalSubmissions) {
            this.totalSubmissions = totalSubmissions;
        }

        public Double getAverageScore() {
            return averageScore;
        }

        public void setAverageScore(Double averageScore) {
            this.averageScore = averageScore;
        }

        public Double getAcceptanceRate() {
            return acceptanceRate;
        }

        public void setAcceptanceRate(Double acceptanceRate) {
            this.acceptanceRate = acceptanceRate;
        }
    }

    public static class TrendData {
        private String date;
        private Long value;
        private Long secondaryValue;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }

        public Long getSecondaryValue() {
            return secondaryValue;
        }

        public void setSecondaryValue(Long secondaryValue) {
            this.secondaryValue = secondaryValue;
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

    public static class HotQuestion {
        private Long id;
        private String title;
        private String difficulty;
        private Long submissionCount;
        private Double acceptanceRate;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public Long getSubmissionCount() {
            return submissionCount;
        }

        public void setSubmissionCount(Long submissionCount) {
            this.submissionCount = submissionCount;
        }

        public Double getAcceptanceRate() {
            return acceptanceRate;
        }

        public void setAcceptanceRate(Double acceptanceRate) {
            this.acceptanceRate = acceptanceRate;
        }
    }

    public static class ActiveUser {
        private Long id;
        private String username;
        private String realName;
        private Long submissionCount;
        private Long solvedCount;
        private Double averageScore;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public Long getSubmissionCount() {
            return submissionCount;
        }

        public void setSubmissionCount(Long submissionCount) {
            this.submissionCount = submissionCount;
        }

        public Long getSolvedCount() {
            return solvedCount;
        }

        public void setSolvedCount(Long solvedCount) {
            this.solvedCount = solvedCount;
        }

        public Double getAverageScore() {
            return averageScore;
        }

        public void setAverageScore(Double averageScore) {
            this.averageScore = averageScore;
        }
    }

    public OverviewStats getOverview() {
        return overview;
    }

    public void setOverview(OverviewStats overview) {
        this.overview = overview;
    }

    public List<TrendData> getUserTrend() {
        return userTrend;
    }

    public void setUserTrend(List<TrendData> userTrend) {
        this.userTrend = userTrend;
    }

    public List<TrendData> getSubmissionTrend() {
        return submissionTrend;
    }

    public void setSubmissionTrend(List<TrendData> submissionTrend) {
        this.submissionTrend = submissionTrend;
    }

    public Map<String, Long> getSubmissionsByHour() {
        return submissionsByHour;
    }

    public void setSubmissionsByHour(Map<String, Long> submissionsByHour) {
        this.submissionsByHour = submissionsByHour;
    }

    public Map<String, Long> getSubmissionsByDayOfWeek() {
        return submissionsByDayOfWeek;
    }

    public void setSubmissionsByDayOfWeek(Map<String, Long> submissionsByDayOfWeek) {
        this.submissionsByDayOfWeek = submissionsByDayOfWeek;
    }

    public List<ErrorTypeStat> getErrorTypeStats() {
        return errorTypeStats;
    }

    public void setErrorTypeStats(List<ErrorTypeStat> errorTypeStats) {
        this.errorTypeStats = errorTypeStats;
    }

    public List<HotQuestion> getHotQuestions() {
        return hotQuestions;
    }

    public void setHotQuestions(List<HotQuestion> hotQuestions) {
        this.hotQuestions = hotQuestions;
    }

    public List<ActiveUser> getMostActiveUsers() {
        return mostActiveUsers;
    }

    public void setMostActiveUsers(List<ActiveUser> mostActiveUsers) {
        this.mostActiveUsers = mostActiveUsers;
    }
}
