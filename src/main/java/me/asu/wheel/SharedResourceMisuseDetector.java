/*
 * Copyright (C) 2017 Bruce Asu<bruceasu@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *  　　
 * 　　The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package me.asu.wheel;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

/**
 * Warn when user creates too many instances to avoid {@link OutOfMemoryError}.
 */
@Slf4j
public class SharedResourceMisuseDetector {

    private static final int MAX_ACTIVE_INSTANCES = 256;
    private final Class<?> type;
    private final AtomicLong    activeInstances = new AtomicLong();
    private final AtomicBoolean logged          = new AtomicBoolean();

    public SharedResourceMisuseDetector(Class<?> type) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        this.type = type;
    }

    public void increase() {
        if (activeInstances.incrementAndGet() > MAX_ACTIVE_INSTANCES) {
            if (logged.compareAndSet(false, true)) {
                log.warn(
                        "You are creating too many " + type.getSimpleName() +
                                " instances.  " + type.getSimpleName() +
                                " is a shared resource that must be reused across the" +
                                " application, so that only a few instances are created.");
            }
        }
    }

    public void decrease() {
        activeInstances.decrementAndGet();
    }
}
