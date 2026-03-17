-- Anyview 初始化数据脚本
-- 执行时间：2026-03-17
-- 说明：此脚本用于初始化管理员用户和示例题目数据

-- 清空现有数据（可选，谨慎使用）
-- DELETE FROM assignment_questions;
-- DELETE FROM assignments;
-- DELETE FROM submissions;
-- DELETE FROM questions;
-- DELETE FROM users WHERE username = 'admin';

-- ============================================
-- 1. 创建管理员用户
-- ============================================
INSERT INTO users (username, password, email, real_name, role, enabled, created_at, updated_at)
VALUES (
    'admin',
    '$2a$10$I7QotZiyAZW8CtM2Uy1AaeHoF4c09le6/E.m0yh/Mr1zRWpJr2j.u',
    '1@qq.com',
    '管理员',
    'ADMIN',
    1,
    NOW(),
    NOW()
);

-- ============================================
-- 2. 创建编程题
-- ============================================

-- 题目1：两数之和
INSERT INTO questions (title, type, description, options, answer, score, difficulty, analysis, template_code, sample_input, sample_output, test_cases, reference_solution, creator_id, time_limit, memory_limit, is_public, created_at, updated_at)
VALUES (
    '两数之和',
    'programming',
    '给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值 target 的那两个整数，并返回它们的数组下标。\n\n你可以假设每种输入只会对应一个答案，并且你不能使用相同的元素两次。\n\n示例：\n输入：nums = [2,7,11,15], target = 9\n输出：[0,1]\n解释：因为 nums[0] + nums[1] == 9，返回 [0, 1]',
    NULL,
    'class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        for (int i = 0; i < nums.length; i++) {\n            for (int j = i + 1; j < nums.length; j++) {\n                if (nums[i] + nums[j] == target) {\n                    return new int[]{i, j};\n                }\n            }\n        }\n        return new int[]{};\n    }\n}',
    10,
    'easy',
    '这道题可以使用暴力枚举的方法，遍历所有可能的两数组合，找到和为target的那两个数。时间复杂度为O(n²)，空间复杂度为O(1)。',
    'class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        \n    }\n}',
    '[2,7,11,15]',
    '9',
    '[[2,7,11,15],9,[0,1]]\n[[3,2,4],6,[1,2]]\n[[3,3],6,[0,1]]',
    'class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        for (int i = 0; i < nums.length; i++) {\n            for (int j = i + 1; j < nums.length; j++) {\n                if (nums[i] + nums[j] == target) {\n                    return new int[]{i, j};\n                }\n            }\n        }\n        return new int[]{};\n    }\n}',
    (SELECT id FROM users WHERE username = 'admin'),
    1000,
    1048576,
    1,
    NOW(),
    NOW()
);

-- 题目2：反转字符串
INSERT INTO questions (title, type, description, options, answer, score, difficulty, analysis, template_code, sample_input, sample_output, test_cases, reference_solution, creator_id, time_limit, memory_limit, is_public, created_at, updated_at)
VALUES (
    '反转字符串',
    'programming',
    '编写一个函数，其作用是将输入的字符串反转过来。输入字符串以字符数组 s 的形式给出。\n\n不要给另外的数组分配额外的空间，你必须原地修改输入数组、使用 O(1) 的额外空间解决这一问题。\n\n示例：\n输入：s = ["h","e","l","l","o"]\n输出：["o","l","l","e","h"]',
    NULL,
    'class Solution {\n    public void reverseString(char[] s) {\n        int left = 0, right = s.length - 1;\n        while (left < right) {\n            char temp = s[left];\n            s[left] = s[right];\n            s[right] = temp;\n            left++;\n            right--;\n        }\n    }\n}',
    10,
    'easy',
    '这道题可以使用双指针的方法，一个指针指向数组的开始，另一个指针指向数组的结束，然后交换这两个指针指向的元素，直到两个指针相遇。时间复杂度为O(n)，空间复杂度为O(1)。',
    'class Solution {\n    public void reverseString(char[] s) {\n        \n    }\n}',
    '["h","e","l","l","o"]',
    '["o","l","l","e","h"]',
    '[["h","e","l","l","o"],["o","l","l","e","h"]]\n[["H","a","n","n","a","h"],["h","a","n","n","a","H"]]',
    'class Solution {\n    public void reverseString(char[] s) {\n        int left = 0, right = s.length - 1;\n        while (left < right) {\n            char temp = s[left];\n            s[left] = s[right];\n            s[right] = temp;\n            left++;\n            right--;\n        }\n    }\n}',
    (SELECT id FROM users WHERE username = 'admin'),
    1000,
    1048576,
    1,
    NOW(),
    NOW()
);

-- 题目3：斐波那契数
INSERT INTO questions (title, type, description, options, answer, score, difficulty, analysis, template_code, sample_input, sample_output, test_cases, reference_solution, creator_id, time_limit, memory_limit, is_public, created_at, updated_at)
VALUES (
    '斐波那契数',
    'programming',
    '斐波那契数，通常用 F(n) 表示，形成的序列称为 斐波那契数列 。该数列由 0 和 1 开始，后面的每一项数字都是前面两项数字的和。也就是：\n\nF(0) = 0，F(1) = 1\nF(n) = F(n - 1) + F(n - 2)，其中 n > 1\n\n给你 n ，请计算 F(n) 。\n\n示例：\n输入：n = 2\n输出：1\n解释：F(2) = F(1) + F(0) = 1 + 0 = 1',
    NULL,
    'class Solution {\n    public int fib(int n) {\n        if (n == 0) return 0;\n        if (n == 1) return 1;\n        int a = 0, b = 1;\n        for (int i = 2; i <= n; i++) {\n            int temp = a + b;\n            a = b;\n            b = temp;\n        }\n        return b;\n    }\n}',
    10,
    'easy',
    '这道题可以使用动态规划的方法，用两个变量来保存前两个斐波那契数，然后迭代计算出第n个斐波那契数。时间复杂度为O(n)，空间复杂度为O(1)。',
    'class Solution {\n    public int fib(int n) {\n        \n    }\n}',
    '2',
    '1',
    '[2,1]\n[3,2]\n[4,3]\n[5,5]',
    'class Solution {\n    public int fib(int n) {\n        if (n == 0) return 0;\n        if (n == 1) return 1;\n        int a = 0, b = 1;\n        for (int i = 2; i <= n; i++) {\n            int temp = a + b;\n            a = b;\n            b = temp;\n        }\n        return b;\n    }\n}',
    (SELECT id FROM users WHERE username = 'admin'),
    1000,
    1048576,
    1,
    NOW(),
    NOW()
);

-- 题目4：回文数
INSERT INTO questions (title, type, description, options, answer, score, difficulty, analysis, template_code, sample_input, sample_output, test_cases, reference_solution, creator_id, time_limit, memory_limit, is_public, created_at, updated_at)
VALUES (
    '回文数',
    'programming',
    '给你一个整数 x ，如果 x 是一个回文整数，返回 true ；否则，返回 false 。\n\n回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。\n\n示例：\n输入：x = 121\n输出：true\n解释：121 从左向右读和从右向左读都是 121',
    NULL,
    'class Solution {\n    public boolean isPalindrome(int x) {\n        if (x < 0) return false;\n        int original = x;\n        int reversed = 0;\n        while (x > 0) {\n            int digit = x % 10;\n            reversed = reversed * 10 + digit;\n            x /= 10;\n        }\n        return original == reversed;\n    }\n}',
    10,
    'easy',
    '这道题可以通过反转整数的后半部分，然后与前半部分进行比较来判断是否是回文数。时间复杂度为O(log n)，空间复杂度为O(1)。',
    'class Solution {\n    public boolean isPalindrome(int x) {\n        \n    }\n}',
    '121',
    'true',
    '[121,true]\n[-121,false]\n[10,false]\n[0,true]',
    'class Solution {\n    public boolean isPalindrome(int x) {\n        if (x < 0) return false;\n        int original = x;\n        int reversed = 0;\n        while (x > 0) {\n            int digit = x % 10;\n            reversed = reversed * 10 + digit;\n            x /= 10;\n        }\n        return original == reversed;\n    }\n}',
    (SELECT id FROM users WHERE username = 'admin'),
    1000,
    1048576,
    1,
    NOW(),
    NOW()
);

-- 题目5：最大子数组和
INSERT INTO questions (title, type, description, options, answer, score, difficulty, analysis, template_code, sample_input, sample_output, test_cases, reference_solution, creator_id, time_limit, memory_limit, is_public, created_at, updated_at)
VALUES (
    '最大子数组和',
    'programming',
    '给你一个整数数组 nums ，请你找出一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。\n\n子数组 是数组中的一个连续部分。\n\n示例：\n输入：nums = [-2,1,-3,4,-1,2,1,-5,4]\n输出：6\n解释：连续子数组 [4,-1,2,1] 的和最大，为 6',
    NULL,
    'class Solution {\n    public int maxSubArray(int[] nums) {\n        int maxSum = nums[0];\n        int currentSum = nums[0];\n        for (int i = 1; i < nums.length; i++) {\n            currentSum = Math.max(nums[i], currentSum + nums[i]);\n            maxSum = Math.max(maxSum, currentSum);\n        }\n        return maxSum;\n    }\n}',
    10,
    'medium',
    '这道题可以使用动态规划的方法，用一个变量来保存当前的最大子数组和，另一个变量来保存全局的最大子数组和。时间复杂度为O(n)，空间复杂度为O(1)。',
    'class Solution {\n    public int maxSubArray(int[] nums) {\n        \n    }\n}',
    '[-2,1,-3,4,-1,2,1,-5,4]',
    '6',
    '[[-2,1,-3,4,-1,2,1,-5,4],6]\n[[1],1]\n[[5,4,-1,7,8],23]',
    'class Solution {\n    public int maxSubArray(int[] nums) {\n        int maxSum = nums[0];\n        int currentSum = nums[0];\n        for (int i = 1; i < nums.length; i++) {\n            currentSum = Math.max(nums[i], currentSum + nums[i]);\n            maxSum = Math.max(maxSum, currentSum);\n        }\n        return maxSum;\n    }\n}',
    (SELECT id FROM users WHERE username = 'admin'),
    1000,
    1048576,
    1,
    NOW(),
    NOW()
);

-- ============================================
-- 3. 创建单选题
-- ============================================

-- 题目6：Java基本数据类型
INSERT INTO questions (title, type, description, options, answer, score, difficulty, analysis, template_code, sample_input, sample_output, test_cases, reference_solution, creator_id, time_limit, memory_limit, is_public, created_at, updated_at)
VALUES (
    'Java基本数据类型',
    'single',
    'Java中以下哪个不是基本数据类型？',
    '["A. int", "B. String", "C. double", "D. boolean"]',
    'B',
    5,
    'easy',
    'Java的基本数据类型包括：byte、short、int、long、float、double、char、boolean。String不是基本数据类型，而是引用类型。',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    (SELECT id FROM users WHERE username = 'admin'),
    NULL,
    NULL,
    1,
    NOW(),
    NOW()
);

-- 题目7：数组初始化
INSERT INTO questions (title, type, description, options, answer, score, difficulty, analysis, template_code, sample_input, sample_output, test_cases, reference_solution, creator_id, time_limit, memory_limit, is_public, created_at, updated_at)
VALUES (
    '数组初始化',
    'single',
    '以下哪种方式可以正确初始化一个长度为10的整型数组？',
    '["A. int[] arr = new int[10];", "B. int arr[10];", "C. int[] arr = int[10];", "D. int arr = new int[10];"]',
    'A',
    5,
    'medium',
    'Java中初始化数组的方式包括：1. 声明并初始化：int[] arr = new int[10]; 2. 声明并赋值：int[] arr = {1,2,3,4,5,6,7,8,9,10}; 3. 使用循环初始化。',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    (SELECT id FROM users WHERE username = 'admin'),
    NULL,
    NULL,
    1,
    NOW(),
    NOW()
);

-- 题目8：循环结构
INSERT INTO questions (title, type, description, options, answer, score, difficulty, analysis, template_code, sample_input, sample_output, test_cases, reference_solution, creator_id, time_limit, memory_limit, is_public, created_at, updated_at)
VALUES (
    '循环结构',
    'single',
    '以下哪个关键字用于跳出循环？',
    '["A. break", "B. continue", "C. return", "D. exit"]',
    'A',
    5,
    'easy',
    'Java中用于跳出循环的关键字是break，用于跳过本次循环的关键字是continue。break会立即终止循环，continue会跳过当前迭代继续下一次迭代。',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    (SELECT id FROM users WHERE username = 'admin'),
    NULL,
    NULL,
    1,
    NOW(),
    NOW()
);

-- ============================================
-- 4. 创建填空题
-- ============================================

-- 题目9：Java变量命名规则
INSERT INTO questions (title, type, description, options, answer, score, difficulty, analysis, template_code, sample_input, sample_output, test_cases, reference_solution, creator_id, time_limit, memory_limit, is_public, created_at, updated_at)
VALUES (
    'Java变量命名规则',
    'fill',
    'Java变量名必须以____、____或____开头，不能包含Java关键字。',
    NULL,
    '字母、下划线、美元符号',
    5,
    'easy',
    'Java变量命名规则：1. 必须以字母（a-z, A-Z）、下划线（_）或美元符号（$）开头。2. 不能包含Java关键字。3. 不能包含空格。4. 区分大小写。',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    (SELECT id FROM users WHERE username = 'admin'),
    NULL,
    NULL,
    1,
    NOW(),
    NOW()
);

-- 题目10：数组长度属性
INSERT INTO questions (title, type, description, options, answer, score, difficulty, analysis, template_code, sample_input, sample_output, test_cases, reference_solution, creator_id, time_limit, memory_limit, is_public, created_at, updated_at)
VALUES (
    '数组长度属性',
    'fill',
    'Java中数组的长度属性是____。',
    NULL,
    'length',
    5,
    'easy',
    'Java中数组的长度属性是length，它是一个public final字段，表示数组的长度。注意：length不是方法，所以调用时不需要加括号。',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    (SELECT id FROM users WHERE username = 'admin'),
    NULL,
    NULL,
    1,
    NOW(),
    NOW()
);

-- 题目11：字符串比较方法
INSERT INTO questions (title, type, description, options, answer, score, difficulty, analysis, template_code, sample_input, sample_output, test_cases, reference_solution, creator_id, time_limit, memory_limit, is_public, created_at, updated_at)
VALUES (
    '字符串比较方法',
    'fill',
    'Java中比较字符串内容应该使用____方法，而不是==运算符。',
    NULL,
    'equals',
    5,
    'medium',
    'Java中比较字符串内容应该使用equals()方法，而不是==运算符。==运算符比较的是对象的引用地址，而equals()方法比较的是字符串的内容。',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    (SELECT id FROM users WHERE username = 'admin'),
    NULL,
    NULL,
    1,
    NOW(),
    NOW()
);

-- ============================================
-- 执行完成
-- ============================================
SELECT '初始化数据完成！' AS message;
SELECT COUNT(*) AS user_count FROM users WHERE username = 'admin';
SELECT COUNT(*) AS question_count FROM questions;
SELECT type, COUNT(*) AS count FROM questions GROUP BY type;
