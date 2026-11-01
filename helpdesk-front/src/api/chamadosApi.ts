import { apiRequest } from './client'
import type {
  Chamado,
  Prioridade,
  ResumoDashboard,
  StatusChamado,
} from './types'

export interface FiltrosChamados {
  status?: StatusChamado | ''
  prioridade?: Prioridade | ''
}

export async function listarChamados(filtros: FiltrosChamados = {}) {
  // O back filtra so por status (/chamados/status/{status}); a prioridade
  // e filtrada aqui no cliente.
  const url = filtros.status ? `/chamados/status/${filtros.status}` : '/chamados'
  const lista = (await apiRequest<Chamado[]>(url)) ?? []
  return filtros.prioridade
    ? lista.filter((c) => c.prioridade === filtros.prioridade)
    : lista
}

export function resumoDashboard() {
  return apiRequest<ResumoDashboard>('/chamados/dashboard/resumo')
}

import type { Apontamento, Categoria, HistoricoItem } from './types'

export interface NovoChamado {
  titulo: string
  descricao: string
  categoria: Categoria
  prioridade: Prioridade
  solicitanteId?: number
}

const json = (body: unknown) => JSON.stringify(body)

export const criarChamado = (b: NovoChamado) =>
  apiRequest<Chamado>('/chamados', { method: 'POST', body: json(b) })

export const buscarChamado = (id: number) =>
  apiRequest<Chamado>(`/chamados/${id}`)

export const historicoChamado = (id: number) =>
  apiRequest<HistoricoItem[]>(`/chamados/${id}/historico`)

export const listarApontamentos = (id: number) =>
  apiRequest<Apontamento[]>(`/chamados/${id}/apontamentos`)

export const criarApontamento = (id: number, mensagem: string) =>
  apiRequest<Apontamento>(`/chamados/${id}/apontamentos`, {
    method: 'POST',
    body: json({ descricao: mensagem }),
  })

export const alocarTecnico = (id: number, tecnicoId: number) =>
  apiRequest<unknown>(`/chamados/${id}/alocar`, {
    method: 'POST',
    body: json({ tecnicoId }),
  })

export const alterarPrioridade = (
  id: number,
  prioridade: Prioridade,
  justificativa: string,
) =>
  apiRequest<unknown>(`/chamados/${id}/prioridade`, {
    method: 'PATCH',
    body: json({ prioridade, justificativa }),
  })

export const fecharChamado = (id: number, solucao: string) =>
  apiRequest<unknown>(`/chamados/${id}/encerrar`, {
    method: 'POST',
    body: json({ solucao }),
  })

export interface EdicaoChamado {
  titulo: string
  descricao: string
  categoria: Categoria
}

export const atualizarChamado = (id: number, b: EdicaoChamado) =>
  apiRequest<Chamado>(`/chamados/${id}`, { method: 'PUT', body: json(b) })

// Exclusao logica: o chamado fica inativo e sai da fila.
export const inativarChamado = (id: number) =>
  apiRequest<void>(`/chamados/${id}`, { method: 'DELETE' })
