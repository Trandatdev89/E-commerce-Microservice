import { get } from "@/utils"

export const searchUsername=async(path:string,token?:string)=>{
   const response=await get(path,token);
   return response;
}