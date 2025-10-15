import axios from "@/utils/axios";
import { getJwt } from "@/utils/jwtUtil";

export const getJobApplications = () =>
  axios.get("/job-applications", {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const getJobApplicationById = (id) =>
  axios.get(`/job-applications/${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const submitJobApplication = (request) =>
  axios.post("/job-applications", request, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const withdrawJobApplication = (id) =>
  axios.delete(`/job-applications/${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
