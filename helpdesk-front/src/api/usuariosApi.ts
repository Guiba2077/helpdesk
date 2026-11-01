import { apiRequest } from './client'
import type { Perfil, UsuarioLogado } from './types'

export const listarUsuarios = () => apiRequest<UsuarioLogado[]>('/usuarios')

export interface NovoUsuario {
  nome: string
  email: string
  senha: string
  telefone?: string
  setor: string
  perfil: Perfil
}

export const criarUsuario = (b: NovoUsuario) =>
  apiRequest<UsuarioLogado>('/usuarios', {
    method: 'POST',
    body: JSON.stringify(b),
  })
