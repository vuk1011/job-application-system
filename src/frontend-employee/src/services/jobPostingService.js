import axios from "@/utils/axios";
import { getJwt } from "@/utils/jwtUtil";

export const getJobPostings = () =>
  axios.get("/job-postings", {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const createJobPosting = (request) =>
  axios.post("/job-postings", request, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const getJobPostingById = (id) =>
  axios.get(`/job-postings/${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const updateJobPosting = (id, request) =>
  axios.put(`/job-postings/${id}`, request, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const deleteJobPosting = (id) =>
  axios.delete(`/job-postings/${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
