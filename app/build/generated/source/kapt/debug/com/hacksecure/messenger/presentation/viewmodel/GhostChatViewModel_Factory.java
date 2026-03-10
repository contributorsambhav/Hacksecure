package com.hacksecure.messenger.presentation.viewmodel;

import com.hacksecure.messenger.data.remote.GhostConnectionManager;
import com.hacksecure.messenger.domain.crypto.IdentityKeyManager;
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
public final class GhostChatViewModel_Factory implements Factory<GhostChatViewModel> {
  private final Provider<GhostConnectionManager> ghostConnectionManagerProvider;

  private final Provider<IdentityKeyManager> identityKeyManagerProvider;

  public GhostChatViewModel_Factory(Provider<GhostConnectionManager> ghostConnectionManagerProvider,
      Provider<IdentityKeyManager> identityKeyManagerProvider) {
    this.ghostConnectionManagerProvider = ghostConnectionManagerProvider;
    this.identityKeyManagerProvider = identityKeyManagerProvider;
  }

  @Override
  public GhostChatViewModel get() {
    return newInstance(ghostConnectionManagerProvider.get(), identityKeyManagerProvider.get());
  }

  public static GhostChatViewModel_Factory create(
      Provider<GhostConnectionManager> ghostConnectionManagerProvider,
      Provider<IdentityKeyManager> identityKeyManagerProvider) {
    return new GhostChatViewModel_Factory(ghostConnectionManagerProvider, identityKeyManagerProvider);
  }

  public static GhostChatViewModel newInstance(GhostConnectionManager ghostConnectionManager,
      IdentityKeyManager identityKeyManager) {
    return new GhostChatViewModel(ghostConnectionManager, identityKeyManager);
  }
}
