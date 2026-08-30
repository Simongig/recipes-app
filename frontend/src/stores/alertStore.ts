import { defineStore } from 'pinia'

interface Alert {
  id: number
  title: string
  message?: string
  duration?: number
  variant?: 'success' | 'destructive' | 'info'
}

type AlertInput = Omit<Alert, 'id'>

let nextAlertId = 0

export const useAlertStore = defineStore('alertStore', {
  state() {
    return {
      alerts: [] as Alert[],
    }
  },
  actions: {
    addAlert(alert: AlertInput) {
      const id = nextAlertId++
      this.alerts = this.alerts.slice(-2) // Keep only the last 2 alerts -> Only three in total
      this.alerts.push({ ...alert, id })

      const alertDuration = (alert.duration ?? 3) * 1000
      setTimeout(() => {
        this.removeAlert(id)
      }, alertDuration)
    },
    removeAlert(id: number) {
      this.alerts = this.alerts.filter(a => a.id !== id)
    }
  },
})
