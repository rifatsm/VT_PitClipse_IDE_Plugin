/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.runner.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

import org.pitest.pitclipse.runner.PitRequest;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.io.ObjectStreamSocket;
import org.pitest.pitclipse.runner.io.SocketProvider;

/**
 * <p>A client used to receive the parameters of a PIT analysis and to send the results.</p>
 * 
 * <p>This client is supposed to be launched from the background VM running PIT.</p>
 */
public class PitClient implements Closeable {

    private final int portNumber;
    private final SocketProvider socketProvider;
    private Optional<ObjectStreamSocket> socket = Optional.empty();

    public PitClient(int portNumber) {
        this(portNumber, new SocketProvider());
    }

    /**
     * Only used by tests
     * 
     * @param portNumber
     * @param socketProvider
     */
    PitClient(int portNumber, SocketProvider socketProvider) {
        this.portNumber = portNumber;
        this.socketProvider = socketProvider;
    }

    public void connect() {
        socket = socketProvider.connectTo(portNumber);
    }

    public void sendResults(PitResults results) {
        socket.ifPresent(objectStreamSocket -> objectStreamSocket.write(results));
    }

    public Optional<PitRequest> readRequest() {
        return socket.map(ObjectStreamSocket::read);
    }

    @Override
    public void close() throws IOException {
        if (socket.isPresent()) {
            socket.get().close();
        }
    }
}
