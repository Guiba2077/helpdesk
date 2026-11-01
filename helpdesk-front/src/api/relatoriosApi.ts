import { apiRequest } from './client'
import type { AuditoriaPrioridade, MetricaSla } from './types'

export const listarAuditoriaPrioridades = () =>
  apiRequest<AuditoriaPrioridade[]>('/auditoria/prioridades')

export const metricasSla = () => apiRequest<MetricaSla[]>('/relatorios/sla-tecnicos')

// Consulta propositalmente pesada: o PostgreSQL cancela em 3s (HTTP 504).
export const relatorioPesado = (segundos = 6) =>
  apiRequest<string>(`/relatorios/pesado?segundos=${segundos}`)
