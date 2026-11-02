import { useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import { listarAuditoriaPrioridades } from '../api/relatoriosApi'
import type { AuditoriaPrioridade, Prioridade } from '../api/types'
import Card from '../components/Card'
import { LinhaEstado } from '../components/Carregando'
import Paginacao from '../components/Paginacao'
import { usePaginacao } from '../components/usePaginacao'
import { formatarData } from '../util/formatar'
import { rotuloPrioridade } from '../util/rotulos'

const COLUNAS = ['Data', 'Chamado', 'De', 'Para', 'Alterado por', 'Justificativa']

const cor: Record<Prioridade, string> = {
  BAIXA: 'bg-slate-100 text-slate-700',
  MEDIA: 'bg-sky-100 text-sky-800',
  ALTA: 'bg-orange-100 text-orange-800',
  CRITICA: 'bg-red-100 text-red-800',
}

function Selo({ prioridade }: { prioridade: Prioridade }) {
  return (
    <span className={`rounded px-2 py-0.5 text-xs font-semibold whitespace-nowrap ${cor[prioridade]}`}>
      {rotuloPrioridade[prioridade]}
    </span>
  )
}

export default function Auditoria() {
  const [lista, setLista] = useState<AuditoriaPrioridade[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const pag = usePaginacao(lista)

  useEffect(() => {
    listarAuditoriaPrioridades()
      .then((l) => setLista(l ?? []))
      .catch((e) => setErro(e instanceof ApiError ? e.message : 'Erro ao carregar a auditoria.'))
      .finally(() => setCarregando(false))
  }, [])

  return (
    <>
      <div className="mb-5">
        <h1 className="text-xl font-semibold text-slate-800">Auditoria de prioridades</h1>
        <p className="text-xs text-slate-500">Início / Relatórios / Auditoria</p>
      </div>
      {erro && (
        <div role="alert" className="mb-4 rounded border border-red-200 bg-red-50 text-red-700 px-3 py-2">{erro}</div>
      )}
      <Card titulo="Trilha gravada pela trigger no schema audit">
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
              {carregando || lista.length === 0 ? (
                <LinhaEstado colunas={COLUNAS.length} carregando={carregando} vazio="Nenhuma alteração de prioridade registrada." />
              ) : (
                pag.itensPagina.map((a) => (
                  <tr key={a.id} className="border-t border-line hover:bg-surface">
                    <td className="px-4 py-3 whitespace-nowrap">{formatarData(a.dataAlteracao)}</td>
                    <td className="px-4 py-3 font-mono text-primary">{a.protocoloChamado}</td>
                    <td className="px-4 py-3"><Selo prioridade={a.prioridadeAnterior} /></td>
                    <td className="px-4 py-3"><Selo prioridade={a.prioridadeNova} /></td>
                    <td className="px-4 py-3">{a.alteradoPorNome ?? '-'}</td>
                    <td className="px-4 py-3 text-slate-600">{a.justificativa ?? '-'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        {!carregando && <Paginacao {...pag} />}
      </Card>
    </>
  )
}
