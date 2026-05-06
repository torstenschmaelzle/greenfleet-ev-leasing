package io.carlease.EV_Catalogue.application.service;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class VehicleStatusBroadcaster {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter register() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        return emitter;
    }

    public void broadcast(String vin, String status) {
        String data = "{\"vin\":\"" + vin + "\",\"status\":\"" + status + "\"}";
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("vehicle-status-changed").data(data));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
