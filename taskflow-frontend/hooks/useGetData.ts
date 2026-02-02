"use client";

import * as React from "react";

export type UseGetDataOptions<T> = {
  /** Dispara automaticamente no mount (padrão: true). */
  immediate?: boolean;
  /** Dependências para re-executar o fetch. */
  deps?: React.DependencyList;
  onSuccess?: (data: T) => void;
  onError?: (error: unknown) => void;
  initialData?: T;
  /** Chave de cache (sessionStorage). Quando presente, usa e atualiza cache. */
  cacheKey?: string;
};

export type UseGetDataState<T> = {
  data: T | undefined;
  loading: boolean;
  error: unknown | null;
};

export type UseGetDataReturn<T> = UseGetDataState<T> & {
  refetch: () => Promise<T | undefined>;
  setData: React.Dispatch<React.SetStateAction<T | undefined>>;
};

/**
 * Hook para leitura (GET) baseada em fetcher.
 * Passe uma função que retorna Promise<T>.
 */
export function useGetData<T>(
  fetcher: () => Promise<T>,
  options: UseGetDataOptions<T> = {},
): UseGetDataReturn<T> {
  const {
    immediate = true,
    deps = [],
    onSuccess,
    onError,
    initialData,
    cacheKey,
  } = options;

  const [data, setData] = React.useState<T | undefined>(initialData);
  const [loading, setLoading] = React.useState<boolean>(false);
  const [error, setError] = React.useState<unknown | null>(null);

  const abortRef = React.useRef<AbortController | null>(null);

  const refetch = React.useCallback(async () => {
    // Cancelar requisição anterior se houver
    if (abortRef.current) {
      abortRef.current.abort();
    }
    const controller = new AbortController();
    abortRef.current = controller;

    setLoading(true);
    setError(null);
    try {
      const result = await fetcher();
      setData(result);
      // Atualiza cache quando aplicável
      if (cacheKey) {
        try {
          sessionStorage.setItem(cacheKey, JSON.stringify(result));
        } catch {}
      }
      onSuccess?.(result);
      return result;
    } catch (e: unknown) {
      // Ignora erro de abort
      const isAbort =
        typeof e === "object" &&
        e !== null &&
        "name" in e &&
        (e as { name?: unknown }).name === "AbortError";
      if (!isAbort) {
        setError(e);
        onError?.(e);
      }
      return undefined;
    } finally {
      setLoading(false);
    }
  }, [fetcher, onSuccess, onError, cacheKey]);

  React.useEffect(() => {
    // Inicializa a partir do cache quando possível
    if (cacheKey && typeof window !== "undefined") {
      try {
        const cached = sessionStorage.getItem(cacheKey);
        if (cached) {
          setData(JSON.parse(cached));
        }
      } catch {}
    }
    if (!immediate) return;
    void refetch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  return { data, loading, error, refetch, setData };
}
