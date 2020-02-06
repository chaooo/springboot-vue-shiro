<template>
  <div class="app-container">
    <p>标题: {{ title }}</p>
    <p v-if="roles">角色: {{ roles }}</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      title: this.$route.meta.title,
      roles: this.$route.meta.roles
    }
  }
}
</script>
