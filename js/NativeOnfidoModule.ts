import { TurboModuleRegistry, TurboModule } from "react-native";

export interface Spec extends TurboModule {
  start(config: Object): Promise<string>;
}

export default TurboModuleRegistry.getEnforcing<Spec>("RNOnfidoSdk");
