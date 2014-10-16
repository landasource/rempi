/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;

import org.landa.rempi.comm.impl.BlinkingCommand;
import org.landa.rempi.server.io.RempiServer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ApplicationController {

    @Inject
    private RempiServer rempiServer;

    public Result index() {

        return Results.html().render("clients", rempiServer.getClients());
    }

    public Result send(final Context context) {

        final String clientId = context.getParameter("clientId");
        final String command = context.getParameter("command");

        rempiServer.sendCommand(clientId, command);

        return Results.redirect("/");
    }

    public Result blink(@Param("clientId") final String clientId) {
        rempiServer.sendCommand(clientId, new BlinkingCommand());

        return Results.redirect("/");
    }

    public Result multicast(final Context context) {

        final String command = context.getParameter("command");

        rempiServer.broadcast(command);

        return Results.redirect("/");
    }

    public Result stopClient(@Param("clientId") final String clientId) {

        rempiServer.disconnet(clientId);

        return Results.redirect("/");
    }

    public Result helloWorldJson() {

        final SimplePojo simplePojo = new SimplePojo();
        simplePojo.content = "Hello World! Hello Json!";

        return Results.json().render(simplePojo);

    }

    public static class SimplePojo {

        public String content;

    }
}
