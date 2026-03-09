package com.hacksecure.messenger.presentation.viewmodel;

import com.hacksecure.messenger.data.remote.ServerConfig;
import com.hacksecure.messenger.data.remote.api.RelayApi;
import com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient;
import com.hacksecure.messenger.domain.crypto.HandshakeManager;
import com.hacksecure.messenger.domain.crypto.IdentityKeyManager;
import com.hacksecure.messenger.domain.crypto.SessionKeyManager;
import com.hacksecure.messenger.domain.repository.ContactRepository;
import com.hacksecure.messenger.domain.repository.IdentityRepository;
import com.hacksecure.messenger.domain.repository.MessageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class ChatViewModel_Factory implements Factory<ChatViewModel> {
  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<ContactRepository> contactRepositoryProvider;

  private final Provider<IdentityRepository> identityRepositoryProvider;

  private final Provider<IdentityKeyManager> identityKeyManagerProvider;

  private final Provider<SessionKeyManager> sessionKeyManagerProvider;

  private final Provider<HandshakeManager> handshakeManagerProvider;

  private final Provider<RelayWebSocketClient> relayWebSocketClientProvider;

  private final Provider<RelayApi> relayApiProvider;

  private final Provider<ServerConfig> serverConfigProvider;

  public ChatViewModel_Factory(Provider<MessageRepository> messageRepositoryProvider,
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<IdentityRepository> identityRepositoryProvider,
      Provider<IdentityKeyManager> identityKeyManagerProvider,
      Provider<SessionKeyManager> sessionKeyManagerProvider,
      Provider<HandshakeManager> handshakeManagerProvider,
      Provider<RelayWebSocketClient> relayWebSocketClientProvider,
      Provider<RelayApi> relayApiProvider, Provider<ServerConfig> serverConfigProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.contactRepositoryProvider = contactRepositoryProvider;
    this.identityRepositoryProvider = identityRepositoryProvider;
    this.identityKeyManagerProvider = identityKeyManagerProvider;
    this.sessionKeyManagerProvider = sessionKeyManagerProvider;
    this.handshakeManagerProvider = handshakeManagerProvider;
    this.relayWebSocketClientProvider = relayWebSocketClientProvider;
    this.relayApiProvider = relayApiProvider;
    this.serverConfigProvider = serverConfigProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(messageRepositoryProvider.get(), contactRepositoryProvider.get(), identityRepositoryProvider.get(), identityKeyManagerProvider.get(), sessionKeyManagerProvider.get(), handshakeManagerProvider.get(), relayWebSocketClientProvider.get(), relayApiProvider.get(), serverConfigProvider.get());
  }

  public static ChatViewModel_Factory create(Provider<MessageRepository> messageRepositoryProvider,
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<IdentityRepository> identityRepositoryProvider,
      Provider<IdentityKeyManager> identityKeyManagerProvider,
      Provider<SessionKeyManager> sessionKeyManagerProvider,
      Provider<HandshakeManager> handshakeManagerProvider,
      Provider<RelayWebSocketClient> relayWebSocketClientProvider,
      Provider<RelayApi> relayApiProvider, Provider<ServerConfig> serverConfigProvider) {
    return new ChatViewModel_Factory(messageRepositoryProvider, contactRepositoryProvider, identityRepositoryProvider, identityKeyManagerProvider, sessionKeyManagerProvider, handshakeManagerProvider, relayWebSocketClientProvider, relayApiProvider, serverConfigProvider);
  }

  public static ChatViewModel newInstance(MessageRepository messageRepository,
      ContactRepository contactRepository, IdentityRepository identityRepository,
      IdentityKeyManager identityKeyManager, SessionKeyManager sessionKeyManager,
      HandshakeManager handshakeManager, RelayWebSocketClient relayWebSocketClient,
      RelayApi relayApi, ServerConfig serverConfig) {
    return new ChatViewModel(messageRepository, contactRepository, identityRepository, identityKeyManager, sessionKeyManager, handshakeManager, relayWebSocketClient, relayApi, serverConfig);
  }
}
