package io.carlease.ev_catalogue.adapter.in.web;

import io.carlease.ev_catalogue.adapter.out.sse.VehicleStatusBroadcaster;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/catalogue/vehicles")
public class VehicleStatusSseController {

    private final VehicleStatusBroadcaster broadcaster;

    public VehicleStatusSseController(VehicleStatusBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events() {
        return broadcaster.register();
    }
}
