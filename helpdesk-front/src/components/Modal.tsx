import type { ReactNode } from 'react'

export default function Modal({
  titulo,
  onClose,
  children,
  rodape,
  largo = false,
}: {
  titulo: string
  onClose: () => void
  children: ReactNode
  rodape?: ReactNode
  largo?: boolean
}) {
  return (
    <div
      className="fixed inset-0 z-10 flex items-start sm:items-center justify-center overflow-y-auto bg-black/50 p-4"
      onClick={onClose}
    >
      <div
        role="dialog"
        aria-label={titulo}
        onClick={(e) => e.stopPropagation()}
        className={`w-full ${largo ? 'max-w-3xl' : 'max-w-2xl'} rounded-md bg-white shadow-xl border-t-4 border-accent`}
      >
        <div className="flex items-center justify-between border-b border-line bg-surface px-5 py-3">
          <h2 className="font-semibold text-slate-800">{titulo}</h2>
          <button
            onClick={onClose}
            aria-label="Fechar"
            className="grid size-7 place-items-center rounded text-slate-500 hover:bg-line hover:text-slate-800"
          >
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" aria-hidden>
              <path d="M6 6l12 12M18 6L6 18" />
            </svg>
          </button>
        </div>
        <div className="max-h-[70vh] overflow-y-auto p-5">{children}</div>
        {rodape && (
          <div className="flex justify-end gap-2 border-t border-line bg-surface px-5 py-3">
            {rodape}
          </div>
        )}
      </div>
    </div>
  )
}
