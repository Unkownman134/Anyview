package com.anyview.controller;

import com.anyview.dto.ApiResponse;
import com.anyview.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/overall")
    public ApiResponse<?> getOverallStatistics() {
        try {
            var stats = statisticsService.getOverallStatistics();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error("获取统计数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/question/{id}")
    public ApiResponse<?> getQuestionStatistics(@PathVariable Long id) {
        try {
            var stats = statisticsService.getQuestionStatistics(id);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error("获取题目统计数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{id}")
    public ApiResponse<?> getUserStatistics(@PathVariable Long id) {
        try {
            var stats = statisticsService.getUserStatistics(id);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error("获取用户统计数据失败: " + e.getMessage());
        }
    }
}