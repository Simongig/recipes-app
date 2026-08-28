<template>
  <main class="mx-auto max-w-6xl px-4 py-8">
    <h1 class="">Dein Wochenplan</h1>

    <section class="grid md:grid-cols-1 gap-4 lg:grid-cols-[1fr_1fr_1fr]">
      <Card class="mb-6 p-[1rem_2rem]">
        <CardHeader>
          <CardTitle class="text-lg text-center">Select the week for the Meal Plan</CardTitle>
        </CardHeader>
        <CardContent class="flex justify-center">
          <Calendar></Calendar>
        </CardContent>
      </Card>
      <Card class="mb-6">
        <CardHeader>
          <CardTitle class="text-lg">Generate with AI</CardTitle>
          <CardDescription>Kicks off an ml-service job and polls until it's done.</CardDescription>
        </CardHeader>
        <CardContent class="space-y-3">
          <div class="flex gap-2">
            
            <Textarea
              v-model="generationQuery"
              type="text"
              placeholder="e.g. high-protein vegetarian meals for next week"
              class="border-input block placeholder:text-muted-foreground flex-1 h-full rounded-md border bg-transparent px-3 py-2 text-sm shadow-xs outline-none"
              :disabled="isPolling || !isLoggedIn"
              @keyup.enter="startGeneration"
            />
            <Button :disabled="isPolling || !generationQuery.trim() || !isLoggedIn" :title="!isLoggedIn ? 'Please log in to generate a meal plan' : undefined" @click="startGeneration">
              {{ isPolling ? 'Generating…' : 'Generate' }}
            </Button>
          </div>

          <div v-if="job" class="space-y-2 text-sm">
            <Badge :variant="jobBadgeVariant">{{ job.status }}</Badge>
            <p v-if="job.status === 'COMPLETED' && mealPlan?.provenance" class="text-muted-foreground">
              {{ mealPlan.provenance.decision }}
            </p>
            <p v-if="job.status === 'FAILED'" class="text-destructive">{{ job.error }}</p>
          </div>
        </CardContent>
      </Card>
      <Card class="mb-6 bg-sg-tertiary-color">
        <CardHeader>
          <CardTitle class="text-lg">Meal Plan Decision</CardTitle>
        </CardHeader>
        <CardContent>
          <p v-if="mealPlan?.provenance" class="text-muted-foreground">
            {{ mealPlan.provenance.decision }}
          </p>
          <p v-else class="text-muted-foreground">No decision available.</p>
        </CardContent>
      </Card>
    </section>

    <div v-if="mealPlan" class="grid grid-cols-1 gap-4">
      <Card class="grid lg:grid-cols-4 outline-none border-none" v-for="day in mealPlan.entries" :key="day.date" :date="day.date" :meals="JSON.stringify(day.meals)" >
        <CardHeader class="col-span-1">
          <CardTitle class="">
            <h2 class="mb-0">{{ DAY_LABELS[new Date(day.date).getDay()] }}</h2>
            <span class="text-muted-foreground text-2xl">
              {{ new Date(day.date).getDay().toString().padStart(2, '0') }}.
              {{ new Date(day.date).getMonth().toString().padStart(2, '0') }}.
            </span>
          </CardTitle>
        </CardHeader>
        <CardContent class="space-y-3 col-span-3">
          <MealPlanRecipeCard v-if="Object.entries(mealPlan.recipes).length > 0" :recipe="mealPlan.recipes[mealId]" :mealSlot="mealSlot" v-for="(mealId, mealSlot) in day.meals" :key="mealSlot" />
          <p v-else class="text-muted-foreground">No recipes available for this day.</p>    
        </CardContent>
      </Card>
    </div>
    <div v-else class="text-center text-muted-foreground">No meal plan available.</div>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import api from '@/services/api'
import MealPlanRecipeCard from '@/components/MealPlanRecipeCard.vue'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Calendar } from '@/components/ui/calendar'
import { Textarea } from '@/components/ui/textarea'
import { useAuthStore } from '@/stores/authStore'
import { useAlertStore } from '@/stores/alertStore'

const authStore = useAuthStore()
const alertStore = useAlertStore()

const isLoggedIn = computed(() => authStore.isLoggedIn)

interface GenerationJob {
  jobId: string
  mealPlanId: string | null
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'
  startDate: string | null
  endDate: string | null
  error: string | null
}

interface MealPlanProvenance {
  jobId: string
  query: string
  decision: string | null
}

interface MealPlan {
  id: string
  ownerId: string
  entries: { date: string; meals: Record<string, string> }[]
  startDate: string
  endDate: string
  recipes: Record<string, { id: string; title: string; description: string, imagePaths: string[], portions: number, duration: number }>
  provenance: MealPlanProvenance | null
}

const generationQuery = ref('')
const job = ref<GenerationJob | null>(null)
const mealPlan = ref<MealPlan | null>(null)
const isPolling = ref(false)
let pollTimer: ReturnType<typeof setInterval> | null = null

const jobBadgeVariant = computed(() => {
  if (!job.value) return 'secondary'
  if (job.value.status === 'COMPLETED') return 'default'
  if (job.value.status === 'FAILED') return 'destructive'
  return 'secondary'
})

onMounted(() => {
  fetchCurrentMealPlan()
})

function startGeneration() {
  if (!generationQuery.value.trim() || isPolling.value) return
  isPolling.value = true

  let thisWeekMonday: Date = getMonday(new Date())
  let thisWeekSunday: Date = new Date(thisWeekMonday.getTime() + 6 * 24 * 60 * 60 * 1000)

  api
    .post<GenerationJob>('/api/v1/mealplans', {
      query: generationQuery.value,
      startDate: toIsoDate(thisWeekMonday),
      endDate: toIsoDate(thisWeekSunday),
    })
    .then((response) => {
      job.value = response.data
      pollTimer = setInterval(pollJob, 2500)
    })
    .catch((error) => {
      console.error('Error starting meal plan generation:', error)
      isPolling.value = false
    })
}

function getMonday(d: Date | string): Date {
  d = new Date(d);
  var day = d.getDay(),
    diff = d.getDate() - day + (day == 0 ? -6 : 1); // adjust when day is sunday
  return new Date(d.setDate(diff));
}

// yyyy-MM-dd in local time — matches the backend's LocalDate (a calendar day,
// not an instant), so this must not go through toISOString()'s UTC shift.
function toIsoDate(d: Date): string {
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function pollJob() {
  if (!job.value) return
  api
    .get<GenerationJob>(`/api/v1/mealplans/jobs/${job.value.jobId}`)
    .then((response) => {
      job.value = response.data
      if (response.data.status === 'COMPLETED') {
        stopPolling()
        fetchGeneratedMealPlanById()
      } else if (response.data.status === 'FAILED') {
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

function fetchCurrentMealPlan() {
  api
    .get<MealPlan>('/api/v1/mealplans/current')
    .then((response) => {
      mealPlan.value = response.data
    })
    .catch((error) => {
      console.error('Error fetching current meal plan, falling back to dummy meal plan:', error)
    })
}

function fetchGeneratedMealPlanById() {
  if (!job.value?.mealPlanId) return
  api
    .get<MealPlan>(`/api/v1/mealplans/${job.value.mealPlanId}`)
    .then((response) => {
      mealPlan.value = response.data
    })
    .catch((error) => {
      console.error('Error fetching meal plan:', error)
    })
}

onUnmounted(stopPolling)

const MEAL_SLOTS = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'] as const
type MealSlot = (typeof MEAL_SLOTS)[number]

const DAY_LABELS = ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag']

</script>
