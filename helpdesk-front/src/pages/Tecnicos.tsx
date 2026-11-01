import { useCallback, useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import { alternarDisponibilidade, listarTecnicos } from '../api/tecnicosApi'
import type { Tecnico } from '../api/types'
import Card from '../components/Card'
import NovoTecnicoModal from '../components/NovoTecnicoModal'

export default function Tecnicos() {
  const [tecnicos, setTecnicos] = useState<Tecnico[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
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
        if (ativo) setErro(e instanceof ApiError ? e.message : 'Erro.')
      })
      .finally(() => {
        if (ativo) setCarregando(false)
      })
    return () => {
      ativo = false
    }
  }, [recarga])

  async function alternar(id: number) {
    try {
      await alternarDisponibilidade(id)
      recarregar()
    } catch (e) {
      setErro(e instanceof ApiError ? e.message : 'Falha na operacao.')
    }
  }

  return (
    <>
      <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold text-slate-800">Tecnicos</h1>
          <p className="text-xs text-slate-500">Inicio / Administracao / Tecnicos</p>
        </div>
        <button
          onClick={() => setNovo(true)}
          className="rounded bg-primary hover:bg-primary-dark text-white px-4 py-2 font-medium"
        >
          + Novo tecnico
        </button>
      </div>

      {erro && (
        <div role="alert" className="mb-4 rounded border border-red-200 bg-red-50 text-red-700 px-3 py-2">
          {erro}
        </div>
      )}

      <Card titulo="Equipe tecnica">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-surface text-xs uppercase tracking-wide text-slate-500">
              <tr>
                {['Matricula', 'Nome', 'Especialidade', 'Nivel', 'Setor', 'Disponibilidade'].map((h) => (
                  <th key={h} className="px-4 py-2.5 font-semibold">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {carregando ? (
                <tr><td colSpan={6} className="px-4 py-8 text-center text-slate-500">Carregando...</td></tr>
              ) : tecnicos.length === 0 ? (
                <tr><td colSpan={6} className="px-4 py-8 text-center text-slate-500">Nenhum tecnico cadastrado.</td></tr>
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
                        title="Alternar disponibilidade"
                        className={`rounded px-2.5 py-1 text-xs font-semibold ${
                          t.disponivel
                            ? 'bg-green-100 text-green-800 hover:bg-green-200'
                            : 'bg-slate-200 text-slate-600 hover:bg-slate-300'
                        }`}
                      >
                        {t.disponivel ? 'Disponivel' : 'Indisponivel'}
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
