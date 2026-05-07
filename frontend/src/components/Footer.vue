<template>
  <footer>
    <div class="footer-content">
      <router-link class="footer-link" to="/impressum">Impressum</router-link>
      <p class="footer-brand-name">Kochbuch.io {{ new Date().getFullYear() }}</p>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer',
}
</script>

<style>
footer {
  padding: 1rem;
  border-top: 1px solid var(--light-grey);
}

.footer-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
}

.footer-content .footer-link:hover {
  color: var(--secondary-color);
}

.footer-brand-name {
  margin-top: 1rem;
  font-size: smaller;
  cursor: default;
}
</style>
