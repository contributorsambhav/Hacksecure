package com.hacksecure.messenger;

import androidx.hilt.work.HiltWorkerFactory;
import com.hacksecure.messenger.data.remote.BackgroundConnectionManager;
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

  private final Provider<BackgroundConnectionManager> backgroundConnectionManagerProvider;

  public HackSecureApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<BackgroundConnectionManager> backgroundConnectionManagerProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.backgroundConnectionManagerProvider = backgroundConnectionManagerProvider;
  }

  public static MembersInjector<HackSecureApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<BackgroundConnectionManager> backgroundConnectionManagerProvider) {
    return new HackSecureApp_MembersInjector(workerFactoryProvider, backgroundConnectionManagerProvider);
  }

  @Override
  public void injectMembers(HackSecureApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectBackgroundConnectionManager(instance, backgroundConnectionManagerProvider.get());
  }

  @InjectedFieldSignature("com.hacksecure.messenger.HackSecureApp.workerFactory")
  public static void injectWorkerFactory(HackSecureApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.hacksecure.messenger.HackSecureApp.backgroundConnectionManager")
  public static void injectBackgroundConnectionManager(HackSecureApp instance,
      BackgroundConnectionManager backgroundConnectionManager) {
    instance.backgroundConnectionManager = backgroundConnectionManager;
  }
}
