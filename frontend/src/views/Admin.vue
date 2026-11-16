<template>
    <main>
        <h1>Admin</h1>
        <section>
            <h2>Benutzerverwaltung</h2>
            <p>Hier können Sie Benutzer verwalten.</p>
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Username</TableHead>
                        <TableHead>Email</TableHead>
                        <TableHead>Aktionen</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    <TableRow v-for="user in users" :key="user.id">
                        <TableCell>{{ user.username }}</TableCell>
                        <TableCell>{{ user.email }}</TableCell>
                        <TableCell>
                            <Button size="icon" variant="destructive" title="Benutzer löschen"
                                @click="requestDelete('user', user)">
                                <Trash />
                            </Button>
                        </TableCell>
                    </TableRow>
                </TableBody>
            </Table>
        </section>
        <section>
            <h2>Rezeptverwaltung</h2>
            <p>Hier können Sie Rezepte verwalten.</p>
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Titel</TableHead>
                        <TableHead>Owner</TableHead>
                        <TableHead>Aktionen</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    <TableRow v-for="recipe in recipes" :key="recipe.id">
                        <TableCell>{{ recipe.title }}</TableCell>
                        <TableCell>{{ recipe.owner }}</TableCell>
                        <TableCell>
                            <Button size="icon" variant="destructive" title="Rezept löschen"
                                @click="requestDelete('recipe', recipe)">
                                <Trash />
                            </Button>
                        </TableCell>
                    </TableRow>
                </TableBody>
            </Table>
        </section>
        <AlertDialog v-model:open="dialogOpen">
            <AlertDialogContent size="sm">
                <AlertDialogHeader>
                    <AlertDialogMedia>
                        <Trash class="h-4 w-4 text-destructive" />
                    </AlertDialogMedia>
                    <AlertDialogTitle>Bist du sicher?</AlertDialogTitle>
                    <AlertDialogDescription>
                        Dieser Vorgang kann nicht rückgängig gemacht werden.
                        {{ dialogDescription }}
                    </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel>Abbrechen</AlertDialogCancel>
                    <AlertDialogAction variant="destructive" @click="confirmDelete">Löschen</AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    </main>
</template>

<script setup>
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { AlertDialog, AlertDialogContent, AlertDialogHeader, AlertDialogTitle, AlertDialogDescription, AlertDialogFooter, AlertDialogCancel, AlertDialogAction } from '@/components/ui/alert-dialog'
import { Button } from '@/components/ui/button'
import { Trash } from '@lucide/vue'
import { ref, computed } from 'vue'
import AlertDialogMedia from '@/components/ui/alert-dialog/AlertDialogMedia.vue'

const users = ref([])
const recipes = ref([])

const dialogOpen = ref(false)
const pendingDelete = ref(null) // { type: 'user' | 'recipe', item }

for (let i = 1; i <= 10; i++) {
    users.value.push({
        id: i,
        username: `user${i}`,
        email: `user${i}@example.com`
    })
}

for (let i = 1; i <= 10; i++) {
    recipes.value.push({
        id: i,
        owner: `user${Math.ceil(Math.random() * 10)}`,
        title: `Rezept ${i}`,
    })
}

function requestDelete(type, item) {
    pendingDelete.value = { type, item }
    dialogOpen.value = true
}

const dialogDescription = computed(() => {
    const p = pendingDelete.value
    if (!p) return ''
    return p.type === 'user'
        ? `Möchtest du den Benutzer „${p.item.username}“ wirklich löschen?`
        : `Möchtest du das Rezept „${p.item.title}“ wirklich löschen?`
})


async function confirmDelete() {
    const { type, item } = pendingDelete.value
    if (type === 'user') {
        // await api.deleteUser(item.id)
        users.value = users.value.filter(u => u.id !== item.id)
    } else {
        // await api.deleteRecipe(item.id)
        recipes.value = recipes.value.filter(r => r.id !== item.id)
    }
    dialogOpen.value = false
}

</script>

<style>
main {
    padding: 2rem;
}

section {
    margin-bottom: 2rem;
}

h1,
h2 {
    margin-bottom: 1rem;
}
</style>