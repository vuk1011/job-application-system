import axios from "@/utils/axios";
import { getJwt } from "@/utils/jwtUtil";

export const getJobAppls = () =>
  axios.get("/job-applications", {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const getJobApplById = (id) =>
  axios.get(`/job-applications/${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const submitJobAppl = (request) =>
  axios.post("/job-applications", request, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const withdrawJobAppl = (id) =>
  axios.delete(`/job-applications/${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
