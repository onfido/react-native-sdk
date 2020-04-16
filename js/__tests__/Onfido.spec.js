import { OnfidoCountryCode, OnfidoCaptureType, OnfidoDocumentType } from "../config_constants";
import Onfido from "../Onfido";

// add mock
jest.mock('../../node_modules/react-native/Libraries/BatchedBridge/NativeModules', () => {
  return {
    OnfidoSdk: {
      start: jest.fn().mockReturnValue(Promise.resolve()),
    },
  };
});

const RESOLVED = 'resolved';
const REJECTED = 'rejected';

const start = (config) => {
  return Onfido.start(config)
    .then(() => {
      return RESOLVED
    })
    .catch(() => {
      return REJECTED
    });
};

const flowSteps = {
  welcome: true,
  captureDocument: {
    docType: OnfidoDocumentType.DRIVING_LICENCE,
    countryCode: OnfidoCountryCode.GBR
  },
  captureFace: {
    type: OnfidoCaptureType.VIDEO
  },
};

const baseConfig = {
  sdkToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c", // Test JWT from jwt.io, non-sensitive
  flowSteps
};

// Valid Configuration Tests
test('resolve with a detailed valid configuration', () => {
  return start({...baseConfig}).then(result => expect(result).toBe(RESOLVED))
});

test('resolve an empty captureDocument object', () => {
  return start({...baseConfig, flowSteps: {...flowSteps, captureDocument: {}}}).then(result => expect(result).toBe(RESOLVED))
});

test('resolve a incorrect config attribute type', () => {
  return start({...baseConfig, flowSteps: {...flowSteps, captureDocument: true}}).then(result => expect(result).toBe(RESOLVED))
});

test('resolve a capture document object with explicitly null attributes', () => {
  return start({...baseConfig, flowSteps: {...flowSteps, captureDocument: {docType: null, countryCode: null}}}).then(result => expect(result).toBe(RESOLVED))
});

// Invalid Configuration Tests
test('reject a null config object', () => {
  return start(null).then(result => expect(result).toBe(REJECTED))
});

test('reject a null flowSteps object', () => {
 return start({...baseConfig, flowSteps: null}).then(result => expect(result).toBe(REJECTED))
});

test('reject a null sdkToken value', () => {
  return start({...baseConfig, sdkToken: null}).then(result => expect(result).toBe(REJECTED))
});

test('reject with docType specified but no countryCode', () => {
  return start({...baseConfig, flowSteps: {...flowSteps, captureDocument: {docType: OnfidoDocumentType.DRIVING_LICENCE}}}).then(result => expect(result).toBe(REJECTED))
});

test('reject with countryCode specified but no docType', () => {
  return start({...baseConfig, flowSteps: {...flowSteps, captureDocument: {countryCode: OnfidoCountryCode.GBR}}}).then(result => expect(result).toBe(REJECTED))
});

test('reject with an invalid docType', () => {
  return start({...baseConfig, flowSteps: {...flowSteps, captureDocument: {docType: OnfidoCountryCode.GBR}}}).then(result => expect(result).toBe(REJECTED))
});

test('reject with an invalid countryCode', () => {
  return start({...baseConfig, flowSteps: {...flowSteps, captureDocument: {docType: OnfidoDocumentType.DRIVING_LICENCE}}}).then(result => expect(result).toBe(REJECTED))
});

test('reject with an invalid capture type', () => {
  return start({...baseConfig, flowSteps: {...flowSteps, captureFace: OnfidoDocumentType.DRIVING_LICENCE}}).then(result => expect(result).toBe(REJECTED))
});

test('reject with an empty captureDocument and captureFace', () => {
  return start({...baseConfig, flowSteps: {...flowSteps, captureDocument: {}, captureFace: {}}}).then(result => expect(result).toBe(REJECTED))
});
