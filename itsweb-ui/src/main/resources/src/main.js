import Vue from 'vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App.vue';
import router from "./router";
import axios from "axios";
if (process.env.NODE_ENV === "development") {
  axios.defaults.baseURL = "/api"
}else {
  var pathName = document.location.pathname;
  var index = pathName.substr(1).indexOf("/");
  var result = pathName.substr(0,index+1);
  axios.defaults.baseURL = result + "/itsok"
}
Vue.use(ElementUI);

new Vue({
  el: '#app',
  router,
  render: h => h(App)
});

