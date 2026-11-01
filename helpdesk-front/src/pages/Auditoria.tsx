import { useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import { listarAuditoriaPrioridades } from '../api/relatoriosApi'
import type { AuditoriaPrioridade, Prioridade } from '../api/types'
import Card from '../components/Card'
import { formatarData } from '../util/formatar'

const cor: Record<Prioridade, string> = {
  BAIXA: 'bg-slate-100 text-slate-700',
  MEDIA: 'bg-sky-100 text-sky-800',
  ALTA: 'bg-orange-100 text-orange-800',
  CRITICA: 'bg-red-100 text-red-800',
}

export default function Auditoria() {
  const [lista, setLista] = useState<AuditoriaPrioridade[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    listarAuditoriaPrioridades()
      .then((l) => setLista(l ?? []))
      .catch((e) => setErro(e instanceof ApiError ? e.message : 'Erro.'))
      .finally(() => setCarregando(false))
  }, [])

  return (
    <>
      <div className="mb-5">
        <h1 className="text-xl font-semibold text-slate-800">Auditoria de prioridades</h1>
        <p className="text-xs text-slate-500">Inicio / Relatorios / Auditoria</p>
      </div>
      {erro && (
        <div role="alert" className="mb-4 rounded border border-red-200 bg-red-50 text-red-700 px-3 py-2">{erro}</div>
      )}
      <Card titulo="Trilha gravada pela trigger no schema audit">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-surface text-xs uppercase tracking-wide text-slate-500">
              <tr>
                {['Data', 'Chamado', 'De', 'Para', 'Alterado por', 'Justificativa'].map((h) => (
                  <th key={h} className="px-4 py-2.5 font-semibold">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {carregando ? (
                <tr><td colSpan={6} className="px-4 py-8 text-center text-slate-500">Carregando...</td></tr>
              ) : lista.length === 0 ? (
                <tr><td colSpan={6} className="px-4 py-8 text-center text-slate-500">Nenhuma alteracao de prioridade registrada.</td></tr>
              ) : (
                lista.map((a) => (
                  <tr key={a.id} className="border-t border-line hover:bg-surface">
                    <td className="px-4 py-3 whitespace-nowrap">{formatarData(a.dataAlteracao)}</td>
                    <td className="px-4 py-3 font-mono text-primary">{a.protocoloChamado}</td>
                    <td className="px-4 py-3"><span className={`rounded px-2 py-0.5 text-xs font-semibold ${cor[a.prioridadeAnterior]}`}>{a.prioridadeAnterior}</span></td>
                    <td className="px-4 py-3"><span className={`rounded px-2 py-0.5 text-xs font-semibold ${cor[a.prioridadeNova]}`}>{a.prioridadeNova}</span></td>
                    <td className="px-4 py-3">{a.alteradoPorNome ?? '-'}</td>
                    <td className="px-4 py-3 text-slate-600">{a.justificativa ?? '-'}</td>
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
