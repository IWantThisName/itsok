import Vue from 'vue';
import Router from 'vue-router'

import Logger from "../components/logger";

import Thread from "../components/thread";

import Homepage from "../components/homepage"

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: "/thread",
            component: Thread
        },
        {
            path: "/logger",
            component: Logger
        },
        {
            path: "/homepage",
            component: Homepage
        },
        {
            path: "/",
            component: Homepage
        }
    ]
})