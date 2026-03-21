package com.anyview.dto.analysis;

import java.util.List;
import java.util.Map;

public class ClassAnalysisDTO {
    private Long classId;
    private String className;
    private String schoolName;
    private String teacherName;

    private Long totalStudents;
    private Long activeStudents;
    private Long totalSubmissions;
    private Long acceptedSubmissions;
    private Double acceptanceRate;
    private Double averageScore;
    private Long totalAssignments;

    private List<StudentPerformance> studentPerformances;
    private List<SubmissionTrend> submissionTrend;
    private Map<String, Long> submissionsByDifficulty;
    private List<HotQuestion> hotQuestions;
    private List<Map<String, Object>> scoreDistribution;
    private List<AssignmentPerformance> assignmentPerformance;

    public static class StudentPerformance {
        private Long studentId;
        private String username;
        private String realName;
        private Long submissionCount;
        private Long solvedCount;
        private Double averageScore;
        private String rank;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
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

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }
    }

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

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getActiveStudents() {
        return activeStudents;
    }

    public void setActiveStudents(Long activeStudents) {
        this.activeStudents = activeStudents;
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

    public Long getTotalAssignments() {
        return totalAssignments;
    }

    public void setTotalAssignments(Long totalAssignments) {
        this.totalAssignments = totalAssignments;
    }

    public List<StudentPerformance> getStudentPerformances() {
        return studentPerformances;
    }

    public void setStudentPerformances(List<StudentPerformance> studentPerformances) {
        this.studentPerformances = studentPerformances;
    }

    public List<SubmissionTrend> getSubmissionTrend() {
        return submissionTrend;
    }

    public void setSubmissionTrend(List<SubmissionTrend> submissionTrend) {
        this.submissionTrend = submissionTrend;
    }

    public Map<String, Long> getSubmissionsByDifficulty() {
        return submissionsByDifficulty;
    }

    public void setSubmissionsByDifficulty(Map<String, Long> submissionsByDifficulty) {
        this.submissionsByDifficulty = submissionsByDifficulty;
    }

    public List<HotQuestion> getHotQuestions() {
        return hotQuestions;
    }

    public void setHotQuestions(List<HotQuestion> hotQuestions) {
        this.hotQuestions = hotQuestions;
    }

    public List<Map<String, Object>> getScoreDistribution() {
        return scoreDistribution;
    }

    public void setScoreDistribution(List<Map<String, Object>> scoreDistribution) {
        this.scoreDistribution = scoreDistribution;
    }

    public List<AssignmentPerformance> getAssignmentPerformance() {
        return assignmentPerformance;
    }

    public void setAssignmentPerformance(List<AssignmentPerformance> assignmentPerformance) {
        this.assignmentPerformance = assignmentPerformance;
    }

    public static class AssignmentPerformance {
        private Long assignmentId;
        private String assignmentName;
        private Integer totalQuestions;
        private Long submissionCount;
        private Double averageScore;
        private Double completionRate;
        private String deadline;

        public Long getAssignmentId() {
            return assignmentId;
        }

        public void setAssignmentId(Long assignmentId) {
            this.assignmentId = assignmentId;
        }

        public String getAssignmentName() {
            return assignmentName;
        }

        public void setAssignmentName(String assignmentName) {
            this.assignmentName = assignmentName;
        }

        public Integer getTotalQuestions() {
            return totalQuestions;
        }

        public void setTotalQuestions(Integer totalQuestions) {
            this.totalQuestions = totalQuestions;
        }

        public Long getSubmissionCount() {
            return submissionCount;
        }

        public void setSubmissionCount(Long submissionCount) {
            this.submissionCount = submissionCount;
        }

        public Double getAverageScore() {
            return averageScore;
        }

        public void setAverageScore(Double averageScore) {
            this.averageScore = averageScore;
        }

        public Double getCompletionRate() {
            return completionRate;
        }

        public void setCompletionRate(Double completionRate) {
            this.completionRate = completionRate;
        }

        public String getDeadline() {
            return deadline;
        }

        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }
    }
}
