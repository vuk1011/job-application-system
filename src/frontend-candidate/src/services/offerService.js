import axios from "@/utils/axios";
import { getJwt } from "@/utils/jwtUtil";

export const getOffersByJobApplId = (id) =>
  axios.get(`/offers?jobApplicationId=${id}`, {
    headers: { Authorization: `Bearer ${getJwt()}` },
  });

export const acceptOffer = (id) =>
  axios.put(
    `/offers/${id}`,
    { accepted: true },
    {
      headers: { Authorization: `Bearer ${getJwt()}` },
    },
  );

export const rejectOffer = (id) =>
  axios.put(
    `/offers/${id}`,
    { accepted: false },
    {
      headers: { Authorization: `Bearer ${getJwt()}` },
    },
  );
