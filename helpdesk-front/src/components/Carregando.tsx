export function Spinner({ className = 'size-4' }: { className?: string }) {
  return (
    <span
      aria-hidden
      className={`inline-block animate-spin rounded-full border-2 border-current border-r-transparent ${className}`}
    />
  )
}

export default function Carregando({ texto = 'Carregando...' }: { texto?: string }) {
  return (
    <span role="status" className="inline-flex items-center gap-2 text-slate-500">
      <Spinner />
      {texto}
    </span>
  )
}

/** Linha de tabela que ocupa todas as colunas, para os estados de carregamento e lista vazia. */
export function LinhaEstado({
  colunas,
  carregando,
  vazio,
}: {
  colunas: number
  carregando: boolean
  vazio: string
}) {
  return (
    <tr>
      <td colSpan={colunas} className="px-4 py-8 text-center text-slate-500">
        {carregando ? <Carregando /> : vazio}
      </td>
    </tr>
  )
}
