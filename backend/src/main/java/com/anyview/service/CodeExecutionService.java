package com.anyview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.*;

@Service
public class CodeExecutionService {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final File tempDir;

    public CodeExecutionService() {
        this.tempDir = new File("temp");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    public ExecutionResult executeCode(String code, String language) throws Exception {
        switch (language.toLowerCase()) {
            case "java":
                return executeJavaCode(code);
            case "python":
                return executePythonCode(code);
            default:
                throw new IllegalArgumentException("不支持的编程语言: " + language);
        }
    }

    private ExecutionResult executeJavaCode(String code) throws Exception {
        File executionDir = new File(tempDir, "anyview_" + System.currentTimeMillis());
        if (!executionDir.mkdir()) {
            throw new IOException("无法创建临时目录");
        }

        try {
            File javaFile = new File(executionDir, "Solution.java");
            try (FileWriter writer = new FileWriter(javaFile)) {
                writer.write(code);
            }

            Process compileProcess = Runtime.getRuntime().exec(
                    "javac " + javaFile.getAbsolutePath()
            );
            String compileError = readStream(compileProcess.getErrorStream());
            int compileExitCode = compileProcess.waitFor();

            if (compileExitCode != 0) {
                return new ExecutionResult(false, "编译错误: " + compileError, null);
            }

            Process executeProcess = Runtime.getRuntime().exec(
                    "java -cp " + executionDir.getAbsolutePath() + " Solution"
            );

            Future<ExecutionResult> future = executorService.submit(() -> {
                String output = readStream(executeProcess.getInputStream());
                String error = readStream(executeProcess.getErrorStream());
                int exitCode = executeProcess.waitFor();
                
                if (exitCode != 0) {
                    return new ExecutionResult(false, "运行错误: " + error, null);
                }
                return new ExecutionResult(true, null, output);
            });

            try {
                return future.get(5, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                executeProcess.destroy();
                return new ExecutionResult(false, "执行超时", null);
            }

        } finally {
            deleteDirectory(executionDir);
        }
    }

    private ExecutionResult executePythonCode(String code) throws Exception {
        File executionDir = new File(tempDir, "anyview_" + System.currentTimeMillis());
        if (!executionDir.mkdir()) {
            throw new IOException("无法创建临时目录");
        }

        File tempFile = new File(executionDir, "solution.py");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(code);
        }

        try {
            Process process = Runtime.getRuntime().exec(
                    "python " + tempFile.getAbsolutePath()
            );

            Future<ExecutionResult> future = executorService.submit(() -> {
                String output = readStream(process.getInputStream());
                String error = readStream(process.getErrorStream());
                int exitCode = process.waitFor();
                
                if (exitCode != 0) {
                    return new ExecutionResult(false, "运行错误: " + error, null);
                }
                return new ExecutionResult(true, null, output);
            });

            try {
                return future.get(5, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                process.destroy();
                return new ExecutionResult(false, "执行超时", null);
            }

        } finally {
            deleteDirectory(executionDir);
        }
    }

    private String readStream(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    public static class ExecutionResult {
        private final boolean success;
        private final String error;
        private final String output;

        public ExecutionResult(boolean success, String error, String output) {
            this.success = success;
            this.error = error;
            this.output = output;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getError() {
            return error;
        }

        public String getOutput() {
            return output;
        }
    }
}