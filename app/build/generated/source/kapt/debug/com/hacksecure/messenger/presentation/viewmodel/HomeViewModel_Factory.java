package com.hacksecure.messenger.presentation.viewmodel;

import com.hacksecure.messenger.domain.repository.ContactRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<ContactRepository> contactRepositoryProvider;

  public HomeViewModel_Factory(Provider<ContactRepository> contactRepositoryProvider) {
    this.contactRepositoryProvider = contactRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(contactRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<ContactRepository> contactRepositoryProvider) {
    return new HomeViewModel_Factory(contactRepositoryProvider);
  }

  public static HomeViewModel newInstance(ContactRepository contactRepository) {
    return new HomeViewModel(contactRepository);
  }
}
