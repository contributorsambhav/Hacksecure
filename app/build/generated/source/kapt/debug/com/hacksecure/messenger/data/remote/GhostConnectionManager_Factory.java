package com.hacksecure.messenger.data.remote;

import com.hacksecure.messenger.domain.crypto.HandshakeManager;
import com.hacksecure.messenger.domain.crypto.IdentityKeyManager;
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
public final class GhostConnectionManager_Factory implements Factory<GhostConnectionManager> {
  private final Provider<HandshakeManager> handshakeManagerProvider;

  private final Provider<IdentityKeyManager> identityKeyManagerProvider;

  private final Provider<ServerConfig> serverConfigProvider;

  private final Provider<OkHttpClient> okHttpClientProvider;

  public GhostConnectionManager_Factory(Provider<HandshakeManager> handshakeManagerProvider,
      Provider<IdentityKeyManager> identityKeyManagerProvider,
      Provider<ServerConfig> serverConfigProvider, Provider<OkHttpClient> okHttpClientProvider) {
    this.handshakeManagerProvider = handshakeManagerProvider;
    this.identityKeyManagerProvider = identityKeyManagerProvider;
    this.serverConfigProvider = serverConfigProvider;
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public GhostConnectionManager get() {
    return newInstance(handshakeManagerProvider.get(), identityKeyManagerProvider.get(), serverConfigProvider.get(), okHttpClientProvider.get());
  }

  public static GhostConnectionManager_Factory create(
      Provider<HandshakeManager> handshakeManagerProvider,
      Provider<IdentityKeyManager> identityKeyManagerProvider,
      Provider<ServerConfig> serverConfigProvider, Provider<OkHttpClient> okHttpClientProvider) {
    return new GhostConnectionManager_Factory(handshakeManagerProvider, identityKeyManagerProvider, serverConfigProvider, okHttpClientProvider);
  }

  public static GhostConnectionManager newInstance(HandshakeManager handshakeManager,
      IdentityKeyManager identityKeyManager, ServerConfig serverConfig, OkHttpClient okHttpClient) {
    return new GhostConnectionManager(handshakeManager, identityKeyManager, serverConfig, okHttpClient);
  }
}
