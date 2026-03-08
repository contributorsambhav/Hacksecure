package com.hacksecure.messenger;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class HackSecureApp_MembersInjector implements MembersInjector<HackSecureApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public HackSecureApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<HackSecureApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new HackSecureApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(HackSecureApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.hacksecure.messenger.HackSecureApp.workerFactory")
  public static void injectWorkerFactory(HackSecureApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
