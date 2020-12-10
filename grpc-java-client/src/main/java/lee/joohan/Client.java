package lee.joohan;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lee.joohan.protos.simple.SimpleMessage;


public class Client {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 6666)
                .useTransportSecurity()
                .usePlaintext()
                .build();

        SimpleClient simpleClient = new SimpleClient(channel);
    }

    public void blockingUnaryCallTest(SimpleClient simpleClient) {
        // It simply sends 100 request.
        // I did this to see if the stream id is changed.
        for (int i = 0; i < 100; i++) {
            SimpleMessage simpleMessage = simpleClient.singleCall();
        }
    }

    public void asyncUnaryCallTest(SimpleClient simpleClient) throws InterruptedException {
        simpleClient.singleCall(v -> System.out.println("The current thread is: " + Thread.currentThread().getName()));
        simpleClient.singleCall(v -> System.out.println("The current thread is: " + Thread.currentThread().getName()));
        simpleClient.singleCall(v -> System.out.println("The current thread is: " + Thread.currentThread().getName()));

        Thread.sleep(10000);
    }

    public void maxThreadPoolTest(SimpleClient simpleClient) throws InterruptedException {

        // Each call takes 5 seconds to execute on the server
        // You have to limit the size of executor thread pool on the server before run this test
        // It prints out the time it received response asynchronously
        simpleClient.waitCall(1, 5000);
        simpleClient.waitCall(2, 5000);
        simpleClient.waitCall(3, 5000);
        simpleClient.waitCall(4, 5000);
        simpleClient.waitCall(5, 5000);
        simpleClient.waitCall(6, 5000);
        simpleClient.waitCall(7, 5000);
        simpleClient.waitCall(8, 5000);
        simpleClient.waitCall(9, 5000);
        simpleClient.waitCall(10, 5000);

        Thread.sleep(20000);
    }

    public void streamingRequestTest(SimpleClient simpleClient) {
        simpleClient.sendStreaming();
    }

}
