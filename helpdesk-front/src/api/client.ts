import type { ApiResponse } from './types'
import { getUsuarioLogado } from '../auth/session'

export const API_URL =
  import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

export class ApiError extends Error {
  status: number
  erros: string[]

  constructor(message: string, status: number, erros: string[] = []) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.erros = erros
  }
}

function mensagemPadrao(status: number): string {
  switch (status) {
    case 400:
      return 'Dados invalidos ou campos obrigatorios ausentes.'
    case 401:
      return 'E-mail ou senha invalidos.'
    case 504:
      return 'A consulta excedeu o tempo limite configurado no PostgreSQL (statement_timeout = 3s).'
    default:
      return 'Ocorreu um erro no servidor.'
  }
}

/**
 * Wrapper de requisicao: injeta X-Usuario-Id, desembrulha o envelope
 * ApiResponse e lanca ApiError com status HTTP em caso de falha.
 */
export async function apiRequest<T>(
  endpoint: string,
  options: RequestInit = {},
): Promise<T> {
  const headers = new Headers(options.headers)
  headers.set('Content-Type', 'application/json')

  const usuario = getUsuarioLogado()
  if (usuario) headers.set('X-Usuario-Id', String(usuario.id))

  let response: Response
  try {
    response = await fetch(`${API_URL}${endpoint}`, { ...options, headers })
  } catch {
    throw new ApiError('Nao foi possivel conectar ao servidor.', 0)
  }

  const body = (await response.json().catch(() => null)) as
    | ApiResponse<T>
    | T
    | null

  if (!response.ok) {
    const env = body as ApiResponse<T> | null
    if (response.status === 500) console.error('Erro 500 do servidor:', body)
    throw new ApiError(
      env?.mensagem || mensagemPadrao(response.status),
      response.status,
      env?.erros ?? [],
    )
  }

  if (body && typeof body === 'object' && 'sucesso' in body) {
    return (body as ApiResponse<T>).dados as T
  }
  return body as T
}
