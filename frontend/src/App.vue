<template>
  <div class="app-shell">
    <header class="app-header">
      <div>
        <p class="eyebrow">Graduate Employment Management System</p>
        <h1>毕业生就业管理系统</h1>
      </div>
      <span class="badge">Skeleton</span>
    </header>
    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  padding: 32px;
  color: #1f2937;
  background:
    radial-gradient(circle at top left, rgba(14, 116, 144, 0.16), transparent 30%),
    linear-gradient(135deg, #f8fafc, #eef6ff);
}

.app-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 32px;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
  color: #0f766e;
}

h1 {
  margin: 0;
  font-size: 32px;
}

.badge {
  padding: 6px 12px;
  border-radius: 999px;
  background: #0f766e;
  color: #fff;
  font-size: 14px;
}

.app-main {
  max-width: 960px;
}

@media (max-width: 640px) {
  .app-shell {
    padding: 20px;
  }

  .app-header {
    flex-direction: column;
  }

  h1 {
    font-size: 26px;
  }
}
</style>

