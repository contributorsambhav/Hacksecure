package com.hacksecure.messenger.presentation.viewmodel;

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

  public ChatViewModel_Factory(Provider<MessageRepository> messageRepositoryProvider,
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<IdentityRepository> identityRepositoryProvider,
      Provider<IdentityKeyManager> identityKeyManagerProvider,
      Provider<SessionKeyManager> sessionKeyManagerProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.contactRepositoryProvider = contactRepositoryProvider;
    this.identityRepositoryProvider = identityRepositoryProvider;
    this.identityKeyManagerProvider = identityKeyManagerProvider;
    this.sessionKeyManagerProvider = sessionKeyManagerProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(messageRepositoryProvider.get(), contactRepositoryProvider.get(), identityRepositoryProvider.get(), identityKeyManagerProvider.get(), sessionKeyManagerProvider.get());
  }

  public static ChatViewModel_Factory create(Provider<MessageRepository> messageRepositoryProvider,
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<IdentityRepository> identityRepositoryProvider,
      Provider<IdentityKeyManager> identityKeyManagerProvider,
      Provider<SessionKeyManager> sessionKeyManagerProvider) {
    return new ChatViewModel_Factory(messageRepositoryProvider, contactRepositoryProvider, identityRepositoryProvider, identityKeyManagerProvider, sessionKeyManagerProvider);
  }

  public static ChatViewModel newInstance(MessageRepository messageRepository,
      ContactRepository contactRepository, IdentityRepository identityRepository,
      IdentityKeyManager identityKeyManager, SessionKeyManager sessionKeyManager) {
    return new ChatViewModel(messageRepository, contactRepository, identityRepository, identityKeyManager, sessionKeyManager);
  }
}
