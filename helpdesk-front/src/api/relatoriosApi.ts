import { apiRequest } from './client'
import type { AuditoriaPrioridade, MetricaSla } from './types'

export const listarAuditoriaPrioridades = () =>
  apiRequest<AuditoriaPrioridade[]>('/auditoria/prioridades')

export const metricasSla = () => apiRequest<MetricaSla[]>('/relatorios/sla-tecnicos')
