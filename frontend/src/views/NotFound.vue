<template>
  <div class="not-found">
    <div class="content">
      <div class="code">404</div>
      <h2>页面不见啦</h2>
      <p>您访问的页面可能已搬家或不存在</p>
      <el-button type="primary" size="large" @click="$router.push('/')">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  height: calc(100vh - 200px);
  text-align: center;
}
.code {
  font-size: 120px;
  font-weight: 700;
  color: var(--primary-color, #8B7355);
  line-height: 1;
  opacity: 0.3;
  margin-bottom: 10px;
}
h2 { font-size: 28px; margin-bottom: 10px; color: var(--text-primary); }
p { color: var(--text-tertiary, #999); margin-bottom: 30px; }
</style>
