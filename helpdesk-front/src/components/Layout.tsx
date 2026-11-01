import type { ReactNode } from 'react'
import { NavLink, Outlet } from 'react-router-dom'
import type { Perfil } from '../api/types'
import { useAuth } from '../auth/AuthContext'

function IconeChamado() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden>
      <path d="M3 9a2 2 0 0 0 0 6v3a1 1 0 0 0 1 1h16a1 1 0 0 0 1-1v-3a2 2 0 0 1 0-6V6a1 1 0 0 0-1-1H4a1 1 0 0 0-1 1z" />
      <path d="M13 5v14" strokeDasharray="2 3" />
    </svg>
  )
}

function IconeTecnico() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden>
      <circle cx="9" cy="8" r="3.5" />
      <path d="M2.5 20a6.5 6.5 0 0 1 13 0" />
      <path d="M17 11.5a3 3 0 1 0 0-6M19 20a5.5 5.5 0 0 0-3-4.9" />
    </svg>
  )
}

const assistencia: { to: string; rotulo: string; icone: ReactNode }[] = [
  { to: '/chamados', rotulo: 'Chamados', icone: <IconeChamado /> },
]
const administracao: { to: string; rotulo: string; icone: ReactNode; perfis: Perfil[] }[] = [
  { to: '/tecnicos', rotulo: 'Tecnicos', icone: <IconeTecnico />, perfis: ['ADMIN'] },
]

export default function Layout() {
  const { usuario, logout } = useAuth()
  const iniciais = (usuario?.nome ?? '?')
    .split(' ')
    .slice(0, 2)
    .map((p) => p[0])
    .join('')
    .toUpperCase()

  const grupos = [
    { titulo: 'Assistencia', itens: assistencia },
    {
      titulo: 'Administracao',
      itens: administracao.filter(
        (i) => usuario && i.perfis.includes(usuario.perfil),
      ),
    },
  ].filter((g) => g.itens.length > 0)

  return (
    <div className="min-h-screen md:flex">
      <aside className="hidden md:flex w-60 shrink-0 flex-col bg-sidebar text-slate-300">
        <div className="flex items-center gap-2 px-5 h-14 border-b border-white/10">
          <span className="grid place-items-center size-8 rounded bg-accent text-sidebar font-black">
            H
          </span>
          <span className="text-white font-semibold tracking-wide">Helpdesk TI</span>
        </div>
        {grupos.map((g) => (
          <div key={g.titulo}>
            <p className="px-5 pt-5 pb-2 text-[11px] font-semibold uppercase tracking-wider text-slate-500">
              {g.titulo}
            </p>
            <nav className="flex flex-col">
              {g.itens.map((i) => (
                <NavLink
                  key={i.to}
                  to={i.to}
                  className={({ isActive }) =>
                    `flex items-center gap-3 px-5 py-2.5 border-l-4 hover:bg-sidebar-hover hover:text-white ${
                      isActive
                        ? 'border-accent bg-sidebar-hover text-white'
                        : 'border-transparent'
                    }`
                  }
                >
                  {i.icone}
                  {i.rotulo}
                </NavLink>
              ))}
            </nav>
          </div>
        ))}
      </aside>

      <div className="flex-1 min-w-0 flex flex-col">
        <header className="h-14 bg-white border-b border-line flex items-center justify-between px-4 sm:px-6">
          <span className="md:hidden font-semibold text-sidebar">Helpdesk TI</span>
          <span className="hidden md:block text-slate-500">
            Central de Chamados de TI
          </span>
          <div className="flex items-center gap-3">
            <div className="text-right leading-tight hidden sm:block">
              <p className="font-medium text-slate-800">{usuario?.nome}</p>
              <p className="text-xs text-slate-500">{usuario?.perfil}</p>
            </div>
            <span className="grid place-items-center size-9 rounded-full bg-primary text-white text-sm font-semibold">
              {iniciais}
            </span>
            <button
              onClick={logout}
              className="rounded border border-line px-3 py-1.5 text-slate-600 hover:bg-surface"
            >
              Sair
            </button>
          </div>
        </header>
        <main className="flex-1 p-4 sm:p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
