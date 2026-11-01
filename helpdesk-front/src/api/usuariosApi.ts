import { apiRequest } from './client'
import type { Perfil, UsuarioLogado } from './types'

export const listarUsuarios = (incluirInativos = false) =>
  apiRequest<UsuarioLogado[]>(
    `/usuarios${incluirInativos ? '?incluirInativos=true' : ''}`,
  )

export interface DadosUsuario {
  nome: string
  email: string
  senha?: string
  telefone?: string
  setor: string
  perfil: Perfil
}

export const criarUsuario = (b: DadosUsuario) =>
  apiRequest<UsuarioLogado>('/usuarios', {
    method: 'POST',
    body: JSON.stringify(b),
  })

// O back nao altera a senha na edicao.
export const atualizarUsuario = (id: number, b: DadosUsuario) =>
  apiRequest<UsuarioLogado>(`/usuarios/${id}`, {
    method: 'PUT',
    body: JSON.stringify(b),
  })

export const alternarStatusUsuario = (id: number) =>
  apiRequest<void>(`/usuarios/${id}/status`, { method: 'PATCH' })
