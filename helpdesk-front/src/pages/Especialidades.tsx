import { useCallback, useEffect, useState, type FormEvent } from 'react'
import { ApiError } from '../api/client'
import { criarEspecialidade, listarEspecialidades } from '../api/tecnicosApi'
import type { Especialidade, NivelAtendimento } from '../api/types'
import Alerta from '../components/Alerta'
import Card from '../components/Card'
import { botaoPrimario, botaoSecundario, campo, Linha } from '../components/form'
import Modal from '../components/Modal'

const NIVEIS: NivelAtendimento[] = ['N1', 'N2', 'N3']

function NovaEspecialidadeModal({ onClose, onCriada }: { onClose: () => void; onCriada: () => void }) {
  const [codigo, setCodigo] = useState('')
  const [nome, setNome] = useState('')
  const [nivel, setNivel] = useState<NivelAtendimento>('N1')
  const [descricao, setDescricao] = useState('')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  async function enviar(e: FormEvent) {
    e.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      await criarEspecialidade({
        codigo: codigo.trim().toUpperCase().replace(/\s+/g, '_'),
        nome: nome.trim(),
        nivelPadrao: nivel,
        descricao: descricao.trim() || undefined,
      })
      onCriada()
      onClose()
    } catch (err) {
      setErro(err instanceof ApiError ? err.message : 'Falha ao salvar.')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <Modal
      titulo="Nova especialidade"
      onClose={onClose}
      rodape={
        <>
          <button type="button" onClick={onClose} className={botaoSecundario}>Cancelar</button>
          <button type="submit" form="form-especialidade" disabled={enviando} className={botaoPrimario}>
            {enviando ? 'Salvando...' : 'Salvar'}
          </button>
        </>
      }
    >
      <form id="form-especialidade" onSubmit={enviar} className="space-y-4">
        <Alerta texto={erro} />
        <Linha rotulo="Codigo" obrigatorio>
          <input required value={codigo} onChange={(e) => setCodigo(e.target.value)} placeholder="CLOUD_DEVOPS" className={campo} />
        </Linha>
        <Linha rotulo="Nome" obrigatorio>
          <input required value={nome} onChange={(e) => setNome(e.target.value)} className={campo} />
        </Linha>
        <Linha rotulo="Nivel padrao" obrigatorio>
          <div className="flex gap-2">
            {NIVEIS.map((n) => (
              <button
                key={n}
                type="button"
                onClick={() => setNivel(n)}
                className={`rounded border px-4 py-1.5 text-sm ${nivel === n ? 'border-primary bg-primary/10 font-semibold' : 'border-line bg-white text-slate-600 hover:bg-surface'}`}
              >
                {n}
              </button>
            ))}
          </div>
        </Linha>
        <Linha rotulo="Descricao">
          <textarea rows={3} value={descricao} onChange={(e) => setDescricao(e.target.value)} className={campo} />
        </Linha>
      </form>
    </Modal>
  )
}

export default function Especialidades() {
  const [lista, setLista] = useState<Especialidade[]>([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [novo, setNovo] = useState(false)
  const [recarga, setRecarga] = useState(0)
  const recarregar = useCallback(() => setRecarga((n) => n + 1), [])

  useEffect(() => {
    let ativo = true
    listarEspecialidades()
      .then((e) => {
        if (!ativo) return
        setLista(e ?? [])
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

  return (
    <>
      <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold text-slate-800">Especialidades</h1>
          <p className="text-xs text-slate-500">Inicio / Administracao / Especialidades</p>
        </div>
        <button onClick={() => setNovo(true)} className="rounded bg-primary hover:bg-primary-dark text-white px-4 py-2 font-medium">
          + Nova especialidade
        </button>
      </div>

      {erro && (
        <div role="alert" className="mb-4 rounded border border-red-200 bg-red-50 text-red-700 px-3 py-2">{erro}</div>
      )}

      <Card titulo="Areas de atuacao tecnica">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-surface text-xs uppercase tracking-wide text-slate-500">
              <tr>
                {['Codigo', 'Nome', 'Nivel padrao'].map((h) => (
                  <th key={h} className="px-4 py-2.5 font-semibold">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {carregando ? (
                <tr><td colSpan={3} className="px-4 py-8 text-center text-slate-500">Carregando...</td></tr>
              ) : lista.length === 0 ? (
                <tr><td colSpan={3} className="px-4 py-8 text-center text-slate-500">Nenhuma especialidade.</td></tr>
              ) : (
                lista.map((e) => (
                  <tr key={e.codigo} className="border-t border-line hover:bg-surface">
                    <td className="px-4 py-3 font-mono text-primary">{e.codigo}</td>
                    <td className="px-4 py-3 font-medium">{e.nome}</td>
                    <td className="px-4 py-3">{e.nivelAtendimento}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {novo && <NovaEspecialidadeModal onClose={() => setNovo(false)} onCriada={recarregar} />}
    </>
  )
}
