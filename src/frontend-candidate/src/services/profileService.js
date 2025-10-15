import axios from "@/utils/axios";
import { getJwt } from "@/utils/jwtUtil";

export const getProfile = () =>
  axios.get("/me/profile", {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const updateProfile = (request) =>
  axios.put("/me/profile", request, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const getResume = () =>
  axios.get("/me/resume", {
    responseType: "blob",
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const uploadResume = (file) => {
  const formData = new FormData();
  formData.append("file", file);

  return axios.put("/me/resume", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
      Authorization: `Bearer ${getJwt()}`,
    },
  });
};

export const deleteResume = () =>
  axios.delete("/me/resume", {
    headers: {
      Authorization: `Bearer ${getJwt()}`,
    },
  });
