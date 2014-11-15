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

package org.landa.rempi.server.web.controller.client;

import io.pallas.core.annotations.Controller;
import io.pallas.core.controller.BaseController;
import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.execution.Redirect;
import io.pallas.core.execution.Response;
import io.pallas.core.execution.Result;
import io.pallas.core.view.View;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;

import org.apache.commons.io.IOUtils;
import org.landa.rempi.comm.impl.CaptureCommand;
import org.landa.rempi.comm.livestream.StartStreamCommand;
import org.landa.rempi.comm.livestream.StopStreamCommand;
import org.landa.rempi.server.io.RempiServer;
import org.landa.rempi.server.io.comm.Promise;
import org.landa.rempi.server.io.livestream.LiveStreamReceiverServer;

@Controller("client")
public class ClientController extends BaseController {

    @Inject
    private RempiServer rempiServer;

    @Inject
    private LiveStreamReceiverServer liveStreamReceiverServer;

    public View index() {
        return view().set("clients", rempiServer.getClients());
    }

    public Result json() {
        final Map<String, Object> clients = new HashMap<String, Object>();
        for (final String clientId : rempiServer.getClients().keySet()) {
            clients.put(clientId, new HashMap<>());
        }

        return json(clients);
    }

    public io.pallas.core.execution.Result capture(@QueryParam("clientId") final String clientId) {

        System.out.println("Capture " + clientId);
        final Promise<Object> syncCommand = rempiServer.sendSyncCommand(clientId, new CaptureCommand());

        syncCommand.waitForComplete();
        System.out.println("Done");
        if (syncCommand.isSucceeded()) {

            try {
                final byte[] originalImage = (byte[]) syncCommand.get();

                System.out.println("Sync result");

                return new Response() {

                    @Override
                    public void render(final HttpServletResponse response) {
                        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP
                                                                                                    // 1.1.
                        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
                        response.setDateHeader("Expires", 0); // Proxies.

                        response.setContentType("image/jpeg");
                        try {
                            IOUtils.write(originalImage, response.getOutputStream());
                            response.flushBuffer();

                        } catch (final IOException e) {
                            e.printStackTrace();
                        }

                    }
                };

            } catch (final Exception exception) {
                throw new InternalServerErrorException(exception.getMessage());
            }

        } else {
            return redirect("/");
        }
    }

    public Redirect multicast(@QueryParam("command") final String command) {
        rempiServer.broadcast(command);
        return redirect("/");
    }

    public void startStream(@QueryParam("clientId") final String clientId) {
        rempiServer.sendCommand(clientId, new StartStreamCommand(liveStreamReceiverServer.getPort()));
    }

    public void stopStream(@QueryParam("clientId") final String clientId) {
        rempiServer.sendCommand(clientId, new StopStreamCommand());
    }

    public void stopClient(@QueryParam("clientId") final String clientId) {
        rempiServer.disconnet(clientId);
    }
}
