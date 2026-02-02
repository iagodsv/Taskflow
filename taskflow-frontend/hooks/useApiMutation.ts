"use client";

import * as React from "react";

export type UseApiMutationOptions<TInput, TOutput> = {
  onSuccess?: (data: TOutput, input: TInput) => void;
  onError?: (error: unknown, input: TInput) => void;
  onSettled?: (
    data: TOutput | undefined,
    error: unknown | null,
    input: TInput,
  ) => void;
};

export type UseApiMutationState<TOutput> = {
  data: TOutput | undefined;
  loading: boolean;
  error: unknown | null;
};

export type UseApiMutationReturn<TInput, TOutput> =
  UseApiMutationState<TOutput> & {
    mutate: (input: TInput) => Promise<TOutput | undefined>;
    reset: () => void;
  };

/**
 * Hook para mutações (POST/PUT/DELETE), baseado em uma função que recebe um input e retorna Promise.
 */
export function useApiMutation<TInput, TOutput>(
  mutationFn: (input: TInput) => Promise<TOutput>,
  options: UseApiMutationOptions<TInput, TOutput> = {},
): UseApiMutationReturn<TInput, TOutput> {
  const { onSuccess, onError, onSettled } = options;
  const [data, setData] = React.useState<TOutput | undefined>(undefined);
  const [loading, setLoading] = React.useState<boolean>(false);
  const [error, setError] = React.useState<unknown | null>(null);

  const reset = React.useCallback(() => {
    setData(undefined);
    setError(null);
    setLoading(false);
  }, []);

  const mutate = React.useCallback(
    async (input: TInput) => {
      setLoading(true);
      setError(null);
      try {
        const result = await mutationFn(input);
        setData(result);
        onSuccess?.(result, input);
        onSettled?.(result, null, input);
        return result;
      } catch (e: unknown) {
        setError(e);
        onError?.(e, input);
        onSettled?.(undefined, e, input);
        return undefined;
      } finally {
        setLoading(false);
      }
    },
    [mutationFn, onSuccess, onError, onSettled],
  );

  return { data, loading, error, mutate, reset };
}
