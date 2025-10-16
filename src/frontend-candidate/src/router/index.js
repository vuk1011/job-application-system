import HomeView from "@/views/HomeView.vue";
import JobApplsView from "@/views/JobApplsView.vue";
import JobApplView from "@/views/JobApplView.vue";
import JobPostingsView from "@/views/JobPostingsView.vue";
import LoginView from "@/views/LoginView.vue";
import ProfileView from "@/views/ProfileView.vue";
import RegisterView from "@/views/RegisterView.vue";
import ResumeView from "@/views/ResumeView.vue";
import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      component: HomeView,
      meta: { requiresAuth: true },
    },
    {
      path: "/login",
      component: LoginView,
    },
    {
      path: "/register",
      component: RegisterView,
    },
    {
      path: "/profile",
      component: ProfileView,
      meta: { requiresAuth: true },
    },
    {
      path: "/profile/resume",
      component: ResumeView,
      meta: { requiresAuth: true },
    },
    {
      path: "/job-postings",
      component: JobPostingsView,
      meta: { requiresAuth: true },
    },
    {
      path: "/job-applications",
      component: JobApplsView,
      meta: { requiresAuth: true },
    },
    {
      path: "/job-applications/:id",
      component: JobApplView,
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
