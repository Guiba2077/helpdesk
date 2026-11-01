export type Perfil = 'ADMIN' | 'TECNICO' | 'OPERADOR'

export interface UsuarioLogado {
  id: number
  nome: string
  email: string
  setor: string
  telefone?: string | null
  ativo?: boolean
  perfil: Perfil
}

export interface ApiResponse<T> {
  sucesso: boolean
  mensagem?: string
  dados: T | null
  erros?: string[]
}

export type StatusChamado = 'ABERTO' | 'ATENDIMENTO' | 'FECHADO'
export type Prioridade = 'BAIXA' | 'MEDIA' | 'ALTA' | 'CRITICA'

export interface Chamado {
  id: number
  protocolo: string
  titulo: string
  descricao: string
  categoria: string
  prioridade: Prioridade
  status: StatusChamado
  solicitanteId: number
  solicitanteNome: string
  solicitanteSetor: string
  solicitanteEmail?: string
  tecnicoId: number | null
  tecnicoNome: string | null
  tecnicoEspecialidade?: string | null
  solucao?: string | null
  dataAbertura: string
  dataAtendimento: string | null
  dataFechamento: string | null
}

export interface ResumoDashboard {
  totalChamados: number
  chamadosAbertos: number
  chamadosEmAtendimento: number
  chamadosFechados: number
  chamadosPrioridadeCritica: number
}

export type Categoria =
  | 'REDE'
  | 'HARDWARE'
  | 'SOFTWARE'
  | 'BANCO_DADOS'
  | 'ACESSO'

export interface Tecnico {
  id: number
  usuarioId: number
  nome: string
  email?: string
  telefone?: string | null
  setor?: string | null
  matricula: string
  especialidade: string
  nivelAtendimento: string
  disponivel: boolean
  ativo?: boolean
}

export interface Especialidade {
  codigo: string
  nome: string
  nivelAtendimento: string
}

export type NivelAtendimento = 'N1' | 'N2' | 'N3'

export interface HistoricoItem {
  id: number
  statusAnterior: StatusChamado | null
  statusNovo: StatusChamado
  usuarioResponsavelNome?: string | null
  observacao?: string | null
  dataMudanca: string
}

export interface Apontamento {
  id: number
  descricao: string
  autorNome?: string
  criadoEm: string
}

export interface AuditoriaPrioridade {
  id: number
  chamadoId: number
  protocoloChamado: string
  tituloChamado?: string | null
  prioridadeAnterior: Prioridade
  prioridadeNova: Prioridade
  alteradoPorNome?: string | null
  justificativa?: string | null
  dataAlteracao: string
}

export interface MetricaSla {
  tecnicoId: number
  tecnicoNome: string
  especialidade: string
  totalChamadosAtribuidos: number
  chamadosEncerrados: number
  chamadosEmAtendimento: number
  tempoMedioResolucaoHoras: number
}
