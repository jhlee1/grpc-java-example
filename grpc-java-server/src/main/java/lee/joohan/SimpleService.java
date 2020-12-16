package lee.joohan;

import io.grpc.stub.StreamObserver;
import lee.joohan.protos.simple.*;
import lombok.extern.java.Log;

@Log
public class SimpleService extends SimpleServiceGrpc.SimpleServiceImplBase {
    @Override
    public void singleCall(SimpleMessage request, StreamObserver<SimpleMessage> responseObserver) {
        log.info("Received message: " + request.getMessage() + " on Thread " + Thread.currentThread());

        responseObserver.onNext(
                SimpleMessage.newBuilder()
                        .setMessage("Response received")
                        .build());
        responseObserver.onCompleted();

    }

    @Override
    public void singleWaitCall(WaitMessage request, StreamObserver<WaitResponse> responseObserver) {
        log.info("Received message: " + request.getAllFields()  + " on Thread " + Thread.currentThread());

        try {
            Thread.sleep(request.getTimeInMilliseconds());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        responseObserver.onNext(
                WaitResponse.newBuilder()
                        .setOrder(request.getOrder())
                        .setTimeInMilliseconds(request.getTimeInMilliseconds())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<SimpleMessage> sendStreamCall(StreamObserver<StreamInfo> responseObserver) {

        return new StreamObserver<>() {
            int size = 0;

            @Override
            public void onNext(SimpleMessage value) {
                System.out.println("stream call " + value.getOrder() + " on Thread" + Thread.currentThread());

                size++;
            }

            @Override
            public void onError(Throwable t) {
                log.throwing("SimpleService", "sendStreamingCall", t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                        StreamInfo.newBuilder()
                                .setSize(size)
                                .build());

                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void receiveStreamCall(StreamInfo request, StreamObserver<SimpleMessage> responseObserver) {
        super.receiveStreamCall(request, responseObserver);
    }
}
