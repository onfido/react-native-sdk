import { Platform } from "react-native";
import { OnfidoCountryCode, OnfidoCaptureType, OnfidoDocumentType, OnfidoConfig } from "../config_constants";
import Onfido from "../Onfido";

// add mock
jest.mock('../../node_modules/react-native/Libraries/BatchedBridge/NativeModules', () => {
  return {
    OnfidoSdk: {
      start: jest.fn().mockReturnValue(Promise.resolve()),
    },
  };
});

jest.mock('../../node_modules/react-native/Libraries/Utilities/Platform', () => {
  return {
    OS: "android",
  }
});

const RESOLVED = 'resolved';
const REJECTED = 'rejected';

const start = (config: OnfidoConfig) => {
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
  sdkToken: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c', // Test JWT from jwt.io, non-sensitive
  flowSteps
};

const workflowRunId = 'cf1b3300-71fa-494b-8a87-059463bf0c31';
const testCases: Array<'android' | 'ios'>  = ['android', 'ios'];

testCases.forEach((platform) => {
  describe(`Testing ${platform}`, () => {
    beforeEach(() => {
      Platform.OS = platform;
    });

    // Valid Configuration Tests
    test('resolve with a detailed valid configuration', () => {
      return start({ ...baseConfig }).then(result => expect(result).toBe(RESOLVED))
    });

    test('resolve an empty captureDocument object', () => {
      return start({ ...baseConfig, flowSteps: { ...flowSteps, captureDocument: {} } }).then(result => expect(result).toBe(RESOLVED))
    });

    test('resolve a incorrect config attribute type', () => {
      return start({ ...baseConfig, flowSteps: { ...flowSteps, captureDocument: true } } as unknown as OnfidoConfig).then(result => expect(result).toBe(RESOLVED))
    });

    test('resolve a capture document object with explicitly null attributes', () => {
      return start({ ...baseConfig, flowSteps: { ...flowSteps, captureDocument: { docType: null, countryCode: null } } } as unknown as OnfidoConfig).then(result => expect(result).toBe(RESOLVED))
    });
    
    test('resolve with a valid workflow runId', () => {
      return start({ ...baseConfig, workflowRunId: workflowRunId }).then(result => expect(result).toBe(RESOLVED))
    });

    test('resolve with a valid workflow runId without flow steps', () => {
      return start({ ...baseConfig, flowSteps: null, workflowRunId: workflowRunId }).then(result => expect(result).toBe(RESOLVED))
    });

    // Invalid Configuration Tests
    test('reject a null config object', () => {
      return start(null as unknown as OnfidoConfig).then(result => expect(result).toBe(REJECTED))
    });

    test('reject a null flowSteps object', () => {
      return start({ ...baseConfig, flowSteps: null } as unknown as OnfidoConfig).then(result => expect(result).toBe(REJECTED))
    });

    test('reject a null sdkToken value', () => {
      return start({ ...baseConfig, sdkToken: null } as unknown as OnfidoConfig).then(result => expect(result).toBe(REJECTED))
    });

    test('reject with docType specified but no countryCode', () => {
      return start({ ...baseConfig, flowSteps: { ...flowSteps, captureDocument: { docType: OnfidoDocumentType.DRIVING_LICENCE } } }).then(result => expect(result).toBe(REJECTED))
    });

    test('reject with countryCode specified but no docType', () => {
      return start({ ...baseConfig, flowSteps: { ...flowSteps, captureDocument: { countryCode: OnfidoCountryCode.GBR } } }).then(result => expect(result).toBe(REJECTED))
    });

    test('reject with an invalid docType', () => {
      return start({ ...baseConfig, flowSteps: { ...flowSteps, captureDocument: { docType: OnfidoCountryCode.GBR } } } as unknown as OnfidoConfig).then(result => expect(result).toBe(REJECTED))
    });

    test('reject with an invalid countryCode', () => {
      return start({ ...baseConfig, flowSteps: { ...flowSteps, captureDocument: { docType: OnfidoDocumentType.DRIVING_LICENCE, countryCode: OnfidoDocumentType.DRIVING_LICENCE } } } as unknown as OnfidoConfig).then(result => expect(result).toBe(REJECTED))
    });

    test('reject with an invalid capture type', () => {
      return start({ ...baseConfig, flowSteps: { ...flowSteps, captureFace: OnfidoDocumentType.DRIVING_LICENCE } } as unknown as OnfidoConfig).then(result => expect(result).toBe(REJECTED))
    });

    test('reject with an empty captureDocument and captureFace', () => {
      return start({ ...baseConfig, flowSteps: { ...flowSteps, captureDocument: {}, captureFace: {} } } as unknown as OnfidoConfig).then(result => expect(result).toBe(REJECTED))
    });
  });
});
