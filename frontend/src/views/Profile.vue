<template>
    <main v-if="user">
        <h1>Hi {{ user.fullName.trim() }}!</h1>
        <table >
            <tr>
                <td>Username:</td>
                <td>{{ user.username }}</td>
            </tr>
            <tr>
                <td>Name:</td>
                <td>{{ user.name }}</td>
            </tr>
            <tr>
                <td>Lastname:</td>
                <td>{{ user.lastName }}</td>
            </tr>
            <tr>
                <td>Email:</td>
                <td>{{ user.email }}</td>
            </tr>
            <tr>
                <td>Roles:</td>
                <td>{{ (user.role || []).map(obj => obj.name.replace("ROLE_", "")).toString() }}</td>
            </tr>
        </table>
        
        <section class="profile_recipes-section" v-if="recipes">
            <h2>Your Recipes ({{ recipes.length }})</h2>
            <ul v-for="recipe in recipes">
                <li>recipe</li>
            </ul>
        </section>
    </main>
</template>

<script>
import api from "@/services/api"

export default {
    components: {},
    name: "Profile",
    data() {
        return {
            user: null,
            recipes: []
        }
    },  
    methods: {
        async fetchUser() {
            await api.get("/api/v1/user/me").then(res => {
                this.user = res.data;
            })
        }
    },
    mounted() {
        this.fetchUser()
    },
}
</script>

<style>
table {
    max-width: 300px;
    display: flex;
    flex-direction: column;
}

tr {
    display: flex;
    
}

td {
    width: 100%;
}

.profile_recipes-section {
    margin-top: 2rem;
    padding-top: 2rem;
    border-top: 1px solid var(--light-grey);
}
</style>
