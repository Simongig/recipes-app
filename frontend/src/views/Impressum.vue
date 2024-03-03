<template>
    <main class="impressum">
        <h1>Impressum</h1>

        <h3>Angaben gem&auml;&szlig; &sect; 5 TMG</h3>
        <p>Simon Giglhuber<br />
            Strelitzstra&szlig;e 8<br />
            12105 Berlin</p>

        <h3>Kontakt</h3>
        <p>Telefon: +4915225699843<br />
            E-Mail: simongig@pm.me</p>

        <p>Quelle: <a href="https://www.e-recht24.de">https://www.e-recht24.de</a></p>
    </main>
</template>

<script>
export default {
    name: "Impressum",
        mounted() {
            window.scroll(0,0)
        }
}
</script>

<style>
    .impressum {
        min-height: 100vh;
    }

    .impressum h1 {
        padding: 0;
    }

    .impressum h3 {
        margin: 1rem 0;
    }
</style>