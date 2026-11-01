import type { UsuarioLogado } from '../api/types'

const KEY = 'usuarioLogado'

export function getUsuarioLogado(): UsuarioLogado | null {
  try {
    const data = localStorage.getItem(KEY)
    return data ? (JSON.parse(data) as UsuarioLogado) : null
  } catch {
    return null
  }
}

export function salvarUsuario(usuario: UsuarioLogado): void {
  localStorage.setItem(KEY, JSON.stringify(usuario))
}

export function limparSessao(): void {
  localStorage.removeItem(KEY)
}
