import type { Categoria, Perfil, Prioridade, StatusChamado } from '../api/types'

// Os valores dos enums continuam iguais aos da API; aqui ficam apenas os textos exibidos.

export const rotuloStatus: Record<StatusChamado, string> = {
  ABERTO: 'Aberto',
  ATENDIMENTO: 'Em atendimento',
  FECHADO: 'Fechado',
}

export const rotuloPrioridade: Record<Prioridade, string> = {
  BAIXA: 'Baixa',
  MEDIA: 'Média',
  ALTA: 'Alta',
  CRITICA: 'Crítica',
}

export const rotuloPerfil: Record<Perfil, string> = {
  ADMIN: 'Administrador',
  TECNICO: 'Técnico',
  OPERADOR: 'Operador',
}

export const CATEGORIAS: { valor: Categoria; rotulo: string }[] = [
  { valor: 'REDE', rotulo: 'Infraestrutura e Rede' },
  { valor: 'HARDWARE', rotulo: 'Hardware e Equipamentos' },
  { valor: 'SOFTWARE', rotulo: 'Sistemas e Software' },
  { valor: 'BANCO_DADOS', rotulo: 'Banco de Dados' },
  { valor: 'ACESSO', rotulo: 'Segurança e Permissões de Acesso' },
]

export function rotuloCategoria(valor: string): string {
  return CATEGORIAS.find((c) => c.valor === valor)?.rotulo ?? valor
}
