import axios from "@/utils/axios";
import { getJwt } from "@/utils/jwtUtil";

export const getInterviewsByJobApplId = (id) =>
  axios.get(`/interviews?jobApplicationId=${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const deleteInterview = (id) =>
  axios.delete(`/interviews/${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
