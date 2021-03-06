/*
 * Copyright (c) 2010-2020. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.eventhandling;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.List;

import static org.axonframework.utils.EventTestUtils.createEvent;
import static org.axonframework.utils.EventTestUtils.createEvents;
import static org.mockito.Mockito.*;

/**
 * Test class validating the {@link SimpleEventHandlerInvoker}.
 *
 * @author Rene de Waele
 */
class SimpleEventHandlerInvokerTest {

    private static final Object NO_RESET_PAYLOAD = null;

    private EventMessageHandler mockHandler1;
    private EventMessageHandler mockHandler2;

    private SimpleEventHandlerInvoker testSubject;

    @BeforeEach
    void setUp() {
        mockHandler1 = mock(EventMessageHandler.class);
        mockHandler2 = mock(EventMessageHandler.class);
        testSubject = SimpleEventHandlerInvoker.builder()
                                               .eventHandlers("test", mockHandler1, mockHandler2)
                                               .build();
    }

    @Test
    void testSingleEventPublication() throws Exception {
        EventMessage<?> event = createEvent();

        testSubject.handle(event, Segment.ROOT_SEGMENT);

        InOrder inOrder = inOrder(mockHandler1, mockHandler2);
        inOrder.verify(mockHandler1).handle(event);
        inOrder.verify(mockHandler2).handle(event);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testRepeatedEventPublication() throws Exception {
        List<? extends EventMessage<?>> events = createEvents(2);

        for (EventMessage<?> event : events) {
            testSubject.handle(event, Segment.ROOT_SEGMENT);
        }

        InOrder inOrder = inOrder(mockHandler1, mockHandler2);
        inOrder.verify(mockHandler1).handle(events.get(0));
        inOrder.verify(mockHandler2).handle(events.get(0));
        inOrder.verify(mockHandler1).handle(events.get(1));
        inOrder.verify(mockHandler2).handle(events.get(1));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testPerformReset() {
        testSubject.performReset();

        verify(mockHandler1).prepareReset(NO_RESET_PAYLOAD);
        verify(mockHandler2).prepareReset(NO_RESET_PAYLOAD);
    }

    @Test
    void testPerformResetWithResetContext() {
        String resetContext = "reset-context";

        testSubject.performReset(resetContext);

        verify(mockHandler1).prepareReset(eq(resetContext));
        verify(mockHandler2).prepareReset(eq(resetContext));
    }
}
