
//#pragma pack(1)
//typedef struct
//{
//	BYTE bPINEncKeyType;			//Type of key used to encrypt online PIN.
//	BYTE pbPINEncKeyIndex[2];	//Index of the key used to encrypt the online PIN. The value range is 0x01~0xFE.
//	BYTE bWorkingKeyAlg;		// Applicable to X9_24_2017 specification, IPEK is AES type. For other cases,     //it needs to be set to 0.
//	BYTE bPINBlockFormat;			//PIN Block format for encrypting online PIN.
//	BYTE bMagTransServiceCodeProcess;	//When the service code of the magnetic stripe card is "2" or "6", set //whether the magnetic stripe card transaction continues to be executed. 0: Continue to //execute the magnetic stripe card transaction. 1: Return error code : //ERR_USE_IC_INTERFACE = 0x0A;
//	BYTE pbPINPolicy[2];				//PIN input policy,defined by bit.
//			//bit0: Whether to support PinBypass.
//			//If it is 1, it means that PinBypass is supported. When entering 0 length PIN and pressing OK, it returns ERR_PIN_BYPASS;
//			//If the bit value is 0, it means that PinBypass is not supported. When a 0-length PIN is entered, the processing method is determined by the bit1 policy.
//			//bit1: When PinBypass is not supported, for 0-length PIN: 0-Does not respond to the button; 1-Returns ERR_PIN_LENGTH_ERR.
//			//bit2: When the PIN length is not 0 but less than the minimum length: 0-Does not respond to the button; 1-Returns ERR_PIN_LENGTH_ERR.
//			//bit3: In the case of fixed PIN length, if the PIN digits have reached the fixed length: 0-not processed; 1-automatically start verification.
//			//bit4: Whether the input is allowed to exceed the maximum length: 0- not allowed; 1- report an error (the last PIN value is not fed back to the App).
//	BYTE pbPINLen[2];				//Indicate the PIN data length range, the high byte indicates the minimum limit, such as 4, and the low byte indicates the maximum limit, such as 12;
//	BYTE bMAGTransOnlinePIN;		//Indicate whether to enter the online PIN for the magnetic stripe transaction. 0: not required 1: required
//}TRANSACTION_OPTIONS;
//#pragma pack()

//替换IN TRANSACTION_OPTIONS *pstTransactionOptions
//IN BYTE bPINEncKeyType,IN BYTE pbPINEncKeyIndex[2],IN BYTE bWorkingKeyAlg,IN BYTE bPINBlockFormat,IN BYTE bMagTransServiceCodeProcess,IN BYTE pbPINPolicy[2],IN BYTE pbPINLen[2],IN BYTE bMAGTransOnlinePIN


DWORD Initialize(void);
DWORD Finalize(void);
DWORD DeleteAllAppParameters(IN DWORD dwMode);
DWORD AddDRL(IN BYTE *pbTlvSet, IN DWORD dwTlvSetLen);
DWORD DeleteDRL(IN BYTE *pbProgramId, IN BYTE bProgramIdLen);
DWORD StartTransaction(IN BYTE *pbTlvTransData, IN DWORD dwTlvTransDataLen, OUT BYTE *pbOutData, IN OUT DWORD *pdwOutDataLen, IN DWORD dwFlag, IN TRANSACTION_OPTIONS *pstTransactionOptions); 
DWORD ContinueTransaction(IN BYTE *pbTlvTransData, IN DWORD dwTlvTransDataLen, OUT BYTE *pbOutData, IN OUT DWORD *pdwOutDataLen, IN DWORD dwFlag, IN TRANSACTION_OPTIONS *pstTransactionOptions);

