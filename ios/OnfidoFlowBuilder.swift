//
//  OnfidoFlowBuilder.swift
//
//  Copyright © 2016-2023 Onfido. All rights reserved.
//

import Foundation
import Onfido

struct OnfidoFlowBuilder {
    private let configBuilder: OnfidoConfigBuilder
    
    init(configBuilder: OnfidoConfigBuilder = OnfidoConfigBuilder()) {
        self.configBuilder = configBuilder
    }
    
    func build(
        with config: OnfidoPluginConfig,
        appearance: Appearance,
        customMediaCallback: CallbackReceiver?
    ) throws -> OnfidoFlow {
        let mode = try configBuilder.build(
            config: config,
            appearance: appearance,
            mediaCallBack: customMediaCallback
        )
        
        switch mode {
        case .classic(configBuilder: let configBuilder):
            let config = try configBuilder.build()
            let flow = OnfidoFlow(withConfiguration: config)
            return flow
        case .studio(workflowConfig: let workflowConfig):
            let flow = OnfidoFlow(workflowConfiguration: workflowConfig)
            return flow
        }
    }    
}
