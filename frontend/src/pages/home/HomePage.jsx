import { useEffect, useState } from 'react';
import { useAuth } from '../../auth/AuthContext';
import { getPrivateHello } from '../../services/home/homeService';

export default function HomePage() {
  const { token } = useAuth();
  const [message, setMessage] = useState('Carregando...');
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;

    async function loadMessage() {
      try {
        const data = await getPrivateHello(token);
        if (active) {
          setMessage(data.message ?? 'Resposta inesperada do backend.');
          setError('');
        }
      } catch (loadError) {
        if (active) {
          setError('Não foi possível conectar ao backend. Tente novamente em instantes.');
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
      <h1>Página Principal</h1>
      <p>Você está autenticado.</p>
      {error ? <p className="error">{error}</p> : <p className="message">{message}</p>}
    </section>
  );
}



