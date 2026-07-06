<template>
  <main class="mx-auto max-w-6xl px-4 py-8">
    <Card class="mb-6">
      <CardHeader class="flex flex-row items-center justify-between space-y-0">
        <div>
          <CardTitle class="text-2xl">Meal Planner</CardTitle>
          <CardDescription>Dummy data — shadcn-vue smoke test.</CardDescription>
        </div>
        <Button @click="shuffle">
          <Shuffle class="size-4" />
          Shuffle plan
        </Button>
      </CardHeader>
    </Card>

    <Card class="mb-6">
      <CardHeader>
        <CardTitle class="text-lg">Generate with AI</CardTitle>
        <CardDescription>Kicks off an ml-service job and polls until it's done.</CardDescription>
      </CardHeader>
      <CardContent class="space-y-3">
        <div class="flex gap-2">
          <input
            v-model="generationQuery"
            type="text"
            placeholder="e.g. high-protein vegetarian meals for next week"
            class="border-input placeholder:text-muted-foreground flex-1 rounded-md border bg-transparent px-3 py-2 text-sm shadow-xs outline-none"
            :disabled="isPolling"
            @keyup.enter="startGeneration"
          />
          <Button :disabled="isPolling || !generationQuery.trim()" @click="startGeneration">
            {{ isPolling ? 'Generating…' : 'Generate' }}
          </Button>
        </div>

        <div v-if="job" class="space-y-2 text-sm">
          <Badge :variant="jobBadgeVariant">{{ job.status }}</Badge>
          <p v-if="job.status === 'done'" class="text-muted-foreground">
            {{ job.decision }}<br />
            Recipe ids: {{ job.recipeIds.join(', ') || '—' }}
          </p>
          <p v-if="job.status === 'failed'" class="text-destructive">{{ job.error }}</p>
        </div>
      </CardContent>
    </Card>

    <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
      <Card v-for="day in week" :key="day.date">
        <CardHeader>
          <CardTitle class="flex items-center justify-between text-base">
            {{ day.label }}
            <Badge :variant="plannedCount(day) === MEAL_SLOTS.length ? 'default' : 'secondary'">
              {{ plannedCount(day) }}/{{ MEAL_SLOTS.length }}
            </Badge>
          </CardTitle>
        </CardHeader>
        <CardContent class="space-y-3">
          <div v-for="slot in MEAL_SLOTS" :key="slot" class="space-y-1.5">
            <Label class="text-muted-foreground text-xs uppercase">{{ slot }}</Label>
            <Select v-model="day.meals[slot]">
              <SelectTrigger class="w-full">
                <SelectValue placeholder="Pick a recipe" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem v-for="recipe in DUMMY_RECIPES" :key="recipe" :value="recipe">
                  {{ recipe }}
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>
    </div>
  </main>
</template>

<script setup lang="ts">
import { computed, onUnmounted, reactive, ref } from 'vue'
import { Shuffle } from 'lucide-vue-next'
import api from '@/services/api'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

interface GenerationJob {
  jobId: string
  status: 'pending' | 'processing' | 'done' | 'failed'
  recipeIds: string[]
  decision: string | null
  error: string | null
}

const generationQuery = ref('')
const job = ref<GenerationJob | null>(null)
const isPolling = ref(false)
let pollTimer: ReturnType<typeof setInterval> | null = null

const jobBadgeVariant = computed(() => {
  if (!job.value) return 'secondary'
  if (job.value.status === 'done') return 'default'
  if (job.value.status === 'failed') return 'destructive'
  return 'secondary'
})

function startGeneration() {
  if (!generationQuery.value.trim() || isPolling.value) return
  isPolling.value = true
  api
    .post<GenerationJob>('/api/v1/mealplans', { query: generationQuery.value })
    .then((response) => {
      job.value = response.data
      pollTimer = setInterval(pollJob, 2500)
    })
    .catch((error) => {
      console.error('Error starting meal plan generation:', error)
      isPolling.value = false
    })
}

function pollJob() {
  if (!job.value) return
  api
    .get<GenerationJob>(`/api/v1/mealplans/${job.value.jobId}`)
    .then((response) => {
      job.value = response.data
      if (response.data.status === 'done' || response.data.status === 'failed') {
        stopPolling()
      }
    })
    .catch((error) => {
      console.error('Error polling meal plan job:', error)
      stopPolling()
    })
}

function stopPolling() {
  isPolling.value = false
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

onUnmounted(stopPolling)

const MEAL_SLOTS = ['Breakfast', 'Lunch', 'Dinner'] as const
type MealSlot = (typeof MEAL_SLOTS)[number]

const DUMMY_RECIPES = [
  'Overnight Oats',
  'Avocado Toast',
  'Chickpea Curry',
  'Margherita Pizza',
  'Miso Soup',
  'Greek Salad',
  'Veggie Stir-fry',
]

const DAY_LABELS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']

interface Day {
  date: string
  label: string
  meals: Record<MealSlot, string>
}

function randomPlan(): Record<MealSlot, string> {
  const meals = {} as Record<MealSlot, string>
  for (const slot of MEAL_SLOTS) {
    meals[slot] = DUMMY_RECIPES[Math.floor(Math.random() * DUMMY_RECIPES.length)]
  }
  return meals
}

const week = reactive<Day[]>(
  DAY_LABELS.map((label, i) => ({
    date: `day-${i}`,
    label,
    meals: randomPlan(),
  })),
)

function plannedCount(day: Day) {
  return MEAL_SLOTS.filter((slot) => day.meals[slot]).length
}

function shuffle() {
  week.forEach((day) => {
    day.meals = randomPlan()
  })
}
</script>
