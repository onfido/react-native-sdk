import { NativeModules } from 'react-native';
import { OnfidoDocumentType, OnfidoCaptureType, OnfidoCountryCode } from "./config_constants";

const { OnfidoSdk } = NativeModules;

const Onfido = {
  start(config) {

    if (!config) {
      return configError("config is missing");
    }

    if (!config.sdkToken) {
      return configError("sdkToken is missing");
    }

    if (!config.sdkToken.match(/^[A-Za-z0-9-_=]+\.[A-Za-z0-9-_=]+\.?[A-Za-z0-9-_.+/=]*$/)) {
      return configError("sdkToken is not a valid jwt");
    }

    if (!config.flowSteps) {
      return configError("flowSteps configuration is missing");
    }

    if (config.flowSteps.captureDocument) {
      if (config.flowSteps.captureDocument.docType && !config.flowSteps.captureDocument.countryCode) {
        return configError("countryCode needs to be a ISO 3166-1 3 letter code if docType is specified");
      }

      if (!config.flowSteps.captureDocument.docType && config.flowSteps.captureDocument.countryCode) {
        return configError("docType needs to be provided if countryCode is specified");
      }

      if (config.flowSteps.captureDocument.docType && !(config.flowSteps.captureDocument.docType in OnfidoDocumentType)) {
        return configError("docType is invalid");
      }

      if (config.flowSteps.captureDocument.countryCode && !(config.flowSteps.captureDocument.countryCode in OnfidoCountryCode)) {
        return configError("countryCode is not a ISO 3166-1 3 letter code");
      }
    }

    if (!config.flowSteps.captureDocument && !config.flowSteps.captureFace) {
      return configError("flowSteps doesn't include either valid captureDocument options or valid captureFace options");
    }

    if (config.flowSteps.captureFace && !(config.flowSteps.captureFace.type in OnfidoCaptureType)) {
      return configError("Capture Face type is invalid");
    }

    return OnfidoSdk.start(config).catch(error => {
      console.log(error);
      throw error;
    });
  }
};

const configError = message => {
  const error = new Error(message);
  error.code = "config_error";
  console.log(error);
  return Promise.reject(error);
};

export default Onfido
