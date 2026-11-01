import type { ReactNode } from 'react'

export const campo =
  'w-full rounded border border-line bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary'

export const botaoPrimario =
  'rounded bg-primary px-5 py-2 text-sm font-medium text-white hover:bg-primary-dark disabled:opacity-60'

export const botaoSecundario =
  'rounded border border-line bg-white px-4 py-2 text-sm text-slate-600 hover:bg-surface'

export function Linha({
  rotulo,
  obrigatorio,
  children,
}: {
  rotulo: string
  obrigatorio?: boolean
  children: ReactNode
}) {
  return (
    <div className="grid gap-1 sm:grid-cols-[9rem_1fr] sm:items-start sm:gap-4">
      <span className="text-sm font-medium text-slate-600 sm:pt-2 sm:text-right">
        {rotulo}
        {obrigatorio && <span className="text-red-600"> *</span>}
      </span>
      <div>{children}</div>
    </div>
  )
}
