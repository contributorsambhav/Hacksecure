package com.hacksecure.messenger.domain.crypto;

import com.hacksecure.messenger.domain.model.CryptoError;
import java.security.MessageDigest;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manages the ephemeral X25519 Diffie-Hellman handshake protocol.
 *
 * Both peers independently send a HANDSHAKE_OFFER when they connect to the relay.
 * On receiving the peer's offer, each side derives the shared session key and the
 * encrypted conversation can begin.
 *
 * ── Handshake offer wire format (97 bytes) ──────────────────────────────────
 *  [1]    magic = 0x01  — distinguishes from WirePacket (which always starts with 0x00,
 *                         since its first 4 bytes encode headerLen whose high byte is 0x00
 *                         for any realistic JSON header < 16 million bytes)
 *  [32]   ephemeral X25519 public key (generated fresh per WebSocket connection)
 *  [64]   Ed25519 signature of SHA-256(bytes[0..32])
 *
 * ── Session key derivation ───────────────────────────────────────────────────
 *  shared_secret = X25519(our_eph_priv, peer_eph_pub)   // symmetric: A⊗B == B⊗A
 *  (id_min, id_max) = sorted-by-hex(my_identity_hash, peer_identity_hash)
 *  session_key = SHA-256(shared_secret ‖ id_min ‖ id_max)
 *
 * Sorting the identity hashes ensures both sides compute the identical key
 * regardless of who sends their offer first.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0007\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\bJ.\u0010\u000b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\tJ\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/hacksecure/messenger/domain/crypto/HandshakeManager;", "", "identityKeyManager", "Lcom/hacksecure/messenger/domain/crypto/IdentityKeyManager;", "sessionKeyManager", "Lcom/hacksecure/messenger/domain/crypto/SessionKeyManager;", "(Lcom/hacksecure/messenger/domain/crypto/IdentityKeyManager;Lcom/hacksecure/messenger/domain/crypto/SessionKeyManager;)V", "createOffer", "Lkotlin/Pair;", "", "Lcom/hacksecure/messenger/domain/crypto/SessionKeyManager$EphemeralKeyPair;", "deriveSessionKey", "offerBytes", "peerIdentityPublicKey", "ourEphemeralPair", "myIdentityHash", "peerIdentityHash", "isHandshakeOffer", "", "packetBytes", "Companion", "app_debug"})
public final class HandshakeManager {
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.crypto.IdentityKeyManager identityKeyManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.hacksecure.messenger.domain.crypto.SessionKeyManager sessionKeyManager = null;
    
    /**
     * First byte of every HANDSHAKE_OFFER — cannot appear as the high byte of a
     * WirePacket headerLen for any header < 16,777,216 bytes (impossible in practice).
     */
    public static final byte PACKET_TYPE = (byte)1;
    
    /**
     * Exact byte count of a HANDSHAKE_OFFER: 1 + 32 + 64
     */
    public static final int OFFER_SIZE = 97;
    @org.jetbrains.annotations.NotNull()
    public static final com.hacksecure.messenger.domain.crypto.HandshakeManager.Companion Companion = null;
    
    @javax.inject.Inject()
    public HandshakeManager(@org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.crypto.IdentityKeyManager identityKeyManager, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.crypto.SessionKeyManager sessionKeyManager) {
        super();
    }
    
    /**
     * Generates a fresh ephemeral X25519 keypair, signs the offer, and returns
     * the serialized 97-byte packet together with the keypair.
     *
     * Call exactly once per new WebSocket connection.
     * Hold the returned [EphemeralKeyPair] until [deriveSessionKey] is called.
     *
     * @return Pair of (serialized offer bytes, ephemeral keypair)
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlin.Pair<byte[], com.hacksecure.messenger.domain.crypto.SessionKeyManager.EphemeralKeyPair> createOffer() {
        return null;
    }
    
    /**
     * Returns true if [packetBytes] has the correct size and magic byte for a
     * HANDSHAKE_OFFER, distinguishing it from a [MessageProcessor] wire packet.
     */
    public final boolean isHandshakeOffer(@org.jetbrains.annotations.NotNull()
    byte[] packetBytes) {
        return false;
    }
    
    /**
     * Verifies a received HANDSHAKE_OFFER and derives the shared 32-byte session key.
     *
     * @param offerBytes           the 97-byte offer received over the relay
     * @param peerIdentityPublicKey peer's Ed25519 public key (32 bytes, from [Contact])
     * @param ourEphemeralPair     our ephemeral keypair from [createOffer] — its private
     *                            key bytes are zeroized inside this call
     * @param myIdentityHash       our 32-byte SHA-256 identity hash
     * @param peerIdentityHash     peer's 32-byte SHA-256 identity hash
     * @return derived 32-byte session key — CALLER must pass it to [onSessionEstablished],
     *        which zeroizes it after creating the hash ratchets
     * @throws CryptoError.SignatureInvalid if the Ed25519 offer signature is invalid
     */
    @org.jetbrains.annotations.NotNull()
    public final byte[] deriveSessionKey(@org.jetbrains.annotations.NotNull()
    byte[] offerBytes, @org.jetbrains.annotations.NotNull()
    byte[] peerIdentityPublicKey, @org.jetbrains.annotations.NotNull()
    com.hacksecure.messenger.domain.crypto.SessionKeyManager.EphemeralKeyPair ourEphemeralPair, @org.jetbrains.annotations.NotNull()
    byte[] myIdentityHash, @org.jetbrains.annotations.NotNull()
    byte[] peerIdentityHash) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0005\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/hacksecure/messenger/domain/crypto/HandshakeManager$Companion;", "", "()V", "OFFER_SIZE", "", "PACKET_TYPE", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}