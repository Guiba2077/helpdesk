export default function Alerta({ texto }: { texto: string }) {
  if (!texto) return null
  return (
    <div
      role="alert"
      className="mb-3 rounded-lg bg-red-50 border border-red-200 text-red-700 text-sm px-3 py-2"
    >
      {texto}
    </div>
  )
}
