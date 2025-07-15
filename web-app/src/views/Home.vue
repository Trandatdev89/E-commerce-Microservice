<template>
   <div class="home">
     <h3 style="text-align: center;margin: 20px 0;">Chat Page</h3>
     <div class="container">
        <div class="row">
          <div class="col-3">
             <SideBar/>
          </div>
           <div class="col-9">
             <Chat/>
          </div>
        </div>
     </div>
   </div>
</template>

<script lang="ts" setup>
import Chat from './chat/Chat.vue';
import SideBar from './chat/SideBar.vue';

</script>

<style lang="scss" scoped>
.home{
    margin-top: 50px;
}
</style>