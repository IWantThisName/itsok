<template>
  <div>
    <div style="width: 20%;">
      <el-input v-model="searchName" placeholder="请输入名称" class="input-with-select">
        <el-button @click="searchList" slot="append" icon="el-icon-search"></el-button>
      </el-input>
    </div>
    <el-table :data="page.data" :empty-text="emptyText">
      <el-table-column prop="name" label="名称">
      </el-table-column>
      <el-table-column label="级别">
        <template slot-scope="scope">
          <el-row>
            <el-button @click="changeLevel(scope.row.name,'DEBUG')"
                       :type="scope.row.level == 'DEBUG' ? 'primary' : ''">DEBUG
            </el-button>
            <el-button @click="changeLevel(scope.row.name,'INFO')" :type="scope.row.level == 'INFO' ? 'info' : ''">
              INFO
            </el-button>
            <el-button @click="changeLevel(scope.row.name,'WARN')"
                       :type="scope.row.level == 'WARN' ? 'warning' : ''">WARN
            </el-button>
            <el-button @click="changeLevel(scope.row.name,'ERROR')"
                       :type="scope.row.level == 'ERROR' ? 'danger' : ''">ERROR
            </el-button>
          </el-row>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        background
        layout="prev, pager, next"
        @current-change="changePage"
        :hide-on-single-page="true"
        :total="page.totalSize">
    </el-pagination>
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
    this.getLoggerList(this.page.currentPage, this.page.pageSize);
  },
  methods: {
    getLoggerList(currentPage, pageSize) {
      var _this = this;
      axios.get("/logger/list", {
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
      axios.get("/logger/changeLevel", {params: {name: name, level: level}}).then(function (res) {
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