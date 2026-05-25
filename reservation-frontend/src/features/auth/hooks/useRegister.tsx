import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiFetch } from '../../../api/apiFetch';

export function useRegister() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const register = async (input: {
    email: string;
    username: string;
    password: string;
  }) => {
    setLoading(true);
    setError(null);

    try {
      await apiFetch('/register', {
        method: 'POST',
        body: JSON.stringify(input),
      });

      navigate('/login');
    } catch {
      setError('회원가입 실패');
    } finally {
      setLoading(false);
    }
  };

  return { register, loading, error };
}
