import { TurboModuleRegistry, TurboModule } from "react-native";
import type { Double } from 'react-native/Libraries/Types/CodegenTypes';

export interface Spec extends TurboModule {
  start(config: Object): Promise<Object>;
  withMediaCallbacksEnabled(): void;

  // those two are here for event emitter methods
  addListener(eventName: string): void;
  removeListeners(type: Double): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>("RNOnfidoSdk");
