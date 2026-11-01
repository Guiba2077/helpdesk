import { useEffect, useState, type FormEvent } from 'react'
import { criarChamado } from '../api/chamadosApi'
import { ApiError } from '../api/client'
import type { Categoria, Prioridade, UsuarioLogado } from '../api/types'
import { listarUsuarios } from '../api/usuariosApi'
import { useAuth } from '../auth/AuthContext'
import Alerta from './Alerta'
import { campo, Linha } from './form'
import Modal from './Modal'

const CATEGORIAS: { valor: Categoria; rotulo: string }[] = [
  { valor: 'REDE', rotulo: 'Infraestrutura e Rede' },
  { valor: 'HARDWARE', rotulo: 'Hardware e Equipamentos' },
  { valor: 'SOFTWARE', rotulo: 'Sistemas e Software' },
  { valor: 'BANCO_DADOS', rotulo: 'Banco de Dados' },
  { valor: 'ACESSO', rotulo: 'Seguranca e Permissoes de Acesso' },
]

const PRIORIDADES: { valor: Prioridade; rotulo: string; cor: string }[] = [
  { valor: 'BAIXA', rotulo: 'Baixa', cor: 'bg-slate-500' },
  { valor: 'MEDIA', rotulo: 'Media', cor: 'bg-sky-600' },
  { valor: 'ALTA', rotulo: 'Alta', cor: 'bg-orange-500' },
  { valor: 'CRITICA', rotulo: 'Critica', cor: 'bg-red-600' },
]

const MAX_DESCRICAO = 2000

export default function NovoChamadoModal({
  onClose,
  onCriado,
}: {
  onClose: () => void
  onCriado: () => void
}) {
  const { usuario } = useAuth()
  const ehAdmin = usuario?.perfil === 'ADMIN'

  const [usuarios, setUsuarios] = useState<UsuarioLogado[]>([])
  const [solicitanteId, setSolicitanteId] = useState<number | undefined>(
    usuario?.id,
  )
  const [titulo, setTitulo] = useState('')
  const [descricao, setDescricao] = useState('')
  const [categoria, setCategoria] = useState<Categoria>('REDE')
  const [prioridade, setPrioridade] = useState<Prioridade>('MEDIA')
  const [agora] = useState(() => new Date().toLocaleString('pt-BR'))
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    if (ehAdmin) listarUsuarios().then((u) => setUsuarios(u ?? [])).catch(() => {})
  }, [ehAdmin])

  async function enviar(e: FormEvent) {
    e.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      await criarChamado({
        titulo: titulo.trim(),
        descricao: descricao.trim(),
        categoria,
        prioridade,
        solicitanteId,
      })
      onCriado()
      onClose()
    } catch (err) {
      setErro(err instanceof ApiError ? err.message : 'Falha ao abrir chamado.')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <Modal
      titulo="Novo chamado"
      onClose={onClose}
      rodape={
        <>
          <button
            type="button"
            onClick={onClose}
            className="rounded border border-line bg-white px-4 py-2 text-sm text-slate-600 hover:bg-surface"
          >
            Cancelar
          </button>
          <button
            type="submit"
            form="form-novo-chamado"
            disabled={enviando}
            className="rounded bg-primary px-5 py-2 text-sm font-medium text-white hover:bg-primary-dark disabled:opacity-60"
          >
            {enviando ? 'Abrindo...' : 'Abrir chamado'}
          </button>
        </>
      }
    >
      <form id="form-novo-chamado" onSubmit={enviar} className="space-y-4">
        <Alerta texto={erro} />

        <Linha rotulo="Data de abertura">
          <input value={agora} disabled className={`${campo} bg-surface text-slate-500`} />
        </Linha>

        <Linha rotulo="Solicitante" obrigatorio>
          {ehAdmin ? (
            <select
              value={solicitanteId ?? ''}
              onChange={(e) => setSolicitanteId(Number(e.target.value))}
              className={campo}
            >
              {usuarios.length === 0 && usuario && (
                <option value={usuario.id}>{usuario.nome}</option>
              )}
              {usuarios.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.nome} ({u.setor})
                </option>
              ))}
            </select>
          ) : (
            <input
              value={`${usuario?.nome ?? ''} (${usuario?.setor ?? ''})`}
              disabled
              className={`${campo} bg-surface text-slate-500`}
            />
          )}
        </Linha>

        <Linha rotulo="Categoria" obrigatorio>
          <select
            value={categoria}
            onChange={(e) => setCategoria(e.target.value as Categoria)}
            className={campo}
          >
            {CATEGORIAS.map((c) => (
              <option key={c.valor} value={c.valor}>
                {c.rotulo}
              </option>
            ))}
          </select>
        </Linha>

        <Linha rotulo="Prioridade" obrigatorio>
          <div role="radiogroup" aria-label="Prioridade" className="flex flex-wrap gap-2">
            {PRIORIDADES.map((p) => (
              <button
                key={p.valor}
                type="button"
                role="radio"
                aria-checked={prioridade === p.valor}
                onClick={() => setPrioridade(p.valor)}
                className={`flex items-center gap-2 rounded border px-3 py-1.5 text-sm ${
                  prioridade === p.valor
                    ? 'border-primary bg-primary/10 font-semibold text-slate-800'
                    : 'border-line bg-white text-slate-600 hover:bg-surface'
                }`}
              >
                <span className={`size-2.5 rounded-full ${p.cor}`} />
                {p.rotulo}
              </button>
            ))}
          </div>
        </Linha>

        <Linha rotulo="Titulo" obrigatorio>
          <input
            required
            maxLength={150}
            value={titulo}
            onChange={(e) => setTitulo(e.target.value)}
            placeholder="Resumo do problema"
            className={campo}
          />
        </Linha>

        <Linha rotulo="Descricao" obrigatorio>
          <textarea
            required
            rows={6}
            maxLength={MAX_DESCRICAO}
            value={descricao}
            onChange={(e) => setDescricao(e.target.value)}
            placeholder="Descreva o que acontece, quando comecou e o impacto no trabalho"
            className={campo}
          />
          <p className="mt-1 text-right text-xs text-slate-400">
            {descricao.length}/{MAX_DESCRICAO}
          </p>
        </Linha>
      </form>
    </Modal>
  )
}
