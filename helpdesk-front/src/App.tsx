import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import Layout from './components/Layout'
import ProtectedRoute from './auth/ProtectedRoute'
import Chamados from './pages/Chamados'
import Login from './pages/Login'
import Tecnicos from './pages/Tecnicos'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path="/chamados" element={<Chamados />} />
              <Route element={<ProtectedRoute perfis={['ADMIN']} />}>
                <Route path="/tecnicos" element={<Tecnicos />} />
              </Route>
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/chamados" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
