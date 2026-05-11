package io.carlease.EV_Catalogue.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;

class VehicleStatusBroadcasterTest {

    private VehicleStatusBroadcaster broadcaster;
    private List<SseEmitter> emitters;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() throws Exception {
        broadcaster = new VehicleStatusBroadcaster();
        Field field = VehicleStatusBroadcaster.class.getDeclaredField("emitters");
        field.setAccessible(true);
        emitters = (List<SseEmitter>) field.get(broadcaster);
    }

    @Nested
    class broadcast {

        @Test
        void sendsEventToAllRegisteredEmitters() throws Exception {
            SseEmitter emitter1 = mock(SseEmitter.class);
            SseEmitter emitter2 = mock(SseEmitter.class);
            emitters.add(emitter1);
            emitters.add(emitter2);

            broadcaster.broadcast("WBA12345678901234", "RESERVED");

            then(emitter1).should().send((SseEmitter.SseEventBuilder) any());
            then(emitter2).should().send((SseEmitter.SseEventBuilder) any());
        }

        @Test
        void doesNothingWhenNoEmittersRegistered() {
            assertThatCode(() -> broadcaster.broadcast("WBA12345678901234", "RESERVED"))
                    .doesNotThrowAnyException();
        }

        @Test
        void doesNotSendWhenNoEmittersRegistered() {
            SseEmitter neverCalled = mock(SseEmitter.class);

            broadcaster.broadcast("WBA12345678901234", "RESERVED");

            verifyNoInteractions(neverCalled);
        }

        @Test
        void removesEmitterWhenSendThrowsIOException() throws Exception {
            SseEmitter failingEmitter = mock(SseEmitter.class);
            emitters.add(failingEmitter);
            willThrow(IOException.class).given(failingEmitter).send((SseEmitter.SseEventBuilder) any());

            broadcaster.broadcast("WBA12345678901234", "RESERVED");

            assertThat(emitters).doesNotContain(failingEmitter);
        }

        @Test
        void continuesSendingToRemainingEmittersWhenOneFails() throws Exception {
            SseEmitter failingEmitter = mock(SseEmitter.class);
            SseEmitter workingEmitter = mock(SseEmitter.class);
            emitters.add(failingEmitter);
            emitters.add(workingEmitter);
            willThrow(IOException.class).given(failingEmitter).send((SseEmitter.SseEventBuilder) any());

            broadcaster.broadcast("WBA12345678901234", "RESERVED");

            then(workingEmitter).should().send((SseEmitter.SseEventBuilder) any());
        }
    }

    @Nested
    class register {

        @Test
        void returnsNonNullSseEmitter() {
            SseEmitter emitter = broadcaster.register();

            assertThat(emitter).isNotNull();
        }

        @Test
        void addsEmitterToRegisteredList() {
            broadcaster.register();

            assertThat(emitters).hasSize(1);
        }

        @Test
        void registersMultipleEmittersIndependently() {
            broadcaster.register();
            broadcaster.register();

            assertThat(emitters).hasSize(2);
        }
    }
}
