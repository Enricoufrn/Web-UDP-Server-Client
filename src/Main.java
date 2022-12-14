import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final String STOP_COMMAND = "stop";

    public static void main(String[] args) {
        EchoServer echoServer = initializeServer();
        ExecutorService executorService = null;
        if(echoServer != null) {
//            executorService = Executors.newSingleThreadExecutor();
//            executorService.submit(echoServer);
            echoServer.start();
            Scanner scanner = new Scanner(System.in);
            while(true){
                String input = scanner.nextLine();
                if(input.equalsIgnoreCase(STOP_COMMAND) || input.equalsIgnoreCase(STOP_COMMAND.substring(0,1))){
                    System.out.println("======= FINALIZANDO SERVIDOR =======");
                    echoServer.interrupt();
                    break;
                }
            }
        }
    }

    public static EchoServer initializeServer(){
        System.out.println("============ INICIANDO O SERVIDOR ============");
        EchoServer echoServer = null;
        try{
            echoServer = new EchoServer(EchoServer.DEFAULT_SERVER_PORT, null);
            System.out.println("============ SERVIDOR INICIADO ============");
        }catch (Exception exception){
            System.out.println("============ FALHA AO INICIAR O SERVIDOR ============");
            exception.printStackTrace();
        }
        return echoServer;
    }

    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        if(pool != null){
            pool.shutdown(); // Disable new tasks from being submitted
            try {
                // Wait a while for existing tasks to terminate
                if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                    pool.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!pool.awaitTermination(10, TimeUnit.SECONDS))
                        System.err.println("Pool did not terminate");
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                pool.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
        }
    }
}