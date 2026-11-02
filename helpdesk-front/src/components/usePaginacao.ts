import { useState } from 'react'

/**
 * Paginação no cliente: a API devolve a lista completa e a tela mostra uma
 * página por vez. Se a página atual deixar de existir (filtro ou recarga),
 * volta para a última página válida.
 */
export function usePaginacao<T>(itens: T[], tamanhoInicial = 10) {
  const [pagina, setPagina] = useState(1)
  const [tamanho, setTamanho] = useState(tamanhoInicial)

  const totalPaginas = Math.max(1, Math.ceil(itens.length / tamanho))
  const atual = Math.min(pagina, totalPaginas)
  const inicio = (atual - 1) * tamanho

  return {
    itensPagina: itens.slice(inicio, inicio + tamanho),
    pagina: atual,
    totalPaginas,
    tamanho,
    total: itens.length,
    inicio,
    irPara: (p: number) => setPagina(Math.min(Math.max(1, p), totalPaginas)),
    mudarTamanho: (t: number) => {
      setTamanho(t)
      setPagina(1)
    },
    reiniciar: () => setPagina(1),
  }
}

export type EstadoPaginacao = Omit<ReturnType<typeof usePaginacao>, 'itensPagina' | 'reiniciar'>
