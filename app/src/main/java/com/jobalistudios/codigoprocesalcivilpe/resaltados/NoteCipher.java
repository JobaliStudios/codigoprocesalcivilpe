package com.jobalistudios.codigoprocesalcivilpe.resaltados;

import java.security.GeneralSecurityException;

interface NoteCipher {

    EncryptedNote encrypt(String highlightId, String plaintext) throws GeneralSecurityException;

    String decrypt(String highlightId, EncryptedNote encryptedNote) throws GeneralSecurityException;

    final class EncryptedNote {
        final String ciphertext;
        final String iv;

        EncryptedNote(String ciphertext, String iv) {
            this.ciphertext = ciphertext;
            this.iv = iv;
        }
    }
}
