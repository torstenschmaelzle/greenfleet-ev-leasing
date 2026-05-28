package io.carlease.EV_Catalogue.adapter.out.sse;

import io.carlease.EV_Catalogue.application.port.out.VehicleBroadcastPort;
import io.carlease.EV_Catalogue.domain.VehicleStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class VehicleStatusBroadcaster implements VehicleBroadcastPort {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter register() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        return emitter;
    }

    @Override
    public void broadcast(String vin, VehicleStatus status) {
        VehicleStatusEvent event = new VehicleStatusEvent(vin, status.name());
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("vehicle-status-changed")
                        .data(event, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    private record VehicleStatusEvent(String vin, String status) {}
}
