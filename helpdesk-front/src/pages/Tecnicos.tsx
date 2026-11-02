import { useCallback, useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import { alternarDisponibilidade, listarTecnicos } from '../api/tecnicosApi'
import type { Tecnico } from '../api/types'
import Card from '../components/Card'
import { LinhaEstado } from '../components/Carregando'
import NovoTecnicoModal from '../components/NovoTecnicoModal'

const COLUNAS = ['Matrícula', 'Nome', 'Especialidade', 'Nível', 'Setor', 'Disponibilidade']

export default function Tecnicos() {
  const [tecnicos, setTecnicos] = useState<Tecnico[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [alternandoId, setAlternandoId] = useState<number | null>(null)
  const [novo, setNovo] = useState(false)
  const [recarga, setRecarga] = useState(0)
  const recarregar = useCallback(() => setRecarga((n) => n + 1), [])

  useEffect(() => {
    let ativo = true
    listarTecnicos()
      .then((t) => {
        if (!ativo) return
        setTecnicos(t ?? [])
        setErro('')
      })
      .catch((e) => {
        if (ativo) setErro(e instanceof ApiError ? e.message : 'Erro ao carregar os técnicos.')
      })
      .finally(() => {
        if (ativo) setCarregando(false)
      })
    return () => {
      ativo = false
    }
  }, [recarga])

  async function alternar(id: number) {
    setAlternandoId(id)
    try {
      await alternarDisponibilidade(id)
      recarregar()
    } catch (e) {
      setErro(e instanceof ApiError ? e.message : 'Falha na operação.')
    } finally {
      setAlternandoId(null)
    }
  }

  return (
    <>
      <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold text-slate-800">Técnicos</h1>
          <p className="text-xs text-slate-500">Início / Administração / Técnicos</p>
        </div>
        <button
          onClick={() => setNovo(true)}
          className="rounded bg-primary hover:bg-primary-dark text-white px-4 py-2 font-medium"
        >
          + Novo técnico
        </button>
      </div>

      {erro && (
        <div role="alert" className="mb-4 rounded border border-red-200 bg-red-50 text-red-700 px-3 py-2">
          {erro}
        </div>
      )}

      <Card titulo="Equipe técnica">
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
              {carregando || tecnicos.length === 0 ? (
                <LinhaEstado colunas={COLUNAS.length} carregando={carregando} vazio="Nenhum técnico cadastrado." />
              ) : (
                tecnicos.map((t) => (
                  <tr key={t.id} className="border-t border-line hover:bg-surface">
                    <td className="px-4 py-3 font-mono text-primary">{t.matricula}</td>
                    <td className="px-4 py-3 font-medium">
                      {t.nome}
                      <span className="block text-xs font-normal text-slate-400">{t.email}</span>
                    </td>
                    <td className="px-4 py-3">{t.especialidade}</td>
                    <td className="px-4 py-3">{t.nivelAtendimento}</td>
                    <td className="px-4 py-3">{t.setor ?? '-'}</td>
                    <td className="px-4 py-3">
                      <button
                        onClick={() => alternar(t.id)}
                        disabled={alternandoId !== null}
                        title="Alternar disponibilidade"
                        className={`rounded px-2.5 py-1 text-xs font-semibold disabled:opacity-60 ${
                          t.disponivel
                            ? 'bg-green-100 text-green-800 hover:bg-green-200'
                            : 'bg-slate-200 text-slate-600 hover:bg-slate-300'
                        }`}
                      >
                        {alternandoId === t.id ? 'Salvando...' : t.disponivel ? 'Disponível' : 'Indisponível'}
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {novo && (
        <NovoTecnicoModal
          tecnicos={tecnicos}
          onClose={() => setNovo(false)}
          onCriado={recarregar}
        />
      )}
    </>
  )
}
