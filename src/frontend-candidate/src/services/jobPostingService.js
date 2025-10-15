import axios from "@/utils/axios";
import { getJwt } from "@/utils/jwtUtil";

export const getJobPostings = () =>
  axios.get("/job-postings", {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
