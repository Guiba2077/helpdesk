import { useCallback, useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import type { Perfil, UsuarioLogado } from '../api/types'
import { alternarStatusUsuario, listarUsuarios } from '../api/usuariosApi'
import { useAuth } from '../auth/AuthContext'
import Card from '../components/Card'
import { botaoSecundario } from '../components/form'
import UsuarioModal from '../components/UsuarioModal'
import { formatarData } from '../util/formatar'

const corPerfil: Record<Perfil, string> = {
  ADMIN: 'bg-purple-100 text-purple-800',
  TECNICO: 'bg-amber-100 text-amber-800',
  OPERADOR: 'bg-sky-100 text-sky-800',
}

export default function Usuarios() {
  const { usuario: logado } = useAuth()
  const [usuarios, setUsuarios] = useState<UsuarioLogado[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [modal, setModal] = useState<{ aberto: boolean; usuario?: UsuarioLogado }>({ aberto: false })
  const [recarga, setRecarga] = useState(0)
  const recarregar = useCallback(() => setRecarga((n) => n + 1), [])

  useEffect(() => {
    let ativo = true
    listarUsuarios(true)
      .then((u) => {
        if (!ativo) return
        setUsuarios(u ?? [])
        setErro('')
      })
      .catch((e) => {
        if (ativo) setErro(e instanceof ApiError ? e.message : 'Erro.')
      })
      .finally(() => {
        if (ativo) setCarregando(false)
      })
    return () => {
      ativo = false
    }
  }, [recarga])

  async function alternar(u: UsuarioLogado) {
    try {
      await alternarStatusUsuario(u.id)
      recarregar()
    } catch (e) {
      setErro(e instanceof ApiError ? e.message : 'Falha na operacao.')
    }
  }

  return (
    <>
      <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold text-slate-800">Usuarios</h1>
          <p className="text-xs text-slate-500">Inicio / Administracao / Usuarios</p>
        </div>
        <button
          onClick={() => setModal({ aberto: true })}
          className="rounded bg-primary hover:bg-primary-dark text-white px-4 py-2 font-medium"
        >
          + Novo usuario
        </button>
      </div>

      {erro && (
        <div role="alert" className="mb-4 rounded border border-red-200 bg-red-50 text-red-700 px-3 py-2">
          {erro}
        </div>
      )}

      <Card titulo="Contas de acesso">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-surface text-xs uppercase tracking-wide text-slate-500">
              <tr>
                {['Nome', 'Setor', 'Telefone', 'Perfil', 'Situacao', 'Cadastro', ''].map((h) => (
                  <th key={h} className="px-4 py-2.5 font-semibold">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {carregando ? (
                <tr><td colSpan={7} className="px-4 py-8 text-center text-slate-500">Carregando...</td></tr>
              ) : usuarios.length === 0 ? (
                <tr><td colSpan={7} className="px-4 py-8 text-center text-slate-500">Nenhum usuario.</td></tr>
              ) : (
                usuarios.map((u) => (
                  <tr key={u.id} className={`border-t border-line hover:bg-surface ${u.ativo === false ? 'opacity-60' : ''}`}>
                    <td className="px-4 py-3 font-medium">
                      {u.nome}
                      <span className="block text-xs font-normal text-slate-400">{u.email}</span>
                    </td>
                    <td className="px-4 py-3">{u.setor}</td>
                    <td className="px-4 py-3">{u.telefone ?? '-'}</td>
                    <td className="px-4 py-3">
                      <span className={`rounded px-2 py-0.5 text-xs font-semibold ${corPerfil[u.perfil]}`}>{u.perfil}</span>
                    </td>
                    <td className="px-4 py-3">
                      <span className={`rounded px-2 py-0.5 text-xs font-semibold ${u.ativo === false ? 'bg-slate-200 text-slate-600' : 'bg-green-100 text-green-800'}`}>
                        {u.ativo === false ? 'Inativo' : 'Ativo'}
                      </span>
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap">{formatarData((u as { dataCadastro?: string }).dataCadastro)}</td>
                    <td className="px-4 py-3 whitespace-nowrap text-right">
                      <button onClick={() => setModal({ aberto: true, usuario: u })} className={`${botaoSecundario} mr-2 py-1`}>
                        Editar
                      </button>
                      {u.id !== logado?.id && (
                        <button onClick={() => alternar(u)} className={`${botaoSecundario} py-1`}>
                          {u.ativo === false ? 'Ativar' : 'Inativar'}
                        </button>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {modal.aberto && (
        <UsuarioModal usuario={modal.usuario} onClose={() => setModal({ aberto: false })} onSalvo={recarregar} />
      )}
    </>
  )
}
