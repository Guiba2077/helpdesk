import { Navigate, Outlet } from 'react-router-dom'
import type { Perfil } from '../api/types'
import { useAuth } from './AuthContext'

export default function ProtectedRoute({ perfis }: { perfis?: Perfil[] }) {
  const { usuario } = useAuth()
  if (!usuario) return <Navigate to="/login" replace />
  if (perfis && !perfis.includes(usuario.perfil))
    return <Navigate to="/chamados" replace />
  return <Outlet />
}
