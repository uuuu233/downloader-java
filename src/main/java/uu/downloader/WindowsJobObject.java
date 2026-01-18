package uu.downloader;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.util.Arrays;
import java.util.List;

public class WindowsJobObject {
    // 定义Windows API接口
    private interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class, W32APIOptions.ASCII_OPTIONS);
        // 定义Windows API函数
        WinNT.HANDLE CreateJobObjectA(WinBase.SECURITY_ATTRIBUTES lpJobAttributes, String lpName);
        boolean AssignProcessToJobObject(WinNT.HANDLE hJob, WinNT.HANDLE hProcess);
        WinNT.HANDLE OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);
        boolean CloseHandle(WinNT.HANDLE hObject);
        int GetLastError();
        boolean CreateProcessA(
                String lpApplicationName,
                String lpCommandLine,
                WinBase.SECURITY_ATTRIBUTES lpProcessAttributes,
                WinBase.SECURITY_ATTRIBUTES lpThreadAttributes,
                boolean bInheritHandles,
                int dwCreationFlags,
                Pointer lpEnvironment,
                String lpCurrentDirectory,
                WinBase.STARTUPINFO lpStartupInfo,
                WinBase.PROCESS_INFORMATION lpProcessInformation
        );
        boolean SetInformationJobObject(
                WinNT.HANDLE hJob,
                int JobObjectInfoClass,
                JOBOBJECT_EXTENDED_LIMIT_INFORMATION lpJobObjectInfo,
                int cbJobObjectInfoLength
        );
        class JOBOBJECT_EXTENDED_LIMIT_INFORMATION extends Structure {
            public JOBOBJECT_BASIC_LIMIT_INFORMATION BasicLimitInformation;  // 基础限制信息
            public int IoInfo;                               // IO 计数器（JNA 未封装，简化为占位）
            public int ProcessMemoryLimit;                // 进程内存限制
            public int JobMemoryLimit;                    // 作业内存限制
            public int PeakProcessMemoryUsed;             // 进程峰值内存使用量
            public int PeakJobMemoryUsed;                 // 作业峰值内存使用量

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(
                        "BasicLimitInformation", "IoInfo", "ProcessMemoryLimit",
                        "JobMemoryLimit", "PeakProcessMemoryUsed", "PeakJobMemoryUsed"
                );
            }
        }
        class JOBOBJECT_BASIC_LIMIT_INFORMATION extends Structure {
            public long PerProcessUserTimeLimit;    // 每个进程的用户时间限制
            public long PerJobUserTimeLimit;        // 每个作业的用户时间限制
            public int LimitFlags;                          // 限制标志（核心：如 JOB_OBJECT_LIMIT_KILL_ON_JOB_CLOSE）
            public int MinimumWorkingSetSize;               // 最小工作集大小
            public int MaximumWorkingSetSize;               // 最大工作集大小
            public int ActiveProcessLimit;                  // 活动进程限制
            public long Affinity;                  // CPU 亲和性
            public int PriorityClass;                       // 优先级类
            public int SchedulingClass;                     // 调度类

            @Override
            protected List<String> getFieldOrder() {
                return Arrays.asList(
                        "PerProcessUserTimeLimit", "PerJobUserTimeLimit", "LimitFlags",
                        "MinimumWorkingSetSize", "MaximumWorkingSetSize", "ActiveProcessLimit",
                        "Affinity", "PriorityClass", "SchedulingClass"
                );
            }
        }
    }

    public static void CreateProcessAndSetJobObject(String path) {
        Kernel32 kernel32 = Kernel32.INSTANCE;
        WinBase.STARTUPINFO startupInfo = new WinBase.STARTUPINFO();
        // 设置为隐藏窗口, 好像设不设置都一样
        startupInfo.dwFlags = 1;
        startupInfo.wShowWindow = new WinDef.WORD(0);
        WinBase.PROCESS_INFORMATION processInfo = new WinBase.PROCESS_INFORMATION();
        if (!kernel32.CreateProcessA(null,
                path,
                null,
                null,
                false,
                0,
                null,
                null,
                startupInfo,
                processInfo
        )) {
            throw new RuntimeException();
        }
        // 创建工作对象
        WinNT.HANDLE JobObjectHandle = kernel32.CreateJobObjectA(null, null);

        // 设置工作对象信息
        Kernel32.JOBOBJECT_EXTENDED_LIMIT_INFORMATION jobObjectInfo = new Kernel32.JOBOBJECT_EXTENDED_LIMIT_INFORMATION();
        jobObjectInfo.BasicLimitInformation.LimitFlags = 0x2000;
        kernel32.SetInformationJobObject(JobObjectHandle, 9, jobObjectInfo, 144);
        // 分配给工作对象
        if (!kernel32.AssignProcessToJobObject(JobObjectHandle, processInfo.hProcess)) {
            kernel32.CloseHandle(JobObjectHandle);
            kernel32.CloseHandle(processInfo.hProcess);
            throw new RuntimeException();
        }
    }

}