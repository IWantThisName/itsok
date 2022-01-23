<template>
  <div>
    <el-row :gutter="12">
      <el-col :span="8">
        <el-card shadow="always">
          <el-descriptions title="系统信息" column="1">
            <el-descriptions-item label="系统名称">{{ systemInfo.name }}</el-descriptions-item>
            <el-descriptions-item label="系统版本">{{ systemInfo.version }}</el-descriptions-item>
            <el-descriptions-item label="操作系统的架构">{{ systemInfo.arch }}</el-descriptions-item>
            <el-descriptions-item label="可用的内核数">{{ systemInfo.availableProcessors }}</el-descriptions-item>
            <el-descriptions-item label="总物理内存(M)">{{ systemInfo.totalPhysicalMemory }}</el-descriptions-item>
            <el-descriptions-item label="已用物理内存(M)">{{ systemInfo.usedPhysicalMemorySize }}</el-descriptions-item>
            <el-descriptions-item label="剩余物理内存(M)">{{ systemInfo.freePhysicalMemory }}</el-descriptions-item>
            <el-descriptions-item label="总交换空间(M)">{{ systemInfo.totalSwapSpaceSize }}</el-descriptions-item>
            <el-descriptions-item label="已用交换空间(M)">{{ systemInfo.usedSwapSpaceSize }}</el-descriptions-item>
            <el-descriptions-item label="剩余交换空间(M)">{{ systemInfo.freeSwapSpaceSize }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="always">
          <el-descriptions title="进程信息" column="1">
            <el-descriptions-item label="进程id">{{ processInfo.pid }}</el-descriptions-item>
            <el-descriptions-item label="启动时间">{{ processInfo.startTime }}</el-descriptions-item>
            <el-descriptions-item label="运行时间">{{ processInfo.runTime }}</el-descriptions-item>
            <el-descriptions-item label="已加载类总数">{{ processInfo.totalLoadedClassCount }}</el-descriptions-item>
            <el-descriptions-item label="已加载当前类">{{ processInfo.loadedClassCount }}</el-descriptions-item>
            <el-descriptions-item label="已卸载类总数">{{ processInfo.unloadedClassCount }}</el-descriptions-item>
            <el-descriptions-item label="线程总数（被创建并执行过的线程总数）">{{ processInfo.totalStartedThreadCount }}
            </el-descriptions-item>
            <el-descriptions-item label="仍活动的线程总数">{{ processInfo.threadCount }}</el-descriptions-item>
            <el-descriptions-item label="峰值线程数">{{ processInfo.peakThreadCount }}</el-descriptions-item>
            <el-descriptions-item label="仍活动的守护线程（daemonThread）总数">{{ processInfo.daemonThreadCount }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="always">
          <el-descriptions title="内存信息" column="1">
            <el-descriptions-item label="堆内存最大大小(M)">{{ memoryInfo.heapMax }}</el-descriptions-item>
            <el-descriptions-item label="堆内存已用大小(M)">{{ memoryInfo.heapUsed }}</el-descriptions-item>
            <el-descriptions-item label="非堆内存初始大小(M)">{{ memoryInfo.nonHeapInit }}</el-descriptions-item>
            <el-descriptions-item label="非堆内存已用大小(M)">{{ memoryInfo.nonHeapUsed }}</el-descriptions-item>
            <el-descriptions-item label="eden区最大大小(M)">{{ memoryInfo.edenMax }}</el-descriptions-item>
            <el-descriptions-item label="eden区已用大小(M)">{{ memoryInfo.edenUsed }}</el-descriptions-item>
            <el-descriptions-item label="survivor区最大大小(M)">{{ memoryInfo.survivorMax }}</el-descriptions-item>
            <el-descriptions-item label="survivor区已用大小(M)">{{ memoryInfo.survivorUsed }}</el-descriptions-item>
            <el-descriptions-item label="old区最大大小(M)">{{ memoryInfo.oldMax }}</el-descriptions-item>
            <el-descriptions-item label="old区已用大小(M)">{{ memoryInfo.oldUsed }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
      <div style="width: 20%;margin-top: 20px;">
        <el-input v-model="searchName" placeholder="请输入名称" class="input-with-select">
          <el-button @click="searchList" slot="append" icon="el-icon-search"></el-button>
        </el-input>
      </div>
      <el-table :data="page.data" :empty-text="emptyText">
        <el-table-column prop="threadId" label="线程id">
        </el-table-column>
        <el-table-column prop="threadName" label="线程名称">
        </el-table-column>
        <el-table-column prop="threadState" label="线程状态">
        </el-table-column>
      </el-table>
      <el-pagination
          background
          layout="prev, pager, next"
          @current-change="changePage"
          :hide-on-single-page="true"
          :total="page.totalSize">
      </el-pagination>
    </el-row>
  </div>
</template>

<style>
</style>

<script>
import axios from "axios";

export default {
  data() {
    return {
      systemInfo: {},
      processInfo: {},
      memoryInfo: {},
      page: {},
      searchName: ""
    }
  },
  mounted() {
    this.getSystemInfo();
    this.getProcessInfo();
    this.getMemoryInfo();
    this.getThreadInfo();
  },
  methods: {
    getSystemInfo() {
      var _this = this;
      axios.get("/management/getSystemInfo").then(function (res) {
        console.info(res.data)
        _this.$data.systemInfo = res.data.data;
      });
    },
    getProcessInfo() {
      var _this = this;
      axios.get("/management/getProcessInfo").then(function (res) {
        console.info(res.data)
        _this.$data.processInfo = res.data.data;
      });
    },
    getMemoryInfo() {
      var _this = this;
      axios.get("/management/getMemoryInfo").then(function (res) {
        console.info(res.data)
        _this.$data.memoryInfo = res.data.data;
      });
    },
    getThreadInfo(currentPage,pageSize) {
      var _this = this;
      axios.get("/management/getThreadInfoList",{params: {currentPage: currentPage,pageSize:pageSize,threadName: this.searchName}}).then(function (res) {
        console.info(res.data)
        _this.$data.page = res.data.data;
      });
    },
    changePage(currentPage) {
      this.getThreadInfo(currentPage, 8);
    },
    searchList() {
      this.getThreadInfo();
    }
  }
};
</script>