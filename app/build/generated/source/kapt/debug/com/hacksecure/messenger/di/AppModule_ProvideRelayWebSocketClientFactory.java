package com.hacksecure.messenger.di;

import com.hacksecure.messenger.data.remote.websocket.RelayWebSocketClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideRelayWebSocketClientFactory implements Factory<RelayWebSocketClient> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  public AppModule_ProvideRelayWebSocketClientFactory(Provider<OkHttpClient> okHttpClientProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public RelayWebSocketClient get() {
    return provideRelayWebSocketClient(okHttpClientProvider.get());
  }

  public static AppModule_ProvideRelayWebSocketClientFactory create(
      Provider<OkHttpClient> okHttpClientProvider) {
    return new AppModule_ProvideRelayWebSocketClientFactory(okHttpClientProvider);
  }

  public static RelayWebSocketClient provideRelayWebSocketClient(OkHttpClient okHttpClient) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRelayWebSocketClient(okHttpClient));
  }
}
