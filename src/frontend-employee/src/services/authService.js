import axios from "@/utils/axios";

export const login = (request) => axios.post("/auth/login", request);
