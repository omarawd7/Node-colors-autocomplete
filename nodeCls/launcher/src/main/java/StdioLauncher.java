import org.myloop.ls.langserver.MyloopLanguageServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Launcher for MyLoop language server.
 */
public class StdioLauncher {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        LogManager.getLogManager().reset();
        Logger globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        globalLogger.setLevel(Level.OFF);

        // start the language server
        startServer(System.in, System.out);
    }

    /**
     * Start the language server.
     * 
     * @param in  System Standard input stream
     * @param out System standard output stream
     * @throws ExecutionException   Unable to start the server
     * @throws InterruptedException Unable to start the server
     */
    private static void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {
        // Initialize the myloopLanguageServer
        MyloopLanguageServer myloopLanguageServer = new MyloopLanguageServer();
        // Create JSON RPC launcher for myloopLanguageServer instance.
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(myloopLanguageServer, in, out);

        // Get the client that request to launch the LS.
        LanguageClient client = launcher.getRemoteProxy();

        // Set the client to language server
        myloopLanguageServer.connect(client);

        // Start the listener for JsonRPC
        Future<?> startListening = launcher.startListening();

        // Get the computed result from LS.
        startListening.get();
    }
}
