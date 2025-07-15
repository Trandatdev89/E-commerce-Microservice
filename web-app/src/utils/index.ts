import axios from 'axios'

export const API_DOMAIN = 'http://localhost:8222/api/v1'

const getHeaders = (accessToken?: string) => {
  let headers: any = {
    'Content-Type': 'application/json',
  }
  if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`
  }
  return { headers }
}

const getHeadersWithFormData = (accessToken?: string) => {
  let headers: any = {
    'Content-Type': 'multipart/form-data',
  }
  if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`
  }
  return { headers }
}

export const get = async (path: string, token?: string) => {
  try {
    const res = await axios.get(API_DOMAIN + path, getHeaders(token))
    return res.data
  } catch (error: any) {
    const errRes = error?.response?.data
    return errRes;
  }
}

export const post = async (path: string, data: object, token?: string) => {
  try {
    const res = await axios.post(API_DOMAIN + path, data, getHeaders(token))
    return res.data
  } catch (error: any) {
    const errRes = error?.response?.data
    return errRes
  }
}

export const patch = async (path: string, data: object, token?: string) => {
  try {
    const res = await axios.patch(API_DOMAIN + path, data, getHeaders(token))
    return res.data
  } catch (error: any) {
    return error?.response?.data
  }
}

export const put = async (path: string, data: object, id: any, token?: string) => {
  try {
    const res = await axios.put(`${API_DOMAIN + path}/${id}`, data, getHeaders(token))
    return res.data
  } catch (error: any) {
    const errRes = error?.response?.data;
    return errRes;
  }
}

export const Delete = async (path: string, id: any, token?: string) => {
  try {
    const res = await axios.delete(`${API_DOMAIN + path}/${id}`, getHeaders(token))
    return res.data
  } catch (error: any) {
    const errRes = error?.response?.data;
    return errRes
  }
}

export const postWithFormData = async (path: string, data: any, token?: string) => {
  try {
    const res = await axios.post(API_DOMAIN + path, data, getHeadersWithFormData(token))
    return res.data
  } catch (error: any) {
    const errRes = error?.response?.data
    return errRes
  }
}

export const patchWithFormData = async (path: string, data: any, token?: string) => {
  try {
    const res = await axios.patch(API_DOMAIN + path, data, getHeadersWithFormData(token))
    return res.data
  } catch (error: any) {
    const errRes = error?.response?.data
    return errRes;
  }
}

export const putWithFormData = async (path: string, data: any, id: any, token?: string) => {
  try {
    const res = await axios.put(`${API_DOMAIN + path}/${id}`, data, getHeadersWithFormData(token))
    return res.data
  } catch (error: any) {
    const errRes = error?.response?.data
    return errRes
  }
}
