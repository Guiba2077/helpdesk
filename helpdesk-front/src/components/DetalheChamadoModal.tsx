import { useCallback, useEffect, useState } from 'react'
import {
  alocarTecnico,
  alterarPrioridade,
  buscarChamado,
  criarApontamento,
  fecharChamado,
  inativarChamado,
  historicoChamado,
  listarApontamentos,
} from '../api/chamadosApi'
import { ApiError } from '../api/client'
import { listarTecnicos } from '../api/tecnicosApi'
import type {
  Apontamento,
  Chamado,
  HistoricoItem,
  Prioridade,
  Tecnico,
} from '../api/types'
import { useAuth } from '../auth/AuthContext'
import Alerta from './Alerta'
import EditarChamadoModal from './EditarChamadoModal'
import Modal from './Modal'

const PRIORIDADES: Prioridade[] = ['BAIXA', 'MEDIA', 'ALTA', 'CRITICA']
const data = (iso: string | null) =>
  iso ? new Date(iso).toLocaleString('pt-BR') : '-'
const campo =
  'rounded border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary'
const botao =
  'rounded bg-primary hover:bg-primary-dark disabled:opacity-60 text-white text-sm font-medium px-4 py-2'

export default function DetalheChamadoModal({
  id,
  onClose,
  onAlterado,
}: {
  id: number
  onClose: () => void
  onAlterado: () => void
}) {
  const { usuario } = useAuth()
  const perfil = usuario?.perfil
  const ehAdmin = perfil === 'ADMIN'
  const ehTecnico = perfil === 'TECNICO' || ehAdmin

  const [chamado, setChamado] = useState<Chamado>()
  const [historico, setHistorico] = useState<HistoricoItem[]>([])
  const [apontamentos, setApontamentos] = useState<Apontamento[]>([])
  const [tecnicos, setTecnicos] = useState<Tecnico[]>([])
  const [erro, setErro] = useState('')
  const [ocupado, setOcupado] = useState(false)
  const [editando, setEditando] = useState(false)

  const [tecnicoId, setTecnicoId] = useState('')
  const [novaPrioridade, setNovaPrioridade] = useState<Prioridade>('MEDIA')
  const [justificativa, setJustificativa] = useState('')
  const [mensagem, setMensagem] = useState('')
  const [solucao, setSolucao] = useState('')

  const carregar = useCallback(async () => {
    try {
      const [c, h, a] = await Promise.all([
        buscarChamado(id),
        historicoChamado(id),
        listarApontamentos(id),
      ])
      setChamado(c)
      setNovaPrioridade(c.prioridade)
      setHistorico(h ?? [])
      setApontamentos(a ?? [])
    } catch (e) {
      setErro(e instanceof ApiError ? e.message : 'Erro ao carregar.')
    }
  }, [id])

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    carregar()
  }, [carregar])

  useEffect(() => {
    if (ehAdmin) listarTecnicos().then((t) => setTecnicos(t ?? [])).catch(() => {})
  }, [ehAdmin])

  async function acao(fn: () => Promise<unknown>, depois?: () => void) {
    setErro('')
    setOcupado(true)
    try {
      await fn()
      depois?.()
      await carregar()
      onAlterado()
    } catch (e) {
      setErro(e instanceof ApiError ? e.message : 'Falha na operacao.')
    } finally {
      setOcupado(false)
    }
  }

  const fechado = chamado?.status === 'FECHADO'
  const emAtendimento = chamado?.status === 'ATENDIMENTO'
  const podeEditar =
    !!chamado && !fechado && (ehAdmin || chamado.solicitanteId === usuario?.id)
  const podeAtender =
    !!chamado && !fechado && emAtendimento && ehTecnico

  async function inativar() {
    if (!window.confirm('Inativar este chamado? Ele sai da fila.')) return
    setErro('')
    setOcupado(true)
    try {
      await inativarChamado(id)
      onAlterado()
      onClose()
    } catch (e) {
      setErro(e instanceof ApiError ? e.message : 'Falha na operacao.')
      setOcupado(false)
    }
  }

  return (
    <Modal
      titulo={chamado ? `${chamado.protocolo} - ${chamado.titulo}` : 'Chamado'}
      onClose={onClose}
      largo
    >
      <Alerta texto={erro} />
      {!chamado ? (
        !erro && <p className="text-slate-500">Carregando...</p>
      ) : (
        <div className="space-y-6 text-sm">
          <section className="grid grid-cols-2 gap-2 text-slate-700">
            <p>Status: <b>{chamado.status}</b></p>
            <p>Prioridade: <b>{chamado.prioridade}</b></p>
            <p>Categoria: <b>{chamado.categoria}</b></p>
            <p>Tecnico: <b>{chamado.tecnicoNome ?? '-'}</b></p>
            <p>Solicitante: <b>{chamado.solicitanteNome}</b> ({chamado.solicitanteSetor})</p>
            <p>Aberto em: <b>{data(chamado.dataAbertura)}</b></p>
            <p className="col-span-2 text-slate-600">{chamado.descricao}</p>
          </section>

          {(podeEditar || ehAdmin) && (
            <section className="flex flex-wrap gap-2">
              {podeEditar && (
                <button
                  onClick={() => setEditando(true)}
                  className="rounded border border-line bg-white px-3 py-1.5 text-sm text-slate-700 hover:bg-surface"
                >
                  Editar chamado
                </button>
              )}
              {ehAdmin && (
                <button
                  disabled={ocupado}
                  onClick={inativar}
                  className="rounded border border-red-200 bg-white px-3 py-1.5 text-sm text-red-700 hover:bg-red-50 disabled:opacity-60"
                >
                  Inativar chamado
                </button>
              )}
            </section>
          )}

          {ehAdmin && !fechado && (
            <section className="space-y-3 rounded bg-slate-50 p-3">
              <div className="flex flex-wrap gap-2">
                <select
                  aria-label="Tecnico"
                  value={tecnicoId}
                  onChange={(e) => setTecnicoId(e.target.value)}
                  className={campo}
                >
                  <option value="">Selecione o tecnico</option>
                  {tecnicos.map((t) => (
                    <option key={t.id} value={t.id}>
                      {t.nome}
                      {t.disponivel ? '' : ' (indisponivel)'}
                    </option>
                  ))}
                </select>
                <button
                  disabled={ocupado || !tecnicoId}
                  onClick={() => acao(() => alocarTecnico(id, Number(tecnicoId)))}
                  className={botao}
                >
                  Alocar
                </button>
              </div>
              <div className="flex flex-wrap gap-2">
                <select
                  aria-label="Nova prioridade"
                  value={novaPrioridade}
                  onChange={(e) => setNovaPrioridade(e.target.value as Prioridade)}
                  className={campo}
                >
                  {PRIORIDADES.map((p) => (
                    <option key={p}>{p}</option>
                  ))}
                </select>
                <input
                  placeholder="Justificativa"
                  value={justificativa}
                  onChange={(e) => setJustificativa(e.target.value)}
                  className={`${campo} flex-1 min-w-40`}
                />
                <button
                  disabled={ocupado || !justificativa.trim()}
                  onClick={() =>
                    acao(
                      () => alterarPrioridade(id, novaPrioridade, justificativa),
                      () => setJustificativa(''),
                    )
                  }
                  className={botao}
                >
                  Alterar prioridade
                </button>
              </div>
            </section>
          )}

          <section>
            <h3 className="mb-2 font-bold text-slate-800">Historico de status</h3>
            <ul className="space-y-1 text-slate-600">
              {historico.map((h) => (
                <li key={h.id}>
                  {data(h.dataMudanca)}: {h.statusAnterior ?? 'inicio'} para{' '}
                  <b>{h.statusNovo}</b>
                </li>
              ))}
              {historico.length === 0 && <li>Sem registros.</li>}
            </ul>
          </section>

          <section>
            <h3 className="mb-2 font-bold text-slate-800">Apontamentos</h3>
            <ul className="space-y-2">
              {apontamentos.map((a) => (
                <li key={a.id} className="rounded bg-slate-50 p-2">
                  <p className="text-xs text-slate-400">
                    {a.autorNome ?? 'Tecnico'} em {data(a.criadoEm)}
                  </p>
                  <p>{a.descricao}</p>
                </li>
              ))}
              {apontamentos.length === 0 && (
                <li className="text-slate-500">Nenhum apontamento.</li>
              )}
            </ul>
            {podeAtender && (
              <div className="mt-2 flex gap-2">
                <input
                  placeholder="Nova nota tecnica"
                  value={mensagem}
                  onChange={(e) => setMensagem(e.target.value)}
                  className={`${campo} flex-1`}
                />
                <button
                  disabled={ocupado || !mensagem.trim()}
                  onClick={() =>
                    acao(() => criarApontamento(id, mensagem), () => setMensagem(''))
                  }
                  className={botao}
                >
                  Registrar
                </button>
              </div>
            )}
          </section>

          {chamado.status === 'ABERTO' && (
            <div className="rounded-md bg-blue-50 border border-blue-200 p-3 text-blue-700 text-xs">
              ℹ️ Este chamado está <b>ABERTO</b> e aguarda alocação de um técnico pelo Administrador de TI para iniciar o atendimento.
            </div>
          )}

          {podeAtender && (
            <section className="space-y-2">
              <h3 className="font-bold text-slate-800">Encerrar chamado</h3>
              <textarea
                rows={2}
                placeholder="Solucao aplicada"
                value={solucao}
                onChange={(e) => setSolucao(e.target.value)}
                className={`${campo} w-full`}
              />
              <button
                disabled={ocupado || !solucao.trim()}
                onClick={() => acao(() => fecharChamado(id, solucao))}
                className={botao}
              >
                Fechar chamado
              </button>
            </section>
          )}
        </div>
      )}
      {editando && chamado && (
        <EditarChamadoModal
          chamado={chamado}
          onClose={() => setEditando(false)}
          onSalvo={() => {
            carregar()
            onAlterado()
          }}
        />
      )}
    </Modal>
  )
}
