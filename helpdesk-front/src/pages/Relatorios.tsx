import { useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import { metricasSla, relatorioPesado } from '../api/relatoriosApi'
import type { MetricaSla } from '../api/types'
import Card from '../components/Card'
import { LinhaEstado, Spinner } from '../components/Carregando'

type ResultadoTuning =
  | { tipo: 'timeout'; mensagem: string }
  | { tipo: 'ok'; mensagem: string }
  | { tipo: 'erro'; mensagem: string }

export default function Relatorios() {
  const [sla, setSla] = useState<MetricaSla[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [rodando, setRodando] = useState(false)
  const [tuning, setTuning] = useState<ResultadoTuning | null>(null)

  useEffect(() => {
    metricasSla()
      .then((m) => setSla(m ?? []))
      .catch((e) => setErro(e instanceof ApiError ? e.message : 'Erro ao carregar as métricas de SLA.'))
      .finally(() => setCarregando(false))
  }, [])

  async function simular() {
    setTuning(null)
    setRodando(true)
    try {
      const msg = await relatorioPesado(6)
      setTuning({ tipo: 'ok', mensagem: String(msg) })
    } catch (e) {
      if (e instanceof ApiError && e.status === 504)
        setTuning({ tipo: 'timeout', mensagem: e.message })
      else
        setTuning({ tipo: 'erro', mensagem: e instanceof ApiError ? e.message : 'Erro ao executar o relatório.' })
    } finally {
      setRodando(false)
    }
  }

  return (
    <>
      <div className="mb-5">
        <h1 className="text-xl font-semibold text-slate-800">Relatórios</h1>
        <p className="text-xs text-slate-500">Início / Relatórios / SLA e tuning</p>
      </div>

      {erro && (
        <div role="alert" className="mb-4 rounded border border-red-200 bg-red-50 text-red-700 px-3 py-2">{erro}</div>
      )}

      <div className="space-y-5">
        <Card titulo="SLA por técnico (function app.fn_metricas_sla_tecnico)">
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-surface text-xs uppercase tracking-wide text-slate-500">
                <tr>
                  {['Técnico', 'Especialidade', 'Atribuídos', 'Em atendimento', 'Encerrados', 'Tempo médio (h)'].map((h) => (
                    <th key={h} className="px-4 py-2.5 font-semibold">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {carregando || sla.length === 0 ? (
                  <LinhaEstado colunas={6} carregando={carregando} vazio="Nenhum técnico cadastrado." />
                ) : (
                  sla.map((m) => (
                    <tr key={m.tecnicoId} className="border-t border-line hover:bg-surface">
                      <td className="px-4 py-3 font-medium">{m.tecnicoNome}</td>
                      <td className="px-4 py-3">{m.especialidade}</td>
                      <td className="px-4 py-3">{m.totalChamadosAtribuidos}</td>
                      <td className="px-4 py-3">{m.chamadosEmAtendimento}</td>
                      <td className="px-4 py-3">{m.chamadosEncerrados}</td>
                      <td className="px-4 py-3">{Number(m.tempoMedioResolucaoHoras).toFixed(2)}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </Card>

        <Card titulo="Proteção de recursos (statement_timeout = 3s)">
          <div className="p-4 space-y-3">
            <p className="text-slate-600">
              Executa no banco uma consulta de 6 segundos. O PostgreSQL deve cancelar
              a consulta aos 3 segundos e a API responde HTTP 504.
            </p>
            <button
              onClick={simular}
              disabled={rodando}
              className="inline-flex items-center gap-2 rounded bg-primary hover:bg-primary-dark disabled:opacity-60 text-white px-4 py-2 font-medium"
            >
              {rodando && <Spinner />}
              {rodando ? 'Executando consulta pesada...' : 'Executar relatório pesado'}
            </button>
            {tuning?.tipo === 'timeout' && (
              <div role="alert" className="rounded border border-red-300 bg-red-50 text-red-800 p-3">
                <p className="font-semibold">Consulta cancelada. A proteção do PostgreSQL funcionou.</p>
                <p className="text-sm">{tuning.mensagem}</p>
              </div>
            )}
            {tuning?.tipo === 'ok' && (
              <div className="rounded border border-amber-300 bg-amber-50 text-amber-800 p-3">
                A consulta terminou sem cancelamento. Verifique o statement_timeout do banco.
              </div>
            )}
            {tuning?.tipo === 'erro' && (
              <div role="alert" className="rounded border border-red-200 bg-red-50 text-red-700 p-3">{tuning.mensagem}</div>
            )}
          </div>
        </Card>
      </div>
    </>
  )
}
