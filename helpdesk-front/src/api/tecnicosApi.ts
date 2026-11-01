import { apiRequest } from './client'
import type { Especialidade, NivelAtendimento, Tecnico } from './types'

export const listarTecnicos = () => apiRequest<Tecnico[]>('/tecnicos')

export const listarEspecialidades = () =>
  apiRequest<Especialidade[]>('/tecnicos/especialidades')

export interface NovoTecnico {
  usuarioId: number
  matricula: string
  especialidade: string
  nivelAtendimento: NivelAtendimento
  disponivel: boolean
}

export const criarTecnico = (b: NovoTecnico) =>
  apiRequest<Tecnico>('/tecnicos', { method: 'POST', body: JSON.stringify(b) })

// O back inverte o valor atual (sem corpo) e so o ADMIN pode usar.
export const alternarDisponibilidade = (id: number) =>
  apiRequest<Tecnico>(`/tecnicos/${id}/disponibilidade`, { method: 'PATCH' })
