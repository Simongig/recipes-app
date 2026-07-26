<template>
    <Card class="grid grid-cols-4 p-[0.5rem]">
        <div class="col-span-1">
            <img v-if="recipe.imagePaths.length > 0" :src="recipe.imagePaths[0]" alt="Recipe Image" class="w-full h-32 object-cover rounded-lg" />
            <div v-else class="w-full h-32 bg-gray-200 flex items-center justify-center rounded-t-lg">
                <span class="text-gray-500">No Image</span>
            </div>
        </div>
        <div class="col-span-3">
            <CardHeader>
                <CardTitle class="text-base text-foreground">
                    <span class="text-lg text-muted-foreground">{{ MealSlotMap[mealSlot] }}</span>
                    <h3 class="text-xl font-semibold">{{ recipe.title }}</h3>
                </CardTitle>
                <CardDescription class="text-sm text-muted-foreground">
                    {{ recipe.description }}
                </CardDescription>
            </CardHeader>
            <CardContent class="space-y-3">
                <p class="text-sm text-muted-foreground">Dauer: {{ recipe.duration }} Minuten</p>
            </CardContent>
        </div>
    </Card>
</template>

<script setup lang="ts">
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'

const name = 'mealPlanRecipeCard'

const MealSlotMap: Record<string, string> = {
  BREAKFAST: 'Frühstück',
  LUNCH: 'Mittagessen',
  DINNER: 'Abendessen',
  SNACK: 'Snack'
}

interface RecipeSummary {
  id: string
  title: string
  description: string
  imagePaths: string[]
  portions: number
  duration: number
}

const props = defineProps<{
  recipe: RecipeSummary
  mealSlot: string
}>();

</script>