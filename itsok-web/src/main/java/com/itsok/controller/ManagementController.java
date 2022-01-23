package com.itsok.controller;

import com.alibaba.fastjson.JSON;
import com.itsok.bean.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.management.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/itsok/management")
public class ManagementController {

    private static final long MB = 1024 * 1024;


    @RequestMapping("/getSystemInfo")
    @ResponseBody
    public ResultBean getSystemInfo() {
        SystemInfoBean operatingSystemInfo = getOperatingSystemInfo();
        return ResultBean.ok().setData(operatingSystemInfo);
    }

    @RequestMapping("/getProcessInfo")
    @ResponseBody
    public ResultBean getProcessInfo() {
        ProcessInfoBean processInfoBean = getCurrentProcessInfo();
        return ResultBean.ok().setData(processInfoBean);
    }

    @RequestMapping("/getMemoryInfo")
    @ResponseBody
    public ResultBean getMemoryInfo() {
        MemoryInfoBean memoryInfoBean = getCurrentMemoryInfo();
        return ResultBean.ok().setData(memoryInfoBean);
    }

    @RequestMapping("/getThreadInfoList")
    @ResponseBody
    public ResultBean getThreadInfoList(Integer currentPage,Integer pageSize,String threadName) {
        PageBean threadInfoBean = getCurrentThreadInfoPageBean(currentPage,pageSize,threadName);
        return ResultBean.ok().setData(threadInfoBean);
    }

    private PageBean getCurrentThreadInfoPageBean(Integer currentPage, Integer pageSize, String threadName) {
        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 8 : pageSize;
        ThreadMXBean thread = ManagementFactory.getThreadMXBean();
        long[] threadIds = thread.getAllThreadIds();
        TreeSet<ThreadInfoBean> threadInfoBeans = new TreeSet<>(new Comparator<ThreadInfoBean>() {
            @Override
            public int compare(ThreadInfoBean o1, ThreadInfoBean o2) {
                return o1.getThreadName().compareTo(o2.getThreadName());
            }
        });
        if (threadIds != null && threadIds.length > 0) {
            ThreadInfo[] threadInfos = thread.getThreadInfo(threadIds);
            for (ThreadInfo threadInfo : threadInfos) {
                ThreadInfoBean threadInfoBean = new ThreadInfoBean();
                threadInfoBean.setThreadId(threadInfo.getThreadId());
                threadInfoBean.setThreadName(threadInfo.getThreadName());
                threadInfoBean.setThreadState(threadInfo.getThreadState().name());
                threadInfoBeans.add(threadInfoBean);
            }
        }
        List<ThreadInfoBean> resultList = new ArrayList<>();
        if(StringUtils.isEmpty(threadName)) {
            resultList.addAll(threadInfoBeans);
        } else {
            for (ThreadInfoBean threadInfoBean : threadInfoBeans) {
                if(threadInfoBean.getThreadName().contains(threadName)) {
                    resultList.add(threadInfoBean);
                }
            }
        }
        int start = (currentPage - 1) * pageSize;
        int end = start + pageSize < resultList.size() ? start + pageSize : resultList.size();
        PageBean pageBean = new PageBean();
        pageBean.setCurrentPage(currentPage);
        pageBean.setPageSize(pageSize);
        pageBean.setTotalSize(resultList.size());
        pageBean.setTotalPage(resultList.size() % pageSize == 0 ? resultList.size() / pageSize : resultList.size() / pageSize + 1);
        pageBean.setData(resultList.subList(start, end));
        return pageBean;
    }

    private MemoryInfoBean getCurrentMemoryInfo() {
        MemoryInfoBean memoryInfoBean = new MemoryInfoBean();
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemory = memory.getHeapMemoryUsage();
        memoryInfoBean.setHeapInit(heapMemory.getInit() / MB);
        memoryInfoBean.setHeapMax(heapMemory.getMax() / MB);
        memoryInfoBean.setHeapUsed(heapMemory.getUsed() / MB);
        MemoryUsage nonHeapMemory = memory.getNonHeapMemoryUsage();
        memoryInfoBean.setNonHeapInit(nonHeapMemory.getInit() / MB);
        memoryInfoBean.setNonHeapMax(nonHeapMemory.getMax() / MB);
        memoryInfoBean.setNonHeapUsed(nonHeapMemory.getUsed() / MB);
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        if (pools != null && !pools.isEmpty()) {
            for (MemoryPoolMXBean pool : pools) {
                if (pool.getName().toLowerCase().contains("eden")) {
                    memoryInfoBean.setEdenInit(pool.getUsage().getInit() / MB);
                    memoryInfoBean.setEdenMax(pool.getUsage().getMax() / MB);
                    memoryInfoBean.setEdenUsed(pool.getUsage().getUsed() / MB);
                } else if (pool.getName().toLowerCase().contains("survivor")) {
                    memoryInfoBean.setSurvivorInit(pool.getUsage().getInit() / MB);
                    memoryInfoBean.setSurvivorMax(pool.getUsage().getMax() / MB);
                    memoryInfoBean.setSurvivorUsed(pool.getUsage().getUsed() / MB);
                } else if (pool.getName().toLowerCase().contains("old")) {
                    memoryInfoBean.setOldInit(pool.getUsage().getInit() / MB);
                    memoryInfoBean.setOldMax(pool.getUsage().getMax() / MB);
                    memoryInfoBean.setOldUsed(pool.getUsage().getUsed() / MB);
                }
            }
        }
        return memoryInfoBean;
    }

    private ProcessInfoBean getCurrentProcessInfo() {
        ProcessInfoBean processInfoBean = new ProcessInfoBean();
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        processInfoBean.setPid(runtime.getName().split("@")[0]);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        processInfoBean.setStartTime(dateFormat.format(new Date(runtime.getStartTime())));
        processInfoBean.setSystemProperties(JSON.toJSONString(runtime.getSystemProperties()));
        long uptime = runtime.getUptime();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(uptime);
        processInfoBean.setRunTime(hms);
        processInfoBean.setVmArgs(JSON.toJSONString(runtime.getInputArguments()));
        processInfoBean.setClassPath(runtime.getClassPath());
        processInfoBean.setBootPath(runtime.getBootClassPath());
        processInfoBean.setLibraryPath(runtime.getLibraryPath());
        ClassLoadingMXBean classLoad = ManagementFactory.getClassLoadingMXBean();
        processInfoBean.setTotalLoadedClassCount(classLoad.getTotalLoadedClassCount());
        processInfoBean.setLoadedClassCount(classLoad.getLoadedClassCount());
        processInfoBean.setUnloadedClassCount(classLoad.getUnloadedClassCount());
        ThreadMXBean thread = ManagementFactory.getThreadMXBean();
        processInfoBean.setThreadCount(thread.getThreadCount());
        processInfoBean.setPeakThreadCount(thread.getPeakThreadCount());
        processInfoBean.setTotalStartedThreadCount(thread.getTotalStartedThreadCount());
        processInfoBean.setDaemonThreadCount(thread.getDaemonThreadCount());
        return processInfoBean;
    }


    public static SystemInfoBean getOperatingSystemInfo() {
        SystemInfoBean systemInfoBean = new SystemInfoBean();
        OperatingSystemMXBean system = ManagementFactory.getOperatingSystemMXBean();
        systemInfoBean.setName(system.getName());
        systemInfoBean.setVersion(system.getVersion());
        systemInfoBean.setArch(system.getArch());
        systemInfoBean.setAvailableProcessors(system.getAvailableProcessors());
        if (isSunOsMBean(system)) {
            long totalPhysicalMemory = getLongFromOperatingSystem(system, "getTotalPhysicalMemorySize");
            long freePhysicalMemory = getLongFromOperatingSystem(system, "getFreePhysicalMemorySize");
            long usedPhysicalMemorySize = totalPhysicalMemory - freePhysicalMemory;
            systemInfoBean.setTotalPhysicalMemory(totalPhysicalMemory / MB);
            systemInfoBean.setFreePhysicalMemory(freePhysicalMemory / MB);
            systemInfoBean.setUsedPhysicalMemorySize(usedPhysicalMemorySize / MB);
            long totalSwapSpaceSize = getLongFromOperatingSystem(system, "getTotalSwapSpaceSize");
            long freeSwapSpaceSize = getLongFromOperatingSystem(system, "getFreeSwapSpaceSize");
            long usedSwapSpaceSize = totalSwapSpaceSize - freeSwapSpaceSize;
            systemInfoBean.setTotalSwapSpaceSize(totalSwapSpaceSize / MB);
            systemInfoBean.setFreeSwapSpaceSize(freeSwapSpaceSize / MB);
            systemInfoBean.setUsedSwapSpaceSize(usedSwapSpaceSize / MB);
        }
        return systemInfoBean;
    }

    private static long getLongFromOperatingSystem(OperatingSystemMXBean operatingSystem, String methodName) {
        try {
            final Method method = operatingSystem.getClass().getMethod(methodName,
                    (Class<?>[]) null);
            method.setAccessible(true);
            return (Long) method.invoke(operatingSystem, (Object[]) null);
        } catch (final InvocationTargetException e) {
            if (e.getCause() instanceof Error) {
                throw (Error) e.getCause();
            } else if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new IllegalStateException(e.getCause());
        } catch (final NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean isSunOsMBean(OperatingSystemMXBean operatingSystem) {
        final String className = operatingSystem.getClass().getName();
        return "com.sun.management.OperatingSystem".equals(className)
                || "com.sun.management.UnixOperatingSystem".equals(className);
    }

}
