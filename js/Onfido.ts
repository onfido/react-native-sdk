import { NativeModules, Platform } from 'react-native';
import { 
  OnfidoDocumentType, 
  OnfidoCaptureType, 
  OnfidoCountryCode,
  OnfidoAlpha2CountryCode, 
  OnfidoConfig,
  OnfidoError,
  OnfidoResult
} from "./config_constants";

import OnfidoSdk from "./NativeOnfidoModule";

const Onfido = {
  start(config: OnfidoConfig): Promise<OnfidoResult | OnfidoError> {

    if (!config) {
      return configError("config is missing");
    }

    if (!config.sdkToken) {
      return configError("sdkToken is missing");
    }

    if (!config.sdkToken.match(/^[A-Za-z0-9-_=]+\.[A-Za-z0-9-_=]+\.?[A-Za-z0-9-_.+/=]*$/)) {
      return configError("sdkToken is not a valid jwt");
    }

    if (config.workflowRunId === undefined) { 
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
    
    
        if (config.flowSteps.captureDocument.countryCode) {
          if (!(config.flowSteps.captureDocument.countryCode in OnfidoCountryCode)) {
            return configError("countryCode is not a ISO 3166-1 3 letter code");
          }
    
          if (Platform.OS === "android") {
            config.flowSteps.captureDocument.alpha2CountryCode = OnfidoAlpha2CountryCode[config.flowSteps.captureDocument.countryCode];
          }
        }
      }
    
      if (!config.flowSteps.captureDocument && !config.flowSteps.captureFace) {
        return configError("flowSteps doesn't include either valid captureDocument options or valid captureFace options");
      }
    
      if (config.flowSteps.captureFace && !(config.flowSteps.captureFace.type in OnfidoCaptureType)) {
        return configError("Capture Face type is invalid");
      }
    }

    return OnfidoSdk.start(config).catch((error: any) => {
      console.log(error);
      throw error;
    }) as Promise<OnfidoResult | OnfidoError>;
  }
};

const configError = (message: string): Promise<OnfidoError> => {
  const error: OnfidoError = new Error(message);
  error.code = "config_error";
  console.log(error);
  return Promise.reject(error);
};

export default Onfido
