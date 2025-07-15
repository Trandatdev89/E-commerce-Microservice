<template>
  <div class="chatSider">
    <el-card style="max-width: 480px">
      <template #header>
          <el-input v-model="searchText" style="max-width: 600px" placeholder="Please input" class="input-with-select">
            <template #append>
              <el-button :icon="Search"/>
            </template>
          </el-input>
      </template>
      <div class="chatSider__main">
        <p v-for="o in 4" :key="o" class="text item">{{ 'List item ' + o }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElCard } from 'element-plus'
import { ref, watch, watchEffect } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { searchUsername } from '@/services/ChatService'
import { API } from '@/configurations/ConfigApiPrefix'
const searchText = ref('')
const listAccount=ref([]);
const token="eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiI0OTgwZmY4My1lMzlhLTQ2NjctOTg2Yy1kNDVjMTc0Yjk5ZTAiLCJzdWIiOiJUaWVueGluaGdhaSIsImV4cCI6MTc1MjY3NTM5OSwiaWF0IjoxNzUyNTg4OTk5LCJqdGkiOiJkZTYyNTZlYy1mMzY1LTQwOTUtOTMyOC01ZDk2YWZkOWEwZDAiLCJzY29wZSI6IlVTRVIifQ.lI74OMdFuvUt0jWBDZKyTDa38ufGzUZrbp1ucOen4U9f8FB_uYVrEUTkeLKQeqz4gRUbTUVQkn4DMsAWr2Y2uQ"

const fetchApi=async()=>{
  const response =await searchUsername(API.SEARCH_USER+`?username=${searchText.value}`,token);
  listAccount.value = response.data;
  console.log(listAccount.value);
}

watch(searchText,()=>{
   fetchApi();
   console.log(listAccount.value);
})

</script>

<style lang="scss" scoped>
.chatSider {
  &__main {
    min-height: 100vh;
    overflow-y: auto;
  }
}
</style>