import type { EstadoPaginacao } from './usePaginacao'

const TAMANHOS = [10, 25, 50]

export default function Paginacao({
  pagina,
  totalPaginas,
  tamanho,
  total,
  inicio,
  irPara,
  mudarTamanho,
}: EstadoPaginacao) {
  if (total === 0) return null

  const botao =
    'rounded border border-line bg-white px-2.5 py-1 text-sm text-slate-600 hover:bg-surface disabled:opacity-40 disabled:hover:bg-white'
  const fim = Math.min(inicio + tamanho, total)

  return (
    <nav
      aria-label="Paginação"
      className="flex flex-wrap items-center justify-between gap-3 border-t border-line px-4 py-3 text-sm text-slate-500"
    >
      <span>
        Mostrando {inicio + 1} a {fim} de {total}
      </span>
      <div className="flex flex-wrap items-center gap-3">
        <label className="flex items-center gap-2">
          Por página
          <select
            value={tamanho}
            onChange={(e) => mudarTamanho(Number(e.target.value))}
            className="rounded border border-line bg-white px-2 py-1 text-sm"
          >
            {TAMANHOS.map((t) => (
              <option key={t}>{t}</option>
            ))}
          </select>
        </label>
        <div className="flex items-center gap-1">
          <button className={botao} disabled={pagina === 1} onClick={() => irPara(pagina - 1)} aria-label="Página anterior">
            ‹
          </button>
          <span className="px-2">
            Página {pagina} de {totalPaginas}
          </span>
          <button className={botao} disabled={pagina === totalPaginas} onClick={() => irPara(pagina + 1)} aria-label="Próxima página">
            ›
          </button>
        </div>
      </div>
    </nav>
  )
}
