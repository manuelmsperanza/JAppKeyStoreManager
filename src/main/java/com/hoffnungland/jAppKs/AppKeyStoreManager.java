package com.hoffnungland.jAppKs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppKeyStoreManager {
	
	private static final Logger logger = LogManager.getLogger(AppKeyStoreManager.class);
	
	private KeyStore ks;
	
	public void init(String keyStorePath, String passwordKs) {
		this.ks = KeyStore.getInstance("pkcs12");
		
		try (FileInputStream fis = new FileInputStream(keyStorePath)) {
			this.ks.load(fis, passwordKs.toCharArray());
		}
	}
	
	public void writePasswordToKeyStore(KeyStore keyStore, String keyStorePath, String keyStorePassword, String passwordPassword, String alias, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException, CertificateException, IOException{

        KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(passwordPassword.toCharArray());

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(password.toCharArray(), RandomStringUtils.randomAlphanumeric(24).getBytes(), 13));

        keyStore.setEntry(alias, new KeyStore.SecretKeyEntry(generatedSecret), keyStorePP);

        FileOutputStream outputStream = new FileOutputStream(new File(keyStorePath));
        keyStore.store(outputStream, keyStorePassword.toCharArray());
    }
	
	public String readPasswordFromKeyStore(KeyStore keyStore, String passwordPassword, String passwordAlias) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, InvalidKeySpecException{

        KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(passwordPassword.toCharArray());

        KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry)keyStore.getEntry(passwordAlias, keyStorePP);
        if(ske == null) {
        	System.err.println("Password for " + passwordAlias + " does not exist");
        	return null;
        }
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        PBEKeySpec keySpec = (PBEKeySpec)factory.getKeySpec(ske.getSecretKey(), PBEKeySpec.class);

        return new String(keySpec.getPassword());
    }
	
	
	
}
