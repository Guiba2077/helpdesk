import type { ReactNode } from 'react'

export default function Card({
  titulo,
  acao,
  children,
}: {
  titulo?: string
  acao?: ReactNode
  children: ReactNode
}) {
  return (
    <section className="rounded-md bg-white border border-line shadow-sm">
      {titulo && (
        <header className="flex items-center justify-between px-4 py-3 border-b border-line">
          <h2 className="font-semibold text-slate-800">{titulo}</h2>
          {acao}
        </header>
      )}
      {children}
    </section>
  )
}
