import { useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import { metricasSla } from '../api/relatoriosApi'
import type { MetricaSla } from '../api/types'
import Card from '../components/Card'
import { LinhaEstado } from '../components/Carregando'

export default function Relatorios() {
  const [sla, setSla] = useState<MetricaSla[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    metricasSla()
      .then((m) => setSla(m ?? []))
      .catch((e) => setErro(e instanceof ApiError ? e.message : 'Erro ao carregar as métricas de SLA.'))
      .finally(() => setCarregando(false))
  }, [])

  return (
    <>
      <div className="mb-5">
        <h1 className="text-xl font-semibold text-slate-800">Relatórios</h1>
        <p className="text-xs text-slate-500">Início / Relatórios / SLA</p>
      </div>

      {erro && (
        <div role="alert" className="mb-4 rounded border border-red-200 bg-red-50 text-red-700 px-3 py-2">{erro}</div>
      )}

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
    </>
  )
}
