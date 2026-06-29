import clinica.Clinica;
import servidor.ServidorClinica;

/**
 * Ponto de entrada da aplicação. Cria a clínica em memória sem banco de
 * dados e inicia o servidor web na porta 8080
*/

public class App {
    public static void main(String[] args) throws Exception {
        Clinica clinica = new Clinica();
        new ServidorClinica(8080, clinica);

        // O servidor roda em background (threads do HttpServer);
        // mantem a thread principal viva para o processo não acabar
        Thread.currentThread().join();
    }
}