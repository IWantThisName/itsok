<template>
  <div>
    <el-row :gutter="12">
      <el-col :span="8">
        <el-card shadow="always">
          总是显示
        </el-card>
      </el-col>
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
      searchName: "",
      emptyText: "",
      page: {
        totalSize: 1000,
        data: [],
        pageSize: 8,
        currentPage: 1,
      },

    }
  },
  mounted() {
  },
  methods: {
    getLoggerList(currentPage, pageSize) {
      var _this = this;
      axios.get("/api/itsok/list", {
        params: {
          currentPage: currentPage,
          pageSize: pageSize,
          searchName: _this.searchName
        }
      }).then(function (res) {
        console.log(res.data)
        if (res.data.code == 0) {
          _this.$data.page = res.data.data;
        } else {
          _this.$data.page = res.data.data;
          _this.emptyText = res.data.message;
        }
      });
    },
    changePage(currentPage) {
      console.log(currentPage)
      this.getLoggerList(currentPage, this.page.pageSize);
    },
    changeLevel(name, level) {
      console.log(name + '...' + level);
      var _this = this;
      axios.get("/api/itsok/changeLevel", {params: {name: name, level: level}}).then(function (res) {
        console.log(res.data)
        _this.getLoggerList(_this.page.currentPage, _this.page.pageSize);
      });
    },
    searchList() {
      this.getLoggerList();
    }
  }
};
</script>