package com.hacksecure.messenger.data.repository;

import com.hacksecure.messenger.data.remote.ServerConfig;
import com.hacksecure.messenger.data.remote.api.RelayApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class GhostRepositoryImpl_Factory implements Factory<GhostRepositoryImpl> {
  private final Provider<RelayApi> relayApiProvider;

  private final Provider<ServerConfig> serverConfigProvider;

  public GhostRepositoryImpl_Factory(Provider<RelayApi> relayApiProvider,
      Provider<ServerConfig> serverConfigProvider) {
    this.relayApiProvider = relayApiProvider;
    this.serverConfigProvider = serverConfigProvider;
  }

  @Override
  public GhostRepositoryImpl get() {
    return newInstance(relayApiProvider.get(), serverConfigProvider.get());
  }

  public static GhostRepositoryImpl_Factory create(Provider<RelayApi> relayApiProvider,
      Provider<ServerConfig> serverConfigProvider) {
    return new GhostRepositoryImpl_Factory(relayApiProvider, serverConfigProvider);
  }

  public static GhostRepositoryImpl newInstance(RelayApi relayApi, ServerConfig serverConfig) {
    return new GhostRepositoryImpl(relayApi, serverConfig);
  }
}
