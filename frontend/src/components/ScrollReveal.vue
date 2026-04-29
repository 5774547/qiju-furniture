<template>
  <div ref="scrollRef" class="scroll-reveal" :class="{ 'is-visible': isVisible }">
    <slot></slot>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  threshold: {
    type: Number,
    default: 0.1
  },
  delay: {
    type: Number,
    default: 0
  }
})

const scrollRef = ref(null)
const isVisible = ref(false)

let observer = null

onMounted(() => {
  if (typeof window !== 'undefined' && 'IntersectionObserver' in window) {
    observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setTimeout(() => {
            isVisible.value = true
          }, props.delay)
          if (observer && scrollRef.value) {
            observer.unobserve(scrollRef.value)
          }
        }
      },
      { threshold: props.threshold }
    )
    if (scrollRef.value) {
      observer.observe(scrollRef.value)
    }
  } else {
    isVisible.value = true
  }
})

onUnmounted(() => {
  if (observer && scrollRef.value) {
    observer.unobserve(scrollRef.value)
  }
})
</script>

<style scoped>
.scroll-reveal {
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.scroll-reveal.is-visible {
  opacity: 1;
  transform: translateY(0);
}
</style>
