import axios from "@/utils/axios";
import { getJwt } from "@/utils/jwtUtil";

export const getUnmanagedJobApplsByJobPostingId = (id) => {
  axios.get(`/job-applications/job-posting/${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
};

export const getUnmanagedJobApplById = (id) => {
  axios.get(`/job-applications/${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
};

export const getManagedJobAppls = () => {
  axios.get("/job-applications/managed", {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
};

export const addJobApplToManaged = (request) => {
  axios.put("/job-applications/managed", request, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
};

export const getManagedJobApplById = (id) => {
  axios.get(`/job-applications/managed/${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
};

export const updateManagedJobAppl = (id, request) => {
  axios.put(`/job-applications/managed/${id}`, request, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
};

export const getCandidateInfoByJobApplId = (id) => {
  axios.get(`/job-applications/managed/${id}/candidate/profile`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
};

export const getCandidateResumeByJobApplId = (id) => {
  axios.get(`/job-applications/managed/${id}/candidate/resume`, {
    responseType: "blob",
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
};
