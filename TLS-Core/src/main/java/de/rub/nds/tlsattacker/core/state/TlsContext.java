/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.state;

import de.rub.nds.modifiablevariable.HoldsModifiableVariable;
import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.constants.ClientCertificateType;
import de.rub.nds.tlsattacker.core.constants.CompressionMethod;
import de.rub.nds.tlsattacker.core.constants.ECPointFormat;
import de.rub.nds.tlsattacker.core.constants.HeartbeatMode;
import de.rub.nds.tlsattacker.core.constants.MaxFragmentLength;
import de.rub.nds.tlsattacker.core.constants.NamedCurve;
import de.rub.nds.tlsattacker.core.constants.PRFAlgorithm;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.constants.SignatureAndHashAlgorithm;
import de.rub.nds.tlsattacker.core.constants.TokenBindingKeyParameters;
import de.rub.nds.tlsattacker.core.constants.TokenBindingVersion;
import de.rub.nds.tlsattacker.core.crypto.MessageDigestCollector;
import de.rub.nds.tlsattacker.core.crypto.ec.CustomECPoint;
import de.rub.nds.tlsattacker.core.protocol.message.extension.KS.KSEntry;
import de.rub.nds.tlsattacker.core.protocol.message.extension.SNI.SNIEntry;
import de.rub.nds.tlsattacker.core.record.layer.RecordLayer;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.core.workflow.chooser.Chooser;
import de.rub.nds.tlsattacker.core.workflow.chooser.ChooserFactory;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import de.rub.nds.tlsattacker.transport.TransportHandler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import org.bouncycastle.crypto.tls.Certificate;

/**
 *
 * @author Juraj Somorovsky <juraj.somorovsky@rub.de>
 * @author Philip Riese <philip.riese@rub.de>
 * @author Matthias Terlinde <matthias.terlinde@rub.de>
 * @author Nurullah Erinola <nurullah.erinola@rub.de>
 */
public class TlsContext {

    /**
     * TlsConfig which contains the configurations for everything TLS-Attacker
     * related
     */
    private Config config;
    /**
     * shared key established during the handshake
     */
    private byte[] handshakeSecret;
    /**
     * shared key established during the handshake
     */
    private byte[] clientHandshakeTrafficSecret;
    /**
     * shared key established during the handshake
     */
    private byte[] serverHandshakeTrafficSecret;
    /**
     * shared key established during the handshake
     */
    private byte[] clientApplicationTrafficSecret0;
    /**
     * shared key established during the handshake
     */
    private byte[] serverApplicationTrafficSecret0;
    /**
     * master secret established during the handshake
     */
    private byte[] masterSecret;
    /**
     * premaster secret established during the handshake
     */
    private byte[] preMasterSecret;

    /**
     * client random, including unix time /** client random, including unix time
     */
    private byte[] clientRandom;

    /**
     * server random, including unix time
     */
    private byte[] serverRandom;

    /**
     * selected cipher suite
     */
    private CipherSuite selectedCipherSuite = null;

    /**
     * compression algorithm
     */
    private CompressionMethod selectedCompressionMethod;

    /**
     * server session ID
     */
    private byte[] serverSessionId;

    /**
     * client session ID
     */
    private byte[] clientSessionId;

    /**
     * server certificate parsed from the server certificate message
     */
    private Certificate serverCertificate;

    /**
     * client certificate parsed from the client certificate message
     */
    private Certificate clientCertificate;

    /**
     * workflow trace containing all the messages exchanged during the
     * communication
     */
    @HoldsModifiableVariable
    private WorkflowTrace workflowTrace;

    private MessageDigestCollector digest;

    private RecordLayer recordLayer;

    private TransportHandler transportHandler;

    private ConnectionEndType talkingConnectionEndType = ConnectionEndType.CLIENT;

    private byte[] dtlsCookie;

    private ProtocolVersion selectedProtocolVersion;

    private ProtocolVersion highestClientProtocolVersion;

    private List<CipherSuite> clientSupportedCiphersuites;

    private List<CompressionMethod> clientSupportedCompressions;

    private List<SignatureAndHashAlgorithm> serverSupportedSignatureAndHashAlgorithms;

    private List<SignatureAndHashAlgorithm> clientSupportedSignatureAndHashAlgorithms;

    private HeartbeatMode heartbeatMode;

    private MaxFragmentLength maxFragmentLength;

    private SignatureAndHashAlgorithm selectedSigHashAlgorithm;

    /**
     * These are the padding bytes as used in the padding extension.
     */
    private byte[] paddingExtensionBytes;

    /**
     * This is the session ticket of the SessionTicketTLS extension.
     */
    private byte[] sessionTicketTLS;

    /**
     * Is the extended master secret extension present?
     */
    private boolean receivedMasterSecretExtension;

    /**
     * This is the renegotiation info of the RenegotiationInfo extension.
     */
    private byte[] renegotiationInfo;
    /**
     * This is the requestContext from the CertificateRequest messsage in TLS
     * 1.3
     */
    private byte[] certificateRequestContext;
    /**
     * This is the timestamp of the SignedCertificateTimestamp extension
     */
    private byte[] signedCertificateTimestamp;

    private BigInteger dhGenerator;

    private BigInteger dhModulus;

    private BigInteger serverDhPrivateKey;

    private BigInteger serverDhPublicKey;

    private BigInteger clientDhPrivateKey;

    private BigInteger clientDhPublicKey;

    private NamedCurve selectedCurve;

    private CustomECPoint clientEcPublicKey;

    private CustomECPoint serverEcPublicKey;

    private BigInteger serverEcPrivateKey;

    private BigInteger clientEcPrivateKey;

    private BigInteger rsaModulus;

    private BigInteger serverRSAPublicKey;

    private BigInteger clientRSAPublicKey;

    private BigInteger serverRSAPrivateKey;

    private BigInteger clientRSAPrivateKey;

    private List<NamedCurve> clientNamedCurvesList;

    private List<ECPointFormat> clientPointFormatsList;

    private List<ECPointFormat> serverPointFormatsList;

    private boolean receivedFatalAlert = false;

    private boolean encryptActive = false;
    /**
     * TLS 1.3, update keys for application data
     */
    private boolean updateKeys = false;

    private List<ClientCertificateType> clientCertificateTypes;

    private byte[] distinguishedNames;

    private ProtocolVersion lastRecordVersion;

    private List<SNIEntry> clientSNIEntryList;

    private List<KSEntry> clientKSEntryList;

    private KSEntry serverKSEntry;

    private int sequenceNumber = 0;

    /**
     * supported protocol versions
     */
    private List<ProtocolVersion> clientSupportedProtocolVersions;

    private TokenBindingVersion tokenBindingVersion;

    private List<TokenBindingKeyParameters> tokenBindingKeyParameters;

    private SignatureAndHashAlgorithm selectedSignatureAndHashAlgorithm;

    private PRFAlgorithm prfAlgorithm;

    @XmlTransient
    private Chooser chooser;

    public TlsContext() {
        this(Config.createConfig());
    }

    public TlsContext(Config config) {
        digest = new MessageDigestCollector();
        this.config = config;
        // init lastRecordVersion for records
        clientCertificateTypes = new LinkedList<>();
        lastRecordVersion = config.getHighestProtocolVersion();
        selectedProtocolVersion = config.getHighestProtocolVersion();
    }

    public Chooser getChooser() {
        if (chooser == null) {
            chooser = ChooserFactory.getChooser(config.getChooserType(), this);
        }
        return chooser;
    }

    public List<ProtocolVersion> getClientSupportedProtocolVersions() {
        return clientSupportedProtocolVersions;
    }

    public void setClientSupportedProtocolVersions(List<ProtocolVersion> clientSupportedProtocolVersions) {
        this.clientSupportedProtocolVersions = clientSupportedProtocolVersions;
    }

    public void setClientSupportedProtocolVersions(ProtocolVersion... clientSupportedProtocolVersions) {
        this.clientSupportedProtocolVersions = Arrays.asList(clientSupportedProtocolVersions);
    }

    public BigInteger getRsaModulus() {
        return rsaModulus;
    }

    public void setRsaModulus(BigInteger rsaModulus) {
        this.rsaModulus = rsaModulus;
    }

    public BigInteger getServerRSAPublicKey() {
        return serverRSAPublicKey;
    }

    public void setServerRSAPublicKey(BigInteger serverRSAPublicKey) {
        this.serverRSAPublicKey = serverRSAPublicKey;
    }

    public BigInteger getClientRSAPublicKey() {
        return clientRSAPublicKey;
    }

    public void setClientRSAPublicKey(BigInteger clientRSAPublicKey) {
        this.clientRSAPublicKey = clientRSAPublicKey;
    }

    public BigInteger getServerEcPrivateKey() {
        return serverEcPrivateKey;
    }

    public void setServerEcPrivateKey(BigInteger serverEcPrivateKey) {
        this.serverEcPrivateKey = serverEcPrivateKey;
    }

    public BigInteger getClientEcPrivateKey() {
        return clientEcPrivateKey;
    }

    public void setClientEcPrivateKey(BigInteger clientEcPrivateKey) {
        this.clientEcPrivateKey = clientEcPrivateKey;
    }

    public NamedCurve getSelectedCurve() {
        return selectedCurve;
    }

    public void setSelectedCurve(NamedCurve selectedCurve) {
        this.selectedCurve = selectedCurve;
    }

    public CustomECPoint getClientEcPublicKey() {
        return clientEcPublicKey;
    }

    public void setClientEcPublicKey(CustomECPoint clientEcPublicKey) {
        this.clientEcPublicKey = clientEcPublicKey;
    }

    public CustomECPoint getServerEcPublicKey() {
        return serverEcPublicKey;
    }

    public void setServerEcPublicKey(CustomECPoint serverEcPublicKey) {
        this.serverEcPublicKey = serverEcPublicKey;
    }

    public BigInteger getDhGenerator() {
        return dhGenerator;
    }

    public void setDhGenerator(BigInteger dhGenerator) {
        this.dhGenerator = dhGenerator;
    }

    public BigInteger getDhModulus() {
        return dhModulus;
    }

    public void setDhModulus(BigInteger dhModulus) {
        this.dhModulus = dhModulus;
    }

    public BigInteger getServerDhPublicKey() {
        return serverDhPublicKey;
    }

    public void setServerDhPublicKey(BigInteger serverDhPublicKey) {
        this.serverDhPublicKey = serverDhPublicKey;
    }

    public BigInteger getClientDhPrivateKey() {
        return clientDhPrivateKey;
    }

    public void setClientDhPrivateKey(BigInteger clientDhPrivateKey) {
        this.clientDhPrivateKey = clientDhPrivateKey;
    }

    public BigInteger getClientDhPublicKey() {
        return clientDhPublicKey;
    }

    public void setClientDhPublicKey(BigInteger clientDhPublicKey) {
        this.clientDhPublicKey = clientDhPublicKey;
    }

    public BigInteger getServerDhPrivateKey() {
        return serverDhPrivateKey;
    }

    public void setServerDhPrivateKey(BigInteger serverDhPrivateKey) {
        this.serverDhPrivateKey = serverDhPrivateKey;
    }

    public SignatureAndHashAlgorithm getSelectedSignatureAndHashAlgorithm() {
        return selectedSignatureAndHashAlgorithm;
    }

    public void setSelectedSignatureAndHashAlgorithm(SignatureAndHashAlgorithm selectedSignatureAndHashAlgorithm) {
        this.selectedSignatureAndHashAlgorithm = selectedSignatureAndHashAlgorithm;
    }

    public List<NamedCurve> getClientNamedCurvesList() {
        return clientNamedCurvesList;
    }

    public void setClientNamedCurvesList(List<NamedCurve> clientNamedCurvesList) {
        this.clientNamedCurvesList = clientNamedCurvesList;
    }

    public void setClientNamedCurvesList(NamedCurve... clientNamedCurvesList) {
        this.clientNamedCurvesList = Arrays.asList(clientNamedCurvesList);
    }

    public List<ECPointFormat> getServerPointFormatsList() {
        return serverPointFormatsList;
    }

    public void setServerPointFormatsList(List<ECPointFormat> serverPointFormatsList) {
        this.serverPointFormatsList = serverPointFormatsList;
    }

    public void setServerPointFormatsList(ECPointFormat... serverPointFormatsList) {
        this.serverPointFormatsList = Arrays.asList(serverPointFormatsList);
    }

    public List<SignatureAndHashAlgorithm> getClientSupportedSignatureAndHashAlgorithms() {
        return clientSupportedSignatureAndHashAlgorithms;
    }

    public void setClientSupportedSignatureAndHashAlgorithms(
            List<SignatureAndHashAlgorithm> clientSupportedSignatureAndHashAlgorithms) {
        this.clientSupportedSignatureAndHashAlgorithms = clientSupportedSignatureAndHashAlgorithms;
    }

    public void setClientSupportedSignatureAndHashAlgorithms(
            SignatureAndHashAlgorithm... clientSupportedSignatureAndHashAlgorithms) {
        this.clientSupportedSignatureAndHashAlgorithms = Arrays.asList(clientSupportedSignatureAndHashAlgorithms);
    }

    public List<SNIEntry> getClientSNIEntryList() {
        return clientSNIEntryList;
    }

    public void setClientSNIEntryList(List<SNIEntry> clientSNIEntryList) {
        this.clientSNIEntryList = clientSNIEntryList;
    }

    public void setClientSNIEntryList(SNIEntry... clientSNIEntryList) {
        this.clientSNIEntryList = Arrays.asList(clientSNIEntryList);
    }

    public ProtocolVersion getLastRecordVersion() {
        return lastRecordVersion;
    }

    public void setLastRecordVersion(ProtocolVersion lastRecordVersion) {
        this.lastRecordVersion = lastRecordVersion;
    }

    public byte[] getDistinguishedNames() {
        return distinguishedNames;
    }

    public void setDistinguishedNames(byte[] distinguishedNames) {
        this.distinguishedNames = distinguishedNames;
    }

    public List<ClientCertificateType> getClientCertificateTypes() {
        return clientCertificateTypes;
    }

    public void setClientCertificateTypes(List<ClientCertificateType> clientCertificateTypes) {
        this.clientCertificateTypes = clientCertificateTypes;
    }

    public void setClientCertificateTypes(ClientCertificateType... clientCertificateTypes) {
        this.clientCertificateTypes = Arrays.asList(clientCertificateTypes);
    }

    public boolean isReceivedFatalAlert() {
        return receivedFatalAlert;
    }

    public void setReceivedFatalAlert(boolean receivedFatalAlert) {
        this.receivedFatalAlert = receivedFatalAlert;
    }

    public boolean isEncryptActive() {
        return encryptActive;
    }

    public void setEncryptActive(boolean encryptActive) {
        this.encryptActive = encryptActive;
    }

    public boolean isUpdateKeys() {
        return updateKeys;
    }

    public void setUpdateKeys(boolean updateKeys) {
        this.updateKeys = updateKeys;
    }

    public List<ECPointFormat> getClientPointFormatsList() {
        return clientPointFormatsList;
    }

    public void setClientPointFormatsList(List<ECPointFormat> clientPointFormatsList) {
        this.clientPointFormatsList = clientPointFormatsList;
    }

    public void setClientPointFormatsList(ECPointFormat... clientPointFormatsList) {
        this.clientPointFormatsList = Arrays.asList(clientPointFormatsList);
    }

    public SignatureAndHashAlgorithm getSelectedSigHashAlgorithm() {
        return selectedSigHashAlgorithm;
    }

    public void setSelectedSigHashAlgorithm(SignatureAndHashAlgorithm selectedSigHashAlgorithm) {
        this.selectedSigHashAlgorithm = selectedSigHashAlgorithm;
    }

    public MaxFragmentLength getMaxFragmentLength() {
        return maxFragmentLength;
    }

    public void setMaxFragmentLength(MaxFragmentLength maxFragmentLength) {
        this.maxFragmentLength = maxFragmentLength;
    }

    public HeartbeatMode getHeartbeatMode() {
        return heartbeatMode;
    }

    public void setHeartbeatMode(HeartbeatMode heartbeatMode) {
        this.heartbeatMode = heartbeatMode;
    }

    public byte[] getPaddingExtensionBytes() {
        return paddingExtensionBytes;
    }

    public void setPaddingExtensionBytes(byte[] paddingExtensionBytes) {
        this.paddingExtensionBytes = paddingExtensionBytes;
    }

    public boolean isExtendedMasterSecretExtension() {
        return receivedMasterSecretExtension;
    }

    public void setReceivedMasterSecretExtension(boolean receivedMasterSecretExtension) {
        this.receivedMasterSecretExtension = receivedMasterSecretExtension;
    }

    public List<CompressionMethod> getClientSupportedCompressions() {
        return clientSupportedCompressions;
    }

    public void setClientSupportedCompressions(List<CompressionMethod> clientSupportedCompressions) {
        this.clientSupportedCompressions = clientSupportedCompressions;
    }

    public void setClientSupportedCompressions(CompressionMethod... clientSupportedCompressions) {
        this.clientSupportedCompressions = Arrays.asList(clientSupportedCompressions);
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public List<CipherSuite> getClientSupportedCiphersuites() {
        return clientSupportedCiphersuites;
    }

    public void setClientSupportedCiphersuites(List<CipherSuite> clientSupportedCiphersuites) {
        this.clientSupportedCiphersuites = clientSupportedCiphersuites;
    }

    public void setClientSupportedCiphersuites(CipherSuite... clientSupportedCiphersuites) {
        this.clientSupportedCiphersuites = Arrays.asList(clientSupportedCiphersuites);
    }

    public List<SignatureAndHashAlgorithm> getServerSupportedSignatureAndHashAlgorithms() {
        return serverSupportedSignatureAndHashAlgorithms;
    }

    public void setServerSupportedSignatureAndHashAlgorithms(
            List<SignatureAndHashAlgorithm> serverSupportedSignatureAndHashAlgorithms) {
        this.serverSupportedSignatureAndHashAlgorithms = serverSupportedSignatureAndHashAlgorithms;
    }

    public void setServerSupportedSignatureAndHashAlgorithms(
            SignatureAndHashAlgorithm... serverSupportedSignatureAndHashAlgorithms) {
        this.serverSupportedSignatureAndHashAlgorithms = Arrays.asList(serverSupportedSignatureAndHashAlgorithms);
    }

    public ProtocolVersion getSelectedProtocolVersion() {
        return selectedProtocolVersion;
    }

    public void setSelectedProtocolVersion(ProtocolVersion selectedProtocolVersion) {
        this.selectedProtocolVersion = selectedProtocolVersion;
    }

    public ProtocolVersion getHighestClientProtocolVersion() {
        return highestClientProtocolVersion;
    }

    public void setHighestClientProtocolVersion(ProtocolVersion highestClientProtocolVersion) {
        this.highestClientProtocolVersion = highestClientProtocolVersion;
    }

    public ConnectionEndType getTalkingConnectionEndType() {
        return talkingConnectionEndType;
    }

    public void setTalkingConnectionEndType(ConnectionEndType talkingConnectionEndType) {
        this.talkingConnectionEndType = talkingConnectionEndType;
    }

    public Config getConfig() {
        return config;
    }

    public byte[] getMasterSecret() {
        return masterSecret;
    }

    public CipherSuite getSelectedCipherSuite() {
        return selectedCipherSuite;
    }

    public void setMasterSecret(byte[] masterSecret) {
        this.masterSecret = masterSecret;
    }

    public void setSelectedCipherSuite(CipherSuite selectedCipherSuite) {
        this.selectedCipherSuite = selectedCipherSuite;
    }

    public byte[] getClientServerRandom() {
        return ArrayConverter.concatenate(clientRandom, serverRandom);
    }

    public byte[] getPreMasterSecret() {
        return preMasterSecret;
    }

    public void setPreMasterSecret(byte[] preMasterSecret) {
        this.preMasterSecret = preMasterSecret;
    }

    public byte[] getClientRandom() {
        return clientRandom;
    }

    public void setClientRandom(byte[] clientRandom) {
        this.clientRandom = clientRandom;
    }

    public byte[] getServerRandom() {
        return serverRandom;
    }

    public void setServerRandom(byte[] serverRandom) {
        this.serverRandom = serverRandom;
    }

    public CompressionMethod getSelectedCompressionMethod() {
        return selectedCompressionMethod;
    }

    public void setSelectedCompressionMethod(CompressionMethod selectedCompressionMethod) {
        this.selectedCompressionMethod = selectedCompressionMethod;
    }

    public byte[] getServerSessionId() {
        return serverSessionId;
    }

    public void setServerSessionId(byte[] serverSessionId) {
        this.serverSessionId = serverSessionId;
    }

    public byte[] getClientSessionId() {
        return clientSessionId;
    }

    public void setClientSessionId(byte[] clientSessionId) {
        this.clientSessionId = clientSessionId;
    }

    public WorkflowTrace getWorkflowTrace() {
        return workflowTrace;
    }

    public void setWorkflowTrace(WorkflowTrace workflowTrace) {
        this.workflowTrace = workflowTrace;
    }

    public Certificate getServerCertificate() {
        return serverCertificate;
    }

    public void setServerCertificate(Certificate serverCertificate) {
        this.serverCertificate = serverCertificate;
    }

    public Certificate getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(Certificate clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public MessageDigestCollector getDigest() {
        return digest;
    }

    public byte[] getDtlsCookie() {
        return dtlsCookie;
    }

    public void setDtlsCookie(byte[] dtlsCookie) {
        this.dtlsCookie = dtlsCookie;
    }

    public TransportHandler getTransportHandler() {
        return transportHandler;
    }

    public void setTransportHandler(TransportHandler transportHandler) {
        this.transportHandler = transportHandler;
    }

    public RecordLayer getRecordLayer() {
        return recordLayer;
    }

    public void setRecordLayer(RecordLayer recordLayer) {
        this.recordLayer = recordLayer;
    }

    public PRFAlgorithm getPrfAlgorithm() {
        return prfAlgorithm;
    }

    public void setPrfAlgorithm(PRFAlgorithm prfAlgorithm) {
        this.prfAlgorithm = prfAlgorithm;
    }

    public byte[] getClientHandshakeTrafficSecret() {
        return clientHandshakeTrafficSecret;
    }

    public void setClientHandshakeTrafficSecret(byte[] clientHandshakeTrafficSecret) {
        this.clientHandshakeTrafficSecret = clientHandshakeTrafficSecret;
    }

    public byte[] getServerHandshakeTrafficSecret() {
        return serverHandshakeTrafficSecret;
    }

    public void setServerHandshakeTrafficSecret(byte[] serverHandshakeTrafficSecret) {
        this.serverHandshakeTrafficSecret = serverHandshakeTrafficSecret;
    }

    public byte[] getClientApplicationTrafficSecret0() {
        return clientApplicationTrafficSecret0;
    }

    public void setClientApplicationTrafficSecret0(byte[] clientApplicationTrafficSecret0) {
        this.clientApplicationTrafficSecret0 = clientApplicationTrafficSecret0;
    }

    public byte[] getServerApplicationTrafficSecret0() {
        return serverApplicationTrafficSecret0;
    }

    public void setServerApplicationTrafficSecret0(byte[] serverApplicationTrafficSecret0) {
        this.serverApplicationTrafficSecret0 = serverApplicationTrafficSecret0;
    }

    public byte[] getHandshakeSecret() {
        return handshakeSecret;
    }

    public void setHandshakeSecret(byte[] handshakeSecret) {
        this.handshakeSecret = handshakeSecret;
    }

    public List<KSEntry> getClientKSEntryList() {
        return clientKSEntryList;
    }

    public void setClientKSEntryList(List<KSEntry> clientKSEntryList) {
        this.clientKSEntryList = clientKSEntryList;
    }

    public void setClientKSEntryList(KSEntry... clientKSEntryList) {
        this.clientKSEntryList = Arrays.asList(clientKSEntryList);
    }

    public KSEntry getServerKSEntry() {
        return serverKSEntry;
    }

    public void setServerKSEntry(KSEntry serverKSEntry) {
        this.serverKSEntry = serverKSEntry;
    }

    public byte[] getSessionTicketTLS() {
        return sessionTicketTLS;
    }

    public void setSessionTicketTLS(byte[] sessionTicketTLS) {
        this.sessionTicketTLS = sessionTicketTLS;
    }

    public byte[] getSignedCertificateTimestamp() {
        return signedCertificateTimestamp;
    }

    public void setSignedCertificateTimestamp(byte[] signedCertificateTimestamp) {
        this.signedCertificateTimestamp = signedCertificateTimestamp;
    }

    public byte[] getRenegotiationInfo() {
        return renegotiationInfo;
    }

    public void setRenegotiationInfo(byte[] renegotiationInfo) {
        this.renegotiationInfo = renegotiationInfo;
    }

    public TokenBindingVersion getTokenBindingVersion() {
        return tokenBindingVersion;
    }

    public void setTokenBindingVersion(TokenBindingVersion tokenBindingVersion) {
        this.tokenBindingVersion = tokenBindingVersion;
    }

    public List<TokenBindingKeyParameters> getTokenBindingKeyParameters() {
        return tokenBindingKeyParameters;
    }

    public void setTokenBindingKeyParameters(List<TokenBindingKeyParameters> tokenBindingKeyParameters) {
        this.tokenBindingKeyParameters = tokenBindingKeyParameters;
    }

    public void setTokenBindingKeyParameters(TokenBindingKeyParameters... tokenBindingKeyParameters) {
        this.tokenBindingKeyParameters = Arrays.asList(tokenBindingKeyParameters);
    }

    public byte[] getCertificateRequestContext() {
        return certificateRequestContext;
    }

    public void setCertificateRequestContext(byte[] certificateRequestContext) {
        this.certificateRequestContext = certificateRequestContext;
    }

    public BigInteger getServerRSAPrivateKey() {
        return serverRSAPrivateKey;
    }

    public void setServerRSAPrivateKey(BigInteger serverRSAPrivateKey) {
        this.serverRSAPrivateKey = serverRSAPrivateKey;
    }

    public BigInteger getClientRSAPrivateKey() {
        return clientRSAPrivateKey;
    }

    public void setClientRSAPrivateKey(BigInteger clientRSAPrivateKey) {
        this.clientRSAPrivateKey = clientRSAPrivateKey;
    }
}
