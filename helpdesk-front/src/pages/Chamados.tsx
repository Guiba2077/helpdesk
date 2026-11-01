import { useCallback, useEffect, useState } from 'react'
import { listarChamados, resumoDashboard } from '../api/chamadosApi'
import { ApiError } from '../api/client'
import type {
  Chamado,
  Prioridade,
  ResumoDashboard,
  StatusChamado,
} from '../api/types'
import { useAuth } from '../auth/AuthContext'
import Card from '../components/Card'
import DetalheChamadoModal from '../components/DetalheChamadoModal'
import NovoChamadoModal from '../components/NovoChamadoModal'

const STATUS: StatusChamado[] = ['ABERTO', 'ATENDIMENTO', 'FECHADO']
const PRIORIDADES: Prioridade[] = ['BAIXA', 'MEDIA', 'ALTA', 'CRITICA']

const corStatus: Record<StatusChamado, string> = {
  ABERTO: 'bg-blue-100 text-blue-800',
  ATENDIMENTO: 'bg-amber-100 text-amber-800',
  FECHADO: 'bg-green-100 text-green-800',
}

const corPrioridade: Record<Prioridade, string> = {
  BAIXA: 'bg-slate-100 text-slate-700',
  MEDIA: 'bg-sky-100 text-sky-800',
  ALTA: 'bg-orange-100 text-orange-800',
  CRITICA: 'bg-red-100 text-red-800',
}

function formatarData(iso: string | null) {
  return iso ? new Date(iso).toLocaleString('pt-BR') : '-'
}

function Indicador({
  titulo,
  valor,
  cor,
}: {
  titulo: string
  valor?: number
  cor: string
}) {
  return (
    <div className={`rounded-md bg-white border border-line border-l-4 ${cor} shadow-sm p-4`}>
      <p className="text-xs uppercase tracking-wide text-slate-500">{titulo}</p>
      <p className="text-3xl font-bold text-slate-800">{valor ?? '-'}</p>
    </div>
  )
}

export default function Chamados() {
  const { usuario } = useAuth()
  const [resumo, setResumo] = useState<ResumoDashboard>()
  const [chamados, setChamados] = useState<Chamado[]>([])
  const [status, setStatus] = useState<StatusChamado | ''>('')
  const [prioridade, setPrioridade] = useState<Prioridade | ''>('')
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(true)
  const [recarga, setRecarga] = useState(0)
  const [novo, setNovo] = useState(false)
  const [detalheId, setDetalheId] = useState<number | null>(null)
  const podeAbrir = usuario?.perfil !== 'TECNICO'
  const recarregar = useCallback(() => setRecarga((n) => n + 1), [])

  useEffect(() => {
    resumoDashboard()
      .then(setResumo)
      .catch((e) => setErro(e instanceof ApiError ? e.message : 'Erro.'))
  }, [recarga])

  useEffect(() => {
    let ativo = true
    listarChamados({ status, prioridade })
      .then((dados) => {
        if (!ativo) return
        setChamados(dados ?? [])
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
  }, [status, prioridade, recarga])

  function trocarFiltro<T>(set: (v: T) => void) {
    return (v: T) => {
      setCarregando(true)
      set(v)
    }
  }

  const selectCls =
    'rounded border border-line bg-white px-3 py-1.5 text-sm'

  return (
    <>
      <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold text-slate-800">Chamados</h1>
          <p className="text-xs text-slate-500">Inicio / Assistencia / Chamados</p>
        </div>
        {podeAbrir && (
          <button
            onClick={() => setNovo(true)}
            className="rounded bg-primary hover:bg-primary-dark text-white px-4 py-2 font-medium"
          >
            + Novo chamado
          </button>
        )}
      </div>

      <section className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-5">
        <Indicador titulo="Abertos" valor={resumo?.chamadosAbertos} cor="border-l-blue-500" />
        <Indicador titulo="Em atendimento" valor={resumo?.chamadosEmAtendimento} cor="border-l-amber-400" />
        <Indicador titulo="Fechados" valor={resumo?.chamadosFechados} cor="border-l-green-500" />
        <Indicador titulo="Criticos" valor={resumo?.chamadosPrioridadeCritica} cor="border-l-red-500" />
      </section>

      {erro && (
        <div
          role="alert"
          className="mb-4 rounded border border-red-200 bg-red-50 text-red-700 px-3 py-2"
        >
          {erro}
        </div>
      )}

      <Card
        titulo="Fila de chamados"
        acao={
          <div className="flex flex-wrap gap-2">
            <select
              aria-label="Filtrar por status"
              value={status}
              onChange={(e) =>
                trocarFiltro(setStatus)(e.target.value as StatusChamado | '')
              }
              className={selectCls}
            >
              <option value="">Todos os status</option>
              {STATUS.map((s) => (
                <option key={s}>{s}</option>
              ))}
            </select>
            <select
              aria-label="Filtrar por prioridade"
              value={prioridade}
              onChange={(e) =>
                trocarFiltro(setPrioridade)(e.target.value as Prioridade | '')
              }
              className={selectCls}
            >
              <option value="">Todas as prioridades</option>
              {PRIORIDADES.map((p) => (
                <option key={p}>{p}</option>
              ))}
            </select>
          </div>
        }
      >
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-surface text-xs uppercase tracking-wide text-slate-500">
              <tr>
                {['Protocolo','Titulo','Solicitante','Tecnico','Prioridade','Status','Aberto em'].map((h) => (
                  <th key={h} className="px-4 py-2.5 font-semibold">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {carregando ? (
                <tr><td colSpan={7} className="px-4 py-8 text-center text-slate-500">Carregando...</td></tr>
              ) : chamados.length === 0 ? (
                <tr><td colSpan={7} className="px-4 py-8 text-center text-slate-500">Nenhum chamado encontrado.</td></tr>
              ) : (
                chamados.map((c) => (
                  <tr
                    key={c.id}
                    onClick={() => setDetalheId(c.id)}
                    className="border-t border-line cursor-pointer hover:bg-surface"
                  >
                    <td className="px-4 py-3 font-mono text-primary">{c.protocolo}</td>
                    <td className="px-4 py-3 font-medium">{c.titulo}</td>
                    <td className="px-4 py-3">
                      {c.solicitanteNome}
                      <span className="block text-xs text-slate-400">{c.solicitanteSetor}</span>
                    </td>
                    <td className="px-4 py-3">{c.tecnicoNome ?? '-'}</td>
                    <td className="px-4 py-3">
                      <span className={`rounded px-2 py-0.5 text-xs font-semibold ${corPrioridade[c.prioridade]}`}>{c.prioridade}</span>
                    </td>
                    <td className="px-4 py-3">
                      <span className={`rounded px-2 py-0.5 text-xs font-semibold ${corStatus[c.status]}`}>{c.status}</span>
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap">{formatarData(c.dataAbertura)}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {novo && (
        <NovoChamadoModal onClose={() => setNovo(false)} onCriado={recarregar} />
      )}
      {detalheId !== null && (
        <DetalheChamadoModal
          id={detalheId}
          onClose={() => setDetalheId(null)}
          onAlterado={recarregar}
        />
      )}
    </>
  )
}
