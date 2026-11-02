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
import { LinhaEstado, Spinner } from '../components/Carregando'
import DetalheChamadoModal from '../components/DetalheChamadoModal'
import NovoChamadoModal from '../components/NovoChamadoModal'
import Paginacao from '../components/Paginacao'
import { usePaginacao } from '../components/usePaginacao'
import { rotuloPrioridade, rotuloStatus } from '../util/rotulos'

const STATUS: StatusChamado[] = ['ABERTO', 'ATENDIMENTO', 'FECHADO']
const PRIORIDADES: Prioridade[] = ['BAIXA', 'MEDIA', 'ALTA', 'CRITICA']
const COLUNAS = ['Protocolo', 'Título', 'Solicitante', 'Técnico', 'Prioridade', 'Status', 'Aberto em']

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
  falhou,
}: {
  titulo: string
  valor?: number
  cor: string
  falhou: boolean
}) {
  return (
    <div className={`rounded-md bg-white border border-line border-l-4 ${cor} shadow-sm p-4`}>
      <p className="text-xs uppercase tracking-wide text-slate-500">{titulo}</p>
      <p className="text-3xl font-bold text-slate-800">
        {valor ?? (falhou ? '-' : <Spinner className="size-6 text-slate-300" />)}
      </p>
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
  const pag = usePaginacao(chamados)

  useEffect(() => {
    resumoDashboard()
      .then(setResumo)
      .catch((e) => setErro(e instanceof ApiError ? e.message : 'Erro ao carregar os indicadores.'))
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
        if (ativo) setErro(e instanceof ApiError ? e.message : 'Erro ao carregar os chamados.')
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
      pag.reiniciar()
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
          <p className="text-xs text-slate-500">Início / Assistência / Chamados</p>
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
        <Indicador titulo="Abertos" valor={resumo?.chamadosAbertos} cor="border-l-blue-500" falhou={!!erro} />
        <Indicador titulo="Em atendimento" valor={resumo?.chamadosEmAtendimento} cor="border-l-amber-400" falhou={!!erro} />
        <Indicador titulo="Fechados" valor={resumo?.chamadosFechados} cor="border-l-green-500" falhou={!!erro} />
        <Indicador titulo="Críticos" valor={resumo?.chamadosPrioridadeCritica} cor="border-l-red-500" falhou={!!erro} />
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
                <option key={s} value={s}>{rotuloStatus[s]}</option>
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
                <option key={p} value={p}>{rotuloPrioridade[p]}</option>
              ))}
            </select>
          </div>
        }
      >
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-surface text-xs uppercase tracking-wide text-slate-500">
              <tr>
                {COLUNAS.map((h) => (
                  <th key={h} className="px-4 py-2.5 font-semibold">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {carregando || chamados.length === 0 ? (
                <LinhaEstado colunas={COLUNAS.length} carregando={carregando} vazio="Nenhum chamado encontrado." />
              ) : (
                pag.itensPagina.map((c) => (
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
                      <span className={`rounded px-2 py-0.5 text-xs font-semibold whitespace-nowrap ${corPrioridade[c.prioridade]}`}>{rotuloPrioridade[c.prioridade]}</span>
                    </td>
                    <td className="px-4 py-3">
                      <span className={`rounded px-2 py-0.5 text-xs font-semibold whitespace-nowrap ${corStatus[c.status]}`}>{rotuloStatus[c.status]}</span>
                    </td>
                    <td className="px-4 py-3 whitespace-nowrap">{formatarData(c.dataAbertura)}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        {!carregando && <Paginacao {...pag} />}
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
