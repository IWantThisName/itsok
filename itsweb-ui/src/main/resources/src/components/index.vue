<template>
  <el-container>
    <el-header style="background-color: #409EFF;height: 66px">
      <p style='font-size: 20px;font-weight:bold;text-align: left;color: white;font-family: "PingFang SC"'>
        itsok后台管理</p>
    </el-header>
    <el-container style="height: 100%">
      <el-aside style="width:200px;background-color: #393d49">
        <el-menu id="indexMenu" default-active="/homepage" style="border: 1px solid #eee;background-color:#393d49" :router='true'>
          <el-menu-item index="/homepage">总览</el-menu-item>
          <el-menu-item index="/logger">日志管理</el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>

<style>
  #indexMenu .el-menu-item {
    color: white
  }

  #indexMenu .el-menu-item:hover {
    background-color: #E6A23C;
  }

  #indexMenu .el-menu-item.is-active {
    background-color: #E6A23C;
  }

</style>

<script>

</script>