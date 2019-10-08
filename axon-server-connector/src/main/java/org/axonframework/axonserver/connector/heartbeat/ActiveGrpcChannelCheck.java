package org.axonframework.axonserver.connector.heartbeat;

import org.axonframework.axonserver.connector.AxonServerConnectionManager;

/**
 * {@link ConnectionSanityCheck} implementation that verifies if
 * the gRPC channel between client and Axon Server is connected.
 *
 * @author Sara Pellegrini
 * @since 4.2.1
 */
public class ActiveGrpcChannelCheck implements ConnectionSanityCheck {

    private final AxonServerConnectionManager connectionManager;
    private final String context;

    /**
     * Constructs an {@link ActiveGrpcChannelCheck} based on the connection manager.
     *
     * @param connectionManager the {@link AxonServerConnectionManager}
     * @param context           the context for which is verified the AxonServer connection through the gRPC channel
     */
    public ActiveGrpcChannelCheck(AxonServerConnectionManager connectionManager, String context) {
        this.connectionManager = connectionManager;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Detects if exists a gRPC channel between the client an Axon Server.
     *
     * @return true if the gRPC channel is opened, false otherwise
     */
    @Override
    public boolean isValid() {
        return connectionManager.isConnected(context);
    }
}
