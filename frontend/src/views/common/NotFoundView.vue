<template>
  <div class="entry-page">
    <div class="entry-page__panel page-card">
      <h1>404</h1>
      <p class="muted-text">页面不存在或当前身份无权访问该页面。</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<style scoped>
.entry-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
}

.entry-page__panel {
  width: min(520px, 100%);
  padding: 32px;
  text-align: center;
}

h1 {
  margin: 0 0 12px;
  font-size: 48px;
}
</style>
