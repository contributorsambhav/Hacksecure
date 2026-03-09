package com.hacksecure.messenger.data.remote;

import com.hacksecure.messenger.data.remote.api.RelayApi;
import com.hacksecure.messenger.domain.crypto.HandshakeManager;
import com.hacksecure.messenger.domain.crypto.IdentityKeyManager;
import com.hacksecure.messenger.domain.repository.ContactRepository;
import com.hacksecure.messenger.domain.repository.IdentityRepository;
import com.hacksecure.messenger.domain.repository.MessageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
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
public final class BackgroundConnectionManager_Factory implements Factory<BackgroundConnectionManager> {
  private final Provider<ContactRepository> contactRepositoryProvider;

  private final Provider<IdentityRepository> identityRepositoryProvider;

  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<HandshakeManager> handshakeManagerProvider;

  private final Provider<IdentityKeyManager> identityKeyManagerProvider;

  private final Provider<RelayApi> relayApiProvider;

  private final Provider<ServerConfig> serverConfigProvider;

  private final Provider<OkHttpClient> okHttpClientProvider;

  public BackgroundConnectionManager_Factory(Provider<ContactRepository> contactRepositoryProvider,
      Provider<IdentityRepository> identityRepositoryProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<HandshakeManager> handshakeManagerProvider,
      Provider<IdentityKeyManager> identityKeyManagerProvider, Provider<RelayApi> relayApiProvider,
      Provider<ServerConfig> serverConfigProvider, Provider<OkHttpClient> okHttpClientProvider) {
    this.contactRepositoryProvider = contactRepositoryProvider;
    this.identityRepositoryProvider = identityRepositoryProvider;
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.handshakeManagerProvider = handshakeManagerProvider;
    this.identityKeyManagerProvider = identityKeyManagerProvider;
    this.relayApiProvider = relayApiProvider;
    this.serverConfigProvider = serverConfigProvider;
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public BackgroundConnectionManager get() {
    return newInstance(contactRepositoryProvider.get(), identityRepositoryProvider.get(), messageRepositoryProvider.get(), handshakeManagerProvider.get(), identityKeyManagerProvider.get(), relayApiProvider.get(), serverConfigProvider.get(), okHttpClientProvider.get());
  }

  public static BackgroundConnectionManager_Factory create(
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<IdentityRepository> identityRepositoryProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<HandshakeManager> handshakeManagerProvider,
      Provider<IdentityKeyManager> identityKeyManagerProvider, Provider<RelayApi> relayApiProvider,
      Provider<ServerConfig> serverConfigProvider, Provider<OkHttpClient> okHttpClientProvider) {
    return new BackgroundConnectionManager_Factory(contactRepositoryProvider, identityRepositoryProvider, messageRepositoryProvider, handshakeManagerProvider, identityKeyManagerProvider, relayApiProvider, serverConfigProvider, okHttpClientProvider);
  }

  public static BackgroundConnectionManager newInstance(ContactRepository contactRepository,
      IdentityRepository identityRepository, MessageRepository messageRepository,
      HandshakeManager handshakeManager, IdentityKeyManager identityKeyManager, RelayApi relayApi,
      ServerConfig serverConfig, OkHttpClient okHttpClient) {
    return new BackgroundConnectionManager(contactRepository, identityRepository, messageRepository, handshakeManager, identityKeyManager, relayApi, serverConfig, okHttpClient);
  }
}
