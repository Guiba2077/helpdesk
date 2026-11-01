import { useState, type FormEvent } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { ApiError } from '../api/client'
import { useAuth } from '../auth/AuthContext'

export default function Login() {
  const { usuario, login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  if (usuario) return <Navigate to="/chamados" replace />

  async function enviar(e: FormEvent) {
    e.preventDefault()
    setErro('')
    setCarregando(true)
    try {
      await login(email, senha)
      navigate('/chamados', { replace: true })
    } catch (err) {
      setErro(err instanceof ApiError ? err.message : 'Falha ao entrar.')
    } finally {
      setCarregando(false)
    }
  }

  return (
    <main className="min-h-screen bg-sidebar flex items-center justify-center px-4">
      <form
        onSubmit={enviar}
        className="w-full max-w-sm bg-white rounded-md shadow-md p-8 space-y-5"
      >
        <header className="text-center">
          <span className="mx-auto mb-3 grid place-items-center size-12 rounded bg-accent text-sidebar text-2xl font-black">
            H
          </span>
          <h1 className="text-xl font-semibold text-slate-800">Helpdesk TI</h1>
          <p className="text-sm text-slate-500">Entre com sua conta</p>
        </header>

        {erro && (
          <div
            role="alert"
            className="rounded bg-red-50 border border-red-200 text-red-700 text-sm px-3 py-2"
          >
            {erro}
          </div>
        )}

        <label className="block text-sm font-medium text-slate-700">
          E-mail
          <input
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </label>

        <label className="block text-sm font-medium text-slate-700">
          Senha
          <input
            type="password"
            required
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </label>

        <button
          type="submit"
          disabled={carregando}
          className="w-full rounded bg-primary hover:bg-primary-dark disabled:opacity-60 text-white font-medium py-2"
        >
          {carregando ? 'Entrando...' : 'Entrar'}
        </button>
      </form>
    </main>
  )
}
