import { createContext, useContext, useState, type ReactNode } from 'react'
import { login as loginApi } from '../api/authApi'
import type { UsuarioLogado } from '../api/types'
import { getUsuarioLogado, limparSessao, salvarUsuario } from './session'

interface AuthValue {
  usuario: UsuarioLogado | null
  login: (email: string, senha: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<UsuarioLogado | null>(
    getUsuarioLogado,
  )

  async function login(email: string, senha: string) {
    const dados = await loginApi(email, senha)
    salvarUsuario(dados)
    setUsuario(dados)
  }

  function logout() {
    limparSessao()
    setUsuario(null)
  }

  return (
    <AuthContext.Provider value={{ usuario, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth(): AuthValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth deve ser usado dentro de AuthProvider')
  return ctx
}
