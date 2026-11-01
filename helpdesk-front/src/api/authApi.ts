import { apiRequest } from './client'
import type { UsuarioLogado } from './types'

export function login(email: string, senha: string): Promise<UsuarioLogado> {
  return apiRequest<UsuarioLogado>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, senha }),
  })
}
