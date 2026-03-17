package com.anyview.service;

import com.anyview.dto.StatisticsDTO;
import com.anyview.entity.SubmissionStatus;
import com.anyview.repository.AssignmentRepository;
import com.anyview.repository.ClassRepository;
import com.anyview.repository.QuestionRepository;
import com.anyview.repository.SubmissionRepository;
import com.anyview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    public StatisticsDTO getStatistics() {
        StatisticsDTO stats = new StatisticsDTO();

        // 总用户数
        stats.setTotalUsers(userRepository.count());

        // 总班级数
        stats.setTotalClasses(classRepository.count());

        // 总题目数
        stats.setTotalQuestions(questionRepository.count());

        // 总作业数
        stats.setTotalAssignments(assignmentRepository.count());

        // 总提交数
        stats.setTotalSubmissions(submissionRepository.count());

        // 按角色统计用户
        Map<String, Long> userByRole = new HashMap<>();
        userByRole.put("student", userRepository.countByRole("STUDENT"));
        userByRole.put("teacher", userRepository.countByRole("TEACHER"));
        userByRole.put("admin", userRepository.countByRole("ADMIN"));
        stats.setUserByRole(userByRole);

        // 按难度统计题目
        Map<Integer, Long> questionsByDifficulty = new HashMap<>();
        String[] difficulties = {"easy", "medium", "hard"};
        for (int i = 0; i < difficulties.length; i++) {
            questionsByDifficulty.put(i + 1, questionRepository.countByDifficulty(difficulties[i]));
        }
        stats.setQuestionsByDifficulty(questionsByDifficulty);

        // 按状态统计提交
        Map<String, Long> submissionsByStatus = new HashMap<>();
        for (SubmissionStatus status : SubmissionStatus.values()) {
            submissionsByStatus.put(status.name(), submissionRepository.countByStatus(status));
        }
        stats.setSubmissionsByStatus(submissionsByStatus);

        // 最近7天提交趋势
        Map<String, Integer> submissionTrend = new HashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            submissionTrend.put(date.toString(), 0); // 暂时返回0，实际应该查询数据库
        }
        stats.setSubmissionTrend(submissionTrend);

        return stats;
    }

    // 为了兼容StatisticsController
    public StatisticsDTO getOverallStatistics() {
        return getStatistics();
    }

    // 为了兼容StatisticsController
    public StatisticsDTO getQuestionStatistics(Long questionId) {
        return getStatistics();
    }

    // 为了兼容StatisticsController
    public StatisticsDTO getUserStatistics(Long userId) {
        return getStatistics();
    }
}
