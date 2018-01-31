/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.protocol.serializer;

import de.rub.nds.modifiablevariable.ModifiableVariableProperty;
import de.rub.nds.modifiablevariable.bytearray.ModifiableByteArray;
import de.rub.nds.modifiablevariable.integer.ModifiableInteger;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.constants.ssl.SSL2ByteLength;
import de.rub.nds.tlsattacker.core.protocol.message.SSL2ClientHelloMessage;
import de.rub.nds.tlsattacker.core.protocol.message.SSL2ClientMasterKeyMessage;

public class SSL2ClientMasterKeySerializer extends ProtocolMessageSerializer {

    private final SSL2ClientMasterKeyMessage msg;

    public SSL2ClientMasterKeySerializer(SSL2ClientMasterKeyMessage message, ProtocolVersion version) {
        super(message, version);
        this.msg = message;
    }

    @Override
    public byte[] serializeProtocolMessageContent() {
        LOGGER.debug("Serializing SSL2ClientMasterKey");
        writeMessageLength(msg);
        writeType(msg);
        writeCipherKind(msg);
        writeClearKeyLength(msg);
        writeEncryptedKeyLength(msg);
        writeKeyArgLength(msg);
        writeClearKeyData(msg);
        writeEncryptedKeyData(msg);
        return getAlreadySerialized();
    }

    private void writeEncryptedKeyData(SSL2ClientMasterKeyMessage msg) {
        byte[] encryptedKeyDataValue = msg.getEncryptedKeyData().getValue();
        appendBytes(encryptedKeyDataValue);
        // TODO logger.
    }

    private void writeClearKeyData(SSL2ClientMasterKeyMessage msg) {
        byte[] clearKeyDataValue = msg.getClearKeyData().getValue();
        appendBytes(clearKeyDataValue);
        // TODO logger.
    }

    private void writeEncryptedKeyLength(SSL2ClientMasterKeyMessage msg) {
        int length = msg.getEncryptedKeyLength().getValue();
        appendInt(length, SSL2ByteLength.ENCRYPTED_KEY_LENGTH);
        LOGGER.debug("EncryptedKeyLength: " + length);
    }

    private void writeKeyArgLength(SSL2ClientMasterKeyMessage msg) {
        int length = msg.getKeyArgLength().getValue();
        appendInt(length, SSL2ByteLength.ENCRYPTED_KEY_LENGTH);
        LOGGER.debug("EncryptedKeyLength: " + length);
    }

    private void writeClearKeyLength(SSL2ClientMasterKeyMessage msg) {
        int length = msg.getClearKeyLength().getValue();
        appendInt(length, SSL2ByteLength.CLEAR_KEY_LENGTH);
        LOGGER.debug("ClearKeyLength: " + length);
    }

    // TODO: Consider de-duplicating vs. SSL2ClientHelloSerializer.
    // TODO: Consider treating the weird length encoding as serializing, not
    // preparing.
    private void writeMessageLength(SSL2ClientMasterKeyMessage msg) {
        appendInt(msg.getMessageLength().getValue(), SSL2ByteLength.LENGTH);
        LOGGER.debug("MessageLength: " + msg.getMessageLength().getValue());
    }

    /**
     * Writes the Type of the SSL2ClientHello into the final byte[]
     */
    private void writeType(SSL2ClientMasterKeyMessage msg) {
        appendByte(msg.getType().getValue());
        LOGGER.debug("Type: " + msg.getType().getValue());
    }

    private void writeCipherKind(SSL2ClientMasterKeyMessage msg) {
        byte[] cipherKindValue = msg.getCipherKind().getValue();
        appendBytes(cipherKindValue);
        LOGGER.debug("CipherKind: " + cipherKindValue);
    }
}
