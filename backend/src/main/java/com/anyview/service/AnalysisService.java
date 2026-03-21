package com.anyview.service;

import com.anyview.dto.analysis.ClassAnalysisDTO;
import com.anyview.dto.analysis.QuestionAnalysisDTO;
import com.anyview.dto.analysis.SystemAnalysisDTO;
import com.anyview.dto.analysis.UserAnalysisDTO;
import com.anyview.entity.*;
import com.anyview.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ClassStudentRepository classStudentRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    public SystemAnalysisDTO getSystemAnalysis(Integer days) {
        SystemAnalysisDTO dto = new SystemAnalysisDTO();

        LocalDateTime startDate = LocalDate.now().minusDays(days).atStartOfDay();

        dto.setOverview(getOverviewStats(startDate));
        dto.setUserTrend(getUserTrend(days));
        dto.setSubmissionTrend(getSubmissionTrend(days));
        dto.setSubmissionsByHour(getSubmissionsByHour(startDate));
        dto.setSubmissionsByDayOfWeek(getSubmissionsByDayOfWeek(startDate));
        dto.setErrorTypeStats(getErrorTypeStats(startDate));
        dto.setHotQuestions(getHotQuestions(startDate));
        dto.setMostActiveUsers(getMostActiveUsers(startDate));

        return dto;
    }

    public UserAnalysisDTO getUserAnalysis(Long userId) {
        UserAnalysisDTO dto = new UserAnalysisDTO();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return dto;
        }

        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());

        List<Submission> submissions = submissionRepository.findByStudentId(userId);

        dto.setTotalSubmissions((long) submissions.size());
        dto.setAcceptedSubmissions(submissions.stream().filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED).count());
        dto.setFailedSubmissions(submissions.stream().filter(s -> isFailedStatus(s.getStatus())).count());

        if (!submissions.isEmpty()) {
            double acceptanceRate = (dto.getAcceptedSubmissions() * 100.0) / dto.getTotalSubmissions();
            dto.setAcceptanceRate(acceptanceRate);

            double avgScore = submissions.stream().mapToInt(Submission::getScore).average().orElse(0.0);
            dto.setAverageScore(avgScore);
        }

        Set<Long> attemptedQuestions = submissions.stream().map(Submission::getQuestionId).collect(Collectors.toSet());
        dto.setTotalQuestionsAttempted((long) attemptedQuestions.size());

        Set<Long> solvedQuestions = submissions.stream()
                .filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED)
                .map(Submission::getQuestionId)
                .collect(Collectors.toSet());
        dto.setTotalQuestionsSolved((long) solvedQuestions.size());

        dto.setActivityTrend(getUserActivityTrend(userId, 30));
        dto.setSubmissionsByDifficulty(getUserSubmissionsByDifficulty(userId));
        dto.setQuestionProgress(getUserQuestionProgress(userId));

        return dto;
    }

    public QuestionAnalysisDTO getQuestionAnalysis(Long questionId) {
        QuestionAnalysisDTO dto = new QuestionAnalysisDTO();

        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            return dto;
        }

        dto.setQuestionId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setDifficulty(question.getDifficulty());
        dto.setType(question.getType());
        dto.setTotalScore(question.getScore());

        List<Submission> submissions = submissionRepository.findByQuestionId(questionId);

        dto.setTotalSubmissions((long) submissions.size());
        dto.setAcceptedSubmissions(submissions.stream().filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED).count());

        if (!submissions.isEmpty()) {
            double acceptanceRate = (dto.getAcceptedSubmissions() * 100.0) / dto.getTotalSubmissions();
            dto.setAcceptanceRate(acceptanceRate);

            double avgScore = submissions.stream().mapToInt(Submission::getScore).average().orElse(0.0);
            dto.setAverageScore(avgScore);
        }

        dto.setFailedSubmissions(submissions.stream().filter(s -> isFailedStatus(s.getStatus())).count());
        dto.setUniqueAttemptUsers(submissions.stream().map(Submission::getStudentId).distinct().count());
        dto.setUniqueSolvedUsers(submissions.stream()
                .filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED)
                .map(Submission::getStudentId)
                .distinct()
                .count());

        dto.setSubmissionTrend(getQuestionSubmissionTrend(questionId, 30));
        dto.setErrorTypeStats(getQuestionErrorTypeStats(questionId));
        dto.setCommonMistakes(getQuestionCommonMistakes(questionId));
        dto.setScoreDistribution(getQuestionScoreDistribution(submissions));

        return dto;
    }

    public ClassAnalysisDTO getClassAnalysis(Long classId) {
        ClassAnalysisDTO dto = new ClassAnalysisDTO();

        ClassInfo classInfo = classRepository.findById(classId).orElse(null);
        if (classInfo == null) {
            return dto;
        }

        dto.setClassId(classInfo.getId());
        dto.setClassName(classInfo.getClassName());

        if (classInfo.getSchool() != null) {
            dto.setSchoolName(classInfo.getSchool().getName());
        }

        if (classInfo.getTeacher() != null) {
            dto.setTeacherName(classInfo.getTeacher().getRealName());
        }

        List<ClassStudent> classStudents = classStudentRepository.findByClassInfoId(classId);
        dto.setTotalStudents((long) classStudents.size());

        List<Assignment> assignments = assignmentRepository.findByClassInfoId(classId);
        dto.setTotalAssignments((long) assignments.size());

        List<Long> studentIds = classStudents.stream()
                .map(cs -> cs.getStudent().getId())
                .collect(Collectors.toList());

        List<Submission> allSubmissions = studentIds.stream()
                .flatMap(studentId -> submissionRepository.findByStudentId(studentId).stream())
                .collect(Collectors.toList());

        dto.setTotalSubmissions((long) allSubmissions.size());
        dto.setAcceptedSubmissions(allSubmissions.stream().filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED).count());

        if (!allSubmissions.isEmpty()) {
            double acceptanceRate = (dto.getAcceptedSubmissions() * 100.0) / dto.getTotalSubmissions();
            dto.setAcceptanceRate(acceptanceRate);

            double avgScore = allSubmissions.stream().mapToInt(Submission::getScore).average().orElse(0.0);
            dto.setAverageScore(avgScore);
        }

        dto.setStudentPerformances(getClassStudentPerformances(studentIds));
        dto.setSubmissionTrend(getClassSubmissionTrend(studentIds, 30));
        dto.setHotQuestions(getClassHotQuestions(studentIds));
        
        List<Map<String, Object>> scoreDistribution = getClassScoreDistribution(allSubmissions);
        System.out.println("Score Distribution: " + scoreDistribution);
        dto.setScoreDistribution(scoreDistribution);
        
        dto.setAssignmentPerformance(getClassAssignmentPerformance(classId));

        return dto;
    }

    public void recordLoginActivity() {
    }

    private SystemAnalysisDTO.OverviewStats getOverviewStats(LocalDateTime startDate) {
        SystemAnalysisDTO.OverviewStats stats = new SystemAnalysisDTO.OverviewStats();

        stats.setTotalUsers(userRepository.count());
        stats.setTotalSubmissions(submissionRepository.count());

        Double avgScore = submissionRepository.findAverageScore();
        stats.setAverageScore(avgScore != null ? avgScore : 0.0);

        long totalSubmissions = stats.getTotalSubmissions();
        long acceptedSubmissions = submissionRepository.countByStatus(SubmissionStatus.ACCEPTED);
        double acceptanceRate = totalSubmissions > 0 ? (acceptedSubmissions * 100.0) / totalSubmissions : 0.0;
        stats.setAcceptanceRate(acceptanceRate);

        return stats;
    }

    private List<SystemAnalysisDTO.TrendData> getUserTrend(Integer days) {
        List<SystemAnalysisDTO.TrendData> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            SystemAnalysisDTO.TrendData data = new SystemAnalysisDTO.TrendData();
            data.setDate(date.format(formatter));

            List<Submission> submissions = submissionRepository.findByCreatedAtBetween(startOfDay, endOfDay);
            long activeUsers = submissions.stream().map(Submission::getStudentId).distinct().count();
            data.setValue(activeUsers);

            long newUsers = userRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            data.setSecondaryValue(newUsers);

            trend.add(data);
        }

        return trend;
    }

    private List<SystemAnalysisDTO.TrendData> getSubmissionTrend(Integer days) {
        List<SystemAnalysisDTO.TrendData> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            SystemAnalysisDTO.TrendData data = new SystemAnalysisDTO.TrendData();
            data.setDate(date.format(formatter));

            long totalSubmissions = submissionRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            data.setValue(totalSubmissions);

            long acceptedSubmissions = submissionRepository.countByCreatedAtBetweenAndStatus(startOfDay, endOfDay, SubmissionStatus.ACCEPTED);
            data.setSecondaryValue(acceptedSubmissions);

            trend.add(data);
        }

        return trend;
    }

    private Map<String, Long> getSubmissionsByHour(LocalDateTime startDate) {
        Map<String, Long> result = new LinkedHashMap<>();
        List<Submission> submissions = submissionRepository.findByCreatedAtAfter(startDate);

        for (int hour = 0; hour < 24; hour++) {
            result.put(String.valueOf(hour), 0L);
        }

        for (Submission submission : submissions) {
            int hour = submission.getCreatedAt().getHour();
            result.put(String.valueOf(hour), result.getOrDefault(String.valueOf(hour), 0L) + 1);
        }

        return result;
    }

    private Map<String, Long> getSubmissionsByDayOfWeek(LocalDateTime startDate) {
        Map<String, Long> result = new LinkedHashMap<>();
        List<Submission> submissions = submissionRepository.findByCreatedAtAfter(startDate);

        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        for (String day : days) {
            result.put(day, 0L);
        }

        for (Submission submission : submissions) {
            int dayOfWeek = submission.getCreatedAt().getDayOfWeek().getValue();
            String dayName = days[(dayOfWeek + 6) % 7];
            result.put(dayName, result.getOrDefault(dayName, 0L) + 1);
        }

        return result;
    }

    private List<SystemAnalysisDTO.ErrorTypeStat> getErrorTypeStats(LocalDateTime startDate) {
        List<SystemAnalysisDTO.ErrorTypeStat> stats = new ArrayList<>();
        List<Submission> submissions = submissionRepository.findByCreatedAtAfter(startDate);

        Map<String, Long> errorCounts = new HashMap<>();
        for (Submission submission : submissions) {
            if (isFailedStatus(submission.getStatus())) {
                String errorType = determineErrorType(submission);
                errorCounts.put(errorType, errorCounts.getOrDefault(errorType, 0L) + 1);
            }
        }

        for (Map.Entry<String, Long> entry : errorCounts.entrySet()) {
            SystemAnalysisDTO.ErrorTypeStat stat = new SystemAnalysisDTO.ErrorTypeStat();
            stat.setErrorType(entry.getKey());
            stat.setCount(entry.getValue());
            stats.add(stat);
        }

        return stats;
    }

    private List<SystemAnalysisDTO.HotQuestion> getHotQuestions(LocalDateTime startDate) {
        List<SystemAnalysisDTO.HotQuestion> hotQuestions = new ArrayList<>();
        List<Submission> submissions = submissionRepository.findByCreatedAtAfter(startDate);

        Map<Long, Long> questionCounts = submissions.stream()
                .collect(Collectors.groupingBy(Submission::getQuestionId, Collectors.counting()));

        List<Map.Entry<Long, Long>> sorted = questionCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        for (Map.Entry<Long, Long> entry : sorted) {
            Question question = questionRepository.findById(entry.getKey()).orElse(null);
            if (question != null) {
                SystemAnalysisDTO.HotQuestion hotQuestion = new SystemAnalysisDTO.HotQuestion();
                hotQuestion.setId(question.getId());
                hotQuestion.setTitle(question.getTitle());
                hotQuestion.setDifficulty(question.getDifficulty());
                hotQuestion.setSubmissionCount(entry.getValue());

                long acceptedCount = submissionRepository.countByQuestionIdAndStatus(question.getId(), SubmissionStatus.ACCEPTED);
                double acceptanceRate = entry.getValue() > 0 ? (acceptedCount * 100.0) / entry.getValue() : 0.0;
                hotQuestion.setAcceptanceRate(acceptanceRate);

                hotQuestions.add(hotQuestion);
            }
        }

        return hotQuestions;
    }

    private List<SystemAnalysisDTO.ActiveUser> getMostActiveUsers(LocalDateTime startDate) {
        List<SystemAnalysisDTO.ActiveUser> activeUsers = new ArrayList<>();
        List<Submission> submissions = submissionRepository.findByCreatedAtAfter(startDate);

        Map<Long, List<Submission>> userSubmissions = submissions.stream()
                .collect(Collectors.groupingBy(Submission::getStudentId));

        List<Map.Entry<Long, List<Submission>>> sorted = userSubmissions.entrySet().stream()
                .sorted(Map.Entry.<Long, List<Submission>>comparingByValue(Comparator.comparingInt(List::size)).reversed())
                .limit(10)
                .collect(Collectors.toList());

        for (Map.Entry<Long, List<Submission>> entry : sorted) {
            User user = userRepository.findById(entry.getKey()).orElse(null);
            if (user != null) {
                SystemAnalysisDTO.ActiveUser activeUser = new SystemAnalysisDTO.ActiveUser();
                activeUser.setId(user.getId());
                activeUser.setUsername(user.getUsername());
                activeUser.setRealName(user.getRealName());
                activeUser.setSubmissionCount((long) entry.getValue().size());

                long solvedCount = entry.getValue().stream()
                        .filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED)
                        .map(Submission::getQuestionId)
                        .distinct()
                        .count();
                activeUser.setSolvedCount(solvedCount);

                double avgScore = entry.getValue().stream()
                        .mapToInt(Submission::getScore)
                        .average()
                        .orElse(0.0);
                activeUser.setAverageScore(avgScore);

                activeUsers.add(activeUser);
            }
        }

        return activeUsers;
    }

    private List<UserAnalysisDTO.ActivityTrend> getUserActivityTrend(Long userId, Integer days) {
        List<UserAnalysisDTO.ActivityTrend> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            UserAnalysisDTO.ActivityTrend data = new UserAnalysisDTO.ActivityTrend();
            data.setDate(date.format(formatter));

            List<Submission> submissions = submissionRepository.findByStudentId(userId);
            long submissionCount = submissions.stream()
                    .filter(s -> s.getCreatedAt().isAfter(startOfDay) && s.getCreatedAt().isBefore(endOfDay))
                    .count();
            data.setSubmissionCount(submissionCount);

            data.setLoginCount(0L);
            data.setTimeSpentMinutes(0L);

            trend.add(data);
        }

        return trend;
    }

    private Map<String, Long> getUserSubmissionsByDifficulty(Long userId) {
        Map<String, Long> result = new HashMap<>();
        List<Submission> submissions = submissionRepository.findByStudentId(userId);

        for (Submission submission : submissions) {
            Question question = questionRepository.findById(submission.getQuestionId()).orElse(null);
            if (question != null) {
                String difficulty = question.getDifficulty();
                result.put(difficulty, result.getOrDefault(difficulty, 0L) + 1);
            }
        }

        return result;
    }

    private List<UserAnalysisDTO.QuestionProgress> getUserQuestionProgress(Long userId) {
        List<UserAnalysisDTO.QuestionProgress> progress = new ArrayList<>();
        List<Submission> submissions = submissionRepository.findByStudentId(userId);

        Map<Long, List<Submission>> questionSubmissions = submissions.stream()
                .collect(Collectors.groupingBy(Submission::getQuestionId));

        for (Map.Entry<Long, List<Submission>> entry : questionSubmissions.entrySet()) {
            Question question = questionRepository.findById(entry.getKey()).orElse(null);
            if (question != null) {
                UserAnalysisDTO.QuestionProgress qp = new UserAnalysisDTO.QuestionProgress();
                qp.setQuestionId(question.getId());
                qp.setQuestionTitle(question.getTitle());
                qp.setDifficulty(question.getDifficulty());
                qp.setAttempts(entry.getValue().size());

                boolean solved = entry.getValue().stream()
                        .anyMatch(s -> s.getStatus() == SubmissionStatus.ACCEPTED);
                qp.setSolved(solved);

                int bestScore = entry.getValue().stream()
                        .mapToInt(Submission::getScore)
                        .max()
                        .orElse(0);
                qp.setBestScore(bestScore);

                String lastSubmitTime = entry.getValue().stream()
                        .map(Submission::getCreatedAt)
                        .max(LocalDateTime::compareTo)
                        .map(dt -> dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .orElse("");
                qp.setLastSubmitTime(lastSubmitTime);

                progress.add(qp);
            }
        }

        return progress;
    }

    private List<QuestionAnalysisDTO.SubmissionTrend> getQuestionSubmissionTrend(Long questionId, Integer days) {
        List<QuestionAnalysisDTO.SubmissionTrend> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            QuestionAnalysisDTO.SubmissionTrend data = new QuestionAnalysisDTO.SubmissionTrend();
            data.setDate(date.format(formatter));

            List<Submission> submissions = submissionRepository.findByQuestionId(questionId);
            long submissionCount = submissions.stream()
                    .filter(s -> s.getCreatedAt().isAfter(startOfDay) && s.getCreatedAt().isBefore(endOfDay))
                    .count();
            data.setSubmissionCount(submissionCount);

            long acceptedCount = submissions.stream()
                    .filter(s -> s.getCreatedAt().isAfter(startOfDay) && s.getCreatedAt().isBefore(endOfDay))
                    .filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED)
                    .count();
            data.setAcceptedCount(acceptedCount);

            trend.add(data);
        }

        return trend;
    }

    private List<QuestionAnalysisDTO.ErrorTypeStat> getQuestionErrorTypeStats(Long questionId) {
        List<QuestionAnalysisDTO.ErrorTypeStat> stats = new ArrayList<>();
        List<Submission> submissions = submissionRepository.findByQuestionId(questionId);

        Map<String, Long> errorCounts = new HashMap<>();
        for (Submission submission : submissions) {
            if (isFailedStatus(submission.getStatus())) {
                String errorType = determineErrorType(submission);
                errorCounts.put(errorType, errorCounts.getOrDefault(errorType, 0L) + 1);
            }
        }

        for (Map.Entry<String, Long> entry : errorCounts.entrySet()) {
            QuestionAnalysisDTO.ErrorTypeStat stat = new QuestionAnalysisDTO.ErrorTypeStat();
            stat.setErrorType(entry.getKey());
            stat.setCount(entry.getValue());
            stats.add(stat);
        }

        return stats;
    }

    private List<QuestionAnalysisDTO.CommonMistake> getQuestionCommonMistakes(Long questionId) {
        List<QuestionAnalysisDTO.CommonMistake> mistakes = new ArrayList<>();
        return mistakes;
    }

    private List<Map<String, Object>> getQuestionScoreDistribution(List<Submission> submissions) {
        List<Map<String, Object>> distribution = new ArrayList<>();

        if (submissions.isEmpty()) {
            return distribution;
        }

        int[] ranges = {0, 60, 70, 80, 90, 100};
        String[] labels = {"0-59", "60-69", "70-79", "80-89", "90-100"};

        for (int i = 0; i < ranges.length - 1; i++) {
            int lower = ranges[i];
            int upper = ranges[i + 1];
            long count = submissions.stream()
                    .filter(s -> s.getScore() >= lower && s.getScore() < upper)
                    .count();

            Map<String, Object> range = new HashMap<>();
            range.put("range", labels[i]);
            range.put("count", count);
            distribution.add(range);
        }

        return distribution;
    }

    private List<ClassAnalysisDTO.StudentPerformance> getClassStudentPerformances(List<Long> studentIds) {
        List<ClassAnalysisDTO.StudentPerformance> performances = new ArrayList<>();

        for (Long studentId : studentIds) {
            User user = userRepository.findById(studentId).orElse(null);
            if (user != null) {
                List<Submission> submissions = submissionRepository.findByStudentId(studentId);

                ClassAnalysisDTO.StudentPerformance perf = new ClassAnalysisDTO.StudentPerformance();
                perf.setStudentId(user.getId());
                perf.setUsername(user.getUsername());
                perf.setRealName(user.getRealName());
                perf.setSubmissionCount((long) submissions.size());

                long solvedCount = submissions.stream()
                        .filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED)
                        .map(Submission::getQuestionId)
                        .distinct()
                        .count();
                perf.setSolvedCount(solvedCount);

                double avgScore = submissions.stream()
                        .mapToInt(Submission::getScore)
                        .average()
                        .orElse(0.0);
                perf.setAverageScore(avgScore);

                performances.add(perf);
            }
        }

        performances.sort((a, b) -> Long.compare(b.getSolvedCount(), a.getSolvedCount()));

        for (int i = 0; i < performances.size(); i++) {
            performances.get(i).setRank(String.valueOf(i + 1));
        }

        return performances;
    }

    private List<ClassAnalysisDTO.SubmissionTrend> getClassSubmissionTrend(List<Long> studentIds, Integer days) {
        List<ClassAnalysisDTO.SubmissionTrend> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            ClassAnalysisDTO.SubmissionTrend data = new ClassAnalysisDTO.SubmissionTrend();
            data.setDate(date.format(formatter));

            long submissionCount = 0;
            long acceptedCount = 0;

            for (Long studentId : studentIds) {
                List<Submission> submissions = submissionRepository.findByStudentId(studentId);
                submissionCount += submissions.stream()
                        .filter(s -> s.getCreatedAt().isAfter(startOfDay) && s.getCreatedAt().isBefore(endOfDay))
                        .count();
                acceptedCount += submissions.stream()
                        .filter(s -> s.getCreatedAt().isAfter(startOfDay) && s.getCreatedAt().isBefore(endOfDay))
                        .filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED)
                        .count();
            }

            data.setSubmissionCount(submissionCount);
            data.setAcceptedCount(acceptedCount);

            trend.add(data);
        }

        return trend;
    }

    private List<ClassAnalysisDTO.HotQuestion> getClassHotQuestions(List<Long> studentIds) {
        List<ClassAnalysisDTO.HotQuestion> hotQuestions = new ArrayList<>();

        Map<Long, Long> questionCounts = new HashMap<>();
        for (Long studentId : studentIds) {
            List<Submission> submissions = submissionRepository.findByStudentId(studentId);
            for (Submission submission : submissions) {
                questionCounts.put(submission.getQuestionId(),
                        questionCounts.getOrDefault(submission.getQuestionId(), 0L) + 1);
            }
        }

        List<Map.Entry<Long, Long>> sorted = questionCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        for (Map.Entry<Long, Long> entry : sorted) {
            Question question = questionRepository.findById(entry.getKey()).orElse(null);
            if (question != null) {
                ClassAnalysisDTO.HotQuestion hotQuestion = new ClassAnalysisDTO.HotQuestion();
                hotQuestion.setId(question.getId());
                hotQuestion.setTitle(question.getTitle());
                hotQuestion.setDifficulty(question.getDifficulty());
                hotQuestion.setSubmissionCount(entry.getValue());

                long acceptedCount = submissionRepository.countByQuestionIdAndStatus(question.getId(), SubmissionStatus.ACCEPTED);
                double acceptanceRate = entry.getValue() > 0 ? (acceptedCount * 100.0) / entry.getValue() : 0.0;
                hotQuestion.setAcceptanceRate(acceptanceRate);

                hotQuestions.add(hotQuestion);
            }
        }

        return hotQuestions;
    }

    private String determineErrorType(Submission submission) {
        if (submission.getCompileError() != null && !submission.getCompileError().isEmpty()) {
            return "编译错误";
        }
        if (submission.getRuntimeError() != null && !submission.getRuntimeError().isEmpty()) {
            return "运行时错误";
        }
        if (submission.getScore() == 0) {
            return "答案错误";
        }
        if (submission.getScore() > 0 && submission.getScore() < submission.getQuestion().getScore()) {
            return "部分正确";
        }
        return "其他错误";
    }

    private boolean isFailedStatus(SubmissionStatus status) {
        return status == SubmissionStatus.WRONG_ANSWER ||
               status == SubmissionStatus.TIME_LIMIT_EXCEEDED ||
               status == SubmissionStatus.MEMORY_LIMIT_EXCEEDED ||
               status == SubmissionStatus.COMPILATION_ERROR ||
               status == SubmissionStatus.RUNTIME_ERROR;
    }

    private List<Map<String, Object>> getClassScoreDistribution(List<Submission> submissions) {
        List<Map<String, Object>> distribution = new ArrayList<>();

        if (submissions.isEmpty()) {
            return distribution;
        }

        int[] ranges = {0, 60, 70, 80, 90, 101};
        String[] labels = {"0-59", "60-69", "70-79", "80-89", "90-100"};

        for (int i = 0; i < ranges.length - 1; i++) {
            int lower = ranges[i];
            int upper = ranges[i + 1];
            long count = submissions.stream()
                    .filter(s -> s.getScore() >= lower && s.getScore() < upper)
                    .count();

            Map<String, Object> range = new HashMap<>();
            range.put("range", labels[i]);
            range.put("count", count);
            distribution.add(range);
        }

        return distribution;
    }

    private List<ClassAnalysisDTO.AssignmentPerformance> getClassAssignmentPerformance(Long classId) {
        List<ClassAnalysisDTO.AssignmentPerformance> performances = new ArrayList<>();

        List<Assignment> assignments = assignmentRepository.findByClassInfoIdWithQuestions(classId);

        for (Assignment assignment : assignments) {
            List<AssignmentQuestion> questions = assignment.getAssignmentQuestions();
            if (questions == null || questions.isEmpty()) {
                continue;
            }

            int totalQuestions = questions.size();

            List<Long> questionIds = questions.stream()
                    .map(AssignmentQuestion::getQuestionId)
                    .collect(Collectors.toList());

            List<Submission> submissions = questionIds.stream()
                    .flatMap(questionId -> submissionRepository.findByQuestionId(questionId).stream())
                    .filter(s -> {
                        User student = userRepository.findById(s.getStudentId()).orElse(null);
                        if (student == null) return false;
                        List<ClassStudent> classStudents = classStudentRepository.findByStudentId(student.getId());
                        return classStudents.stream().anyMatch(cs -> cs.getClassInfo().getId().equals(classId));
                    })
                    .collect(Collectors.toList());

            long submissionCount = submissions.stream()
                    .map(Submission::getStudentId)
                    .distinct()
                    .count();

            double averageScore = submissions.isEmpty() ? 0.0 :
                    submissions.stream()
                            .mapToInt(Submission::getScore)
                            .average()
                            .orElse(0.0);

            double completionRate = submissionCount == 0 ? 0.0 :
                    (submissionCount * 100.0) / totalQuestions;

            ClassAnalysisDTO.AssignmentPerformance perf = new ClassAnalysisDTO.AssignmentPerformance();
            perf.setAssignmentId(assignment.getId());
            perf.setAssignmentName(assignment.getTitle());
            perf.setTotalQuestions(totalQuestions);
            perf.setSubmissionCount(submissionCount);
            perf.setAverageScore(averageScore);
            perf.setCompletionRate(completionRate);
            perf.setDeadline(assignment.getEndTime() != null ? assignment.getEndTime().toString() : null);

            performances.add(perf);
        }

        performances.sort((a, b) -> {
            if (a.getDeadline() == null && b.getDeadline() == null) return 0;
            if (a.getDeadline() == null) return 1;
            if (b.getDeadline() == null) return -1;
            return b.getDeadline().compareTo(a.getDeadline());
        });

        return performances;
    }
}
