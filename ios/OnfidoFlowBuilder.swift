//
//  OnfidoFlowBuilder.swift
//
//  Copyright © 2016-2023 Onfido. All rights reserved.
//

import Foundation
import Onfido

final class OnfidoFlowBuilder {
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

        let flow: OnfidoFlow
        switch mode {
        case .classic(configBuilder: let configBuilder):
            let config = try configBuilder.build()
            flow = OnfidoFlow(withConfiguration: config)
        case .studio(workflowConfig: let workflowConfig):
            flow = OnfidoFlow(workflowConfiguration: workflowConfig)
        }

        return flow
    }
}
