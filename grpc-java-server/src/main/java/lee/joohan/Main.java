package lee.joohan;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(6666)
                .addService(new SimpleService())
//                .executor(Executors.newFixedThreadPool(2)) // Limit the executor thread pool size
                .build();

        server.start();

        if (server != null) {
            server.awaitTermination();
        }
    }
}
