import { useEffect, useState } from 'react';

export default function HomePage() {
  const [message, setMessage] = useState('Carregando...');
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;

    async function loadMessage() {
      try {
        const response = await fetch('/api/hello');

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
  }, []);

  return (
    <section>
      <h1>Pagina Principal</h1>
      {error ? <p className="error">{error}</p> : <p className="message">{message}</p>}
    </section>
  );
}

