import { TurboModuleRegistry, TurboModule } from "react-native";

export interface Spec extends TurboModule {
  start(config: Object): Promise<Object>;
}

export default TurboModuleRegistry.getEnforcing<Spec>("RNOnfidoSdk");
