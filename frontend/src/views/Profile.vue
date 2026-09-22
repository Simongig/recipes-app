<template>
    <main v-if="user">
        <section>
            <div class="flex flex-row justify-between items-center">
                <h1>Hi {{ user.fullName.trim() }}!</h1>
                <router-link :if="user.role && user.role.includes('ADMIN')" to="/admin" class="text-sm text-blue-500 hover:underline">Zur Admin-Seite</router-link>
            </div>
            <table>
                <tr>
                    <td>Username:</td>
                    <td>{{ user.username }}</td>
                </tr>
                <tr>
                    <td>Name:</td>
                    <td>{{ user.firstName }}</td>
                </tr>
                <tr>
                    <td>Lastname:</td>
                    <td>{{ user.lastName }}</td>
                </tr>
                <tr>
                    <td>Email:</td>
                    <td>{{ user.email }}</td>
                </tr>
                <tr v-if="user.role && user.role.length > 0">
                    <td>Roles:</td>
                    <td>{{(user.role || []).map(obj => obj.name.replace("ROLE_", "")).toString()}}</td>
                </tr>
            </table>
    </section>
        <section class="profile_recipes-section" v-if="user.recipes">
            <h2>Deine Rezepte ({{ user.recipes.length }})</h2>
            <ul v-if="user.recipes.length > 0">
                <li v-for="recipe in user.recipes" class="flex flex-row items-center gap-5 px-4 py-2 border rounded-lg mt-2" :key="recipe.id">
                    <img :src="recipe.imagePaths[0]" alt="Recipe Image"
                            class="w-12 h-12 object-cover rounded-lg" />
                    <router-link :to="`/recipe/id/${recipe.id}`">
                        {{recipe.title}}
                    </router-link>
                    <Button size="icon" variant="outline" class="ml-auto" title="Rezept bearbeiten"><Pencil /></Button>
                    <Button size="icon" variant="destructive" @click="deleteRecipe(recipe.id)"><Trash /></Button>
                </li>
            </ul>
            <p v-else>Du hast noch keine Rezepte erstellt.</p>
        </section>

        <section class="mt-8">
            <h2>Deine Favoriten ({{ user.favorites.length }})</h2>
            <ul v-if="user.favorites.length > 0">
                <li v-for="favorite in user.favorites" class="flex flex-row items-center gap-5 px-4 py-2 border rounded-lg mt-2" :key="favorite.id">                        
                    <img :src="favorite.imagePaths[0]" alt="Recipe Image"
                            class="w-12 h-12 object-cover rounded-lg" />
                    <router-link :to="`/recipe/id/${favorite.id}`"
                        class="">

                        {{ favorite.title }}
                    </router-link>
                    <Button size="icon" variant="destructive" class="ml-auto" title="Favorit entfernen" @click="removeFavorite(favorite.id)"><HeartOff /></Button>
                </li>
            </ul>
            <p v-else>Du hast noch keine Favoriten.</p>
        </section>
    </main>
</template>

<script setup>
import api from "@/services/api"
import { onMounted, ref } from "vue"
import { Pencil, Trash, HeartOff } from '@lucide/vue'
import { Button } from '@/components/ui/button'

const name = "Profile";
const user = ref(null);

async function fetchUser() {
    await api.get("/api/v1/user/me").then(res => {
        user.value = res.data;
    })
}

async function removeFavorite(recipeId) {
    await api.patch("/api/v1/user/updateFavorite", { recipeId: recipeId, add: false }).then(res => {
        user.value = res.data;
    })
}
async function deleteRecipe(recipeId) {
    await api.delete(`/api/v1/recipe/delete/${recipeId}`).then(res => {
        if (res.status === 200) {
            fetchUser();
        }
    })
}

onMounted(() => {
    fetchUser()
})
</script>

<style scoped>
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

section + section {
    margin-top: 2rem;
    padding-top: 2rem;
    border-top: 1px solid var(--light-grey);
}
</style>
