<template>
  <div class="skeleton-card">
    <div class="skeleton-image shimmer" />
    <div class="skeleton-body">
      <div class="skeleton-line w-40 shimmer" />
      <div class="skeleton-line w-60 shimmer" />
      <div class="skeleton-line w-30 shimmer" />
      <div class="skeleton-line w-20 shimmer" />
    </div>
  </div>
</template>

<style scoped>
.skeleton-card {
  background: var(--card-bg, #fff);
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid var(--border-color, #e8e3dc);
}
.skeleton-image {
  width: 100%;
  aspect-ratio: 1;
  background: #e8e3dc;
}
.skeleton-body { padding: 16px; }
.skeleton-line {
  height: 14px;
  border-radius: 4px;
  background: #e8e3dc;
  margin-bottom: 10px;
}
.w-20 { width: 20%; }
.w-30 { width: 30%; }
.w-40 { width: 40%; }
.w-60 { width: 60%; }
.shimmer {
  background: linear-gradient(90deg, #e8e3dc 25%, #f0ebe5 50%, #e8e3dc 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}
@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
