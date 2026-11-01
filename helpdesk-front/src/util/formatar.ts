export function formatarData(iso?: string | null): string {
  return iso ? new Date(iso).toLocaleString('pt-BR') : '-'
}
