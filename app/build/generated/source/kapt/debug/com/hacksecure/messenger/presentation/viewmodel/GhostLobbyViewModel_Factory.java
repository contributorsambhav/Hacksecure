package com.hacksecure.messenger.presentation.viewmodel;

import com.hacksecure.messenger.data.remote.GhostConnectionManager;
import com.hacksecure.messenger.domain.repository.GhostRepository;
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
public final class GhostLobbyViewModel_Factory implements Factory<GhostLobbyViewModel> {
  private final Provider<GhostRepository> ghostRepositoryProvider;

  private final Provider<GhostConnectionManager> ghostConnectionManagerProvider;

  public GhostLobbyViewModel_Factory(Provider<GhostRepository> ghostRepositoryProvider,
      Provider<GhostConnectionManager> ghostConnectionManagerProvider) {
    this.ghostRepositoryProvider = ghostRepositoryProvider;
    this.ghostConnectionManagerProvider = ghostConnectionManagerProvider;
  }

  @Override
  public GhostLobbyViewModel get() {
    return newInstance(ghostRepositoryProvider.get(), ghostConnectionManagerProvider.get());
  }

  public static GhostLobbyViewModel_Factory create(
      Provider<GhostRepository> ghostRepositoryProvider,
      Provider<GhostConnectionManager> ghostConnectionManagerProvider) {
    return new GhostLobbyViewModel_Factory(ghostRepositoryProvider, ghostConnectionManagerProvider);
  }

  public static GhostLobbyViewModel newInstance(GhostRepository ghostRepository,
      GhostConnectionManager ghostConnectionManager) {
    return new GhostLobbyViewModel(ghostRepository, ghostConnectionManager);
  }
}
