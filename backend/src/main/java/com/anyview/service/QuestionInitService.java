package com.anyview.service;

import com.anyview.entity.Question;
import com.anyview.entity.User;
import com.anyview.repository.QuestionRepository;
import com.anyview.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionInitService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void initQuestions() {
        if (questionRepository.count() == 0) {
            System.out.println("初始化题库...");
            List<Question> questions = new ArrayList<>();

            // 获取管理员用户
            User admin = userRepository.findByUsername("admin").orElse(null);
            if (admin == null) {
                System.out.println("未找到管理员用户，跳过题库初始化");
                return;
            }

            // 题目1：两数之和
            Question q1 = new Question();
            q1.setTitle("两数之和");
            q1.setContent("给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值 target 的那两个整数，并返回它们的数组下标。\n\n你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。\n\n示例：\n输入：nums = [2,7,11,15], target = 9\n输出：[0,1]\n解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。");
            q1.setType("programming");
            q1.setAnswer("class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        for (int i = 0; i < nums.length; i++) {\n            for (int j = i + 1; j < nums.length; j++) {\n                if (nums[i] + nums[j] == target) {\n                    return new int[]{i, j};\n                }\n            }\n        }\n        return new int[]{};\n    }\n}");
            q1.setScore(10);
            q1.setDifficulty("easy");
            q1.setAnalysis("这道题可以使用暴力枚举的方法，遍历所有可能的两数组合，找到和为target的那两个数。时间复杂度为O(n²)，空间复杂度为O(1)。");
            q1.setTemplateCode("class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        \n    }\n}");
            q1.setTestCases("[[2,7,11,15],9,[0,1]]\n[[3,2,4],6,[1,2]]\n[[3,3],6,[0,1]]");
            q1.setCreator(admin);
            questions.add(q1);

            // 题目2：反转字符串
            Question q2 = new Question();
            q2.setTitle("反转字符串");
            q2.setContent("编写一个函数，其作用是将输入的字符串反转过来。输入字符串以字符数组 s 的形式给出。\n\n不要给另外的数组分配额外的空间，你必须原地修改输入数组、使用 O(1) 的额外空间解决这一问题。\n\n示例：\n输入：s = [\"h\",\"e\",\"l\",\"l\",\"o\"]\n输出：[\"o\",\"l\",\"l\",\"e\",\"h\"]");
            q2.setType("programming");
            q2.setAnswer("class Solution {\n    public void reverseString(char[] s) {\n        int left = 0, right = s.length - 1;\n        while (left < right) {\n            char temp = s[left];\n            s[left] = s[right];\n            s[right] = temp;\n            left++;\n            right--;\n        }\n    }\n}");
            q2.setScore(10);
            q2.setDifficulty("easy");
            q2.setAnalysis("这道题可以使用双指针的方法，一个指针指向数组的开始，另一个指针指向数组的结束，然后交换这两个指针指向的元素，直到两个指针相遇。时间复杂度为O(n)，空间复杂度为O(1)。");
            q2.setTemplateCode("class Solution {\n    public void reverseString(char[] s) {\n        \n    }\n}");
            q2.setTestCases("[[\"h\",\"e\",\"l\",\"l\",\"o\"],[\"o\",\"l\",\"l\",\"e\",\"h\"]]\n[[\"H\",\"a\",\"n\",\"n\",\"a\",\"h\"],[\"h\",\"a\",\"n\",\"n\",\"a\",\"H\"]]");
            q2.setCreator(admin);
            questions.add(q2);

            // 题目3：斐波那契数
            Question q3 = new Question();
            q3.setTitle("斐波那契数");
            q3.setContent("斐波那契数，通常用 F(n) 表示，形成的序列称为 斐波那契数列 。该数列由 0 和 1 开始，后面的每一项数字都是前面两项数字的和。也就是：\n\nF(0) = 0，F(1) = 1\nF(n) = F(n - 1) + F(n - 2)，其中 n > 1\n\n给你 n ，请计算 F(n) 。\n\n示例：\n输入：n = 2\n输出：1\n解释：F(2) = F(1) + F(0) = 1 + 0 = 1");
            q3.setType("programming");
            q3.setAnswer("class Solution {\n    public int fib(int n) {\n        if (n == 0) return 0;\n        if (n == 1) return 1;\n        int a = 0, b = 1;\n        for (int i = 2; i <= n; i++) {\n            int temp = a + b;\n            a = b;\n            b = temp;\n        }\n        return b;\n    }\n}");
            q3.setScore(10);
            q3.setDifficulty("easy");
            q3.setAnalysis("这道题可以使用动态规划的方法，用两个变量来保存前两个斐波那契数，然后迭代计算出第n个斐波那契数。时间复杂度为O(n)，空间复杂度为O(1)。");
            q3.setTemplateCode("class Solution {\n    public int fib(int n) {\n        \n    }\n}");
            q3.setTestCases("[2,1]\n[3,2]\n[4,3]\n[5,5]");
            q3.setCreator(admin);
            questions.add(q3);

            // 题目4：回文数
            Question q4 = new Question();
            q4.setTitle("回文数");
            q4.setContent("给你一个整数 x ，如果 x 是一个回文整数，返回 true ；否则，返回 false 。\n\n回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。\n\n示例：\n输入：x = 121\n输出：true\n解释：121 从左向右读和从右向左读都是 121");
            q4.setType("programming");
            q4.setAnswer("class Solution {\n    public boolean isPalindrome(int x) {\n        if (x < 0) return false;\n        int original = x;\n        int reversed = 0;\n        while (x > 0) {\n            int digit = x % 10;\n            reversed = reversed * 10 + digit;\n            x /= 10;\n        }\n        return original == reversed;\n    }\n}");
            q4.setScore(10);
            q4.setDifficulty("easy");
            q4.setAnalysis("这道题可以通过反转整数的后半部分，然后与前半部分进行比较来判断是否是回文数。时间复杂度为O(log n)，空间复杂度为O(1)。");
            q4.setTemplateCode("class Solution {\n    public boolean isPalindrome(int x) {\n        \n    }\n}");
            q4.setTestCases("[121,true]\n[-121,false]\n[10,false]\n[0,true]");
            q4.setCreator(admin);
            questions.add(q4);

            // 题目5：最大子数组和
            Question q5 = new Question();
            q5.setTitle("最大子数组和");
            q5.setContent("给你一个整数数组 nums ，请你找出一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。\n\n子数组 是数组中的一个连续部分。\n\n示例：\n输入：nums = [-2,1,-3,4,-1,2,1,-5,4]\n输出：6\n解释：连续子数组 [4,-1,2,1] 的和最大，为 6");
            q5.setType("programming");
            q5.setAnswer("class Solution {\n    public int maxSubArray(int[] nums) {\n        int maxSum = nums[0];\n        int currentSum = nums[0];\n        for (int i = 1; i < nums.length; i++) {\n            currentSum = Math.max(nums[i], currentSum + nums[i]);\n            maxSum = Math.max(maxSum, currentSum);\n        }\n        return maxSum;\n    }\n}");
            q5.setScore(10);
            q5.setDifficulty("medium");
            q5.setAnalysis("这道题可以使用动态规划的方法，用一个变量来保存当前的最大子数组和，另一个变量来保存全局的最大子数组和。时间复杂度为O(n)，空间复杂度为O(1)。");
            q5.setTemplateCode("class Solution {\n    public int maxSubArray(int[] nums) {\n        \n    }\n}");
            q5.setTestCases("[[-2,1,-3,4,-1,2,1,-5,4],6]\n[[1],1]\n[[5,4,-1,7,8],23]");
            q5.setCreator(admin);
            questions.add(q5);

            // 保存题目
            questionRepository.saveAll(questions);
            System.out.println("初始化题库完成，添加了 " + questions.size() + " 道编程题");
        } else {
            System.out.println("题库已初始化，跳过");
        }
    }
}
