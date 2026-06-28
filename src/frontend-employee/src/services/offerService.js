import axios from "@/utils/axios";
import { getJwt } from "@/utils/jwtUtil";

export const getOffersByJobApplId = (id) =>
  axios.get(`/offers?jobApplicationId=${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const createOffer = (request) =>
  axios.post("/offers", request, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const deleteOffer = (id) =>
  axios.delete(`/offers/${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });
