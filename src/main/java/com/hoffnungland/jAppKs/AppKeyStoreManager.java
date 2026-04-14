package com.hoffnungland.jAppKs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides read/write operations for a PKCS12 keystore used by the app.
 */
public class AppKeyStoreManager {
	
	private static final Logger logger = LogManager.getLogger(AppKeyStoreManager.class);
	
	private String keyStorePath;
	private String passwordKs;
	private KeyStore ks;
	
	
	/**
	 * Creates a keystore manager.
	 *
	 * @param keyStorePath path to the keystore file
	 * @param passwordKs keystore password
	 */
	public AppKeyStoreManager(String keyStorePath, String passwordKs) {
		super();
		this.keyStorePath = keyStorePath;
		this.passwordKs = passwordKs;
	}

	/**
	 * Loads the keystore from disk.
	 *
	 * @throws KeyStoreException if the keystore type is unavailable
	 * @throws FileNotFoundException if the file is missing
	 * @throws IOException if the keystore cannot be read
	 * @throws NoSuchAlgorithmException if the integrity algorithm is unavailable
	 * @throws CertificateException if certificates cannot be parsed
	 */
	public void init() throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException {
		logger.traceEntry();
		this.ks = KeyStore.getInstance("pkcs12");
		
		try (FileInputStream fis = new FileInputStream(this.keyStorePath)) {
			this.ks.load(fis, this.passwordKs.toCharArray());
		}
		logger.traceExit();
	}
	
	/**
	 * Stores a secret value in the keystore under the provided alias.
	 *
	 * @param entryAlias alias to write
	 * @param entryValue secret value
	 * @return generated entry password required for retrieval
	 * @throws NoSuchAlgorithmException if the cipher algorithm is unavailable
	 * @throws InvalidKeySpecException if key material generation fails
	 * @throws KeyStoreException if writing to keystore fails
	 * @throws CertificateException if keystore serialization fails
	 * @throws IOException if persisting the keystore fails
	 */
	public String writePasswordToKeyStore(String entryAlias, String entryValue) throws NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException, CertificateException, IOException{
		logger.traceEntry();
		String entryPassword = RandomStringUtils.secureStrong().nextAlphabetic(12);
        KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(entryPassword.toCharArray());

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(entryValue.toCharArray(), RandomStringUtils.secureStrong().nextAlphabetic(24).getBytes(), 13));

        this.ks.setEntry(entryAlias, new KeyStore.SecretKeyEntry(generatedSecret), keyStorePP);

        FileOutputStream outputStream = new FileOutputStream(new File(this.keyStorePath));
        this.ks.store(outputStream, this.passwordKs.toCharArray());
        logger.traceExit();
        return entryPassword;
    }
	
	/**
	 * Reads a stored secret value.
	 *
	 * @param entryAlias alias to resolve
	 * @param entryPassword password created for the stored entry
	 * @return decrypted value or {@code null} if alias/password are invalid
	 * @throws NoSuchAlgorithmException if the cipher algorithm is unavailable
	 * @throws UnrecoverableEntryException if the entry cannot be recovered
	 * @throws KeyStoreException if keystore access fails
	 * @throws InvalidKeySpecException if conversion to character data fails
	 */
	public String readPasswordFromKeyStore(String entryAlias, String entryPassword) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, InvalidKeySpecException{
		logger.traceEntry();
        KeyStore.PasswordProtection keyStorePP = new KeyStore.PasswordProtection(entryPassword.toCharArray());
        
        KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry)this.ks.getEntry(entryAlias, keyStorePP);
        if(ske == null) {
        	System.err.println("Password for " + entryAlias + " does not exist");
        	return null;
        }
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        PBEKeySpec keySpec = (PBEKeySpec)factory.getKeySpec(ske.getSecretKey(), PBEKeySpec.class);
        
        logger.traceExit();
        return new String(keySpec.getPassword());
    }
	
	/**
	 * Lists all aliases stored in the keystore.
	 *
	 * @return alias enumeration
	 * @throws KeyStoreException if aliases cannot be read
	 */
	public Enumeration<String> listAliases() throws KeyStoreException {
		return this.ks.aliases();
	}
	
}
