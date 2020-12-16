package lee.joohan;

import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import lee.joohan.protos.simple.*;

import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class SimpleClient {
  private final SimpleServiceGrpc.SimpleServiceStub asyncStub;
  private final SimpleServiceGrpc.SimpleServiceBlockingStub blockingStub;

  public SimpleClient(Channel channel) {
    asyncStub = SimpleServiceGrpc.newStub(channel);
    blockingStub = SimpleServiceGrpc.newBlockingStub(channel);
  }

  public void singleCall(Consumer<SimpleMessage> callback) {
    asyncStub.singleCall(SimpleMessage.newBuilder().setMessage("AsyncCall")
            .build(), new StreamObserver<>() {
      @Override
      public void onNext(SimpleMessage value) {
        callback.accept(value);
      }

      @Override
      public void onError(Throwable t) {

      }

      @Override
      public void onCompleted() {

      }
    });
  }


  public SimpleMessage singleCall() {
    System.out.println("In single Call thread: " + Thread.currentThread());
    return blockingStub.singleCall(SimpleMessage.newBuilder()
            .setMessage("Hello request")
            .build());
  }

  public void waitCall(int order, int timeoutInMillis) {
    System.out.println("Sending request at " + new Date());

    asyncStub.singleWaitCall(WaitMessage.newBuilder()
            .setOrder(order)
            .setTimeInMilliseconds(timeoutInMillis)
            .build(), new StreamObserver<>() {
      @Override
      public void onNext(WaitResponse value) {
        System.out.println("Response received! " + "order: " + order + " sleep for " + timeoutInMillis + " Arrived at" + new Date());
      }

      @Override
      public void onError(Throwable t) {

      }

      @Override
      public void onCompleted() {

      }
    });
  }
  public void sendStreaming() {
    StreamObserver<StreamInfo> responseObserver = new StreamObserver<StreamInfo>() {
      @Override
      public void onNext(StreamInfo value) {
        System.out.println("Received on next response");

      }

      @Override
      public void onError(Throwable t) {

      }

      @Override
      public void onCompleted() {
        System.out.println("Received complete response");
      }
    };

    StreamObserver<SimpleMessage> requestObserver = asyncStub.sendStreamCall(responseObserver);

    IntStream.range(0, 100)
            .parallel()
            .forEach(it -> {
                      requestObserver.onNext(
                              SimpleMessage.newBuilder()
                                      .setOrder(it)
                                      .build()
                      );
                      System.out.println("Sending " + it);
                    }
            );

    requestObserver.onCompleted();
  }
}
