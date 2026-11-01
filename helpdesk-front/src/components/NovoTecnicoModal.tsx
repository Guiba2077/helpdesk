import { useEffect, useState, type FormEvent } from 'react'
import { ApiError } from '../api/client'
import { criarTecnico, listarEspecialidades } from '../api/tecnicosApi'
import type {
  Especialidade,
  NivelAtendimento,
  Tecnico,
  UsuarioLogado,
} from '../api/types'
import { criarUsuario, listarUsuarios } from '../api/usuariosApi'
import Alerta from './Alerta'
import { campo, Linha } from './form'
import Modal from './Modal'

const NIVEIS: NivelAtendimento[] = ['N1', 'N2', 'N3']

type Modo = 'novo' | 'existente'

export default function NovoTecnicoModal({
  tecnicos,
  onClose,
  onCriado,
}: {
  tecnicos: Tecnico[]
  onClose: () => void
  onCriado: () => void
}) {
  const [modo, setModo] = useState<Modo>('novo')
  const [especialidades, setEspecialidades] = useState<Especialidade[]>([])
  const [usuarios, setUsuarios] = useState<UsuarioLogado[]>([])

  const proxima =
    tecnicos.reduce((max, t) => {
      const n = Number(/(\d+)$/.exec(t.matricula)?.[1] ?? 0)
      return Math.max(max, n)
    }, 0) + 1

  // usuario novo
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [telefone, setTelefone] = useState('')
  const [setor, setSetor] = useState('Suporte de TI')
  // usuario existente
  const [usuarioId, setUsuarioId] = useState('')
  // dados tecnicos
  const [matricula, setMatricula] = useState(
    `TEC-${String(proxima).padStart(3, '0')}`,
  )
  const [especialidade, setEspecialidade] = useState('')
  const [nivel, setNivel] = useState<NivelAtendimento>('N1')
  const [disponivel, setDisponivel] = useState(true)

  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    listarEspecialidades()
      .then((e) => {
        setEspecialidades(e ?? [])
        if (e?.length) {
          setEspecialidade(e[0].codigo)
          setNivel(e[0].nivelAtendimento as NivelAtendimento)
        }
      })
      .catch(() => {})
    listarUsuarios()
      .then((u) => setUsuarios(u ?? []))
      .catch(() => {})
  }, [])

  const jaTecnico = new Set(tecnicos.map((t) => t.usuarioId))
  const elegiveis = usuarios.filter(
    (u) => u.perfil === 'TECNICO' && !jaTecnico.has(u.id),
  )

  function trocarEspecialidade(codigo: string) {
    setEspecialidade(codigo)
    const e = especialidades.find((x) => x.codigo === codigo)
    if (e && NIVEIS.includes(e.nivelAtendimento as NivelAtendimento))
      setNivel(e.nivelAtendimento as NivelAtendimento)
  }

  async function enviar(e: FormEvent) {
    e.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      let idUsuario: number
      if (modo === 'novo') {
        const u = await criarUsuario({
          nome: nome.trim(),
          email: email.trim(),
          senha,
          telefone: telefone.trim() || undefined,
          setor: setor.trim(),
          perfil: 'TECNICO',
        })
        idUsuario = u.id
      } else {
        idUsuario = Number(usuarioId)
      }
      await criarTecnico({
        usuarioId: idUsuario,
        matricula: matricula.trim(),
        especialidade,
        nivelAtendimento: nivel,
        disponivel,
      })
      onCriado()
      onClose()
    } catch (err) {
      setErro(err instanceof ApiError ? err.message : 'Falha ao cadastrar tecnico.')
    } finally {
      setEnviando(false)
    }
  }

  const aba = (m: Modo, rotulo: string) => (
    <button
      type="button"
      onClick={() => setModo(m)}
      className={`px-4 py-2 text-sm border-b-2 ${
        modo === m
          ? 'border-primary font-semibold text-primary'
          : 'border-transparent text-slate-500 hover:text-slate-800'
      }`}
    >
      {rotulo}
    </button>
  )

  return (
    <Modal
      titulo="Novo tecnico"
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
            form="form-novo-tecnico"
            disabled={enviando || (modo === 'existente' && !usuarioId)}
            className="rounded bg-primary px-5 py-2 text-sm font-medium text-white hover:bg-primary-dark disabled:opacity-60"
          >
            {enviando ? 'Salvando...' : 'Cadastrar tecnico'}
          </button>
        </>
      }
    >
      <div className="-mt-1 mb-4 flex border-b border-line">
        {aba('novo', 'Novo usuario')}
        {aba('existente', 'Usuario existente')}
      </div>

      <form id="form-novo-tecnico" onSubmit={enviar} className="space-y-4">
        <Alerta texto={erro} />

        {modo === 'novo' ? (
          <>
            <Linha rotulo="Nome" obrigatorio>
              <input required value={nome} onChange={(e) => setNome(e.target.value)} className={campo} />
            </Linha>
            <Linha rotulo="E-mail" obrigatorio>
              <input required type="email" value={email} onChange={(e) => setEmail(e.target.value)} className={campo} />
            </Linha>
            <Linha rotulo="Senha inicial" obrigatorio>
              <input required type="password" minLength={6} value={senha} onChange={(e) => setSenha(e.target.value)} className={campo} />
            </Linha>
            <Linha rotulo="Telefone">
              <input value={telefone} onChange={(e) => setTelefone(e.target.value)} placeholder="(62) 98888-0000" className={campo} />
            </Linha>
            <Linha rotulo="Setor" obrigatorio>
              <input required value={setor} onChange={(e) => setSetor(e.target.value)} className={campo} />
            </Linha>
          </>
        ) : (
          <Linha rotulo="Usuario" obrigatorio>
            <select
              required
              value={usuarioId}
              onChange={(e) => setUsuarioId(e.target.value)}
              className={campo}
            >
              <option value="">Selecione um usuario com perfil TECNICO</option>
              {elegiveis.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.nome} ({u.email})
                </option>
              ))}
            </select>
            {elegiveis.length === 0 && (
              <p className="mt-1 text-xs text-slate-500">
                Nenhum usuario TECNICO sem cadastro tecnico.
              </p>
            )}
          </Linha>
        )}

        <hr className="border-line" />

        <Linha rotulo="Matricula" obrigatorio>
          <input required value={matricula} onChange={(e) => setMatricula(e.target.value)} className={campo} />
        </Linha>
        <Linha rotulo="Especialidade" obrigatorio>
          <select required value={especialidade} onChange={(e) => trocarEspecialidade(e.target.value)} className={campo}>
            {especialidades.map((e) => (
              <option key={e.codigo} value={e.codigo}>
                {e.nome}
              </option>
            ))}
          </select>
        </Linha>
        <Linha rotulo="Nivel" obrigatorio>
          <div className="flex gap-2">
            {NIVEIS.map((n) => (
              <button
                key={n}
                type="button"
                onClick={() => setNivel(n)}
                className={`rounded border px-4 py-1.5 text-sm ${
                  nivel === n
                    ? 'border-primary bg-primary/10 font-semibold'
                    : 'border-line bg-white text-slate-600 hover:bg-surface'
                }`}
              >
                {n}
              </button>
            ))}
          </div>
        </Linha>
        <Linha rotulo="Disponivel">
          <label className="flex items-center gap-2 pt-2 text-sm text-slate-600">
            <input type="checkbox" checked={disponivel} onChange={(e) => setDisponivel(e.target.checked)} />
            Apto a receber chamados
          </label>
        </Linha>
      </form>
    </Modal>
  )
}
