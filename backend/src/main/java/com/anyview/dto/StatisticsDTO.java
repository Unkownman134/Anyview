package com.anyview.dto;

import java.util.Map;

public class StatisticsDTO {
    // 总体统计
    private long totalUsers;
    private long totalClasses;
    private long totalQuestions;
    private long totalAssignments;
    private long totalSubmissions;

    // 按角色统计
    private Map<String, Long> userByRole;

    // 按难度统计
    private Map<Integer, Long> questionsByDifficulty;

    // 按状态统计
    private Map<String, Long> submissionsByStatus;

    // 最近7天提交趋势
    private Map<String, Integer> submissionTrend;

    // 平均得分
    private double averageScore;

    // 完成率
    private double completionRate;

    // Getter and Setter methods
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(long totalClasses) {
        this.totalClasses = totalClasses;
    }

    public long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public long getTotalAssignments() {
        return totalAssignments;
    }

    public void setTotalAssignments(long totalAssignments) {
        this.totalAssignments = totalAssignments;
    }

    public long getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(long totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public Map<String, Long> getUserByRole() {
        return userByRole;
    }

    public void setUserByRole(Map<String, Long> userByRole) {
        this.userByRole = userByRole;
    }

    public Map<Integer, Long> getQuestionsByDifficulty() {
        return questionsByDifficulty;
    }

    public void setQuestionsByDifficulty(Map<Integer, Long> questionsByDifficulty) {
        this.questionsByDifficulty = questionsByDifficulty;
    }

    public Map<String, Long> getSubmissionsByStatus() {
        return submissionsByStatus;
    }

    public void setSubmissionsByStatus(Map<String, Long> submissionsByStatus) {
        this.submissionsByStatus = submissionsByStatus;
    }

    public Map<String, Integer> getSubmissionTrend() {
        return submissionTrend;
    }

    public void setSubmissionTrend(Map<String, Integer> submissionTrend) {
        this.submissionTrend = submissionTrend;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }
}