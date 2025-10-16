import CandidateView from "@/views/CandidateView.vue";
import HomeView from "@/views/HomeView.vue";
import ManagedJobApplsView from "@/views/job-appl/ManagedJobApplsView.vue";
import ManagedJobApplView from "@/views/job-appl/ManagedJobApplView.vue";
import UnmanagedJobApplsView from "@/views/job-appl/UnmanagedJobApplsView.vue";
import UnmanagedJobApplView from "@/views/job-appl/UnmanagedJobApplView.vue";
import CreateJobPostingView from "@/views/job-posting/CreateJobPostingView.vue";
import JobPostingsView from "@/views/job-posting/JobPostingsView.vue";
import JobPostingView from "@/views/job-posting/JobPostingView.vue";
import LoginView from "@/views/LoginView.vue";
import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      component: LoginView,
    },
    {
      path: "/",
      component: HomeView,
      meta: { requiresAuth: true },
    },
    {
      path: "/path",
      component: UnmanagedJobApplsView,
      meta: { requiresAuth: true },
    },
    {
      path: "/path",
      component: UnmanagedJobApplView,
      meta: { requiresAuth: true },
    },
    {
      path: "/path",
      component: ManagedJobApplsView,
      meta: { requiresAuth: true },
    },
    {
      path: "/path",
      component: ManagedJobApplView,
      meta: { requiresAuth: true },
    },
    {
      path: "/path",
      component: CandidateView,
      meta: { requiresAuth: true },
    },
    {
      path: "/path",
      component: JobPostingsView,
      meta: { requiresAuth: true },
    },
    {
      path: "/path",
      component: JobPostingView,
      meta: { requiresAuth: true },
    },
    {
      path: "/path",
      component: CreateJobPostingView,
      meta: { requiresAuth: true },
    },
  ],
});

router.beforeEach((to, from, next) => {
  const jwt = localStorage.getItem("jwt");

  if (to.meta.requiresAuth && !jwt) {
    next("/login");
  } else {
    next();
  }
});

export default router;
