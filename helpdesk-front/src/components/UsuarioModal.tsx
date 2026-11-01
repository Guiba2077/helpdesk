import { useState, type FormEvent } from 'react'
import { ApiError } from '../api/client'
import type { Perfil, UsuarioLogado } from '../api/types'
import { atualizarUsuario, criarUsuario } from '../api/usuariosApi'
import Alerta from './Alerta'
import { botaoPrimario, botaoSecundario, campo, Linha } from './form'
import Modal from './Modal'

const PERFIS: { valor: Perfil; rotulo: string }[] = [
  { valor: 'OPERADOR', rotulo: 'Operador (abre chamados)' },
  { valor: 'TECNICO', rotulo: 'Tecnico (atende chamados)' },
  { valor: 'ADMIN', rotulo: 'Administrador (acesso total)' },
]

export default function UsuarioModal({
  usuario,
  onClose,
  onSalvo,
}: {
  usuario?: UsuarioLogado
  onClose: () => void
  onSalvo: () => void
}) {
  const edicao = !!usuario
  const [nome, setNome] = useState(usuario?.nome ?? '')
  const [email, setEmail] = useState(usuario?.email ?? '')
  const [senha, setSenha] = useState('')
  const [telefone, setTelefone] = useState(usuario?.telefone ?? '')
  const [setor, setSetor] = useState(usuario?.setor ?? '')
  const [perfil, setPerfil] = useState<Perfil>(usuario?.perfil ?? 'OPERADOR')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  async function enviar(e: FormEvent) {
    e.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      const dados = {
        nome: nome.trim(),
        email: email.trim(),
        telefone: telefone.trim() || undefined,
        setor: setor.trim(),
        perfil,
      }
      if (usuario) await atualizarUsuario(usuario.id, dados)
      else await criarUsuario({ ...dados, senha })
      onSalvo()
      onClose()
    } catch (err) {
      setErro(err instanceof ApiError ? err.message : 'Falha ao salvar usuario.')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <Modal
      titulo={edicao ? 'Editar usuario' : 'Novo usuario'}
      onClose={onClose}
      rodape={
        <>
          <button type="button" onClick={onClose} className={botaoSecundario}>
            Cancelar
          </button>
          <button type="submit" form="form-usuario" disabled={enviando} className={botaoPrimario}>
            {enviando ? 'Salvando...' : 'Salvar'}
          </button>
        </>
      }
    >
      <form id="form-usuario" onSubmit={enviar} className="space-y-4">
        <Alerta texto={erro} />
        <Linha rotulo="Nome" obrigatorio>
          <input required value={nome} onChange={(e) => setNome(e.target.value)} className={campo} />
        </Linha>
        <Linha rotulo="E-mail" obrigatorio>
          <input required type="email" value={email} onChange={(e) => setEmail(e.target.value)} className={campo} />
        </Linha>
        {!edicao && (
          <Linha rotulo="Senha inicial" obrigatorio>
            <input required type="password" minLength={6} value={senha} onChange={(e) => setSenha(e.target.value)} className={campo} />
            <p className="mt-1 text-xs text-slate-400">
              Guardada com hash Blowfish (pgcrypto) no banco.
            </p>
          </Linha>
        )}
        <Linha rotulo="Telefone">
          <input value={telefone} onChange={(e) => setTelefone(e.target.value)} placeholder="(62) 98888-0000" className={campo} />
        </Linha>
        <Linha rotulo="Setor" obrigatorio>
          <input required value={setor} onChange={(e) => setSetor(e.target.value)} className={campo} />
        </Linha>
        <Linha rotulo="Perfil" obrigatorio>
          <select value={perfil} onChange={(e) => setPerfil(e.target.value as Perfil)} className={campo}>
            {PERFIS.map((p) => (
              <option key={p.valor} value={p.valor}>
                {p.rotulo}
              </option>
            ))}
          </select>
        </Linha>
      </form>
    </Modal>
  )
}
