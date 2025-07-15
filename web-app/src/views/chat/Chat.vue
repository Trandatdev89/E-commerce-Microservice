<template>
  <div class="chat">
    <div class="chat__card">
            <div class="chat__header">
              <div class="chat__wrapper">
                <div class="chat__avatar">
                  <img :src="avatarAI" alt="avatarAI...">
                </div>
                <p>Chat with AI</p>
              </div>
              <div class="chat__tools">
                <div class="chat__info">
                  <i class="fa-solid fa-info"></i>
                </div>
                <div class="chat__call">
                  <i class="fa-solid fa-phone"></i>
                </div>
                <div class="chat__search">
                  <i class="fa-solid fa-magnifying-glass"></i>
                </div>
                <div class="chat__menu">
                  <i class="fa-solid fa-bars"></i>
                </div>
              </div>
            </div>
            <div class="chat__main">
              <p style="text-align: center;color: #ccc; margin-top: 20px;">TODAY</p>
              <div class="chat__content">
                <div class="chat__avatar">
                  <img :src="avatarAI" alt="avatarAI...">
                </div>
                <div class="chat__text">
                  <div>
                    <span style="color: red;font-size: 14px;margin-right: 5px;">
                      James
                    </span>
                    <span style="font-size: 12px;color: #ccc;">4:06 PM</span>
                  </div>
                  <div style="margin-top: 5px;">
                    Hello anyone?
                  </div>
                </div>
              </div>
            </div>
            <div class="chat__message">
              <input type="text" class="chat__input">
              <div class="chat__icons-wrapper">
                <i :class="['fa-solid', 'fa-microphone']"></i>
                <i class="fa-solid fa-face-smile"></i>
                <i class="fa-solid fa-paper-plane"></i>
              </div>
            </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import avatarAI from "@/assets/avater.jpg"

</script>

<style lang="scss" scoped>
* {
  box-sizing: border-box;
}

@mixin flex-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

@mixin flex-item-noJustify {
  display: flex;
  align-items: center;
}

.recording {
  color: red;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }

  50% {
    transform: scale(1.2);
    opacity: 0.6;
  }

  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.chat {
    
  &__card {
    border: 1px solid #ddd;
    border-radius: 5px;
    overflow: hidden;
  }

  &__wrapper {
    @include flex-item;

    p {
      margin-bottom: 0;
      margin-left: 10px;
      font-size: 20px;
      font-weight: 600;
    }
  }

  &__header {
    @include flex-item;
    padding: 20px 10px;
    border-bottom: 1px solid #ddd;
    background-color: #fff;
  }

  &__avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    border: 1px solid #ddd;
    @include flex-item-noJustify;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    p {
      font-size: 18px;
      font-weight: 500;
      margin-left: 5px;
    }
  }

  &__tools {
    @include flex-item-noJustify;

    div {
      margin: 0 5px;
      padding: 5px;
      @include flex-item;
    }
  }

  &__main {
    min-height: 100vh;
    padding: 0 10px;
    display: flex;
    background: linear-gradient(to bottom, #e9e4f07d, #d3cce34f);
    flex-direction: column;
    justify-content: flex-start;
  }

  &__message {
    display: flex;
    align-items: center;
    padding: 10px;
    border-bottom: 1px solid rgb(225, 225, 227);
  }

  &__input {
    flex: 1;
    padding: 5px;
    border-radius: 5px;
  }

  &__icons-wrapper {
    display: flex;
    gap: 12px; // 👈 tự giãn cách đều nhau
    margin-left: 10px;

    i {
      font-size: 18px;
      color: #000;
      cursor: pointer;
    }
  }


  &__content {
    @include flex-item-noJustify;
    margin: 10px 0;

  }

  &__text {
    padding: 5px 15px;
    border-radius: 8px;
    font-size: 16px;
    margin-left: 10px;
    background-color: #fff;
  }
}
</style>