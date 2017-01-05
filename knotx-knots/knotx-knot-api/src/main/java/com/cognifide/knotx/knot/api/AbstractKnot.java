/*
 * Knot.x - Reactive microservice assembler - Knot API
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.knotx.knot.api;

import com.cognifide.knotx.dataobjects.KnotContext;
import com.cognifide.knotx.fragments.Fragment;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import rx.Observable;

/**
 * Abstract class that should be root for all custom knots
 */
public abstract class AbstractKnot<C extends KnotConfiguration> extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKnot.class);

  protected C configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.configuration = initConfiguration(config());
  }

  @Override
  public void start() throws Exception {
    LOGGER.debug("Starting <{}>", this.getClass().getName());

    Observable<Message<KnotContext>> observable = vertx.eventBus().<KnotContext>consumer(
        configuration.getAddress()).toObservable();
    observable
        .doOnNext(this::traceMessage)
        .subscribe(
            msg -> {
              if (shouldProcess(msg)) {
                process(msg.body())
                    .subscribe(
                        msg::reply,
                        error -> {
                          LOGGER
                              .error("Error occurred in " + this.getClass().getName() + ".", error);
                          msg.reply(processError(msg.body(), error));
                        });
              } else {
                msg.reply(msg.body());
              }
            }
        );
  }

  protected abstract rx.Observable<KnotContext> process(KnotContext message);

  protected abstract boolean shouldProcess(Set<String> fragmentsIdentifiers);

  protected abstract KnotContext processError(KnotContext knotContext, Throwable error);

  protected abstract C initConfiguration(JsonObject config);

  protected boolean shouldProcess(Message<KnotContext> msg) {
    Optional<List<Fragment>> fragments = msg.body().fragments();
    Set<String> allFragmentsIdentifiers = getAllFragmentsIdentifiers(fragments);
    return shouldProcess(allFragmentsIdentifiers);
  }

  private Set<String> getAllFragmentsIdentifiers(Optional<List<Fragment>> fragments) {
    return fragments.map(f ->
        f.stream()
            .map(Fragment::identifiers)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet())
    ).orElse(Collections.emptySet());
  }

  private void traceMessage(Message<KnotContext> message) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Got message from <{}> with value <{}>", message.replyAddress(), message.body());
    }
  }

}
