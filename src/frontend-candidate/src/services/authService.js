import axios from "@/utils/axios";

export const login = (request) => axios.post("/auth/login", request);

export const register = (request) => axios.post("/auth/register", request);
