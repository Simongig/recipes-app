import { defineStore } from 'pinia'

interface Alert {
  id: number
  title: string
  message?: string
  duration?: number
  type?: 'success' | 'error' | 'info'
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
