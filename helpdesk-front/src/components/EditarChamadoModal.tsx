import { useState, type FormEvent } from 'react'
import { atualizarChamado } from '../api/chamadosApi'
import { ApiError } from '../api/client'
import type { Categoria, Chamado } from '../api/types'
import Alerta from './Alerta'
import { botaoPrimario, botaoSecundario, campo, Linha } from './form'
import Modal from './Modal'

const CATEGORIAS: { valor: Categoria; rotulo: string }[] = [
  { valor: 'REDE', rotulo: 'Infraestrutura e Rede' },
  { valor: 'HARDWARE', rotulo: 'Hardware e Equipamentos' },
  { valor: 'SOFTWARE', rotulo: 'Sistemas e Software' },
  { valor: 'BANCO_DADOS', rotulo: 'Banco de Dados' },
  { valor: 'ACESSO', rotulo: 'Seguranca e Permissoes de Acesso' },
]

export default function EditarChamadoModal({
  chamado,
  onClose,
  onSalvo,
}: {
  chamado: Chamado
  onClose: () => void
  onSalvo: () => void
}) {
  const [titulo, setTitulo] = useState(chamado.titulo)
  const [descricao, setDescricao] = useState(chamado.descricao)
  const [categoria, setCategoria] = useState<Categoria>(
    chamado.categoria as Categoria,
  )
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  async function enviar(e: FormEvent) {
    e.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      await atualizarChamado(chamado.id, {
        titulo: titulo.trim(),
        descricao: descricao.trim(),
        categoria,
      })
      onSalvo()
      onClose()
    } catch (err) {
      setErro(err instanceof ApiError ? err.message : 'Falha ao salvar.')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <Modal
      titulo={`Editar chamado ${chamado.protocolo}`}
      onClose={onClose}
      rodape={
        <>
          <button type="button" onClick={onClose} className={botaoSecundario}>
            Cancelar
          </button>
          <button type="submit" form="form-editar-chamado" disabled={enviando} className={botaoPrimario}>
            {enviando ? 'Salvando...' : 'Salvar'}
          </button>
        </>
      }
    >
      <form id="form-editar-chamado" onSubmit={enviar} className="space-y-4">
        <Alerta texto={erro} />
        <Linha rotulo="Categoria" obrigatorio>
          <select value={categoria} onChange={(e) => setCategoria(e.target.value as Categoria)} className={campo}>
            {CATEGORIAS.map((c) => (
              <option key={c.valor} value={c.valor}>
                {c.rotulo}
              </option>
            ))}
          </select>
        </Linha>
        <Linha rotulo="Titulo" obrigatorio>
          <input required maxLength={150} value={titulo} onChange={(e) => setTitulo(e.target.value)} className={campo} />
        </Linha>
        <Linha rotulo="Descricao" obrigatorio>
          <textarea required rows={6} maxLength={2000} value={descricao} onChange={(e) => setDescricao(e.target.value)} className={campo} />
        </Linha>
        <p className="text-xs text-slate-400 sm:pl-[10rem]">
          Prioridade, tecnico e status nao mudam aqui: use os controles do detalhe.
        </p>
      </form>
    </Modal>
  )
}
