import { useEffect, useState } from 'react';
import { useAuth } from '../auth/AuthContext';

export default function HomePage() {
  const { token } = useAuth();
  const [message, setMessage] = useState('Carregando...');
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;

    async function loadMessage() {
      try {
        const response = await fetch('/api/private/hello', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`);
        }

        const data = await response.json();
        if (active) {
          setMessage(data.message ?? 'Resposta inesperada do backend.');
          setError('');
        }
      } catch (loadError) {
        if (active) {
          setError('Nao foi possivel conectar ao backend. Tente novamente em instantes.');
          setMessage('');
        }
      }
    }

    loadMessage();

    return () => {
      active = false;
    };
  }, [token]);

  return (
    <section>
      <h1>Pagina Principal</h1>
      <p>Voce esta autenticado.</p>
      {error ? <p className="error">{error}</p> : <p className="message">{message}</p>}
    </section>
  );
}

