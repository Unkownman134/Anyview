import { defineStore } from 'pinia'
import { ref } from 'vue'

interface User {
  id: number
  username: string
  realName: string
  email: string
  role: 'ADMIN' | 'TEACHER' | 'STUDENT'
  schoolId?: number
  classId?: number
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref<User | null>(JSON.parse(localStorage.getItem('user') || 'null'))

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUser(newUser: User) {
    user.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return {
    token,
    user,
    setToken,
    setUser,
    logout
  }
})
