import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import Layout from './components/Layout'
import ProtectedRoute from './auth/ProtectedRoute'
import Auditoria from './pages/Auditoria'
import Chamados from './pages/Chamados'
import Especialidades from './pages/Especialidades'
import Login from './pages/Login'
import Relatorios from './pages/Relatorios'
import Tecnicos from './pages/Tecnicos'
import Usuarios from './pages/Usuarios'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path="/chamados" element={<Chamados />} />
              <Route path="/relatorios" element={<Relatorios />} />
              <Route path="/auditoria" element={<Auditoria />} />
              <Route element={<ProtectedRoute perfis={['ADMIN']} />}>
                <Route path="/usuarios" element={<Usuarios />} />
                <Route path="/tecnicos" element={<Tecnicos />} />
                <Route path="/especialidades" element={<Especialidades />} />
              </Route>
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/chamados" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
