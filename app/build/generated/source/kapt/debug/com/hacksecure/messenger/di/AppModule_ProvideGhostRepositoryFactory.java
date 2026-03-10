package com.hacksecure.messenger.di;

import com.hacksecure.messenger.data.remote.ServerConfig;
import com.hacksecure.messenger.data.remote.api.RelayApi;
import com.hacksecure.messenger.domain.repository.GhostRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideGhostRepositoryFactory implements Factory<GhostRepository> {
  private final Provider<RelayApi> relayApiProvider;

  private final Provider<ServerConfig> serverConfigProvider;

  public AppModule_ProvideGhostRepositoryFactory(Provider<RelayApi> relayApiProvider,
      Provider<ServerConfig> serverConfigProvider) {
    this.relayApiProvider = relayApiProvider;
    this.serverConfigProvider = serverConfigProvider;
  }

  @Override
  public GhostRepository get() {
    return provideGhostRepository(relayApiProvider.get(), serverConfigProvider.get());
  }

  public static AppModule_ProvideGhostRepositoryFactory create(Provider<RelayApi> relayApiProvider,
      Provider<ServerConfig> serverConfigProvider) {
    return new AppModule_ProvideGhostRepositoryFactory(relayApiProvider, serverConfigProvider);
  }

  public static GhostRepository provideGhostRepository(RelayApi relayApi,
      ServerConfig serverConfig) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideGhostRepository(relayApi, serverConfig));
  }
}
